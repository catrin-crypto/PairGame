package catrin.dev.pairgame.presentation

import android.os.SystemClock.uptimeMillis
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import catrin.dev.pairgame.data.CellState
import catrin.dev.pairgame.data.GameCard
import catrin.dev.pairgame.data.GameCell
import catrin.dev.pairgame.data.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TIMER_DELAY_MS = 500L
const val COINS_PER_ROUND = 100L
const val START_PENALTY_S = 20L
const val PENALTY_MULTIPLIER = 5L
const val MIN_PRIZE_COINS_PER_GAME = 10L

class GameViewModel : ViewModel() {
    private var gameField: MutableList<GameCell> = mutableListOf()
    private var currentCard: GameCard? = null
    private var startTimerMs : Long? = null
    private var totalTimerMs : Long = 0
    private var totalCoins = 0L
    @Volatile
    private var isTimerRunning = false
    @Volatile
    private var isTimerPaused = false
    private val _gameCoinCount = MutableLiveData<Long>()
    private val _state = MutableLiveData<GameState>()
    private val _cellState = MutableLiveData<CellState>()
    private val _error = MutableLiveData<Throwable>()
    private val _timer = MutableLiveData<Long>()
    val gameCoinCount : LiveData<Long> = _gameCoinCount
    val state: LiveData<GameState> = _state
    val cellState : LiveData<CellState> = _cellState
    val error: LiveData<Throwable> = _error
    val timer: LiveData<Long> = _timer

    fun initGamefield(cellsCount: Int) {
        try {
            gameField.clear()
            _cellState.value?.gameCell?.isOpened = false

            if (cellsCount % 2 != 0
                || cellsCount < 2 || GameCard.values().count() * 2 < cellsCount)
                throw RuntimeException(
                    "Cells count: $cellsCount must be odd " +
                            "and in range [2..${GameCard.values().count() * 2}]!"
                )
            for (i in 0..(cellsCount - 1) / 2) {
                gameField.add(GameCell(GameCard.values()[i]))
                gameField.add(GameCell(GameCard.values()[i]))
            }
            gameField.shuffle()
            _state.value = GameState.InitGame(gameField)
        }catch (t:Throwable){handleError(t)}
    }

    fun runTimer(){
       startTimerMs = uptimeMillis()
        isTimerRunning = true
        var pauseShift = 0L
       viewModelScope.launch {
           while (isTimerRunning) {
               delay(TIMER_DELAY_MS)
               if (!isTimerRunning) break
               if (!isTimerPaused) {
                   totalTimerMs =
                       uptimeMillis() - (startTimerMs ?: uptimeMillis()) - pauseShift
                   _timer.postValue(totalTimerMs)
                   appendCoins()
               }else pauseShift += TIMER_DELAY_MS;
           }
       }
    }

    fun pauseTimer(){
        isTimerPaused = true
    }

    fun resumeTimer(){
        isTimerPaused = false
    }

    fun startGame(){
        try{
            _gameCoinCount.value = COINS_PER_ROUND
        _state.value=GameState.ChooseFirstCard(gameField)

        runTimer()
        }catch (t:Throwable){handleError(t)}
    }

    fun activateItem(itemIndex: Int) {
        try{
        if (itemIndex < 0 || itemIndex >= gameField.size)
            throw RuntimeException(
                "Invalid item index $itemIndex" +
                        " must be > 0 and < ${gameField.size}"
            )
        val cell = gameField[itemIndex]
        if (!cell.isOpened) {
            when (_state.value) {
                is GameState.ChooseFirstCard-> {
                    cell.isOpened = true
                    currentCard = cell.card
                    _state.value = GameState.ChooseSecondCard(gameField)
                    _cellState.value = CellState.OpenedCell(itemIndex,cell)
                }
                is GameState.ChooseSecondCard -> {
                    if (cell.card == currentCard) {
                        cell.isOpened = true
                        if (gameField.all { e->e.isOpened }) {
                            isTimerRunning = false
                            totalCoins += gameCoinCount.value ?: 0
                            _state.value = GameState.GameOver(gameField)

                        }
                        _cellState.value = CellState.OpenedCell(itemIndex,cell)
                        _state.value = GameState.ChooseFirstCard(gameField)
                    } else {
                        cell.fadeStartTimeMs = uptimeMillis()
                        _state.value = GameState.ChooseSecondCard(gameField)
                        _cellState.value = CellState.QueryingCell(itemIndex,cell)
                    }
                }
                else ->{}
            }
        }
        }catch (t:Throwable){handleError(t)}
    }

    private fun appendCoins(){
        val timePassed = totalTimerMs / 1000
        var prize = COINS_PER_ROUND
        var penalty = 0L
        if (timePassed > START_PENALTY_S){
            penalty = (timePassed - START_PENALTY_S) * PENALTY_MULTIPLIER
        }
        prize -= penalty
        if (prize < MIN_PRIZE_COINS_PER_GAME)
            prize = MIN_PRIZE_COINS_PER_GAME
        if (_gameCoinCount.value != prize)
            _gameCoinCount.value = prize
    }

    fun getTotalCoins() = totalCoins
    fun setTotalCoins(coins : Long) {
        totalCoins = coins
    }

    fun handleError(error: Throwable) {
        try {
            _error.postValue(error)
        } catch (e: Throwable) {
            Log.e("UE", "Unexpected exception in handleError() exception handler", e)
        }
    }
}
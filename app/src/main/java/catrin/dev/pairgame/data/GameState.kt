package catrin.dev.pairgame.data

sealed class GameState(val gameField : List<GameCell> ){
    class InitGame(gameField : List<GameCell>):GameState(gameField)
    class ChooseFirstCard(gameField : List<GameCell>):GameState(gameField)
    class ChooseSecondCard(gameField : List<GameCell>):GameState(gameField)
    class GameOver(gameField : List<GameCell>):GameState(gameField)
}


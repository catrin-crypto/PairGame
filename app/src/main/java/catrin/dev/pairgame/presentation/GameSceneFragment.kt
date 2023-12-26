package catrin.dev.pairgame.presentation

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import catrin.dev.pairgame.R
import catrin.dev.pairgame.data.GameCard
import catrin.dev.pairgame.data.GameCell
import catrin.dev.pairgame.data.GameState
import catrin.dev.pairgame.databinding.FragmentGameSceneBinding
import catrin.dev.pairgame.utils.ResourceUtils.getDrawable
import java.time.LocalTime

const val GAMEFIELD_CELLS_COUNT = 20;
const val FADEOUT_TIME_MS = 1000;

class GameSceneFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private val binding: FragmentGameSceneBinding by viewBinding()
    private var shouldRedrawGamefield = true
    private lateinit var backDrawable:Drawable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game_scene, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            gameViewModel.state.observe(viewLifecycleOwner) { gameState ->
                try {
                    displayGameState(gameState)
                } catch (t: Throwable) {
                    gameViewModel.handleError(t)
                }
            }
            gameViewModel.cellState.observe(viewLifecycleOwner) { gameState ->
                try {
                    redrawCell(gameState.cellIndex, gameState.gameCell)
                } catch (t: Throwable) {
                    gameViewModel.handleError(t)
                }
            }
            gameViewModel.timer.observe(viewLifecycleOwner) { timer ->
                try {
                    if (timer / 1000 < 24 * 60 * 60 - 1)
                        binding.timer.text = LocalTime.ofSecondOfDay(timer / 1000).toString()
                } catch (t: Throwable) {
                    gameViewModel.handleError(t)
                }
            }
            gameViewModel.gameCoinCount.observe(viewLifecycleOwner) { coins ->
                try {
                    binding.coins.text = coins.toString()
                } catch (t: Throwable) {
                    gameViewModel.handleError(t)
                }
            }
            binding.gameGrid.useDefaultMargins = true
            binding.gameGrid.columnCount = GAMEFIELD_CELLS_COUNT / 5
            binding.gameGrid.rowCount = GAMEFIELD_CELLS_COUNT / 4
             //val density = requireActivity().resources.displayMetrics.density
             val cellwidth = requireActivity().resources.displayMetrics.widthPixels / 5
             backDrawable = getDrawable(requireContext(),R.drawable.game_card_background)!!
             val width = cellwidth//(55 * density).toInt()
             val height = 65 / 50 * cellwidth
            backDrawable.setBounds(0, 0, width, height)
            drawGameField()
            initGame()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }

    }

    private fun initCardsImages() {
        try {
            GameCard.Doorbell.setImage(getDrawable(requireContext(), R.drawable.doorbell_24))
            GameCard.DownhillSkiing.setImage(
                getDrawable(
                    requireContext(),
                    R.drawable.downhill_skiing_24
                )
            )
            GameCard.DryCleaning.setImage(getDrawable(requireContext(), R.drawable.dry_cleaning_24))
            GameCard.EdgeSensor.setImage(
                getDrawable(
                    requireContext(),
                    R.drawable.edgesensor_low_24
                )
            )
            GameCard.ElectricBike.setImage(
                getDrawable(
                    requireContext(),
                    R.drawable.electric_bike_24
                )
            )
            GameCard.ElectricCar.setImage(getDrawable(requireContext(), R.drawable.electric_car_24))
            GameCard.EmojiNature.setImage(getDrawable(requireContext(), R.drawable.emoji_nature_24))
            GameCard.Events.setImage(getDrawable(requireContext(), R.drawable.events_24))
            GameCard.Favorite.setImage(getDrawable(requireContext(), R.drawable.favorite_24))
            GameCard.HeartBroken.setImage(getDrawable(requireContext(), R.drawable.heart_broken_24))
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun initGame() {
        try {
            initCardsImages()
            GameCard.checkImagesAreNotNull()
            gameViewModel.initGamefield(GAMEFIELD_CELLS_COUNT)
            gameViewModel.startGame()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }


    private fun displayGameState(gameState: GameState) {
        try {
            when (gameState) {
                is GameState.InitGame -> {
                    drawGameField(gameState)
                }

                is GameState.GameOver -> {
                    findNavController()
                        .navigate(R.id.action_game_scene_fragment_to_game_over_fragment)
                }

                else -> {
//                    if (shouldRedrawGamefield) {
//                        shouldRedrawGamefield = false
//                        drawGameField(gameState)
//                    }
                }
            }
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun drawGameField(gameState: GameState? = null) {
        try {
            binding.gameGrid.removeAllViews()
            repeat(GAMEFIELD_CELLS_COUNT) { i ->
                val layout = ConstraintLayout(requireContext())
                layout.id = View.generateViewId()
                //val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                //    ViewGroup.LayoutParams.WRAP_CONTENT)
                val layoutParams = ConstraintLayout.LayoutParams(backDrawable.bounds.width(),
                backDrawable.bounds.height())
                layout.layoutParams = layoutParams
                val set = ConstraintSet()
                val imageView = ImageView(requireContext())
                imageView.setOnClickListener {
                    try {
                        gameViewModel.activateItem(i)
                    } catch (t: Throwable) {
                        gameViewModel.handleError(t)
                    }
                }

                imageView.id = View.generateViewId()

                imageView.background = backDrawable
                val imageViewParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT)
                imageView.layoutParams = imageViewParams
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                val backImageView = ImageView(requireContext())
                backImageView.setOnClickListener {
                    try {
                        gameViewModel.activateItem(i)
                    } catch (t: Throwable) {
                        gameViewModel.handleError(t)
                    }
                }
                backImageView.id = View.generateViewId()
                backImageView.background = backDrawable
                val backViewParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT)
                backImageView.layoutParams = backViewParams
                backImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                layout.addView(backImageView)
                layout.addView(imageView)

                set.clone(layout)
                val margin = requireActivity().resources.displayMetrics.widthPixels / 60
                set.connect(imageView.id, ConstraintSet.TOP,layout.id,ConstraintSet.TOP,margin)
                set.connect(imageView.id, ConstraintSet.START,layout.id,ConstraintSet.START,margin)
                set.connect(backImageView.id, ConstraintSet.TOP,layout.id,ConstraintSet.TOP,margin)
                set.connect(backImageView.id, ConstraintSet.START,layout.id,ConstraintSet.START,margin)
                set.applyTo(layout)

                binding.gameGrid.addView(layout, i)
                //binding.gameGrid.addView(imageView, i)
                //binding.gameGrid.addView(backImageView,i)
                gameState?.let { redrawCell(i, it.gameField[i]) }
            }
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            gameViewModel.resumeTimer()
            shouldRedrawGamefield = true
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            gameViewModel.pauseTimer()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun redrawCell(cellIndex: Int, gameCell: GameCell) {
        try {
            if (cellIndex < 0 || cellIndex >= binding.gameGrid.childCount)
                throw RuntimeException(
                    "Index of cell $cellIndex is incorrect " +
                            ", must be >= 0 and < ${binding.gameGrid.childCount}!"
                )
            val vg = binding.gameGrid.getChildAt(cellIndex) as ViewGroup
            val viewCell = vg.getChildAt(0) as ImageView
            val backCard = vg.getChildAt(1) as ImageView
            if (gameCell.isOpened) {
                viewCell.setImageDrawable(gameCell.card?.getImage())
                backCard.visibility = GONE
            } else {
                val curTm = SystemClock.uptimeMillis()
                val msecsLeft = FADEOUT_TIME_MS - (curTm - (gameCell.fadeStartTimeMs
                    ?: (curTm - FADEOUT_TIME_MS)))
                if (msecsLeft > 0) {
                     animateCrossFadeCell(viewCell,backCard,gameCell,msecsLeft)
                } else viewCell.setImageDrawable(backDrawable)
            }
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun animateFadeoutCell(
        viewCell: ImageView,
        gameCell: GameCell,
        msecsLeft: Long
    ) {
        try {
            viewCell.setImageDrawable(gameCell.card?.getImage())
            val fadeoutStart = msecsLeft.toFloat() / FADEOUT_TIME_MS
            val fadeOut = AlphaAnimation(fadeoutStart, 0.0f)
            fadeOut.interpolator = LinearInterpolator()
            fadeOut.startOffset = 0
            fadeOut.duration = msecsLeft
            viewCell.animation = fadeOut
            viewCell.animate()
                .withEndAction {
                    viewCell.setImageDrawable(backDrawable)
                }
                .start()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun animateCrossFadeCell(
        viewCell: ImageView,
        backCell: ImageView,
        gameCell: GameCell,
        msecsLeft: Long
    ) {
        try {
            viewCell.setImageDrawable(gameCell.card?.getImage())
            viewCell.scaleType = ImageView.ScaleType.FIT_XY
            //backCell.setImageDrawable( backDrawable)
            val fadeoutStart = msecsLeft.toFloat() / FADEOUT_TIME_MS
            val fadeOut = AlphaAnimation(fadeoutStart, 0.0f)
            val fadeIn = AlphaAnimation(fadeoutStart, 1f)
            fadeOut.interpolator = LinearInterpolator()
            fadeIn.interpolator = LinearInterpolator()
            fadeOut.startOffset = 0
            fadeIn.startOffset = 0
            fadeOut.duration = msecsLeft / 2
            fadeIn.duration = msecsLeft / 2
            fadeIn.repeatCount = 2
            fadeIn.repeatMode = AlphaAnimation.REVERSE
            fadeOut.repeatCount = 1
            fadeOut.repeatMode = AlphaAnimation.REVERSE
            fadeOut.repeatCount = 1
            backCell.animation = fadeOut
            viewCell.animation = fadeIn
            backCell.animate()
                .start()
            viewCell.animate()
                .withEndAction {
                    viewCell.setImageDrawable( backDrawable)
                    viewCell.scaleType = ImageView.ScaleType.FIT_XY
                }.start()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun animateFlipCard(
        viewCell: ImageView,
        backView : ImageView,
        gameCell: GameCell,
        msecsLeft: Long
    ) {
        var flip_anim: AnimatorSet
        var back_anim: AnimatorSet
        var flip_anim2: AnimatorSet
        var back_anim2: AnimatorSet

        flip_anim = AnimatorInflater.loadAnimator(
            requireContext().applicationContext,
            R.animator.rotation
        ) as AnimatorSet


        //setting the back animaton
        back_anim = AnimatorInflater.loadAnimator(
            requireContext().applicationContext,
            R.animator.back_flip
        ) as AnimatorSet

        flip_anim2 = AnimatorInflater.loadAnimator(
            requireContext().applicationContext,
            R.animator.rotation
        ) as AnimatorSet


        //setting the back animaton
        back_anim2 = AnimatorInflater.loadAnimator(
            requireContext().applicationContext,
            R.animator.back_flip
        ) as AnimatorSet


        flip_anim.setTarget(viewCell)
        back_anim.setTarget(backView)

        flip_anim2.startDelay = 800
        flip_anim2.setTarget(backView)
        back_anim2.setTarget(viewCell)

        flip_anim.start()
        back_anim.start()
        flip_anim2.start()
        back_anim2.start()
    }
}
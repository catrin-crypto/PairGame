package catrin.dev.pairgame.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import by.kirich1409.viewbindingdelegate.viewBinding
import catrin.dev.pairgame.R
import catrin.dev.pairgame.databinding.FragmentGameOverBinding
import catrin.dev.pairgame.utils.ResourceUtils

class GameOverFragment : Fragment() {
    private val binding: FragmentGameOverBinding by viewBinding()
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game_over, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.coinsEarned.text = gameViewModel.gameCoinCount.value.toString()
            PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
                .edit().putLong(TOTAL_COINS_TAG, gameViewModel.getTotalCoins()).apply()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }

        binding.homeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_game_over_fragment_to_menu_fragment)
        }
        val image = binding.winPrizeIv
        if (gameViewModel.gameCoinCount.value!! in 10..29) {
            image.setImageDrawable(
                ResourceUtils.getDrawable(
                    requireContext(),
                    R.drawable.bronze_prize_24
                )
            )
        } else if (gameViewModel.gameCoinCount.value!! in 30..90) {
            image.setImageDrawable(
                ResourceUtils.getDrawable(
                    requireContext(),
                    R.drawable.silver_prize
                )
            )
        } else {
            image.setImageDrawable(
                ResourceUtils.getDrawable(
                    requireContext(),
                    R.drawable.golden_prize
                )
            )
        }

        scaleView(image as View, 0f, 1f)
    }

    private fun scaleView(v: View, startScale: Float, endScale: Float) {
        val anim: Animation = ScaleAnimation(
            startScale, endScale,  // Start and end values for the X axis scaling
            startScale, endScale,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
        )
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = 600
        anim.interpolator = DecelerateInterpolator()
        v.startAnimation(anim)
    }
}
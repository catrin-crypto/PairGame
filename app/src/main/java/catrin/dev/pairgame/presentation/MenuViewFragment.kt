package catrin.dev.pairgame.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import catrin.dev.pairgame.R
import catrin.dev.pairgame.databinding.FragmentMenuViewBinding


class MenuViewFragment : Fragment() {
    private val binding: FragmentMenuViewBinding by viewBinding()
    private val gameViewModel : GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_menu_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.playButton.setOnClickListener {
                try{
                findNavController().navigate(R.id.action_menu_fragment_to_game_scene_fragment)
                }catch(t:Throwable){gameViewModel.handleError(t)}
            }
            binding.totalCoins.text = gameViewModel.getTotalCoins().toString()
        }catch(t:Throwable){gameViewModel.handleError(t)}
    }
}
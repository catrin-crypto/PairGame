package catrin.dev.pairgame.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import catrin.dev.pairgame.R
import catrin.dev.pairgame.databinding.ActivityMainBinding
import java.lang.System.exit

const val TOTAL_COINS_TAG = "TotalCoins"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false
    private val gameViewModel: GameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        gameViewModel.error.observe(this) { error ->
            logAndToast(error)
        }
        try {
            gameViewModel.setTotalCoins(
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    .getLong(TOTAL_COINS_TAG, 0)
            )
            binding = ActivityMainBinding.inflate(layoutInflater)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setContentView(binding.root)
            initNavController()
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun initNavController() {
        try {
            navController = findNavController(R.id.nav_host_fragment_activity_main)
        } catch (t: Throwable) {
            gameViewModel.handleError(t)
        }
    }

    private fun logAndToast(t: Throwable) = logAndToast(t, this::class.java.toString())
    private fun logAndToast(t: Throwable, TAG: String) {
        try {
            Log.e(TAG, "", t)
            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
        } catch (ex: Throwable) {
            Log.e("UE", "Unexpected exception in logAndToast exception handler", ex)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        try {

            if (doubleBackToExitPressedOnce) {
                exit(0)
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this,
                "Press BACK to EXIT",
                Toast.LENGTH_SHORT
            ).show()

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    try {
                        doubleBackToExitPressedOnce = false
                    } catch (_: Throwable) {
                    }
                },
                2000
            )
        } catch (t: Throwable) {
            logAndToast(t)
        }

    }

}
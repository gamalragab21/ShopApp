package com.developers.shopapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ActivitySetupBinding
import com.developers.shopapp.entities.Splash
import com.developers.shopapp.ui.adapters.AdapterViewPagerSplash
import com.developers.shopapp.utils.Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT
import com.developers.shopapp.utils.Constants.TAG
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySetupBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent=intent
        navigateToLoginFragmentIfNeeded(intent)

    }
    private fun navigateToLoginFragmentIfNeeded(intent: Intent?){
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragmentSetup.id) as NavHostFragment
        val navController = navHostFragment.navController
        if (intent?.action==ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT){
           navController.navigate(R.id.action_global_to_loginFragment)
        }
    }
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.i(TAG, "onNewIntent: ${intent?.action}")
//        navigateToLoginFragmentIfNeeded(intent)
//    }
}
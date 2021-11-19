package com.developers.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.prefs.Preferences

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    lateinit var dataStore:DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


// her bottomNavigation is visible in all time ( means in all fragments )

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)
        binding.navView.setOnItemReselectedListener {
            /*No Operation*/
        }



        // to hide or show bottomNavigation in some Fragments

       navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when(destination.id){
//                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment->{
//                    bottomNavigationView.isVisible=true
//                }
                R.id.navigation_home,R.id.navigation_cart->{}
//                else ->  bottomNavigationView.isVisible=false

            }
        }


    }

    //(activity as MainActivity?)!!.hide()
    public open fun show() {
        binding.navView.visibility= View.VISIBLE
    }
    open fun hide() {
        binding.navView.visibility= View.GONE
    }
}
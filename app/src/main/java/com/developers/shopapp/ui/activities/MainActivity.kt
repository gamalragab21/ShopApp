package com.developers.shopapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ActivityMainBinding
import com.developers.shopapp.helpers.ConnectionLiveData
import com.developers.shopapp.ui.dialog.NoInternetConnectionDialog
import com.developers.shopapp.utils.isNetworkConnected
import com.developers.shopapp.utils.isOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    private lateinit var connectionLiveData: ConnectionLiveData

    lateinit var dataStore:DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // check internet

        connectionLiveData= ConnectionLiveData(this)


// her bottomNavigation is visible in all time ( means in all fragments )

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)
        binding.navView.setOnItemReselectedListener {
            /*No Operation*/
        }

        connectionLiveData.observe(this,{networkAvailable->
            if (!networkAvailable){
                Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show()

                //navController.navigate(R.id.noInternetConnectionDialog)
            }
        })

        // to hide or show bottomNavigation in some Fragments

       navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when(destination.id){
                R.id.restaurantDetailsFragment,
                R.id.foodDetailsFragment,
                R.id.mapsFragment,
                R.id.imageViewerFragment,
                R.id.searchRestaurantFragment->{
                    // setFullScreen()
                    hide()
                }
                else->{
                  //  deleteFullScreen()
                    show()
                }

//                else ->  bottomNavigationView.isVisible=false

            }
        }


    }

    //(activity as MainActivity?)!!.hide()
    fun show() {
        binding.navView.visibility= View.VISIBLE
    }
    fun hide() {
        binding.navView.visibility= View.GONE
    }
}
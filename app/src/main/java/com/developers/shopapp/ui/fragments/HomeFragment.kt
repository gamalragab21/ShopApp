package com.developers.shopapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentHomeBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.activities.SetupActivity
import com.developers.shopapp.ui.adapters.FavRestaurantAdapter
import com.developers.shopapp.ui.adapters.RestaurantAdapter
import com.developers.shopapp.ui.adapters.ViewPagerFragmentAdapter
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.snackbar
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.recyclerview.widget.StaggeredGridLayoutManager




@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val restaurantViewModel: RestaurantViewModel by viewModels()

    @Inject
    lateinit var favRestaurantAdapter: FavRestaurantAdapter


    private val authenticationViewModel: AuthenticationViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val myHomeLocation = dataStoreManager.glucoseFlow.value

        favRestaurantAdapter.laLng = myHomeLocation?.latLng

        setupRecyclerViewFavRestaurant()

        subscribeToObservers()

        adapterActions()


        setTabs()

        binding.seeMoreFav.setOnClickListener {

        }

    }

    private fun setupRecyclerViewFavRestaurant() = binding.homeFragmentRecyclerFav.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,true)
        adapter = favRestaurantAdapter

    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            restaurantViewModel.favouritesRestaurantStatus.collect(
                EventObserver(
                    onLoading = {
                        binding.spinKitFav.isVisible = true
                    },
                    onSuccess = { favRestaurant ->
                        binding.spinKitFav.isVisible = false

                        favRestaurant.data?.let {
                            binding.leanerFav.isVisible=it.isNotEmpty()
                            binding.seeMoreFav.isVisible = it.size > 2
                            favRestaurantAdapter.restaurants = it
                        }
                    },
                    onError = {
                        Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                        snackbar(it)
                        binding.spinKitFav.isVisible = false
                    }
                )
            )

        }

    }

    private fun adapterActions() {

        favRestaurantAdapter.setOnItemClickListener {
            val bundle = bundleOf(Constants.CURRENT_RESTAURANT to it)
            findNavController().navigate(R.id.restaurantDetailsFragment, bundle)
        }


    }


    private fun setTabs() {

        binding.tabs.addTab(binding.tabs.newTab().setText("Recent"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Favourite"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Rating"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Popular"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Trends"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Notification"))

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager2.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        setupViewPager(binding.viewPager2)


    }

    private fun setupViewPager(viewPager2: ViewPager2) {

        val adapter = ViewPagerFragmentAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
            }

            override fun onPageScrollStateChanged(state: Int) {
                /*empty*/
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private suspend fun clearToken() {
        dataStoreManager.setUserInfo(token = "")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        restaurantViewModel.getAllFavouritesRestaurant()
    }


}
package com.developers.shopapp.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentSavesBinding
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.RestaurantAdapter
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SavesFragment:Fragment() {
    private var _binding: FragmentSavesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var restaurantAdapter: RestaurantAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val restaurantViewModel: RestaurantViewModel by viewModels()


    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value

        restaurantAdapter.laLng = dataUserInfo?.latLng



        setupRecyclerViewForFavFoods()
        setupRecyclerViewForFavRestaurant()

        subscribeToObservers(dataUserInfo)

        adapterRestaurantActions()

        binding.foodsButton.setOnClickListener {
            binding.foodsButton.setTextColor(Color.WHITE)

            binding.foodsButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryDark)

            binding.restaurantButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorAccent)
              binding.restaurantButton.setTextColor(Color.BLACK)

            binding.favFoodsRecyclerview.isVisible=true
            binding.favRestaurantsRecyclerview.isVisible=false

        }

        binding.restaurantButton.setOnClickListener {
            binding.restaurantButton.setTextColor(Color.WHITE)
            binding.restaurantButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryDark)

            binding.foodsButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorAccent)
            binding.foodsButton.setTextColor(Color.BLACK)

            binding.favFoodsRecyclerview.isVisible=false
            binding.favRestaurantsRecyclerview.isVisible=true
            restaurantViewModel.getAllFavouritesRestaurant()

        }


    }
    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    restaurantViewModel.favouritesRestaurantStatus.collect(
                        EventObserver(
                            onLoading = {
                                binding.spinKit.isVisible = true
                                binding.emptyView.isVisible = false
                            },
                            onSuccess = { favRestaurant ->
                                binding.spinKit.isVisible = false
                                binding.emptyView.isVisible = false

                                favRestaurant.data?.let {
                                    binding.emptyView.isVisible = it.isEmpty()
                                    restaurantAdapter.restaurantes = it
                                }


                            },
                            onError = {
                                Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                                snackbar(it)
                                binding.spinKit.isVisible = false
                                binding.emptyView.isVisible = true
                            }
                        )
                    )

                }

                launch {
                    restaurantViewModel.deleteFavRestaurantStatus.collect(
                        EventObserver(
                            onLoading = {

                            },
                            onSuccess = {

                                snackbar(it.message)



                            },
                            onError = {
                                snackbar(it)

                            }
                        )
                    )
                }

            }
        }

    }
    private fun adapterRestaurantActions() {
        restaurantAdapter.setOnSavedClickListener { restaurant, imageView, position ->
            if (restaurant.inFav!!) {
                restaurantViewModel.deleteFavRestaurant(restaurant)
                imageView.setImageResource(R.drawable.not_save)
                binding.emptyView.isVisible = restaurantAdapter.clearItemAndIfLast(restaurant,position)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding= FragmentSavesBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun setupRecyclerViewForFavFoods() = binding.favFoodsRecyclerview.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
      //  adapter = recentAdapter

    }

    private fun setupRecyclerViewForFavRestaurant() = binding.favRestaurantsRecyclerview.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = restaurantAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
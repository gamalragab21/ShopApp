package com.developers.shopapp.ui.fragments.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentRecentBinding
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.RecentAdapter
import com.developers.shopapp.ui.viewmodels.TabsViewModel
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecentFragment : Fragment() {
    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var recentAdapter: RecentAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val tabsViewModel: TabsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value

        recentAdapter.laLng = dataUserInfo?.latLng

        tabsViewModel.getAllRestaurant()
        setupRecyclerViewRecent()
        subscribeToObservers(dataUserInfo)

        recentAdapter.setOnSavedClickListener { restaurant, imageView ->
            if (restaurant.inFav!!) {
                tabsViewModel.deleteFavRestaurant(restaurant)
                imageView.setImageResource(R.drawable.not_save)
                restaurant.inFav = false
            } else {
                tabsViewModel.setFavRestaurant(restaurant)
                restaurant.inFav = true
                imageView.setImageResource(R.drawable.saved)
            }
        }

    }

    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    tabsViewModel.restaurantStatus.collect(EventObserver(
                        onLoading = {
                            binding.spinKit.isVisible = true
                            binding.emptyView.isVisible = false
                        },
                        onSuccess = { restaurant ->

                            binding.spinKit.isVisible = false
                            binding.emptyView.isVisible = false

                            restaurant.data?.let {
                                recentAdapter.restaurantes = it
                            }


                        },
                        onError = {
                            Log.i(TAG, "subscribeToObservers: ${it}")
                            snackbar(it)
                            binding.spinKit.isVisible = false
                            binding.emptyView.isVisible = true
                        }
                    )
                    )

                }

                launch {
                    tabsViewModel.deleteFavRestaurantStatus.collect(EventObserver(
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

                launch {
                    tabsViewModel.setFavRestaurantStatus.collect(EventObserver(
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

    private fun setupRecyclerViewRecent() = binding.recentFragmentRecycler.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = recentAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
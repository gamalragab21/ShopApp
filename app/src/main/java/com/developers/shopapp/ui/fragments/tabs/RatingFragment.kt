package com.developers.shopapp.ui.fragments.tabs

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentRatingBinding
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.RestaurantAdapter
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.PermissionsUtility
import com.developers.shopapp.utils.Utils
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class RatingFragment:Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentRatingBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var restaurantAdapter: RestaurantAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val restaurantViewModel: RestaurantViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value

        restaurantAdapter.laLng = dataUserInfo?.latLng


        setupRecyclerViewRecent()
        subscribeToObservers(dataUserInfo)

        adapterActions()

    }
    private fun adapterActions() {
        restaurantAdapter.setOnSavedClickListener { restaurant, imageView, position->
            if (restaurant.inFav!!) {
                restaurantViewModel.deleteFavRestaurant(restaurant)
                imageView.setImageResource(R.drawable.not_save)
                restaurant.inFav = false
            } else {
                restaurantViewModel.setFavRestaurant(restaurant)
                restaurant.inFav = true
                imageView.setImageResource(R.drawable.saved)
            }
        }

        restaurantAdapter.setOnItemClickListener {
            val bundle = bundleOf(Constants.CURRENT_RESTAURANT to it)
            findNavController().navigate(R.id.restaurantDetailsFragment,bundle)
        }

        restaurantAdapter.setOnContactClickListener {
            restaurantViewModel.callPhone.postValue(it.contact)
            requestPermissions()
        }
    }

    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    restaurantViewModel.ratingRestaurantStatus.collect(
                        EventObserver(
                        onLoading = {
                            binding.shimmer.isVisible=true
                            binding.shimmer.startShimmer()
                            binding.spinKit.isVisible = true
                            binding.emptyView.isVisible = false
                        },
                        onSuccess = { ratingRestaurant ->
                            binding.shimmer.isVisible=false
                            binding.shimmer.stopShimmer()
                            binding.spinKit.isVisible = false
                            binding.emptyView.isVisible = false

                            ratingRestaurant.data?.let {
                                binding.emptyView.isVisible = it.isEmpty()
                                restaurantAdapter.restaurantes = it
                            }


                        },
                        onError = {
                            binding.shimmer.isVisible=false
                            binding.shimmer.stopShimmer()
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

                launch {
                    restaurantViewModel.setFavRestaurantStatus.collect(
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

    private fun setupRecyclerViewRecent() = binding.ratingFragmentRecycler.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = restaurantAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding= FragmentRatingBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onResume() {
        super.onResume()

        restaurantViewModel.getAllRatingRestaurant()
    }
    private fun requestPermissions() {

        if (PermissionsUtility.hasCallPhonePermissions(requireContext())) {
            callPhone()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "you need to accept Call Phone permissions to use this Option.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.CALL_PHONE
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "you need to accept Call Phone permissions to use this Option.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.CALL_PHONE
            )
        }
    }

    private fun callPhone() {
        try {
            Utils.startCallIntent(requireContext(),restaurantViewModel.callPhone.value.toString())
        }catch (e:Exception){
            snackbar("Phone Not Found")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        callPhone()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }
}
package com.developers.shopapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.developers.shopapp.databinding.FragmentSavesBinding
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.ProductAdapter
import com.developers.shopapp.ui.adapters.RestaurantAdapter
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
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
class SavesFragment:Fragment(),EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentSavesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var restaurantAdapter: RestaurantAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    @Inject
    lateinit var productAdapter: ProductAdapter
    val restaurantViewModel: RestaurantViewModel by viewModels()
    private val categoryProductViewModel: CategoryProductViewModel by viewModels()


    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value

        restaurantAdapter.laLng = dataUserInfo?.latLng



        setupRecyclerViewForFavFoods()

        setupRecyclerViewForFavRestaurant()

        subscribeToObservers(dataUserInfo)

        adapterRestaurantActions()

        adapterActions()

        binding.foodsButton.setOnClickListener {
            binding.foodsButton.setTextColor(Color.WHITE)

            binding.foodsButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryDark)

            binding.restaurantButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorAccent)
              binding.restaurantButton.setTextColor(Color.BLACK)

            binding.favFoodsRecyclerview.isVisible=true
            binding.favRestaurantsRecyclerview.isVisible=false

            categoryProductViewModel.getAllFavProducts()

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
    private fun adapterActions() {
        productAdapter.setOnSavedClickListener { product, imageView, position ->
            if (product.inFav!!) {
                categoryProductViewModel.deleteFavProduct(product)
                imageView.setImageResource(R.drawable.not_save)
                product.inFav = false
                binding.emptyView.isVisible = productAdapter.clearItemAndIfLast(product,position)
            } else {
                categoryProductViewModel.setFavProduct(product)
                product.inFav = true
                imageView.setImageResource(R.drawable.saved)
            }
        }

        productAdapter.setOnItemClickListener {
            val bundle = bundleOf(Constants.CURRENT_PRODUCT to it)
            //navController.navigate(R.id.restaurantDetailsFragment,bundle)
        }


    }
    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    restaurantViewModel.favouritesRestaurantStatus.collect(
                        EventObserver(
                            onLoading = {
                                binding.shimmer.isVisible=true
                                binding.shimmer.startShimmer()
                                binding.spinKit.isVisible = true
                                binding.emptyView.isVisible = false
                            },
                            onSuccess = { favRestaurant ->
                                binding.spinKit.isVisible = false
                                binding.emptyView.isVisible = false
                                binding.shimmer.isVisible=false
                                binding.shimmer.stopShimmer()
                                favRestaurant.data?.let {
                                    binding.emptyView.isVisible = it.isEmpty()
                                    restaurantAdapter.restaurantes = it
                                }


                            },
                            onError = {
                                Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                                snackbar(it)
                                binding.shimmer.isVisible=false
                                binding.shimmer.stopShimmer()
                                binding.spinKit.isVisible = false
                                binding.emptyView.isVisible = true
                            }
                        )
                    )

                }

                launch {
                    categoryProductViewModel.favouritesProductStatus.collect(
                        EventObserver(
                            onLoading = {
                                binding.shimmer.isVisible=true
                                binding.shimmer.startShimmer()
                                binding.spinKit.isVisible = true
                                binding.emptyView.isVisible = false
                            },
                            onSuccess = { favProduct ->
                                binding.shimmer.isVisible=false
                                binding.shimmer.stopShimmer()
                                binding.spinKit.isVisible = false
                                binding.emptyView.isVisible = false

                                favProduct.data?.let {
                                    binding.emptyView.isVisible = it.isEmpty()
                                    productAdapter.products = it
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
                    categoryProductViewModel.deleteFavProductStatus.collect(EventObserver(
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
                    categoryProductViewModel.setFavProductStatus.collect(EventObserver(
                        onLoading = {},
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
        restaurantAdapter.setOnItemClickListener {
            val bundle = bundleOf(Constants.CURRENT_RESTAURANT to it)
            findNavController().navigate(R.id.restaurantDetailsFragment,bundle)
        }

        restaurantAdapter.setOnContactClickListener {
            restaurantViewModel.callPhone.postValue(it.contact)
            requestPermissions()
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
       adapter = productAdapter

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

    override fun onResume() {
        super.onResume()
        categoryProductViewModel.getAllFavProducts()
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
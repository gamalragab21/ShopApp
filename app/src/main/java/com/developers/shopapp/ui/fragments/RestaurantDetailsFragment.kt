package com.developers.shopapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentDetailsRestaurantBinding
import com.developers.shopapp.entities.Category
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.activities.MainActivity
import com.developers.shopapp.ui.adapters.ProductAdapter
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Constants.CURRENT_PRODUCT
import com.developers.shopapp.utils.Constants.CURRENT_RESTAURANT
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.PermissionsUtility
import com.developers.shopapp.utils.Utils.getTimeAgo
import com.developers.shopapp.utils.Utils.startCallIntent
import com.developers.shopapp.utils.setFullScreen
import com.developers.shopapp.utils.snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class RestaurantDetailsFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentDetailsRestaurantBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var productAdapter: ProductAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var glide: RequestManager

    private val categoryProductViewModel: CategoryProductViewModel by viewModels()
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    val navController by lazy { findNavController() }


    val currentRestaurant by lazy {
        arguments?.getParcelable<Restaurant>(CURRENT_RESTAURANT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value



        onScrollViewAndRecyclerView()

        currentRestaurant?.let { restaurant ->
            glide.load(restaurant.imageRestaurant).into(binding.resturantImage)

            binding.restaurantName.text = restaurant.restaurantName

            binding.restaurantRating.text = "${restaurant.rateCount}"

            binding.restaurantInfo.text = restaurant.restaurantType


            if (restaurant.inFav == true) {
                binding.imageSave.setImageResource(R.drawable.saved)
            } else {
                binding.imageSave.setImageResource(R.drawable.not_save)
            }

            binding.restaurantDeliervery.text = if (restaurant.freeDelivery == true) {
                "Free Delivery"
            } else {
                "Not Free Delivery"
            }

            binding.restaurantTime.text = getTimeAgo(restaurant.createAt, requireContext())
            binding.restaurantCall.setOnClickListener {
                requestPermissions()
            }
            quickActions(restaurant)
        }

        subscribeToObservers(dataUserInfo)

        setupRecyclerViewProduct()

        adapterActions()
    }

    private fun adapterActions() {
        productAdapter.setOnSavedClickListener { product, imageView, position ->
            if (product.inFav!!) {
                categoryProductViewModel.deleteFavProduct(product)
                imageView.setImageResource(R.drawable.not_save)
                product.inFav = false
            } else {
                categoryProductViewModel.setFavProduct(product)
                product.inFav = true
                imageView.setImageResource(R.drawable.saved)
            }
        }

        productAdapter.setOnItemClickListener {
          //  val bundle = bundleOf(CURRENT_PRODUCT to it)
            //navController.navigate(R.id.restaurantDetailsFragment,bundle)
        }


    }

    private fun quickActions(restaurant: Restaurant) {
        binding.imageSave.setOnClickListener {
            if (restaurant.inFav!!) {
                restaurantViewModel.deleteFavRestaurant(restaurant)
                binding.imageSave.setImageResource(R.drawable.not_save)
                restaurant.inFav = false
            } else {
                restaurantViewModel.setFavRestaurant(restaurant)
                restaurant.inFav = true
                binding.imageSave.setImageResource(R.drawable.saved)
            }
        }
    }

    private fun setupActionTabsCategory(categories: List<Category>) {
        binding.tabs.addTab(binding.tabs.newTab().setText("For You").setId(0))
        for (i in categories.indices) {
            binding.tabs.addTab(
                binding.tabs.newTab().setText(categories[i].categoryName)
                    .setId(categories[i].categoryId!!)
            )
        }

        categoryProductViewModel.getProductOfCategory(currentRestaurant!!.restaurantId!!, 0)
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    val categoryId = tab.id
                    categoryProductViewModel.getProductOfCategory(
                        currentRestaurant!!.restaurantId!!,
                        categoryId
                    )
                    productAdapter.products = arrayListOf()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    private fun onScrollViewAndRecyclerView() {
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            when {
                scrollY > oldScrollY -> {
                    Log.i(TAG, "Scroll DOWN")
                    (activity as MainActivity?)!!.hide()

                }
                scrollY < oldScrollY -> {
                    Log.i(TAG, "Scroll UP")
                    (activity as MainActivity?)!!.show()
                }
                scrollY == 0 -> {
                    Log.i(TAG, "TOP SCROLL")
                }
                scrollY == v.measuredHeight - v.getChildAt(0).measuredHeight -> {
                    Log.i(TAG, "BOTTOM SCROLL")
                }
            }
        })
        binding.productRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling up
                    (activity as MainActivity?)!!.hide()
                } else {
                    // Scrolling down
                    (activity as MainActivity?)!!.show()
                }

            }
        })
    }

    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    categoryProductViewModel.categoryStatus.collect(
                        EventObserver(
                            onLoading = {
                                binding.spinKit.isVisible = true
                            },
                            onSuccess = { category ->
                                binding.tabs.isVisible = category.success
                                binding.spinKit.isVisible = false

                                category.data?.let {
                                    setupActionTabsCategory(it)

                                }
                            },
                            onError = {
                                snackbar(it)
                                binding.spinKit.isVisible = false
                            }
                        )
                    )
                }

                launch {
                    restaurantViewModel.deleteFavRestaurantStatus.collect(EventObserver(
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
                    restaurantViewModel.setFavRestaurantStatus.collect(EventObserver(
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
                    categoryProductViewModel.productStatus.collect(EventObserver(
                        onLoading = {
                            binding.spinKitSmallProduct.isVisible = true
                        },
                        onSuccess = {
                            binding.spinKitSmallProduct.isVisible = false

                            it.data?.let {
                                productAdapter.products = it
                            }
                        },
                        onError = {
                            binding.spinKitSmallProduct.isVisible = false
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsRestaurantBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViewProduct() = binding.productRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = productAdapter

    }

    override fun onResume() {
        super.onResume()
        categoryProductViewModel.getCategoriesOfRestaurant(currentRestaurant!!.restaurantId!!)
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
            startCallIntent(requireContext(), currentRestaurant!!.contact)
        } catch (e: Exception) {
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

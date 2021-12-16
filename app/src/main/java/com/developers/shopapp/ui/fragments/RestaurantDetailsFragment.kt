package com.developers.shopapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentDetailsRestaurantBinding
import com.developers.shopapp.entities.Category
import com.developers.shopapp.entities.RateRestaurant
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.ProductAdapter
import com.developers.shopapp.ui.dialog.RateDialogListener
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.*
import com.developers.shopapp.utils.Constants.CURRENT_PRODUCT
import com.developers.shopapp.utils.Constants.CURRENT_RESTAURANT
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.Utils.getTimeAgo
import com.developers.shopapp.utils.Utils.getTimeStamp
import com.developers.shopapp.utils.Utils.startCallIntent
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class RestaurantDetailsFragment : Fragment(), EasyPermissions.PermissionCallbacks, RateDialogListener {
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



    private lateinit var currentRestaurant :Restaurant


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentRestaurant = requireArguments().getParcelable(CURRENT_RESTAURANT)!!

        val dataUserInfo = dataStoreManager.glucoseFlow.value


        //onScrollViewAndRecyclerView()

        currentRestaurant?.let { restaurant ->
           bindRestaurantData(restaurant)
            restaurantViewModel.findMyRestaurant(restaurant.restaurantId!!)
        }

        quickActions(currentRestaurant!!)

        subscribeToObservers(dataUserInfo)

        setupRecyclerViewProduct()

        adapterActions()


    }

    private fun bindRestaurantData(restaurant: Restaurant) {
        Log.d(TAG, "bindRestaurantData: ${restaurant}")

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

          val action=RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToFoodDetailsFragment(it)
            navController.navigate(action)
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
        binding.restaurantTakeAway.setOnClickListener {
            val action =
                RestaurantDetailsFragmentDirections.actionRestaurantDetailsFragmentToMapsFragment(
                    currentRestaurant!!
                )
            navController.navigate(action)
        }
        binding.restaurantCall.setOnClickListener {
            requestPermissions()
        }
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.rate.setOnClickListener {
            Log.i(TAG, "quickActions: ${currentRestaurant.rateRestaurant}")
          val rateRestaurant:RateRestaurant?= currentRestaurant!!.rateRestaurant?.firstOrNull {
              it.userId == currentRestaurant!!.user.id
          }

            showRatingDialog(requireContext(),
                rateRestaurant?.rateId,
                false,
                rateRestaurant?.countRate?.toFloat()?:3f,
                this,
                currentRestaurant!!.restaurantName,
                null,childFragmentManager)

        }

        binding.viewImageRestaurant.setOnClickListener {
            val images = arrayOf(currentRestaurant?.imageRestaurant!!)
            val action =
                RestaurantDetailsFragmentDirections.globalActionToImageViewFragment(images = images)
            findNavController().navigate(action)
        }

    }

    private fun setupActionTabsCategory(categories: List<Category>) {
        binding.tabs.removeAllTabs()
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


    @OptIn(InternalCoroutinesApi::class)
    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                // category
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
                //rating
                launch {
                    restaurantViewModel.myRatingRestaurantStatus.collect(
                        EventObserver(
                            onLoading = {
                                Log.w(TAG, "EventObserver: ${"onLoading"}" )
                            },
                            onSuccess = { item ->
                                  snackbar(item.message)
                                item.data?.let {
                                 // updateRating(it)
                                    restaurantViewModel.findMyRestaurant(currentRestaurant!!.restaurantId!!)
                                }

                            },
                            onError = {
                                snackbar(it)
                            }
                        )
                    )
                }

                // delete fa
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

                // set fav
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

                // load product
                launch {
                    categoryProductViewModel.productStatus.collect(EventObserver(
                        onLoading = {
                            setupViewBeforeLoadData(
                                spinKit = binding.spinKitSmallProduct,
                                shimmerFrameLayout = binding.shimmer, onLoading = true
                            )

                        },
                        onSuccess = {
                            setupViewBeforeLoadData(
                                spinKit = binding.spinKitSmallProduct,
                                shimmerFrameLayout = binding.shimmer,
                                onLoading = false,
                                emptyView = binding.emptyView
                            )
                            binding.spinKitSmallProduct.isVisible = false

                            it.data?.let {
                                if (it.isEmpty()) setupViewBeforeLoadData(
                                    onLoading = false,
                                    onError = true,
                                    emptyView = binding.emptyView,
                                    tvError = binding.textEmpty,
                                    errorMessage = "No Data Found"
                                )
                                productAdapter.products = it
                                binding.countItem.text="${it.size}+ ${binding.tabs.getTabAt(binding.tabs.selectedTabPosition)?.text}"
                            }
                        },
                        onError = {
                            setupViewBeforeLoadData(
                                spinKit = binding.spinKit,
                                shimmerFrameLayout = binding.shimmer,
                                onLoading = false,
                                onError = true,
                                errorMessage = it,
                                emptyView = binding.emptyView,
                                tvError = binding.textEmptyErr
                            )
                            snackbar(it)

                        }
                    )
                    )
                }

                // delete fav product
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

                // set fav product
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

                // find restaurant
                launch {
                    restaurantViewModel.findRestaurantStatus.collect(EventObserver(
                        onLoading = {},
                        onSuccess = {
                          it.data?.let {
                              currentRestaurant=it
                              Log.d(TAG, "subscribeToObservers: ${currentRestaurant.rateRestaurant.toString()}")
                              bindRestaurantData(it)
                          }
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
        Log.i(TAG, "onResume: ")
        categoryProductViewModel.getCategoriesOfRestaurant(currentRestaurant!!.restaurantId!!)
        categoryProductViewModel.findProductById(currentRestaurant!!.restaurantId!!)
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

    override fun onSubMitClick(rateCount: Float, feedbackMessage: String,rateId:Int?) {
        Log.i(TAG, "onSubMitClick: $rateCount  , $feedbackMessage , $rateId")
        if (rateId==null) {
            val rateRestaurant = RateRestaurant(
                restaurantId = currentRestaurant!!.restaurantId!!,
                countRate = rateCount.toDouble(), createAt = getTimeStamp()
            )
            restaurantViewModel.setupRatingMyRestaurant(rateRestaurant)
        }else{
            val rateRestaurant = RateRestaurant(
                rateId=rateId,
                restaurantId = currentRestaurant!!.restaurantId!!,
                countRate = rateCount.toDouble(), createAt = getTimeStamp()
            )
            restaurantViewModel.updateRateRestaurant(rateRestaurant)
        }

    }
}

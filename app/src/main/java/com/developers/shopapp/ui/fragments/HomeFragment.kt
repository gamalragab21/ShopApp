package com.developers.shopapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentHomeBinding
import com.developers.shopapp.entities.*
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.PopularFoodAdapter
import com.developers.shopapp.ui.adapters.ViewPagerFragmentTabsAdapter
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.ui.viewmodels.OrdersViewModel
import com.developers.shopapp.utils.*
import com.developers.shopapp.utils.Constants.TAG
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@InternalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val categoryProductViewModel: CategoryProductViewModel by viewModels()

    val ordersViewModel: OrdersViewModel by viewModels()

    @Inject
    lateinit var popularFoodAdapter: PopularFoodAdapter


    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    private val navController by lazy {
        findNavController()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    

        val myHomeLocation = dataStoreManager.glucoseFlow.value


        setupRecyclerViewPopularProducts()

        subscribeToObservers()

        adapterActions()


        setTabs()

        binding.seeMorePopular.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToPopularFragmentProduct()
            navController.navigate(action)
        }


        binding.inputEditTextSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToSearchRestaurantFragment()
            navController.navigate(action)
        }
        hideBottomSheetOrShowWhenScroll(
            scrollView = binding.scrollView,
            activity = requireActivity()
        )



        setupActionToolBar()

    }

    private fun setupActionToolBar() {
        binding.breakFast.setOnClickListener {
            startToFilterFragment("Breakfast")
        }
        binding.burger.setOnClickListener {
            startToFilterFragment("Burger")
        }
        binding.pizza.setOnClickListener {
            startToFilterFragment("Pizza")
        }
        binding.cafe.setOnClickListener {
            startToFilterFragment("Coffee")
        }
        binding.drinks.setOnClickListener {
            startToFilterFragment("Drinks")
        }
    }

  private fun startToFilterFragment(filterName: String) {
        val action=HomeFragmentDirections.actionNavigationHomeToFilterFragment(filterName)
        navController.navigate(action)
    }

    private fun setupRecyclerViewPopularProducts() = binding.homeFragmentRecyclerPopular.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        adapter = popularFoodAdapter

    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            launch {
            categoryProductViewModel.popularProductStatus.collect(
                EventObserver(
                    onLoading = {
                        setupViewBeforeLoadData(

                            shimmerFrameLayout = binding.shimmer, onLoading = true
                        )
                    },
                    onSuccess = { poplarRestaurant ->
                        setupViewBeforeLoadData(

                            shimmerFrameLayout = binding.shimmer, onLoading = false
                        )
                        poplarRestaurant.data?.let {
                            if (it.isEmpty()) setupViewBeforeLoadData(
                                onLoading = false, onError = true, emptyView = binding.leanerFav
                            )
                            binding.leanerFav.isVisible = it.isNotEmpty()
                            binding.seeMorePopular.isVisible = it.size > 10
                            popularFoodAdapter.products = it
                        }
                    },
                    onError = {
                        Log.e(TAG, "subscribeToObservers: ${it} ")
                        setupViewBeforeLoadData(

                            shimmerFrameLayout = binding.shimmer, onLoading = false,
                            onError = true, errorMessage = it
                        )
                        snackbar(it)
                    }
                )
            )

        }
            // add to cart
            launch {
                ordersViewModel.addProductToCartStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = { success ->

                            if (success>0)
                                snackbar("\uD83D\uDE0D Success Add To Cart")
                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )
            }
        }

    }

    private fun adapterActions() {


        popularFoodAdapter.setOnItemClickListener {

//            val bundle = bundleOf(Constants.CURRENT_PRODUCT to it)
//            findNavController().navigate(R.id.foodDetailsFragment,bundle)
            val action = HomeFragmentDirections.actionNavigationHomeToFoodDetailsFragment(it)
            navController.navigate(action)
        }

        popularFoodAdapter.setOnItemAddCartClickListener {currentFood->

            val productCart= ProductCart(
                currentFood.productName,
                currentFood.images[0].imageProduct,
                currentFood.productPrice,
                0.0,
                currentFood.coinType,
               1,
                currentFood.freeDelivery,
                currentFood.user!!.mobile,
                currentFood.user!!.id!!,currentFood.restaurantId!!,
                Utils.getTimeStamp()
            )
            ordersViewModel.addProductToCart(productCart)
        }


    }


    private fun setTabs() {

        binding.tabs.addTab(binding.tabs.newTab().setText("Recent"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Favourite"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Rating"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Popular"))
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

        val adapter = ViewPagerFragmentTabsAdapter(childFragmentManager, lifecycle)
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
        categoryProductViewModel.getPopularRestaurant()
    }


}
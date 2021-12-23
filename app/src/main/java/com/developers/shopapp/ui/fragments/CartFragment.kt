package com.developers.shopapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.databinding.FragmentCartBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.ProductImage
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.CartAdapter
import com.developers.shopapp.ui.viewmodels.OrdersViewModel
import com.developers.shopapp.utils.hideBottomSheetOrShowWhenScroll
import com.developers.shopapp.utils.setupViewBeforeLoadData
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val ordersViewModels: OrdersViewModel by viewModels()

    @Inject
    lateinit var cartAdapter: CartAdapter
    private val navController by lazy {
        findNavController()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerViewCarts()

        subscribeToObservers()

        adapterActions()

        hideBottomSheetOrShowWhenScroll(
            recyclerView = binding.cartsRecyclerview,
            activity = requireActivity()
        )



    }

    private fun adapterActions() {
        cartAdapter.setOnItemClickListener { cartProduct->
            val action=CartFragmentDirections.actionNavigationCartToCheckOutBottomSheetDialog(cartProduct)
            navController.navigate(action)
        }


        cartAdapter.setOnImageClickListener { cartProduct->
            val productFack=Product(
                cartProduct.foodId,
                0,0,cartProduct.foodName,
                cartProduct.foodPrice,0,true,"",
                inFav = false,
                inCart = true,
                rateCount = null,
                coinType = cartProduct.coinType,
                images = listOf<ProductImage>(),
                rating = null,
                user = null
            )
            val action=CartFragmentDirections.actionNavigationCartToFoodDetailsFragment(productFack)
            navController.navigate(action)
        }



        cartAdapter.setOnCounterClickListener { itemProduct, s , position ->

            ordersViewModels.updateItemQuality(itemProduct)

        }

    }


    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            launch {
                ordersViewModels.cartsStatus.collect(
                    EventObserver(
                        onLoading = {
                            setupViewBeforeLoadData(
                                shimmerFrameLayout = binding.shimmer, onLoading = true
                            )
                        },
                        onSuccess = { carts ->

                            setupViewBeforeLoadData(
                                shimmerFrameLayout = binding.shimmer,
                                onLoading = false,
                                emptyView = binding.emptyView
                            )

                            if (carts.isEmpty()) setupViewBeforeLoadData(
                                onLoading = false,
                                onError = true,
                                emptyView = binding.emptyView,
                                tvError = binding.textEmpty,
                                errorMessage = "No Data Found"
                            )
                            cartAdapter.carts = carts

                        },
                        onError = {
                            snackbar(it)
                            setupViewBeforeLoadData(
                                shimmerFrameLayout = binding.shimmer,
                                onLoading = false,
                                onError = true,
                                errorMessage = it,
                                emptyView = binding.emptyView,
                                tvError = binding.textEmptyErr
                            )

                        }
                    )
                )
            }
            launch {
                ordersViewModels.updateCartStatus.collect(
                    EventObserver(
                        onLoading = {

                        },
                        onSuccess = { carts ->
                            ordersViewModels.getAllCarts(false)
                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )
            }
        }

    }


    private fun setupRecyclerViewCarts() = binding.cartsRecyclerview.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = cartAdapter


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        ordersViewModels.getAllCarts(true)
    }


}
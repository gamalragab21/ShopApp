package com.developers.shopapp.ui.fragments.myOrders

import android.os.Build
import android.os.Bundle
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
import com.developers.shopapp.databinding.FragmentHistoryBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.OrderAdapter
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
class HistoryOrdersFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!


    private val navController by lazy {
        findNavController()
    }
    private val ordersViewModels: OrdersViewModel by viewModels()

    @Inject
    lateinit var orderAdapter: OrderAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViewRecent()
        orderAdapter.showReorder = 0


        subscribeToObservers()

        adapterActions()
        hideBottomSheetOrShowWhenScroll(
            recyclerView = binding.historyOrderFragmentRecycler,
            activity = requireActivity()
        )


    }


    private fun adapterActions() {
        orderAdapter.setOnReOrderClickListener { order, position ->
            ordersViewModels.deleteOrder(order.orderId!!)
            binding.emptyView.isVisible = orderAdapter.clearItemAndIfLast(order, position)
        }
        orderAdapter.setOnItemClickListener {
            val productFack = Product(
                it.foodId,
                0, 0, it.productName,
                it.productPrice, 0, true, "",
                inFav = false,
                inCart = true,
                rateCount = null,
                coinType = it.coinType,
                images = listOf(),
                rating = null,
                user = null
            )
            val action =
                OnComingOrderFragmentDirections.globalActionOrdersFragmentsToFoodDetailsFragment(
                    productFack
                )
            navController.navigate(action)
        }
    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            launch {
                ordersViewModels.historyOrderStatus.collect(EventObserver(
                    onSuccess = { orders ->
                        setupViewBeforeLoadData(
                            shimmerFrameLayout = binding.shimmer,
                            onLoading = false,
                            emptyView = binding.emptyView
                        )
                        orders.data?.let {
                            if (it.isEmpty()) setupViewBeforeLoadData(
                                onLoading = false,
                                onError = true,
                                emptyView = binding.emptyView,
                                tvError = binding.textEmpty,
                                errorMessage = "No Data Found"
                            )
                            orderAdapter.orders = it
                        }
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
                    },
                    onLoading = {
                        setupViewBeforeLoadData(
                            shimmerFrameLayout = binding.shimmer, onLoading = true
                        )
                    }
                ))
            }
        }

    }

    private fun setupRecyclerViewRecent() = binding.historyOrderFragmentRecycler.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = orderAdapter

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        ordersViewModels.historyOrders()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
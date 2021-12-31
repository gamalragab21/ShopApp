package com.developers.shopapp.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentProfileBinding
import com.developers.shopapp.databinding.FragmentTrackingBinding
import com.developers.shopapp.entities.Order
import com.developers.shopapp.entities.Tracking
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.helpers.TAG
import com.developers.shopapp.ui.viewmodels.OrdersViewModel
import com.developers.shopapp.utils.Utils.getDateAndTimeForTracking
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class OrderTrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var glide: RequestManager

    private val args: OrderTrackingFragmentArgs by navArgs()
    private val ordersViewModel: OrdersViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderId = args.orderId
        binding.orderId.text = orderId.toString()
        subscribeToObservables()
        binding.swipeRefreshLayout.setOnRefreshListener {
            ordersViewModel.getOrderTracking(orderId)
        }
    }

    private fun subscribeToObservables() {

        lifecycleScope.launchWhenStarted {
            launch {
                ordersViewModel.orderTrackingStatus.collect(EventObserver(
                    onError = {
                        snackbar(it)
                        binding.spinKit.isVisible = false
                        binding.swipeRefreshLayout.isRefreshing = false
                    },
                    onLoading = {
                        binding.spinKit.isVisible = true
                        binding.swipeRefreshLayout.isRefreshing = false

                    },
                    onSuccess = { tracking ->
                        binding.spinKit.isVisible = false
                        binding.swipeRefreshLayout.isRefreshing = false

                        tracking.data?.let {
                            Log.i(TAG, "subscribeToObservables: ${it.toString()}")
                            if (it.isNotEmpty()) {
                                bindDataOrder(it[0].order!!)
                                updateOrderTracking(it)
                            }
                        }
                    }
                ))
            }
        }

    }

    private fun updateOrderTracking(trackingList: List<Tracking>) {
        when (trackingList.size) {
            1 -> {
                val item1 = trackingList[0]
                binding.viewPointDelivery.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.dateDelivery.text =
                    getDateAndTimeForTracking(requireContext(), item1.createAt)
            }
            2 -> {
                val item1 = trackingList[0]
                val item2 = trackingList[1]

                binding.viewPointDelivery.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointOnWay.setBackgroundResource(R.drawable.status_order_tracking_active)

                binding.dateDelivery.text =
                    getDateAndTimeForTracking(requireContext(), item1.createAt)
                binding.onWayDate.text = getDateAndTimeForTracking(requireContext(), item2.createAt)


            }
            3 -> {
                val item1 = trackingList[0]
                val item2 = trackingList[1]
                val item3 = trackingList[2]

                binding.viewPointDelivery.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointOnWay.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointProcessing.setBackgroundResource(R.drawable.status_order_tracking_active)

                binding.dateDelivery.text =
                    getDateAndTimeForTracking(requireContext(), item1.createAt)
                binding.onWayDate.text = getDateAndTimeForTracking(requireContext(), item2.createAt)
                binding.processingDate.text =
                    getDateAndTimeForTracking(requireContext(), item3.createAt)
            }
            4 -> {
                val item1 = trackingList[0]
                val item2 = trackingList[1]
                val item3 = trackingList[2]
                val item4 = trackingList[3]

                binding.viewPointDelivery.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointOnWay.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointProcessing.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointConfirm.setBackgroundResource(R.drawable.status_order_tracking_active)

                binding.dateDelivery.text =
                    getDateAndTimeForTracking(requireContext(), item1.createAt)
                binding.onWayDate.text = getDateAndTimeForTracking(requireContext(), item2.createAt)
                binding.processingDate.text =
                    getDateAndTimeForTracking(requireContext(), item3.createAt)
                binding.confirmDate.text =
                    getDateAndTimeForTracking(requireContext(), item4.createAt)
            }
            5 -> {
                val item1 = trackingList[0]
                val item2 = trackingList[1]
                val item3 = trackingList[2]
                val item4 = trackingList[3]
                val item5 = trackingList[5]

                binding.viewPointDelivery.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointOnWay.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointProcessing.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointConfirm.setBackgroundResource(R.drawable.status_order_tracking_active)
                binding.viewPointPlaced.setBackgroundResource(R.drawable.status_order_tracking_active)

                binding.dateDelivery.text =
                    getDateAndTimeForTracking(requireContext(), item1.createAt)
                binding.onWayDate.text = getDateAndTimeForTracking(requireContext(), item2.createAt)
                binding.processingDate.text =
                    getDateAndTimeForTracking(requireContext(), item3.createAt)
                binding.confirmDate.text =
                    getDateAndTimeForTracking(requireContext(), item4.createAt)
                binding.placedDate.text =
                    getDateAndTimeForTracking(requireContext(), item5.createAt)
            }
        }

    }

    private fun bindDataOrder(order: Order) {
        Log.i(TAG, "bindDataOrder: ${order.toString()}")
        binding.restaurantName.text = order.restaurant?.restaurantName
        glide.load(order.foodImage).into(binding.itemImageOrder)
        binding.foodOrderName.text = order.productName
        binding.priceItem.text = "${order.coinType}${order.productPrice}"
        binding.itemOrderQuantity.text = "${order.productQuantity}"

        binding.totalPriceItem.text = "${order.coinType}${order.productPrice}"
        binding.discountPriceItem.text = "${order.coinType}${order.productDistCount}"
        binding.delveryType.text = if (order.freeDelivery) "Free" else "Not Free"
        binding.totalPrice.text =
            "${order.coinType}${(order.productPrice - order.productDistCount) * order.productQuantity}"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        ordersViewModel.getOrderTracking(args.orderId)
    }


}



package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.CheckOutOrderBinding
import com.developers.shopapp.entities.Order
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.helpers.MyLocation
import com.developers.shopapp.helpers.TAG
import com.developers.shopapp.ui.viewmodels.OrdersViewModel
import com.developers.shopapp.utils.Constants.HISTORY_ORDER
import com.developers.shopapp.utils.Utils.getTimeStamp
import com.developers.shopapp.utils.snackbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@InternalCoroutinesApi
@AndroidEntryPoint
class CheckOutBottomSheetDialog : BottomSheetDialogFragment() {


    val args: CheckOutBottomSheetDialogArgs by navArgs()

    private var _binding: CheckOutOrderBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val ordersViewModel: OrdersViewModel by viewModels()

    val myLocation by lazy {
        MyLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myHomeLocation = dataStoreManager.glucoseFlow.value

        val itemProduct = args.productItem
        binding.totalPriceItem.text = "${itemProduct.coinType}${itemProduct.foodPrice}"
        binding.delveryType.text = if (itemProduct.foodDelivery) "Free" else "Not Free"
        binding.totalPrice.text =
            "${itemProduct.coinType}${(itemProduct.foodPrice - itemProduct.foodDiscount) * itemProduct.foodQuality}"
        myHomeLocation?.let {
            binding.userLocation.text = myLocation.getAddressFromLatLng(
                requireContext(),
                it.latLng.latitude,
                it.latLng.longitude
            )

        }

        binding.countPrice.text =
            "${itemProduct.coinType}${(itemProduct.foodPrice - itemProduct.foodDiscount) * itemProduct.foodQuality}"

        setupAction()

        subscribeToObservers()

    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            launch {
                ordersViewModel.createOrderStatus.collect(
                    EventObserver(
                        onError = {
                            snackbar(it)
                            binding.spinKit.isVisible=false
                        },
                        onLoading = {
                            binding.spinKit.isVisible=true

                        },
                        onSuccess = {
                            binding.spinKit.isVisible=false

                            if (it.success){
                                ordersViewModel.deleteOrderCart(args.productItem.foodId!!)
                               showMySuccessOrderDialog()
                            }else{
                                snackbar(it.message)
                            }
                        }
                    )
                )
            }
        }
    }

    private fun showMySuccessOrderDialog() {
        val action=CheckOutBottomSheetDialogDirections.actionCheckOutBottomSheetDialogToSuccessDialogFragment()
        findNavController().navigate(action)
    }

    private fun setupAction() {

        binding.checkOutLiner.setOnClickListener {
            Log.i(TAG, "setupAction: ${args.productItem.foodQuality}")
            val order = Order(
                restaurantId = args.productItem.restaurantId,
                userId = args.productItem.userId,
                foodId = args.productItem.foodId!!,
                productName = args.productItem.foodName,
                productPrice = args.productItem.foodPrice,
                productDistCount = args.productItem.foodDiscount,
                productQuantity=args.productItem.foodQuality,
                freeDelivery = args.productItem.foodDelivery,
                createAt = getTimeStamp(),
                coinType = args.productItem.coinType,
                foodImage = args.productItem.foodImage,
                orderType = HISTORY_ORDER

            )
            ordersViewModel.createOrder(order)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CheckOutOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

}
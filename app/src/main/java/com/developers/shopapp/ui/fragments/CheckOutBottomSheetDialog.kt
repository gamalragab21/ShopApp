package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.CheckOutOrderBinding
import com.developers.shopapp.databinding.FragmentCartBinding
import com.developers.shopapp.helpers.MyLocation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CheckOutBottomSheetDialog : BottomSheetDialogFragment() {


    val args: CheckOutBottomSheetDialogArgs by navArgs()

    private var _binding: CheckOutOrderBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager

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
            "${itemProduct.coinType}${(itemProduct.foodPrice - itemProduct.foodDiscount)*itemProduct.foodQuality}"
        myHomeLocation?.let {
            binding.userLocation.text = myLocation.getAddressFromLatLng(
                requireContext(),
                it.latLng.latitude,
                it.latLng.longitude
            )

        }

        binding.countPrice.text =
            "${itemProduct.coinType}${(itemProduct.foodPrice - itemProduct.foodDiscount)*itemProduct.foodQuality}"

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
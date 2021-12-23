package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.databinding.FragmentReviewsBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.RateProduct
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.ReviewAdapter
import com.developers.shopapp.ui.dialog.RateDialogListener
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Utils
import com.developers.shopapp.utils.showRatingDialog
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class ReviewsFragment:Fragment(), RateDialogListener {
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!


   private val args:ReviewsFragmentArgs by navArgs()
    private lateinit var currentProduct:Product

    val categoryProductViewModel :CategoryProductViewModel by viewModels()
    @Inject
    lateinit var reviewAdapter: ReviewAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

          currentProduct=args.product

        setupRecyclerView()

        subscribeToObservers()


       currentProduct.also {
           bindReviewData(it)
        }

        binding.addRating.setOnClickListener {
            val rateProduct: RateProduct?= currentProduct.rating?.firstOrNull {
                it.userId == currentProduct.user!!.id
            }
            showRatingDialog(requireContext(),
                rateProduct?.rateId,
                true,
                rateProduct?.countRate?.toFloat()?:3f,
                this,
                currentProduct.productName,
                rateProduct?.messageRate,childFragmentManager)
        }
    }

    private fun bindReviewData(it: Product) {
        it.rating?.let {
            reviewAdapter.rateProducts=it
        }
        binding.ratingCount.rating=it.rateCount?.toFloat()?:0f
        binding.tvCountRating.text="${it.rateCount?.toFloat()?:0f}"
        binding.tvCountReview.text="${it.rating?.size?:0f} reviews"
    }

    private fun setupRecyclerView() = binding.reviewRecyclerview.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = reviewAdapter

    }

    private fun subscribeToObservers() {

        lifecycleScope.launchWhenCreated {
            launch {
                categoryProductViewModel.myRatingProductStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            snackbar(it.message)
                            categoryProductViewModel.findProductById(it.data!!.productId)
                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )
            }

            launch {
                categoryProductViewModel.findProductStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = { category ->

                            category.data?.let {
                                currentProduct=it
                                bindReviewData(it)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentReviewsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onSubMitClick(rateCount: Float, feedbackMessage: String, rateId: Int?) {
        Log.i(Constants.TAG, "onSubMitClick: $rateCount  , $feedbackMessage , $rateId")
        if (rateId==null) {
            Log.i(Constants.TAG, "onSubMitClick: Insert ")

            val rateProduct = RateProduct(
                productId = args.product.productId!!,
                countRate = rateCount.toDouble(), createAt = Utils.getTimeStamp(),messageRate=feedbackMessage
            )
            categoryProductViewModel.setupRatingMyProduct(rateProduct)
        }else{
            Log.i(Constants.TAG, "onSubMitClick: Update ")
            val rateProduct = RateProduct(
                rateId=rateId,
                productId = args.product.productId!!,
                countRate = rateCount.toDouble(), createAt = Utils.getTimeStamp(),messageRate=feedbackMessage
            )
            categoryProductViewModel.updateRateProduct(rateProduct)
        }
    }


}
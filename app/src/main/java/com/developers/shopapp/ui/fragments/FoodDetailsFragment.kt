package com.developers.shopapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentDetailsFoodBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.ProductImage
import com.developers.shopapp.entities.RateProduct
import com.developers.shopapp.entities.RateRestaurant
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.ImageSliderAdapter
import com.developers.shopapp.ui.dialog.RateDialogListener
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.Utils
import com.developers.shopapp.utils.Utils.getTimeAgo
import com.developers.shopapp.utils.showRatingDialog
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class FoodDetailsFragment : Fragment(), RateDialogListener {

    private var _binding: FragmentDetailsFoodBinding? = null
    private val binding get() = _binding!!


    private val args: FoodDetailsFragmentArgs by navArgs()

    private lateinit var currentFood: Product


    private val categoryProductViewModel: CategoryProductViewModel by viewModels()


    @Inject
    lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: ")
        currentFood = args.product

        loadingImageSlider(currentFood.images)

        bindFoodData(currentFood)


        quickAction(currentFood)

        subscribeToObservers()

    }


    private fun quickAction(currentFood: Product) {
        binding.btnMark.setOnClickListener {
            if (currentFood.inFav!!) {
                categoryProductViewModel.deleteFavProduct(currentFood)
                binding.btnMark.setImageResource(R.drawable.not_save)
                currentFood.inFav = false
            } else {
                categoryProductViewModel.setFavProduct(currentFood)
                currentFood.inFav = true
                binding.btnMark.setImageResource(R.drawable.saved)
            }
        }

        binding.textReadMore.setOnClickListener {
            if (binding.textReadMore.text.toString() == getString(R.string.read_more)) {
                binding.textDescripition.maxLines = Int.MAX_VALUE
                binding.textDescripition.ellipsize = null
                binding.textReadMore.text = getString(R.string.read_less)
            } else {
                binding.textDescripition.maxLines = 4
                binding.textDescripition.ellipsize = TextUtils.TruncateAt.END
                binding.textReadMore.text = getString(R.string.read_more)
            }
        }

        binding.detailsProduct.setOnClickListener {

            binding.detailsProduct.setTextColor(Color.BLACK)
            binding.reviewProduct.setTextColor(Color.GRAY)
            binding.pointDetails.isVisible = true
            binding.pointReview.isVisible = false
        }

        binding.reviewProduct.setOnClickListener {
            binding.reviewProduct.setTextColor(Color.BLACK)
            binding.detailsProduct.setTextColor(Color.GRAY)
            binding.pointDetails.isVisible = false
            binding.pointReview.isVisible = true

            val action =
                FoodDetailsFragmentDirections.actionFoodDetailsFragmentToReviewsFragment(currentFood)

            findNavController().navigate(action)
        }

        binding.rate.setOnClickListener {
            Log.i(TAG, "quickAction: ${currentFood.rating.toString()}")

            val rateProduct: RateProduct? = currentFood.rating?.firstOrNull {
                it.userId == currentFood.user.id
            }
            Log.i(TAG, "rateProduct: ${rateProduct.toString()}")

            showRatingDialog(
                requireContext(),
                rateProduct?.rateId,
                true,
                rateProduct?.countRate?.toFloat() ?: 0f,
                this,
                currentFood.productName,
                rateProduct?.messageRate, childFragmentManager
            )
        }
        binding.search.setOnClickListener {
            Log.i(TAG, "currentFood is: ${currentFood.rating.toString()}")

        }
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {

                // product
                launch {
                    categoryProductViewModel.findProductStatus.collect(
                        EventObserver(
                            onLoading = {
                            },
                            onSuccess = { category ->

                                category.data?.let {


                                    Log.i(
                                        TAG,
                                        "findProductStatus: ${currentFood.rating.toString()}"
                                    )
                                    bindFoodData(it)
                                }
                            },
                            onError = {
                                snackbar(it)
                            }
                        )
                    )
                }
                launch {
                    categoryProductViewModel.deleteFavProductStatus.collect(
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
                    categoryProductViewModel.setFavProductStatus.collect(
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

            }
        

    }

    private fun bindFoodData(it: Product) {
        currentFood = it
        Log.i(TAG, "bindFoodData: ${currentFood.rating.toString()}")
        binding.foodName.text = it.productName
        binding.rating.text = "${it.rateCount}"
        binding.foodPrice.text = "${it.productPrice}"
        binding.foodTime.text = "${getTimeAgo(it.createAt, requireContext())}"

        binding.textDescripition.text = it.productDescription

        if (it.freeDelivery) {
            binding.fooddeleivery.text = "Free Delivery"
        } else {
            binding.fooddeleivery.text = "Not Free Delivery"
        }
        if (it.inFav == true) {
            binding.btnMark.setImageResource(R.drawable.saved)
        } else {
            binding.btnMark.setImageResource(R.drawable.not_save)
        }
    }

    private fun loadingImageSlider(pictures: List<ProductImage>) {
        imageSliderAdapter.images = pictures
        binding.sliderViewPager.offscreenPageLimit = 1
        binding.sliderViewPager.adapter = imageSliderAdapter
        binding.sliderViewPager.isVisible = true
        binding.viewFadingEdge.isVisible = true
        setUpSliderUpIndicators(pictures.size)
        binding.sliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentSliderIndicators(position)
            }
        })
    }

    private fun setUpSliderUpIndicators(count: Int) {
        val indictors = arrayOfNulls<ImageView>(count)
        val layoutParms = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParms.setMargins(8, 0, 8, 0)
        for (i in indictors.indices) {
            indictors[i] = ImageView(requireContext())
            indictors[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.background_slider_indicator_inactive
                )
            )
            indictors[i]?.layoutParams = layoutParms
            binding.layoutSliderIndicators.addView(indictors[i])
        }
        binding.layoutSliderIndicators.isVisible = true
        setCurrentSliderIndicators(0)
    }

    private fun setCurrentSliderIndicators(position: Int) {
        val childCount = binding.layoutSliderIndicators.childCount
        var i = 0;
        while (i < childCount) {
            val imageView = binding.layoutSliderIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.background_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.background_slider_indicator_inactive
                    )
                )
            }
            i++
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsFoodBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //
    override fun onResume() {
        super.onResume()
        categoryProductViewModel.findProductById(currentFood?.productId!!)
    }

    override fun onSubMitClick(rateCount: Float, feedbackMessage: String, rateId: Int?) {
        Log.i(TAG, "onSubMitClick: $rateCount  , $feedbackMessage , $rateId")
        if (rateId == null) {
            Log.i(TAG, "onSubMitClick: Insert ")

            val rateProduct = RateProduct(
                productId = currentFood!!.productId!!,
                countRate = rateCount.toDouble(),
                createAt = Utils.getTimeStamp(),
                messageRate = feedbackMessage
            )
            categoryProductViewModel.setupRatingMyProduct(rateProduct)
        } else {
            Log.i(TAG, "onSubMitClick: Update ")
            val rateProduct = RateProduct(
                rateId = rateId,
                productId = currentFood!!.productId!!,
                countRate = rateCount.toDouble(),
                createAt = Utils.getTimeStamp(),
                messageRate = feedbackMessage
            )
            categoryProductViewModel.updateRateProduct(rateProduct)
        }
    }
}

package com.developers.shopapp.ui.fragments

import android.os.Bundle
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
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentDetailsFoodBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.ProductImage
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.ImageSliderAdapter
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Utils.getTimeAgo
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class FoodDetailsFragment :Fragment(){

    private var _binding: FragmentDetailsFoodBinding? = null
    private val binding get() = _binding!!
    val currentFood by lazy {
        arguments?.getParcelable<Product>(Constants.CURRENT_PRODUCT)
    }

    private val categoryProductViewModel: CategoryProductViewModel by viewModels()
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    val navController by lazy { findNavController() }



    @Inject
    lateinit var imageSliderAdapter : ImageSliderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentFood?.let {
            loadingImageSlider(it.images)
            bindFoodData(it)
        }

        quickAction(currentFood!!)

        subscribeToObservers()
    }

    private fun quickAction(currentFood: Product) {
        binding.imageSave.setOnClickListener {
            if (currentFood.inFav!!) {
                categoryProductViewModel.deleteFavProduct(currentFood)
                binding.imageSave.setImageResource(R.drawable.not_save)
                currentFood.inFav = false
            } else {
                categoryProductViewModel.setFavProduct(currentFood)
                currentFood.inFav = true
                binding.imageSave.setImageResource(R.drawable.saved)
            }
        }
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                // product
                launch {
                    categoryProductViewModel.findProductStatus.collect(
                        EventObserver(
                            onLoading = {
                            },
                            onSuccess = { category ->

                                category.data?.let {
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

            }
        }

    }

    private fun bindFoodData(it: Product) {

        binding.foodName.text=it.productName
        binding.rating.text="${it.rateCount}"
        binding.foodPrice.text="${it.productPrice}"
        binding.foodTime.text="${getTimeAgo(it.createAt,requireContext())}"

        if (it.freeDelivery){
            binding.fooddeleivery.text="Free Delivery"
        }else{
            binding.fooddeleivery.text="Not Free Delivery"
        }
        if (it.inFav==true){
            binding.imageSave.setImageResource(R.drawable.saved)
        }else{
            binding.imageSave.setImageResource(R.drawable.not_save)
        }
    }

    private fun loadingImageSlider(pictures: List<ProductImage>) {
        imageSliderAdapter.images = pictures
        binding.sliderViewPager.offscreenPageLimit = 1
        binding.sliderViewPager.adapter = imageSliderAdapter
        binding.sliderViewPager.isVisible = true
        binding.viewFadingEdge.isVisible = true
        setUpSliderUpIndicators(pictures.size)
        binding.sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position : Int) {
                super.onPageSelected(position)
                setCurrentSliderIndicators(position)
            }
        })
    }
    private fun setUpSliderUpIndicators(count : Int) {
        val indictors = arrayOfNulls<ImageView>(count)
        val layoutParms = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParms.setMargins(8 , 0 , 8 , 0)
        for (i in indictors.indices) {
            indictors[i] = ImageView(requireContext())
            indictors[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext() , R.drawable.background_slider_indicator_inactive
                )
            )
            indictors[i]?.layoutParams = layoutParms
            binding.layoutSliderIndicators.addView(indictors[i])
        }
        binding.layoutSliderIndicators.isVisible = true
        setCurrentSliderIndicators(0)
    }

    private fun setCurrentSliderIndicators(position : Int) {
        val childCount =  binding.layoutSliderIndicators.childCount
        var i = 0;
        while (i < childCount) {
            val imageView =  binding.layoutSliderIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext() , R.drawable.background_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext() , R.drawable.background_slider_indicator_inactive
                    )
                )
            }
            i ++
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentDetailsFoodBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onResume() {
        super.onResume()
        categoryProductViewModel.findProductById(currentFood?.productId!!)
    }
}
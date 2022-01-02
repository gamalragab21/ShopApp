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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentCartBinding
import com.developers.shopapp.databinding.FragmentFilterBinding
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.ProductAdapter
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.hideBottomSheetOrShowWhenScroll
import com.developers.shopapp.utils.setupViewBeforeLoadData
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@InternalCoroutinesApi
@AndroidEntryPoint
class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    val args:FilterFragmentArgs by navArgs()

    @Inject
    lateinit var productAdapter: ProductAdapter

    val categoryProductViewModel:CategoryProductViewModel by viewModels()
    val navController by lazy {
        findNavController()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterName=args.filterName

        binding.filterName.text=filterName


        subscribeToObservers()

        setupRecyclerViewProduct()

        hideBottomSheetOrShowWhenScroll(recyclerView = binding.productRecyclerView,
            activity = requireActivity())

        adapterProductActions()
    }

    private fun setupRecyclerViewProduct() = binding.productRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter =   productAdapter

    }

    private fun subscribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

                launch {
                    categoryProductViewModel.filterProductStatus.collect(
                        EventObserver(
                            onLoading = {
                                setupViewBeforeLoadData(
                                    shimmerFrameLayout= binding.shimmer, onLoading = true)
                            },
                            onSuccess = { filterProduct ->
                                setupViewBeforeLoadData(
                                    shimmerFrameLayout= binding.shimmer, onLoading = false
                                )

                                filterProduct.data?.let {
                                    if(it.isEmpty()) setupViewBeforeLoadData(
                                        onLoading = false, onError = true
                                        , emptyView = binding.emptyView, tvError = binding.textEmpty, errorMessage = "No Data Found")
                                    productAdapter.products = it
                                }


                            },
                            onError = {

                                Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                                snackbar(it)
                                setupViewBeforeLoadData(shimmerFrameLayout= binding.shimmer,
                                    onLoading =false, onError = true, errorMessage = it
                                    , emptyView = binding.emptyView, tvError = binding.textEmptyErr)
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


        }

    }

    private fun adapterProductActions() {
        productAdapter.setOnSavedClickListener { product, imageView, position ->
            if (product.inFav!!) {
                categoryProductViewModel.deleteFavProduct(product)
                imageView.setImageResource(R.drawable.not_save)
                product.inFav = false
                binding.emptyView.isVisible = productAdapter.clearItemAndIfLast(product,position)
            } else {
                categoryProductViewModel.setFavProduct(product)
                product.inFav = true
                imageView.setImageResource(R.drawable.saved)
            }
        }

        productAdapter.setOnItemClickListener {
            val action=FilterFragmentDirections.actionFilterFragmentToFoodDetailsFragment(it)
            navController.navigate(action)
        }


    }
    override fun onResume() {
        super.onResume()
        categoryProductViewModel.filterProduct(args.filterName)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentFilterBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
package com.developers.shopapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentCartBinding
import com.developers.shopapp.databinding.FragmentPopularProductBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.PopularFoodAdapter
import com.developers.shopapp.ui.viewmodels.CategoryProductViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.hideBottomSheetOrShowWhenScroll
import com.developers.shopapp.utils.setupViewBeforeLoadData
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject


@InternalCoroutinesApi
@AndroidEntryPoint
class PopularFragmentProduct: Fragment() {
    private var _binding: FragmentPopularProductBinding? = null
    private val binding get() = _binding!!


    val categoryProductViewModel: CategoryProductViewModel by viewModels()

    @Inject
    lateinit var popularFoodAdapter: PopularFoodAdapter
    private val navController by lazy {
        findNavController()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        setupRecyclerViewPopularProducts()

        subscribeToObservers()

        adapterActions()

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        hideBottomSheetOrShowWhenScroll(recyclerView = binding.popularFragmentRecycler, activity = requireActivity())

    }

    private fun setupRecyclerViewPopularProducts() = binding.popularFragmentRecycler.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = popularFoodAdapter

    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            categoryProductViewModel.popularProductStatus.collect(
                EventObserver(
                    onLoading = {
                        setupViewBeforeLoadData( spinKit = binding.spinKit,
                            shimmerFrameLayout= binding.shimmer, onLoading = true)
                    },
                    onSuccess = { poplarRestaurant ->
                        setupViewBeforeLoadData( spinKit = binding.spinKit,
                            shimmerFrameLayout= binding.shimmer, onLoading = false)
                        poplarRestaurant.data?.let {
                            if(it.isEmpty()) setupViewBeforeLoadData(
                                onLoading = false, onError = true
                                , emptyView = binding.emptyView)
                            popularFoodAdapter.products = it
                        }
                    },
                    onError = {
                        Log.e(Constants.TAG, "subscribeToObservers: ${it} ", )
                        setupViewBeforeLoadData( spinKit = binding.spinKit,
                            shimmerFrameLayout= binding.shimmer, onLoading = false,
                            onError = true, errorMessage = it)
                        snackbar(it)
                    }
                )
            )

        }

    }

    private fun adapterActions() {


        popularFoodAdapter.setOnItemClickListener {

           val action = PopularFragmentProductDirections.actionPopularFragmentProductToFoodDetailsFragment(it)
            navController.navigate(action)
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPopularProductBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onResume() {
        super.onResume()
        categoryProductViewModel.getPopularRestaurant()
    }
}
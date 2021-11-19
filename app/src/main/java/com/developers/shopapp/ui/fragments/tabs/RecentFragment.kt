package com.developers.shopapp.ui.fragments.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.databinding.FragmentCartBinding
import com.developers.shopapp.databinding.FragmentHomeBinding
import com.developers.shopapp.databinding.FragmentRecentBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.RecentAdapter
import com.developers.shopapp.ui.viewmodels.TabsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class RecentFragment : Fragment() {
    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var recentAdapter: RecentAdapter

    val tabsViewModel: TabsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabsViewModel.getAllProduct()
        setupRecyclerViewRecent()
        subscribeToObservers()

    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            tabsViewModel.productStatus.collect(EventObserver(
                onLoading = {
                 binding.spinKit.isVisible=true
                    binding.emptyView.isVisible=false
                },
                onSuccess = {product->
                    binding.spinKit.isVisible=false
                    binding.emptyView.isVisible=false

                    product.productPagesData.data?.let {
                        recentAdapter.producties=it
                    }


                },
                onError = {
                    binding.spinKit.isVisible=false
                   binding.emptyView.isVisible=true
                }
            )
            )
        }
    }

    private fun setupRecyclerViewRecent() = binding.recentFragmentRecycler.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = recentAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
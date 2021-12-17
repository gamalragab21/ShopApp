package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.databinding.FragmentRestaurantSearchBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.adapters.SearchHistoryAdapter
import com.developers.shopapp.ui.viewmodels.SearchViewModel
import com.developers.shopapp.utils.snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.view.inputmethod.EditorInfo

import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import com.developers.shopapp.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import com.developers.shopapp.R
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.developers.shopapp.ui.adapters.PopularTagsAdapter
import com.developers.shopapp.utils.setTextViewDrawableColor
import com.developers.shopapp.utils.setupViewBeforeLoadData


@InternalCoroutinesApi
@AndroidEntryPoint
class SearchRestaurantFragment : Fragment() {

    private var _binding: FragmentRestaurantSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var searchHistoryAdapter: SearchHistoryAdapter

    @Inject
    lateinit var popularTagsAdapter: PopularTagsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerViewPopularTags()
        setupRecyclerViewSearchHistory()

        subscribeToObservers()

        binding.inputTextLayoutSearch.editText!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })


        binding.viewMore.setOnClickListener {
            if (binding.viewMore.text == requireContext().getString(R.string.view_more)) {
                searchHistoryAdapter.viewMore = false
                setTextForViewMoreOrLess(false)
                searchViewModel.getAllSearchHistory(30)

            } else {
                searchHistoryAdapter.viewMore = true
                setTextForViewMoreOrLess(true)
                searchViewModel.getAllSearchHistory(3)

            }
        }

        binding.existTv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.clearAll.setOnClickListener {
            searchViewModel.clearAllSearchHistory()
        }

        binding.viewAllPopularTags.setOnClickListener {
            if (binding.viewAllPopularTags.text == "View All") {
                binding.viewAllPopularTags.text = getString(R.string.view_less)
                popularTagsAdapter.viewAll=false
            } else {
                binding.viewAllPopularTags.text = getString(R.string.view_all)
                popularTagsAdapter.viewAll=true


            }
        }
        adapterActions()

    }

    private fun adapterActions() {
        searchHistoryAdapter.setOnItemClickListener {
            binding.inputTextLayoutSearch.editText!!.setText(it.searchName)
        }

        searchHistoryAdapter.setonCloseImageClickListener {
            searchViewModel.deleteSearchHistory(it)
        }
    }

    private fun performSearch(searchText: String) {
        if (searchText.isNotEmpty())
            searchViewModel.insertNewSearchKeyWord(searchText)
    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            // get search
            launch {

                searchViewModel.searchHistoryStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            binding.emptySearchTv.isVisible = it.isEmpty()
                            binding.viewMore.isVisible = it.isNotEmpty()
                            binding.clearAll.isVisible = it.isNotEmpty()

                            searchHistoryAdapter.searchHistories = it

                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )

            }

            //insert
            launch {

                searchViewModel.insertSearchKeyWordStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            searchViewModel.getAllSearchHistory(3)
                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )

            }

            //delete
            launch {

                searchViewModel.deleteSearchKeyWordStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            searchViewModel.getAllSearchHistory(3)
                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )

            }

            //clear All
            launch {

                searchViewModel.clearAllSearchHistoryStatus.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            binding.emptySearchTv.isVisible = true
                            binding.viewMore.isVisible = false
                            searchHistoryAdapter.searchHistories = listOf()
                            snackbar("Cleared Success")
                        },
                        onError = {
                            snackbar(it)
                        }
                    )
                )

            }

            launch {
                searchViewModel.popularRestaurantStatus.collect(
                    EventObserver(
                        onLoading = {},
                        onSuccess = { poplarRestaurant ->
                            poplarRestaurant.data?.let {
                                binding.viewAllPopularTags.isVisible = it.isNotEmpty()
                                binding.emptyPopularTagsTv.isVisible = it.isEmpty()
                                popularTagsAdapter.popularTags = it
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

    private fun setTextForViewMoreOrLess(viewMore: Boolean) {
        if (viewMore) {
            binding.viewMore.text = requireContext().getString(R.string.view_more)
            val img = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.view_more
            )
            binding.viewMore.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
        } else {
            binding.viewMore.text = requireContext().getString(R.string.view_less)
            val img = ContextCompat.getDrawable(requireContext(), R.drawable.view_less)
            binding.viewMore.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
        }
        setTextViewDrawableColor(binding.viewMore, R.color.colorPrimary)
    }

    private fun setupRecyclerViewSearchHistory() = binding.recyclerViewHistorySearch.apply {
        itemAnimator = null
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext())
        adapter = searchHistoryAdapter
    }

    private fun setupRecyclerViewPopularTags() = binding.recyclerViewPopularTags.apply {
        itemAnimator = null
        isNestedScrollingEnabled = false
        layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        adapter = popularTagsAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantSearchBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.getAllSearchHistory(3)
        searchViewModel.getPopularRestaurant()
    }
}
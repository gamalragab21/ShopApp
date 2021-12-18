package com.developers.shopapp.ui.fragments

import android.Manifest
import android.os.Build
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
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.ui.adapters.PopularTagsAdapter
import com.developers.shopapp.ui.adapters.RestaurantAdapter
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@InternalCoroutinesApi
@AndroidEntryPoint
class SearchRestaurantFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentRestaurantSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    @Inject
    lateinit var searchHistoryAdapter: SearchHistoryAdapter

    @Inject
    lateinit var popularTagsAdapter: PopularTagsAdapter

    @Inject
    lateinit var restaurantAdapter: RestaurantAdapter
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value

        restaurantAdapter.laLng = dataUserInfo?.latLng
        setupRecyclerViewPopularTags()
        setupRecyclerViewSearchHistory()
        setupRecyclerViewRecent()

        subscribeToObservers()

        binding.inputTextLayoutSearch.editText!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        binding.inputTextLayoutSearch.editText!!.doOnTextChanged { text, start, before, count ->
            if(text!!.isEmpty()){
                binding.rootContent.visibility=View.VISIBLE
                binding.rootItems.visibility=View.GONE

            }else{
                binding.rootItems.visibility=View.VISIBLE
                binding.rootContent.visibility=View.GONE

            }
            binding.emptyView.isVisible=false

        }


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
        adapterSearchHistoryActions()
        adapterRestaurantActions()
        adapterPopularTagsActions()

    }

    private fun adapterPopularTagsActions() {
        popularTagsAdapter.setOnItemClickListener {
            binding.inputTextLayoutSearch.editText!!.setText(it.restaurantName)
            performSearch(it.restaurantName)
        }
    }

    private fun adapterRestaurantActions() {
        restaurantAdapter.setOnSavedClickListener { restaurant, imageView, position->
            if (restaurant.inFav!!) {
                restaurantViewModel.deleteFavRestaurant(restaurant)
                imageView.setImageResource(R.drawable.not_save)
                restaurant.inFav = false
            } else {
                restaurantViewModel.setFavRestaurant(restaurant)
                restaurant.inFav = true
                imageView.setImageResource(R.drawable.saved)
            }
        }

        restaurantAdapter.setOnItemClickListener {
            val bundle = bundleOf(Constants.CURRENT_RESTAURANT to it)
            findNavController().navigate(R.id.restaurantDetailsFragment,bundle)
        }

        restaurantAdapter.setOnContactClickListener {
            restaurantViewModel.callPhone.postValue(it.contact)
            requestPermissions()
        }
    }

    private fun adapterSearchHistoryActions() {
        searchHistoryAdapter.setOnItemClickListener {
            binding.inputTextLayoutSearch.editText!!.setText(it.searchName)
            performSearch(it.searchName)
        }

        searchHistoryAdapter.setonCloseImageClickListener {
            searchViewModel.deleteSearchHistory(it)
        }
    }

    private fun performSearch(searchText: String) {
        if (searchText.isNotEmpty()){
            restaurantViewModel.filterRestaurant(searchText)
            searchViewModel.insertNewSearchKeyWord(searchText)
        }
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
                            searchHistoryAdapter.viewMore = it.size<=3
                            setTextForViewMoreOrLess(it.size<=3)

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
                restaurantViewModel.popularRestaurantStatus.collect(
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

            launch {
                restaurantViewModel.filterRestaurantStatus.collect(EventObserver(
                    onError = {
                        snackbar(it)
                    },
                    onLoading = null,
                    onSuccess = {
                        it.data?.let {
                            binding.emptyView.isVisible=it.isEmpty()
                            Log.i(TAG, "subscribeToObservers: ${it.toString()}")
                            restaurantAdapter.restaurantes=it
                        }
                    }
                ))
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
    private fun setupRecyclerViewRecent() = binding.recyclerViewRestaurant.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = restaurantAdapter

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
        restaurantViewModel.getPopularRestaurant()
    }
    private fun requestPermissions() {

        if (PermissionsUtility.hasCallPhonePermissions(requireContext())) {
            callPhone()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "you need to accept Call Phone permissions to use this Option.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.CALL_PHONE
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "you need to accept Call Phone permissions to use this Option.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.CALL_PHONE
            )
        }
    }

    private fun callPhone() {
        try {
            Utils.startCallIntent(requireContext(),restaurantViewModel.callPhone.value.toString())
        }catch (e:Exception){
            snackbar("Phone Not Found")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        callPhone()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }
}
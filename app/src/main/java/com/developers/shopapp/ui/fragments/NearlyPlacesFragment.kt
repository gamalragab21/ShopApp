package com.developers.shopapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentNearlyPlacesBinding
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.helpers.MyLocation
import com.developers.shopapp.ui.adapters.RestaurantAdapter
import com.developers.shopapp.ui.viewmodels.RestaurantViewModel
import com.developers.shopapp.utils.*
import com.developers.shopapp.utils.Constants.SEARCH_TIME_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class NearlyPlacesFragment:Fragment() , AdapterView.OnItemSelectedListener, EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentNearlyPlacesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var restaurantAdapter: RestaurantAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val restaurantViewModel: RestaurantViewModel by viewModels()
    val myLocation by lazy {
        MyLocation()
    }

    val navController by lazy {
        findNavController()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUserInfo = dataStoreManager.glucoseFlow.value

        restaurantAdapter.laLng = dataUserInfo?.latLng

        var job: Job? = null
        binding.inputTextLayoutSearch.editText!!.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if (it.isEmpty()) {
                        val latLng=dataStoreManager.glucoseFlow.value!!.latLng
                        restaurantViewModel.getNearlyRestaurant(latLng.latitude,latLng.longitude)
                    } else {
                        restaurantViewModel.filterRestaurant(it.toString())
                    }
                }
            }
        }
        setupRecyclerViewRecent()
        subscribeToObservers(dataUserInfo)

        adapterActions()

        setupSpinner(dataUserInfo)

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        hideBottomSheetOrShowWhenScroll(recyclerView = binding.nearlyRestaurantFragmentRecycler, activity = requireActivity())


    }
    private fun setupSpinner(dataUserInfo: UserInfoDB?) {

        val listAddress=ArrayList<String>()
        val address=myLocation.getAddressFromLatLng(requireContext(),dataUserInfo!!.latLng.latitude,dataUserInfo.latLng.longitude)
        listAddress.add(address)
// Create an ArrayAdapter using the string array and a default spinner layout
         ArrayAdapter(
            requireContext(),
           R.layout.spinner_item, R.id.textItem,listAddress
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.custam_drop_down_layout)
            // Apply the adapter to the spinner
            binding.spinner.adapter = adapter
            binding.spinner.onItemSelectedListener = this
        }
    }
    private fun adapterActions() {
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
           navController.navigate(R.id.restaurantDetailsFragment,bundle)

        }

        restaurantAdapter.setOnContactClickListener {
            restaurantViewModel.callPhone.postValue(it.contact)
            requestPermissions()
        }
    }

    private fun subscribeToObservers(dataUserInfo: UserInfoDB?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    restaurantViewModel.nearlyRestaurantStatus.collect(
                        EventObserver(
                        onLoading = {
                            setupViewBeforeLoadData(
                                shimmerFrameLayout= binding.shimmer, onLoading = true)
                        },
                        onSuccess = { restaurant ->
                            setupViewBeforeLoadData(
                                shimmerFrameLayout= binding.shimmer, onLoading = false, emptyView = binding.emptyView
                                )

                            restaurant.data?.let {
                                if(it.isEmpty())setupViewBeforeLoadData(
                                    onLoading = false, onError = true
                                    , emptyView = binding.emptyView, tvError = binding.textEmpty, errorMessage = "No Data Found")
                                binding.countRestaurant.text="${it.size} Restaurants found near you"
                                restaurantAdapter.restaurantes = it
                            }


                        },
                        onError = {
                            setupViewBeforeLoadData(shimmerFrameLayout= binding.shimmer,
                                onLoading =false, onError = true, errorMessage = it
                                , emptyView = binding.emptyView, tvError = binding.textEmptyErr)

                            Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                            snackbar(it)

                        }
                    )
                    )

                }

                launch {
                    restaurantViewModel.filterRestaurantStatus.collect(
                        EventObserver(
                        onLoading = {
                            setupViewBeforeLoadData(
                                shimmerFrameLayout= binding.shimmer, onLoading = true)
                        },
                        onSuccess = { filterRestaurant ->

                            setupViewBeforeLoadData(
                                shimmerFrameLayout= binding.shimmer, onLoading = false
                                , emptyView = binding.emptyView)
                             restaurantAdapter.restaurantes=ArrayList()
                            filterRestaurant.data?.let {
                                if(it.isEmpty())setupViewBeforeLoadData(
                                    onLoading = false, onError = true
                                    , emptyView = binding.emptyView, tvError = binding.textEmpty, errorMessage = "No Data Found")
                                binding.countRestaurant.text="${it.size} Restaurants found near you"

                                restaurantAdapter.restaurantes = it
                            }

                        },
                        onError = {
                            Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                            snackbar(it)
                            setupViewBeforeLoadData(
                                onLoading =false, onError = true, errorMessage = it
                                , emptyView = binding.emptyView, tvError = binding.textEmptyErr)
                        }
                    )
                    )

                }

                launch {
                    restaurantViewModel.deleteFavRestaurantStatus.collect(
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
                    restaurantViewModel.setFavRestaurantStatus.collect(
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

    private fun setupRecyclerViewRecent() = binding.nearlyRestaurantFragmentRecycler.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = restaurantAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding= FragmentNearlyPlacesBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onResume() {
        super.onResume()
        val latLng=dataStoreManager.glucoseFlow.value!!.latLng
        restaurantViewModel.getNearlyRestaurant(latLng.latitude,latLng.longitude)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // Another interface callback
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
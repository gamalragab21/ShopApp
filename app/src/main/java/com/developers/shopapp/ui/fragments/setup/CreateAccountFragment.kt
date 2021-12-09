package com.developers.shopapp.ui.fragments.setup

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentRegisterBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.activities.MainActivity
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.utils.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.developers.shopapp.utils.PermissionsUtility
import com.developers.shopapp.utils.snackbar
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import com.developers.shopapp.helpers.MyLocation
import com.developers.shopapp.utils.Constants.TAG


@AndroidEntryPoint
class CreateAccountFragment() : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private var latLong: LatLng? = null


    lateinit var locationResult: MyLocation.LocationResult
    val myLocation by lazy { MyLocation() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location?) {
                location?.let {
                    latLong = LatLng(it.latitude, it.longitude)
                }
            }

        }

        //  fusedLocationProviderClient = FusedLocationProviderClient(this)
        // to get email , password if sets it as remember me
//        val dataUserInfo=  dataStoreManager.glucoseFlow.value
//        binding.inputTextEmail.setText(dataUserInfo?.email)
//        binding.inputTextPassword.setText(dataUserInfo?.password)

        // clear error text
        binding.inputTextUserName.doAfterTextChanged {
            binding.inputTextLayoutUserName.isHelperTextEnabled = false
        }
        binding.inputTextEmail.doAfterTextChanged {
            binding.inputTextLayoutEmail.isHelperTextEnabled = false
        }
        binding.inputTextPhone.doAfterTextChanged {
            binding.inputTextLayoutPhone.isHelperTextEnabled = false
        }
        binding.inputTextPassword.doAfterTextChanged {
            binding.inputTextLayoutPassword.isHelperTextEnabled = false
        }

        binding.alreadyHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_createAccountFragment_to_loginFragment)
        }
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        // sign in
        binding.createAccountBtn.setOnClickListener {
            requestPermissions()
        }

        // check login state
        subscribeToObservables()


    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            authenticationViewModel.createAccountStatus.collect(EventObserver(
                onLoading = {
                    binding.spinKit.isVisible = true
                },
                onSuccess = {
                    binding.spinKit.isVisible = false

                    snackbar(it.message)
                    it.data?.let {
                        lifecycleScope.launch {
                            saveDataAndNavigate(it)
                        }
                    }
                },
                onError = {
                    binding.spinKit.isVisible = false
                    snackbar(it)
                }
            ))
        }
    }

    private suspend fun saveDataAndNavigate(it: String) {
        updateTokenAndLatLng(it, latLong)
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun requestPermissions() {

        if (PermissionsUtility.hasLocationPermissions(requireContext())) {
            getMyLocation()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "you need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "you need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }


    private fun saveEmailAndPassword(email: String, password: String) {
        lifecycleScope.launch {
            dataStoreManager.setUserInfo(email = email, password = password)
        }
    }

    private suspend fun updateTokenAndLatLng(token: String, latLong: LatLng?) {
        dataStoreManager.setUserInfo(token = token, latLng = latLong)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getMyLocation()
    }

    private fun getMyLocation() {

        Log.i(TAG, "getMyLocation: ")
        if (myLocation.getLocation(requireContext(), locationResult)) {

            createAccountUser()
        } else {
            authenticationViewModel.showNoGpsDialog(requireContext())
            snackbar("PLZ,open your location")
        }
    }

    private fun createAccountUser() {
        authenticationViewModel.createAccount(
            binding.inputTextLayoutUserName,
            binding.inputTextLayoutEmail,
            binding.inputTextLayoutPhone,
            binding.inputTextLayoutPassword,
            latLong
        )
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
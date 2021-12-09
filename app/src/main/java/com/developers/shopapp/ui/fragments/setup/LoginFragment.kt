package com.developers.shopapp.ui.fragments.setup

import android.Manifest
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
import com.developers.shopapp.databinding.FragmentLoginBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Intent
import android.location.Location
import android.os.Build
import com.developers.shopapp.helpers.MyLocation
import com.developers.shopapp.ui.activities.MainActivity
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.PermissionsUtility
import com.google.android.gms.maps.model.LatLng
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class LoginFragment : Fragment(),EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    lateinit var locationResult:MyLocation.LocationResult

    private var latLong: LatLng?=null
    val myLocation by lazy {   MyLocation()}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location?) {
                Log.i(TAG, "gotLocation: ${location.toString()}")
                location?.let {
                    latLong= LatLng(it.latitude,it.longitude)
                }
            }

        }
        // to get email , password if sets it as remember me
        val dataUserInfo = dataStoreManager.glucoseFlow.value
        binding.inputTextEmail.setText(dataUserInfo?.email)
        binding.inputTextPassword.setText(dataUserInfo?.password)
        Log.i(TAG, "dataUserInfo: ${dataUserInfo.toString()}")
        // clear error text
        binding.inputTextEmail.doAfterTextChanged {
            binding.inputTextLayoutEmail.isHelperTextEnabled = false
        }

        binding.inputTextPassword.doAfterTextChanged {
            binding.inputTextLayoutPassword.isHelperTextEnabled = false
        }

        binding.textSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }

        binding.forgetPassword.setOnClickListener {
           findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }
        binding.backIcon.setOnClickListener {
           requireActivity().finish()
        }
        // sign in
        binding.singinBtn.setOnClickListener {
            requestPermissions()

        }

        // check login state
        subscribeToObservables()


    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            authenticationViewModel.authUserStatus.collect(EventObserver(
                onLoading = {
                    binding.spinKit.isVisible = true

                    Log.i(TAG, "onViewCreated:Loding ")
                },
                onError = {
                    binding.spinKit.isVisible = false

                    snackbar(it)
                    Log.i(TAG, "onViewCreated: error $it")
                },
                onSuccess = {
                    binding.spinKit.isVisible=false


                    Log.i(TAG, "onViewCreated: onSuccess $it")

                    snackbar(it.message)
                    it.data?.let { token ->
                        lifecycleScope.launch {
                           saveDataAndNavigate(token,latLong)
                        }
                    }

                }
            ))


        }
    }

    private suspend fun saveDataAndNavigate(token: String, latLong: LatLng?) {
        updateTokenAndLatLng(token,latLong)
        if (binding.switchRememberMe.isChecked) {
            val password = binding.inputTextPassword.text.toString()
            val email = binding.inputTextEmail.text.toString()
            saveEmailAndPassword(email, password)
        }
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun saveEmailAndPassword(email: String, password: String) {
        lifecycleScope.launch {
            dataStoreManager.setUserInfo(email = email, password = password)
        }
    }

    private fun requestPermissions(){

        if (PermissionsUtility.hasLocationPermissions(requireContext())){
            getMyLocation()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "you need to accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                this,
                "you need to accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }



    private suspend fun updateTokenAndLatLng(token: String, latLong: LatLng?) {
        dataStoreManager.setUserInfo(token = token,latLng = latLong)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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
       if ( myLocation.getLocation(requireContext(), locationResult)){
           loginUser()
       }else{
          authenticationViewModel.showNoGpsDialog(requireContext())
           snackbar("PLZ, open your location ")
       }
    }

    private fun loginUser() {
        authenticationViewModel.loginUser(
            binding.inputTextLayoutEmail,
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
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }
}
package com.developers.shopapp.ui.fragments.setup

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
import com.developers.shopapp.entities.UserData
import com.developers.shopapp.ui.activities.MainActivity


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            authenticationViewModel.loginUser(
                binding.inputTextLayoutEmail,
                binding.inputTextLayoutPassword,
            )
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
                    it.userData?.let { data ->
                        lifecycleScope.launch {
                           saveDataAndNavigate(data)
                        }
                    }

                }
            ))


        }
    }

    private suspend fun saveDataAndNavigate(userData: UserData) {
        updateToken(userData.token)
        if (binding.switchRememberMe.isChecked) {
            val password = binding.inputTextPassword.text.toString()
            saveEmailAndPassword(userData.email, password)
        }
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun saveEmailAndPassword(email: String, password: String) {
        lifecycleScope.launch {
            dataStoreManager.setUserInfo(email = email, password = password)
        }
    }

    private suspend fun updateToken(token: String) {
            dataStoreManager.setUserInfo(token = token)

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
}
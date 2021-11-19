package com.developers.shopapp.ui.fragments.setup

import android.content.Intent
import android.os.Bundle
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
import com.developers.shopapp.entities.UserData
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.activities.MainActivity
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            authenticationViewModel.createAccount(
                binding.inputTextLayoutUserName,
                binding.inputTextLayoutEmail,
                binding.inputTextLayoutPhone,
                binding.inputTextLayoutPassword
            )
        }

        // check login state
        subscribeToObservables()





    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
               authenticationViewModel.createAccountStatus.collect(EventObserver(
                   onLoading ={
                   binding.spinKit.isVisible=true
                   },
                   onSuccess = {
                       binding.spinKit.isVisible=false

                       snackbar(it.message)
                       it.userData?.let {
                           lifecycleScope.launch {

                              saveDataAndNavigate(it)
                           }
                       }
                   },
                   onError = {
                       binding.spinKit.isVisible=false
                       snackbar(it)
                   }
               ))
        }
    }

    private suspend fun saveDataAndNavigate(it: UserData) {
        updateToken(it.token)
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun saveEmailAndPassword(email: String, password: String) {
        lifecycleScope.launch {
            dataStoreManager.setUserInfo(email = email,password = password)
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
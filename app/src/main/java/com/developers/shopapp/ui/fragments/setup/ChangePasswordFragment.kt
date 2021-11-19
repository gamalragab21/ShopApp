package com.developers.shopapp.ui.fragments.setup

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
import androidx.navigation.fragment.navArgs
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentChangePasswordBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordFragment:Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private val args:ChangePasswordFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputTextPassword.doAfterTextChanged {
            binding.inputTextLayoutPassword.isHelperTextEnabled = false
        }

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.restPasswordBtn.setOnClickListener {
            args?.let {
                authenticationViewModel.resetPassword(binding.inputTextLayoutPassword,args.userEmail,args.code)
            }

           // findNavController().navigate(R.id.action_forgetPasswordFragment_to_verifyCodeEmailFragment)

        }

        subscribeToObservables()


    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            authenticationViewModel.resetPasswordStatus.collect(EventObserver(
                onError = {
                    binding.spinKit.isVisible=false
                     snackbar(it)
                },
                onSuccess = {
                    binding.spinKit.isVisible=false
                    snackbar(it.message)
                   it.userData?.let { data ->
                       findNavController().navigate(R.id.action_changePasswordFragment_to_loginFragment)
                   }
                },
                onLoading = {
                    binding.spinKit.isVisible=true
                }
            ))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding= FragmentChangePasswordBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
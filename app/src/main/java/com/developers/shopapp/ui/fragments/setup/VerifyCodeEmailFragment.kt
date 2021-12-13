package com.developers.shopapp.ui.fragments.setup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentVerifyCodeEmailBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.viewmodels.AuthenticationViewModel
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class VerifyCodeEmailFragment:Fragment() {
    private var _binding: FragmentVerifyCodeEmailBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val args:VerifyCodeEmailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.userEmail.text=args.userEmail
      binding.verifyBtn.setOnClickListener {
          args?.let {
              authenticationViewModel.verifyCode(it.userEmail,binding.codeEmail)
          }
      }

        subscribeToObservables()


    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            authenticationViewModel.verifyCodeStatus.collect(EventObserver(
                onError = {
                    binding.spinKit.isVisible=false
                    snackbar(it)
                },
                onSuccess = {
                    binding.spinKit.isVisible=false
                    snackbar("Verified")
                    it.userData?.let { data ->
                        Log.i(TAG, "subscribeToObservables: ${data}")
                        val action =VerifyCodeEmailFragmentDirections.actionVerifyCodeEmailFragmentToChangePasswordFragment(data,binding.codeEmail.text.toString())
                        findNavController().navigate(action)
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
       _binding= FragmentVerifyCodeEmailBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
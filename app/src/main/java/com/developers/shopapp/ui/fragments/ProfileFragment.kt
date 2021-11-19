package com.developers.shopapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.developers.shopapp.databinding.FragmentHomeBinding
import com.developers.shopapp.databinding.FragmentProfileBinding
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.activities.SetupActivity
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment:Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        authenticationViewModel.logoutStatus.collect(EventObserver(
//            onLoading = {
//                Log.i(Constants.TAG, "onViewCreated:Loding ")
//            },
//            onError = {
//                snackbar(it)
//                Log.i(Constants.TAG, "onViewCreated: error $it")
//            },
//            onSuccess = {
//
//
//                Log.i(Constants.TAG, "onViewCreated: onSuccess $it")
//
//                snackbar(it.message)
//                it.data?.let { data ->
//                    lifecycleScope.launch {
//
//                        Log.i(Constants.TAG, "onViewCreated: token ${data.token}")
//                        clearToken()
//                        val loginIntent =
//                            Intent(requireContext(), SetupActivity::class.java).apply {
//                                action = Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT
//                            }
//                        startActivity(loginIntent)
//                        requireActivity().finish()
//                    }
//                }
//
//            }
//        ))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding= FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
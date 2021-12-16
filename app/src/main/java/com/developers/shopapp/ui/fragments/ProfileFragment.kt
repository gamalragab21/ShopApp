package com.developers.shopapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.databinding.FragmentHomeBinding
import com.developers.shopapp.databinding.FragmentProfileBinding
import com.developers.shopapp.entities.User
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.helpers.EventObserver
import com.developers.shopapp.ui.activities.SetupActivity
import com.developers.shopapp.ui.viewmodels.UserViewModel
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Constants.LOGIN_ME
import com.developers.shopapp.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment:Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val userViewModel:UserViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
     lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//

        subscribeToObservers()

        binding.logoutProfile.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                logOut()
            }
        }
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private suspend fun logOut() {
         dataStoreManager.setUserInfo(token = "")
            startActivity(Intent(requireContext(), SetupActivity::class.java)
                .setAction(Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT))
            requireActivity().finish()
    }


    private fun subscribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    userViewModel.userProfileStatus.collect(
                        EventObserver(
                            onLoading = {
                                binding.spinKit.isVisible = true
                            },
                            onSuccess = { restaurant ->

                                binding.spinKit.isVisible = false

                                restaurant.data?.let {
                                  setUserData(it)
                                }


                            },
                            onError = {
                                Log.i(Constants.TAG, "subscribeToObservers: ${it}")
                                snackbar(it)
                                binding.spinKit.isVisible = false
                            }
                        )
                    )

                }

            }
        }

    }

    private fun setUserData(it: User) {
        glide.load(it.image).into(binding.imageProfile)
        binding.username.text=it.username
        binding.email.text=it.email
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

    override fun onResume() {
        super.onResume()
        userViewModel.getAllFavouritesRestaurant()
    }
}
package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.developers.shopapp.databinding.FragmentCartBinding
import com.developers.shopapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment:Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding= FragmentCartBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
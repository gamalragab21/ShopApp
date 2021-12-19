package com.developers.shopapp.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ActivityMainBinding.bind
import com.developers.shopapp.databinding.ActivityMainBinding.inflate
import com.developers.shopapp.databinding.DialogRateBinding
import com.developers.shopapp.databinding.NoInternetLayoutBinding
import com.developers.shopapp.utils.snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NoInternetConnectionDialog:DialogFragment() {
    private var _binding: NoInternetLayoutBinding? = null
    private val binding get()  = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = NoInternetLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        return MaterialAlertDialogBuilder(requireContext())
            .setBackground(ColorDrawable(Color.WHITE))
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retry.setOnClickListener {
            snackbar("Retry")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


}
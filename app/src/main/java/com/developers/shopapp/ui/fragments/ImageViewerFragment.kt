package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.developers.shopapp.databinding.FragmentImageViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.net.Uri
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader


@AndroidEntryPoint
class ImageViewerFragment: Fragment() {
    private var _binding: FragmentImageViewerBinding? = null
    private val binding get() = _binding!!

    val args:ImageViewerFragmentArgs by navArgs()

    @Inject
     lateinit var  glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mBigImage.showImage(Uri.parse(args.images[0]))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentImageViewerBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
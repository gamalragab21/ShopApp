package com.developers.shopapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentRateDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object MyCustomRateDialog : DialogFragment(), MyBuilder {
    private var _binding: FragmentRateDialogBinding? = null
    private val binding get() = _binding!!

    val builder: MyBuilder = this

    private var rateId: Int? = null

    private lateinit var rateDialogListener: RateDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        return MaterialAlertDialogBuilder(requireContext())
            .setBackground(ColorDrawable(Color.TRANSPARENT))
            .setView(binding.root)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.cancelRating.setOnClickListener {
            dismiss()
        }

        binding.submitRating.setOnClickListener {
            rateDialogListener.onSubMitClick(
                binding.ratingStars.rating, binding.messageFeedBack.text.toString(), rateId
            )

            dismiss()
        }
        binding.ratingStars.setOnRatingBarChangeListener { ratingBar, fl, b ->
            setupImageRating(fl)
            animateImageView()
        }
        return binding.root
    }

    private fun setupImageRating(fl: Float) {
        when {
            fl <= 1 -> {
                binding.emojiStatus.setImageResource(R.drawable.one_star)
            }
            fl <= 2 -> {
                binding.emojiStatus.setImageResource(R.drawable.two_star)
            }
            fl <= 3 -> {
                binding.emojiStatus.setImageResource(R.drawable.three_star)
            }
            fl <= 4 -> {
                binding.emojiStatus.setImageResource(R.drawable.four_star)
            }
            else -> {
                binding.emojiStatus.setImageResource(R.drawable.five_start)

            }
        }
    }

    private fun animateImageView() {
        val scaleAnimation = ScaleAnimation(
            0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )

        scaleAnimation.fillAfter = true
        scaleAnimation.duration = 200
        binding.emojiStatus.startAnimation(scaleAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun enableEditText(enable: Boolean): MyBuilder {
        binding.messageFeedBack.isVisible = enable
        return this
    }

    override fun setTitle(title: String): MyBuilder {
        binding.title.text = title
        return this
    }

    override fun build(context: Context): MyBuilder {
        _binding = FragmentRateDialogBinding.inflate(LayoutInflater.from(context))
        return this
    }

    override fun show(childFragmentManager: FragmentManager) {
        show(childFragmentManager, null)


    }

    override fun rateId(id: Int?): MyBuilder {
        rateId = id
        return this
    }


    override fun ratingCount(ratingCount: Float): MyBuilder {
        binding.ratingStars.rating = ratingCount
        setupImageRating(ratingCount)
        return this
    }

    override fun feedBackMessage(message: String?): MyBuilder {
        binding.messageFeedBack.setText(message)
        return this
    }

    override fun setListenerWith(_rateDialogListener: RateDialogListener): MyBuilder {
        rateDialogListener = _rateDialogListener
        return this
    }

}

interface MyBuilder {
    fun enableEditText(enable: Boolean): MyBuilder
    fun setTitle(title: String): MyBuilder
    fun ratingCount(ratingCount: Float = 3f): MyBuilder
    fun feedBackMessage(message: String? = ""): MyBuilder
    fun setListenerWith(_rateDialogListener: RateDialogListener): MyBuilder
    fun build(context: Context): MyBuilder
    fun show(childFragmentManager: FragmentManager)
    fun rateId(rateId: Int?): MyBuilder
}

interface RateDialogListener {

    fun onSubMitClick(rateCount: Float, feedbackMessage: String, rateId: Int?)
}


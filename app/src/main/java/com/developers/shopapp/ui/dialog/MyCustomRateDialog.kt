package com.developers.shopapp.ui.dialog

import android.app.AlertDialog
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
import com.developers.shopapp.databinding.DialogFeedbackBinding
import com.developers.shopapp.databinding.DialogRateBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object MyCustomRateDialog : DialogFragment(), MyBuilder {
    private var _bindingRateDialog: DialogRateBinding? = null
    private val bindingRateDialog get()  = _bindingRateDialog!!
    lateinit var _bindingFeedBackDialog: DialogFeedbackBinding
    private val bindingFeedBackDialog get()  = _bindingFeedBackDialog!!


    private var showFeedBackDialog:Boolean=false


    private var rateId: Int? = null
    private var rateStarCount:Float=0.0f

    private lateinit var rateDialogListener: RateDialogListener
    private lateinit var dialogFeedback:androidx.appcompat.app.AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialAlertDialogBuilder(requireContext())
            .setBackground(ColorDrawable(Color.TRANSPARENT))
            .setView(bindingRateDialog.root)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingRateDialog.btNo.setOnClickListener {
            dismiss()
        }
        bindingFeedBackDialog.btNo.setOnClickListener {
            dialogFeedback.dismiss()
        }

        bindingRateDialog.btSend.setOnClickListener {
            rateStarCount=bindingRateDialog.rbStars.rating

            if (showFeedBackDialog){
                dialogFeedback= MaterialAlertDialogBuilder(requireContext())
                    .setBackground(ColorDrawable(Color.TRANSPARENT))
                    .setView(bindingFeedBackDialog.root)
                    .create()
                dialogFeedback.show()
            }else{
                rateDialogListener.onSubMitClick(
                    rateStarCount, "", rateId
                )
            }

            dismiss()
        }
        bindingFeedBackDialog.btSend.setOnClickListener {
            rateDialogListener.onSubMitClick(
                rateStarCount, bindingFeedBackDialog.etFeedback.text.toString(), rateId
            )

            dialogFeedback.dismiss()

        }

        bindingRateDialog.rbStars.setOnRatingBarChangeListener { ratingBar, fl, b ->
            setupImageRating(fl)
            animateImageView()
        }
        return bindingRateDialog.root
    }

    private fun setupImageRating(fl: Float) {
        when {
            fl <= 1 -> {
                bindingRateDialog.icon.setImageResource(R.drawable.one_star)
            }
            fl <= 2 -> {
                bindingRateDialog.icon.setImageResource(R.drawable.two_star)
            }
            fl <= 3 -> {
                bindingRateDialog.icon.setImageResource(R.drawable.three_star)
            }
            fl <= 4 -> {
                bindingRateDialog.icon.setImageResource(R.drawable.four_star)
            }
            else -> {
                bindingRateDialog.icon.setImageResource(R.drawable.five_start)

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
        bindingRateDialog.icon.startAnimation(scaleAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingRateDialog = null
    }

    override fun setTitle(title: String): MyBuilder {
        bindingRateDialog.dialogTitle.text = title
        return this
    }

    override fun build(context: Context,enableEditText:Boolean): MyBuilder {
        showFeedBackDialog=enableEditText
        _bindingRateDialog = DialogRateBinding.inflate(LayoutInflater.from(context))
        _bindingFeedBackDialog = DialogFeedbackBinding.inflate(LayoutInflater.from(context))

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
        bindingRateDialog.rbStars.rating = ratingCount
        setupImageRating(ratingCount)
        return this
    }

    override fun feedBackMessage(message: String?): MyBuilder {
        bindingFeedBackDialog.etFeedback.setText(message)
        return this
    }

    override fun setListenerWith(_rateDialogListener: RateDialogListener): MyBuilder {
        rateDialogListener = _rateDialogListener
        return this
    }

}

interface MyBuilder {
    fun setTitle(title: String): MyBuilder
    fun ratingCount(ratingCount: Float = 3f): MyBuilder
    fun feedBackMessage(message: String? = ""): MyBuilder
    fun setListenerWith(_rateDialogListener: RateDialogListener): MyBuilder
    fun build(context: Context,enableEditText:Boolean): MyBuilder
    fun show(childFragmentManager: FragmentManager)
    fun rateId(rateId: Int?): MyBuilder
}

interface RateDialogListener {

    fun onSubMitClick(rateCount: Float, feedbackMessage: String, rateId: Int?)
}


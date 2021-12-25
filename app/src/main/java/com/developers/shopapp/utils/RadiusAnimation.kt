package com.developers.shopapp.utils

import com.google.android.gms.maps.model.GroundOverlay

import android.view.animation.Animation
import android.view.animation.Transformation


class RadiusAnimation(private val groundOverlay: GroundOverlay) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        groundOverlay.setDimensions(100 * interpolatedTime)
        groundOverlay.transparency = interpolatedTime
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
    }
}
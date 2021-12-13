package com.developers.shopapp.app

import android.app.Application
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyBaseApp:Application(){
    override fun onCreate() {
        super.onCreate()
        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))

    }
}
package com.developers.shopapp.utils

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import pub.devrel.easypermissions.EasyPermissions

object PermissionsUtility {
    fun hasLocationPermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }

    fun hasCallPhonePermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CALL_PHONE,
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CALL_PHONE
                )
            }
        }
}
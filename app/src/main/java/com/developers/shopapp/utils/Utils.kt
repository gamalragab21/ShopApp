package com.developers.shopapp.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat.MEDIUM
import android.icu.text.DateFormat.getDateInstance
import android.net.Uri
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import com.developers.shopapp.utils.Constants.TAG
import com.google.android.gms.maps.model.LatLng
import java.text.DateFormat.LONG
import java.text.DateFormat.MEDIUM
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*





object Utils {



    fun getTimeAgo(time: Long, ctx: Context?): String? {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }
        val now: Long = Date().time
        if (time > now || time <= 0) {
            return null
        }

        // TODO: localize
        val diff = now - time
        return when {
            diff < Constants.MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * Constants.MINUTE_MILLIS -> {
                "an min"
            }
            diff < 50 * Constants.MINUTE_MILLIS -> {
                "${diff / Constants.MINUTE_MILLIS}min"
            }
            diff < 90 * Constants.MINUTE_MILLIS -> {
                "an hour"

            }
            diff < 24 * Constants.HOUR_MILLIS -> {
                "${diff / Constants.HOUR_MILLIS}hours"
            }
            diff < 48 * Constants.HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                "${diff / Constants.DAY_MILLIS}days"
            }
        }
    }

    fun getTimeStamp()=System.currentTimeMillis() / 1000

    fun buildAlertMessageNoGps(context: Context) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { dialog, id ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                  dialog.dismiss()
            }
            .setNegativeButton("No"
            ) { dialog, id ->
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    fun calculationByDistance(StartP: LatLng, EndP: LatLng): Int {
        val Radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        //return Radius * c
        return  meterInDec
    }


    fun startCallIntent(context: Context,phone:String){
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phone")
        context.startActivity(callIntent)
    }


     fun getDate(context: Context,milliSeconds: Long): String? {

         var time = milliSeconds
         if (time < 1000000000000L) {
             // if timestamp given in seconds, convert to millis
             time *= 1000
         }
         val date=Date(time)
         val  formatter = DateFormat.getLongDateFormat(context)
         val  dateString = formatter.format( date)
        return dateString
    }

    fun getDateAndTimeForTracking(context: Context,milliSeconds: Long): String? {

        var time = milliSeconds
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }
        val now: Long = Date().time
        if (time > now || time <= 0) {
            return null
        }

        // TODO: localize
        val diff = now - time
        return when {
            diff < Constants.MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * Constants.MINUTE_MILLIS -> {
                "an min"
            }
            diff < 50 * Constants.MINUTE_MILLIS -> {
                "${diff / Constants.MINUTE_MILLIS}min"
            }
            diff < 90 * Constants.MINUTE_MILLIS -> {
                "an hour"

            }
            diff < 24 * Constants.HOUR_MILLIS -> {
                "${diff / Constants.HOUR_MILLIS}hours"
            }
            diff < 48 * Constants.HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                "${diff / Constants.DAY_MILLIS}days"
            }
        }
    }
}
//
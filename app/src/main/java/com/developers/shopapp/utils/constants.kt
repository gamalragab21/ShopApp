package com.developers.shopapp.utils

import android.content.Context
import java.util.*


object Constants {

    const val BASE_URL="https://corona.lmao.ninja/v3/covid-19/"
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

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
        return if (diff < MINUTE_MILLIS) {
            "just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "a minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            " ${diff / MINUTE_MILLIS}  minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            " ${diff / HOUR_MILLIS} hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "yesterday"
        } else {
            "${diff / DAY_MILLIS } days ago"
        }
    }

    private var suffixes: NavigableMap<Long, String> = TreeMap()

            fun init() {
                suffixes[1_000L] = "k"
                suffixes[1_000_000L] = "M"
                suffixes[1_000_000_000L] = "G"
                suffixes[1_000_000_000_000L] = "T"
                suffixes[1_000_000_000_000_000L] = "P"
                suffixes[1_000_000_000_000_000_000L] = "E"
            }

    fun format(value: Long): String {
        init()
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1)
        if (value < 0) return "-" + format(-value)
        if (value < 1000) return java.lang.Long.toString(value) //deal with easy case
        val e = suffixes.floorEntry(value)
        val divideBy = e.key
        val suffix = e.value
        val truncated = value / (divideBy / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }

}
package com.mriofrio.rideapp.other

import android.Manifest.permission.*
import android.content.Context
import android.os.Build
import com.mriofrio.rideapp.other.Constants.EARTH_RADIUS
import com.vmadalin.easypermissions.EasyPermissions
import kotlin.math.*

object TrackingUtil {
    fun hasLocationPermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            ACCESS_FINE_LOCATION
        )


    fun distanceInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = lat1 * (PI / 180)
        val phi2 = lat2 * (PI / 180)
        val deltaPhi = (lat2 - lat1) * (PI / 180)
        val deltaLambda = (lon2 - lon1) * (PI / 180)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)

        val c = 2 * (atan2(sqrt(a), sqrt(1 - a)))

        return EARTH_RADIUS * c // distance in meters
    }

    fun metersToMiles(meters: Double): Double {
        val km = meters / 1000
        return km / 1.609
    }

    fun secondsToTime(sec: Int): String {
        val hours: Int = sec / 3600
        val minutes: Int = (sec % 3600) / 60
        val secs: Int = sec % 60
        return getTimeString(secs, minutes, hours)
    }

    private fun getTimeString(sec: Int, min: Int, hour: Int): String {
        val hourString = if (hour < 1) "" else "$hour:"
        val minString = if (min < 10) "0$min" else "$min"
        val secString = if (sec < 10) "0$sec" else "$sec"
        return "$hourString$minString:$secString"

    }
}
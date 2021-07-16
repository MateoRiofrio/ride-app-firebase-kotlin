package com.mriofrio.rideapp.other

import com.mriofrio.rideapp.R

object Constants {
    // Tracking Service Actions
    const val ACTION_ON_START_PAUSE_OR_RESUME_SERVICE = "ACTION_ON_START_PAUSE_OR_RESUME_SERVICE"
    const val ACTION_ON_STOP_SERVICE = "ACTION_ON_STOP_SERVICE"
    const val ACTION_ON_LAST_LOCATION = "ACTION_ON_LAST_LOCATION"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_ON_SAVE_RIDE = "ACTION_ON_SAVE_RIDE"

    // Location Request
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L
    const val REQUEST_CODE_LOCATION_PERMISSION = 0x1

    // Notification channel for foreground service
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    // Distance calculation
    const val EARTH_RADIUS = 6371e3 // meters

    //Map fragment
    const val POLYLINE_WIDTH = 16f
    const val POLYLINE_COLOR = R.color.purple_200
    const val MAP_ZOOM = 16f
}
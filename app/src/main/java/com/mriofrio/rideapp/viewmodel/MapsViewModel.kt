package com.mriofrio.rideapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mriofrio.rideapp.other.TrackingUtil
import com.mriofrio.rideapp.service.TrackingService

class MapsViewModel : ViewModel() {

    private val seconds: LiveData<Int>
        get() = TrackingService.seconds

    private val meters: LiveData<Double>
        get() = TrackingService.meters

    val distance: LiveData<Double>
        get() = Transformations.map(meters) { TrackingUtil.metersToMiles(it) }

    val path: LiveData<MutableList<LatLng>>
        get() = TrackingService.path

    val duration: LiveData<String>
        get() = Transformations.map(seconds) { TrackingUtil.secondsToTime(it) }

    val lastKnownLocation: LiveData<LatLng>
        get() = TrackingService.lastKnownLocation

    val startButtonText: LiveData<String>
        get() = TrackingService.startButtonText
}
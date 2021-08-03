package com.mriofrio.rideapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.mriofrio.rideapp.MainActivity
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.other.Constants.ACTION_ON_LAST_LOCATION
import com.mriofrio.rideapp.other.Constants.ACTION_ON_SAVE_RIDE
import com.mriofrio.rideapp.other.Constants.ACTION_ON_START_PAUSE_OR_RESUME_SERVICE
import com.mriofrio.rideapp.other.Constants.ACTION_ON_STOP_SERVICE
import com.mriofrio.rideapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.mriofrio.rideapp.other.Constants.FASTEST_LOCATION_INTERVAL
import com.mriofrio.rideapp.other.Constants.LOCATION_UPDATE_INTERVAL
import com.mriofrio.rideapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.mriofrio.rideapp.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.mriofrio.rideapp.other.Constants.NOTIFICATION_ID
import com.mriofrio.rideapp.other.TrackingUtil
import com.mriofrio.rideapp.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias Polyline = MutableList<LatLng>

class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var timer: Job
    private val db = FirebaseDatabaseRepository()

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val path = MutableLiveData<Polyline>()
        val lastKnownLocation = MutableLiveData<LatLng>()
        val meters = MutableLiveData<Double>()
        val seconds = MutableLiveData<Int>()
        val startButtonText = MutableLiveData<String>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitValues()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        isTracking.observe(this, Observer { trackLocation(it) })
        seconds.observe(this, Observer { updateNotificationTime(it) })
        path.observe(this, Observer { calculateDistance(it) })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ON_START_PAUSE_OR_RESUME_SERVICE -> {
                when (isTracking.value) {
                    // if we are not tracking, then it is either the first run or we are resuming the run
                    false -> {
                        if (isFirstRun) {
                            startTrackingService()
                        } else {
                            resumeTrackingService()
                        }
                    }
                    // if we are tracking, the we are simply pausing the run
                    true ->
                        pauseTrackingService()
                }
            }
            ACTION_ON_STOP_SERVICE -> {
                stopTrackingService()
            }
            ACTION_ON_LAST_LOCATION -> {
                updateLastKnownLocation()
            }
            ACTION_ON_SAVE_RIDE -> {
                saveRide()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun saveRide() {
        var time = seconds.value?.let { TrackingUtil.secondsToTime(it) }
        val dist = meters.value?.let { TrackingUtil.metersToMiles(it)}
        var date = "Unknown"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd"))
        }
        if (time == null) {
            time = "--:--"
        }
        db.saveRide(time, dist.toString(), date)

    }

    private fun postInitValues() {
        isTracking.postValue(false)
        path.postValue(mutableListOf())
        meters.postValue(0.0)
        seconds.postValue(0)
        startButtonText.postValue("Begin Ride")
        isFirstRun = true
    }

    private fun startTrackingService() {
        startButtonText.postValue("Pause")
        isTracking.postValue(true)
        isFirstRun = false
        createNotificationChannelAndNotification()
        startTimer()
    }

    private fun pauseTrackingService() {
        startButtonText.postValue("Resume")
        isTracking.postValue(false)
        stopTimer()
    }

    private fun resumeTrackingService() {
        startButtonText.postValue("Pause")
        isTracking.postValue(true)
        startTimer()
    }

    private fun stopTrackingService() {
        stopTimer()
        postInitValues()
        killService()
    }

    private fun killService() {
        stopForeground(true)
        stopSelf()
        deleteNotification()
    }

    private fun startTimer() {
        timer = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                seconds.postValue(seconds.value?.plus(1))
                delay(1000L)
            }
        }
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun calculateDistance(listOfPoints: MutableList<LatLng>?) {
        listOfPoints?.let {
            if (it.size >= 2) {
                meters.postValue(
                    TrackingUtil.distanceInMeters(
                        it.first().latitude, it.first().longitude,
                        it.last().latitude, it.last().longitude
                    )
                )
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPointToPath(location)
                    }
                }
            }
        }
    }

    private fun addPointToPath(location: Location?) {
        location?.let {
            val latLng = LatLng(location.latitude, location.longitude)
            path.value?.apply {
                add(latLng)
                path.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun trackLocation(isTracking: Boolean) {
        if (isTracking) {
            Log.d(
                "TrackingService",
                "hasLocationPermissions: $TrackingUtil.hasLocationPermissions(this)"
            )
            if (TrackingUtil.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.apply {
                lastKnownLocation.postValue(LatLng(location.latitude, location.longitude))
            }
        }

    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun createNotificationChannelAndNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)

            val pendingIntent: PendingIntent = getMainActivityPendingIntent()

            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getText(R.string.trackingservice_notification_title))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentText(getString(R.string.trackingservice_notification_message, "00:00"))
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentIntent(pendingIntent)
                .build()

            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotificationTime(newSecs: Int?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val pendingIntent: PendingIntent = getMainActivityPendingIntent()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getText(R.string.trackingservice_notification_title))
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText(
                getString(
                    R.string.trackingservice_notification_message,
                    TrackingUtil.secondsToTime(newSecs ?: 0)
                )
            )
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun deleteNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)
        }

    }
}
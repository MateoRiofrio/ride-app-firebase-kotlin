package com.mriofrio.rideapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mriofrio.rideapp.model.Ride
import com.mriofrio.rideapp.repository.FirebaseDatabaseRepository

typealias Rides = List<Ride>
class HistoryViewModel: ViewModel() {
    private var db = FirebaseDatabaseRepository()
    val userRides: LiveData<Rides>
        get() = db.userRides

    fun loadData() {
        db.fetchRides()
    }
}
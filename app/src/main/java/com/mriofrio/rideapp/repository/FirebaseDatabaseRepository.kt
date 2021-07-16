package com.mriofrio.rideapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mriofrio.rideapp.model.Ride

class FirebaseDatabaseRepository {
    private val db = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser
    private var _userRides = MutableLiveData<List<Ride>>()

    val userRides: LiveData<List<Ride>>
        get() = _userRides

    fun fetchRides() {
        user?.uid?.let { uid ->
            db.child("users").child(uid).child("rides").get().addOnSuccessListener {
                val tempList = mutableListOf<Ride>()
                for (child in it.children) {
                    child.getValue(Ride::class.java)?.let { it1 -> tempList.add(it1) }
                }
                createUserRides(tempList)
            }
        }
    }
    fun saveRide(time: String, distance:String, date:String) {
        user?.uid?.let { uid ->
            db.push().key?.let {
                val newRide = Ride(date, distance.toDouble(), time)
                db.child("users").child(uid).child("rides").child(it).setValue(newRide)
            }
        }
    }
    private fun createUserRides(list: List<Ride>) {
        _userRides.postValue(list)
    }
}
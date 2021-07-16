package com.mriofrio.rideapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.model.Ride
import com.mriofrio.rideapp.model.RideAdapter
import com.mriofrio.rideapp.viewmodel.HistoryViewModel


class HistoryFragment : Fragment() {


    private val viewModel = HistoryViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.loadData()
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userRides.observe(viewLifecycleOwner, Observer {
            view.findViewById<RecyclerView>(R.id.recyclerView).apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RideAdapter(it, requireContext())
            }
        })

    }
}


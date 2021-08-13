package com.mriofrio.rideapp.view

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.databinding.FragmentMapsBinding
import com.mriofrio.rideapp.other.Constants.ACTION_ON_LAST_LOCATION
import com.mriofrio.rideapp.other.Constants.ACTION_ON_SAVE_RIDE
import com.mriofrio.rideapp.other.Constants.ACTION_ON_START_PAUSE_OR_RESUME_SERVICE
import com.mriofrio.rideapp.other.Constants.ACTION_ON_STOP_SERVICE
import com.mriofrio.rideapp.other.Constants.MAP_ZOOM
import com.mriofrio.rideapp.other.Constants.POLYLINE_COLOR
import com.mriofrio.rideapp.other.Constants.POLYLINE_WIDTH
import com.mriofrio.rideapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.mriofrio.rideapp.other.TrackingUtil
import com.mriofrio.rideapp.service.TrackingService
import com.mriofrio.rideapp.viewmodel.MapsViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class MapsFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentMapsBinding
    private var map: GoogleMap? = null
    private val viewModel: MapsViewModel by viewModels()

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map?.isMyLocationEnabled = TrackingUtil.hasLocationPermissions(requireContext())
        sendServiceCommand(ACTION_ON_LAST_LOCATION)
        viewModel.lastKnownLocation.observe(viewLifecycleOwner, Observer { moveCameraToUser(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // request permissions if user is on maps fragment
        requestLocationPermission()

        // Set the viewModel for data binding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.viewmodel = viewModel
        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        binding.startRideButton.setOnClickListener { onStartRideButton() }
        binding.stopRideButton.setOnClickListener { onStopRideButton() }
    }

    private fun onStartRideButton() {
        // request location permissions if user tries to use tracking feature
        requestLocationPermission()
        setViewVisibility()
        subscribeToObservers()
        sendServiceCommand(ACTION_ON_START_PAUSE_OR_RESUME_SERVICE)
    }

    private fun onStopRideButton() {
        // pause the service
        sendServiceCommand(ACTION_ON_START_PAUSE_OR_RESUME_SERVICE)
        saveRide(requireContext()).show()
        binding.stopRideButton.visibility = View.GONE

    }

    private fun subscribeToObservers() {
        viewModel.path.observe(viewLifecycleOwner, Observer { drawPolylines(it) })
        viewModel.startButtonText.observe(viewLifecycleOwner, Observer { updateStartButton(it) })
    }

    private fun moveCameraToUser(currLatLng: LatLng) {
        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                currLatLng,
                MAP_ZOOM
            )
        )
    }

    private fun drawPolylines(path: MutableList<LatLng>) {
        if (path.size >= 2) {
            val polyline = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(path[path.size - 2])
                .add(path.last())
            map?.addPolyline(polyline)
            moveCameraToUser(path.last())
        }
    }


    private fun saveRide(context: Context) =
        AlertDialog.Builder(context).apply {
            setTitle("Save Ride")
            setMessage("Would you like to save this ride?")
            setPositiveButton("Save") { _, _ ->
                sendServiceCommand(ACTION_ON_SAVE_RIDE)
                sendServiceCommand(ACTION_ON_STOP_SERVICE)
            }
            setNegativeButton("No") { _, _ ->
                // stop run and reset values
                sendServiceCommand(ACTION_ON_STOP_SERVICE)
            }
        }

    private fun sendServiceCommand(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun requestLocationPermission() {
        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            return
        } else {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        setViewVisibility()
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        setViewVisibility()
    }

    private fun setViewVisibility() {
        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            binding.startRideButton.isEnabled = true
            binding.stopRideButton.visibility = View.VISIBLE
        } else {
            binding.startRideButton.isEnabled = false
            binding.stopRideButton.visibility = View.GONE
        }

    }

    private fun updateStartButton(state: String) {
        binding.startRideButton.text = state
    }
}
package com.mriofrio.rideapp.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.RecyclerView
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.other.TrackingUtil

class RideAdapter(
    private val rideList: List<Ride>,
    private val context: Context
    ) :
    RecyclerView.Adapter<RideAdapter.RideVideHolder>() {

    class RideVideHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.rideDate)
        val dist: TextView = itemView.findViewById(R.id.rideDistance)
        val dur: TextView = itemView.findViewById(R.id.rideTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideVideHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.ride_item, parent, false)

        return RideVideHolder(itemView)
    }

    override fun onBindViewHolder(holder: RideVideHolder, position: Int) {
        val currentItem = rideList[position]

        holder.date.text = context.resources.getString(R.string.historyfragment_date_title,currentItem.date)
        holder.dist.text = context.resources.getString(R.string.historyfragment_distance_title, currentItem.distance)
        holder.dur.text = context.resources.getString(R.string.historyfragment_time_title,currentItem.time)

    }

    override fun getItemCount() = rideList.size


}
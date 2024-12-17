package com.example.travelapp

// Import Statements
import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


class RouteAdapter(private val routes: MutableList<Route>) :
    RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]


        val fromLocationName = getLocationName(holder.itemView.context, route.from)
        val toLocationName = getLocationName(holder.itemView.context, route.to)

        holder.fromLocation.text = "From: $fromLocationName"
        holder.toLocation.text = "To: $toLocationName"

        holder.walkingTime.text = "Walking time: ${route.transportTimes["walking"]}"
        holder.drivingTime.text = "Driving time: ${route.transportTimes["driving"]}"
        holder.transitTime.text = "Transit time: ${route.transportTimes["transit"]}"
    }

    override fun getItemCount(): Int = routes.size


    fun addRoute(route: Route) {
        routes.add(route)
        notifyDataSetChanged()
    }


    private fun getLocationName(context: Context, latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addresses?.firstOrNull()
            address?.getAddressLine(0) ?: "Unknown Location"
        } catch (e: Exception) {
            "Unknown Location"
        }
    }

    class RouteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromLocation: TextView = view.findViewById(R.id.fromLocationTextView)
        val toLocation: TextView = view.findViewById(R.id.toLocationTextView)
        val walkingTime: TextView = view.findViewById(R.id.walkingTimeTextView)
        val drivingTime: TextView = view.findViewById(R.id.drivingTimeTextView)
        val transitTime: TextView = view.findViewById(R.id.transitTimeTextView)
    }
}


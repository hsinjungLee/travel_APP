package com.example.travelapp


import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import android.graphics.Color
import android.util.Log
import java.util.Calendar


class  RouteAdapter(private val routes: MutableList<Route>) :
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

        val savedTime = getSavedTimeForLocation(holder.itemView.context, route.to)


        val walkingTime = route.transportTimes["walking"] ?: "0 mins"
        val drivingTime = route.transportTimes["driving"] ?: "0 mins"
        val bicycleTime = route.transportTimes["bicycle"] ?: "0 mins"
        val motorcycleTime = route.transportTimes["motorcycle"] ?:"0 mins"

        holder.walkingTime.text = "Walking time: $walkingTime"
        holder.drivingTime.text = "Driving time: $drivingTime"
        holder.bicycleTime.text = "Bicycle time: $bicycleTime"
        holder.motorcycleTime.text = "Motorcycle: $motorcycleTime"


        val isWalkingOnTime = checkArrivalTime(savedTime, walkingTime)
        val isDrivingOnTime = checkArrivalTime(savedTime, drivingTime)
        val isbicycleOnTime = checkArrivalTime(savedTime, bicycleTime)
        val ismotorcycleOnTime = checkArrivalTime(savedTime, motorcycleTime)


        holder.walkingTime.setTextColor(if (isWalkingOnTime) Color.GREEN else Color.RED)
        holder.drivingTime.setTextColor(if (isDrivingOnTime) Color.GREEN else Color.RED)
        holder.bicycleTime.setTextColor(if (isbicycleOnTime) Color.GREEN else Color.RED)
        holder.motorcycleTime.setTextColor(if (ismotorcycleOnTime) Color.GREEN else Color.RED)

        Log.d("RouteAdapter", "Checking time - Walking: $isWalkingOnTime, Driving: $isDrivingOnTime, Bicycle: $isbicycleOnTime, Motorcycle: $ismotorcycleOnTime")

    }


    private fun getSavedTimeForLocation(context: Context, location: LatLng): String {
        val sharedPreferences = context.getSharedPreferences("maps_pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("saved_time_for_${location.latitude},${location.longitude}", "00:00") ?: "00:00"
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
        val bicycleTime: TextView = view.findViewById(R.id.bicycleTimeTextView)
        val motorcycleTime: TextView = view.findViewById(R.id.motorcycleTimeTextView)


    }

    private fun isTimeWithinLimit(transportTime: String, savedTime: String): Boolean {

        val transportMinutes = extractTimeInMinutes(transportTime)


        val parts = savedTime.split(":")
        val savedMinutes = (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)


        val currentTime = Calendar.getInstance()
        val currentMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)

        val remainingTime = savedMinutes - currentMinutes
        return transportMinutes <= remainingTime
    }


    private fun String.toMinutes(): Int {
        val parts = this.split(" ")
        var totalMinutes = 0


        if (parts.contains("hour") || parts.contains("hours")) {
            val hourIndex = parts.indexOfFirst { it.contains("hour") || it.contains("hours") }
            totalMinutes += (parts[hourIndex - 1].toIntOrNull() ?: 0) * 60
        }


        if (parts.contains("min") || parts.contains("minutes")) {
            val minIndex = parts.indexOfFirst { it.contains("min") || it.contains("minutes") }
            totalMinutes += parts[minIndex - 1].toIntOrNull() ?: 0
        }

        return totalMinutes
    }


    private fun extractTimeInMinutes(timeString: String): Int {
        return try {
            val regex = Regex("(\\d+)\\s*(hour|hours)?\\s*(\\d+)?\\s*(min|minutes)?")
            val match = regex.find(timeString)
            if (match != null) {
                val (hours, _, minutes, _) = match.destructured
                val totalMinutes = (hours.toIntOrNull() ?: 0) * 60 + (minutes.toIntOrNull() ?: 0)
                totalMinutes
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e("TimeParsingError", "Error parsing travel time: ${e.message}")
            0
        }
    }


    private fun checkArrivalTime(savedTime: String, travelTime: String): Boolean {
        try {

            val savedTimeParts = savedTime.split(":").mapNotNull { it.toIntOrNull() }
            if (savedTimeParts.size != 2) return false // 如果格式不正確，直接返回 false
            val savedMinutes = savedTimeParts[0] * 60 + savedTimeParts[1]


            val travelMinutes = extractTimeInMinutes(travelTime)


            val currentTime = Calendar.getInstance()
            val currentMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)


            val remainingMinutes = if (savedMinutes >= currentMinutes) {
                savedMinutes - currentMinutes
            } else {
                24 * 60 - currentMinutes + savedMinutes
            }


            return travelMinutes <= remainingMinutes
        } catch (e: Exception) {
            Log.e("CheckArrivalTime", "Error parsing times: ${e.message}")
            return false
        }



    }





}


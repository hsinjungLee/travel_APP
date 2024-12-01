package com.example.travelapp

// Import Statements
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class RouteAdapter(private val routes: List<Route>) :
    RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.fromLocation.text = "From: ${route.from.latitude}, ${route.from.longitude}"
        holder.toLocation.text = "To: ${route.to.latitude}, ${route.to.longitude}"

        // 在 item_route.xml 中的 TextView 设置交通时间
        holder.walkingTime.text = "Walking time: ${route.transportTimes["walking"]}"
        holder.drivingTime.text = "Driving time: ${route.transportTimes["driving"]}"
        holder.transitTime.text = "Transit time: ${route.transportTimes["transit"]}"
    }

    override fun getItemCount(): Int = routes.size

    class RouteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromLocation: TextView = view.findViewById(R.id.fromLocationTextView)
        val toLocation: TextView = view.findViewById(R.id.toLocationTextView)
        val walkingTime: TextView = view.findViewById(R.id.walkingTimeTextView)
        val drivingTime: TextView = view.findViewById(R.id.drivingTimeTextView)
        val transitTime: TextView = view.findViewById(R.id.transitTimeTextView)
    }
}

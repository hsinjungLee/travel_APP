package com.example.travelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import com.android.volley.Request
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import kotlin.collections.*

class ShowRouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_route)

        val savedLocations = intent.getStringArrayListExtra("saved_locations") ?: arrayListOf()
        if (savedLocations.size < 2) {
            Toast.makeText(this, "Not enough locations to display routes", Toast.LENGTH_SHORT).show()
            return
        }

        val locations = savedLocations.map {
            val parts = it.split(",")
            LatLng(parts[0].toDouble(), parts[1].toDouble())
        }

        val recyclerView: RecyclerView = findViewById(R.id.routeRecyclerView)
        val routeAdapter = RouteAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = routeAdapter

        for (i in 0 until locations.size - 1) {
            val origin = locations[i]
            val destination = locations[i + 1]
            DirectionsApiRequest(
                origin,
                destination,
                { route ->
                    routeAdapter.addRoute(route)
                },
                { error ->
                    Log.e("Directions", error)
                    Toast.makeText(this, "Failed to get directions for route $origin -> $destination", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }



    private fun DirectionsApiRequest(
        origin: LatLng,
        destination: LatLng,
        onComplete: (Route) -> Unit,
        onError: (String) -> Unit
    ) {

        val modes = listOf("driving", "walking", "bicycling", "motorcycle")
        val times = mutableMapOf<String, String>()

        var requestsCompleted = 0

        modes.forEach { mode ->
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&mode=$mode" +
                    "&key=AIzaSyD3xV0G2iyvAnkc02FlTH92q11xx9GcjPQ"

            Log.d("RequestURL", url)

            val request = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    try {
                        val modeTime = parseDurationFromResponse(response)
                        times[mode] = modeTime
                    } catch (e: Exception) {
                        times[mode] = "N/A"
                    }

                    requestsCompleted++
                    if (requestsCompleted == modes.size) {
                        val route = Route(origin, destination, times)
                        onComplete(route)
                    }
                },
                { error ->
                    times[mode] = "N/A"
                    requestsCompleted++
                    if (requestsCompleted == modes.size) {
                        val route = Route(origin, destination, times)
                        onComplete(route)
                    }
                }
            )
            Volley.newRequestQueue(this).add(request)
        }
    }

    private fun parseDurationFromResponse(response: JSONObject): String {
        return try {
            val routes = response.optJSONArray("routes")
            if (routes == null || routes.length() == 0) return "No routes available"

            val route = routes.getJSONObject(0)
            val legs = route.optJSONArray("legs")
            if (legs == null || legs.length() == 0) return "No legs available"

            val leg = legs.getJSONObject(0)
            val duration = leg.optJSONObject("duration")
            duration?.getString("text") ?: "Duration not found"
        } catch (e: JSONException) {
            Log.e("ParseError", "Error parsing JSON: ${e.message}")
            "Parsing error"
        }
    }

}

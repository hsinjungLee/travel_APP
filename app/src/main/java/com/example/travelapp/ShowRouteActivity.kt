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
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving" +
                "&departure_time=now" +
                "&traffic_model=best_guess" +
                "&key=AIzaSyD3xV0G2iyvAnkc02FlTH92q11xx9GcjPQ"


        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val times = parseDirectionsResponse(response)
                    val route = Route(origin, destination,times)
                    onComplete(route)
                } catch (e: Exception) {
                    onError("Error parsing response: ${e.message}")
                }
            },
            { error ->
                onError("Request failed: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun parseDirectionsResponse(response: JSONObject): Map<String, String> {
        val result = mutableMapOf<String, String>()

        try {
            val routes = response.getJSONArray("routes")
            if (routes.length() == 0) {
                throw JSONException("No routes found")
            }

            val route = routes.getJSONObject(0)
            val legs = route.getJSONArray("legs")
            if (legs.length() == 0) {
                throw JSONException("No legs found")
            }

            val leg = legs.getJSONObject(0)
            result["walking"] = leg.getJSONObject("duration")?.getString("text") ?: "N/A"
            result["driving"] = leg.getJSONObject("duration_in_traffic")?.getString("text")
                ?: leg.getJSONObject("duration")?.getString("text") ?: "N/A"
            result["transit"] = "Transit unavailable"

        } catch (e: JSONException) {
            throw JSONException("Error parsing response: ${e.message}")
        }

        return result
    }




    /*private fun updateUIWithRouteInfo(route: Route) {
        val recyclerView: RecyclerView = findViewById(R.id.routeRecyclerView)
        val routeAdapter = RouteAdapter(listOf(route))
        recyclerView.adapter = routeAdapter
    }


     */

}

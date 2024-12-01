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
import kotlin.collections.*

class ShowRouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_route)
        val recyclerView: RecyclerView = findViewById(R.id.routeRecyclerView)


        val origin = LatLng(40.748817, -73.985428)  // Example origin
        val destination = LatLng(40.730610, -73.935242)  // Example destination

        // 呼叫 DirectionsApiRequest 函數來獲取交通資訊
        DirectionsApiRequest(
            origin,
            destination,
            { route ->
                // 更新UI顯示交通時間資訊
                updateUIWithRouteInfo(route)
            },
            { error ->
                Log.e("Directions", error)
                Toast.makeText(this, "Failed to get directions", Toast.LENGTH_SHORT).show()
            }
        )


        val routeAdapter = RouteAdapter(listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = routeAdapter

    }

    // DirectionsApiRequest 函數，發送 API 請求
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

    fun parseDirectionsResponse(response: JSONObject): Map<String, String> {
        val result = mutableMapOf<String, String>()

        // 解析 API 回應中的路徑信息
        val routes = response.getJSONArray("routes")
        val route = routes.getJSONObject(0)
        val legs = route.getJSONArray("legs")
        val leg = legs.getJSONObject(0)

        // 獲取行駛時間等信息
        val durationWalking = leg.getJSONObject("duration")?.getString("text") ?: "N/A"
        val durationDriving = leg.getJSONObject("duration_in_traffic")?.getString("text")
            ?: leg.getJSONObject("duration")?.getString("text") ?: "N/A"

        val stepsArray = leg.getJSONArray("steps")
        val stepList = mutableListOf<String>()
        for (i in 0 until stepsArray.length()) {
            stepList.add(stepsArray.getJSONObject(i).toString())
        }

        val durationTransit = stepList.joinToString { it }


        // 將各種時間添加到 Map 中
        result["walking"] = durationWalking
        result["driving"] = durationDriving
        result["transit"] = durationTransit

        return result
    }



    private fun updateUIWithRouteInfo(route: Route) {
        // 假設這裡更新顯示 UI
        val walkingTimeTextView: TextView = findViewById(R.id.walkingTimeTextView)
        val drivingTimeTextView: TextView = findViewById(R.id.drivingTimeTextView)
        val transitTimeTextView: TextView = findViewById(R.id.transitTimeTextView)

        // 確保使用正確的屬性名稱
        walkingTimeTextView.text = "Walking time: ${route.transportTimes["walking"]}"
        drivingTimeTextView.text = "Driving time: ${route.transportTimes["driving"]}"
        transitTimeTextView.text = "Transit time: ${route.transportTimes["transit"]}"
    }

}

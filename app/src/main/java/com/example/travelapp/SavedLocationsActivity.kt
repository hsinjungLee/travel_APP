package com.example.travelapp


import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.location.Geocoder
import android.widget.Button
import java.util.Locale
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class SavedLocationsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var savedLocations: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations)

        listView = findViewById(R.id.savedLocationsList)
        savedLocations = loadSavedLocations()


        val data = savedLocations.map {
            val locationName = getReadableLocationName(it)
            val time = getSavedTimeForLocation(it)
            mapOf("locationName" to locationName, "time" to time, "coordinates" to it)
        }.sortedBy { it["time"] }


        val adapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("locationName", "time"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listView.adapter = adapter


        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLocation = data[position]["coordinates"]
            Toast.makeText(this, "Selected: ${data[position]["locationName"]}", Toast.LENGTH_SHORT)
                .show()

            val intent = Intent(this, LocationDetailActivity::class.java)
            intent.putExtra("location_coordinates", selectedLocation)
            intent.putExtra("location_name", data[position]["locationName"])
            startActivity(intent)
        }

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val showRoutesButton: Button = findViewById(R.id.showRoutesButton)

        showRoutesButton.setOnClickListener {
            if (savedLocations.size < 2) {
                Toast.makeText(this, "Not enough locations to show routes", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ShowRouteActivity::class.java)
                intent.putStringArrayListExtra("saved_locations", ArrayList(savedLocations))
                startActivity(intent)
            }
        }


    }




    private fun loadSavedLocations(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
        val savedLocationsSet =
            sharedPreferences.getStringSet("saved_locations", mutableSetOf()) ?: mutableSetOf()

        return savedLocationsSet.toMutableList()
    }


    private fun getReadableLocationName(location: String): String {
        val parts = location.split(",")
        return if (parts.size == 2) {
            try {
                val lat = parts[0].toDouble()
                val lng = parts[1].toDouble()
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
            } catch (e: Exception) {
                "Invalid Location"
            }
        } else {
            "Unknown Location"
        }
    }

    private fun getSavedTimeForLocation(location: String): String {
        val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
        return sharedPreferences.getString("saved_time_for_$location", "00:00")
            ?: "24:59"
    }


}

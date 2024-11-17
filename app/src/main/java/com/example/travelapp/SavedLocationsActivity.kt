package com.example.travelapp


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import android.location.Geocoder
import java.util.Locale

class SavedLocationsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var savedLocations: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations)

        listView = findViewById(R.id.savedLocationsList)

        savedLocations = loadSavedLocations()

        // 使用地點名稱顯示
        val locationNames = savedLocations.map { getReadableLocationName(it) }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationNames)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLocation = savedLocations[position]
            Toast.makeText(this, "Selected: ${locationNames[position]}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LocationDetailActivity::class.java)
            intent.putExtra("location_coordinates", selectedLocation)
            startActivity(intent)
        }
    }

    private fun loadSavedLocations(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
        val savedLocationsSet = sharedPreferences.getStringSet("saved_locations", mutableSetOf()) ?: mutableSetOf()

        return savedLocationsSet.toMutableList()
    }

    private fun getReadableLocationName(location: String): String {
        val parts = location.split(",") // 假設格式為 "lat,lng"
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
}




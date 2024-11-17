package com.example.travelapp

import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class LocationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_detail)

        // 從 Intent 獲取經緯度資料（假設格式為 "latitude,longitude"）
        val locationCoordinates = intent.getStringExtra("location_coordinates") ?: "Unknown"
        val locationTextView: TextView = findViewById(R.id.locationNameTextView)

        if (locationCoordinates != "Unknown") {
            val parts = locationCoordinates.split(",")
            if (parts.size == 2) {
                val latitude = parts[0].toDoubleOrNull()
                val longitude = parts[1].toDoubleOrNull()

                if (latitude != null && longitude != null) {
                    val readableName = getReadableLocationName(latitude, longitude)
                    locationTextView.text = readableName
                } else {
                    locationTextView.text = "Invalid Coordinates"
                }
            } else {
                locationTextView.text = "Invalid Format"
            }
        } else {
            locationTextView.text = "No Coordinates Provided"
        }
    }

    private fun getReadableLocationName(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to fetch location name", Toast.LENGTH_SHORT).show()
            "Unknown Location"
        }
    }
}

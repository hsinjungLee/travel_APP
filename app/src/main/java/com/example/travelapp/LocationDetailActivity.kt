package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity

class LocationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_detail)

        val locationCoordinates = intent.getStringExtra("location_coordinates") ?: "Unknown"
        val locationName = intent.getStringExtra("location_name") ?: "Location Name"

        val locationTextView: TextView = findViewById(R.id.locationNameTextView)

        locationTextView.text = locationName

        val timePicker: TimePicker = findViewById(R.id.timePicker)
        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val time = String.format("%02d:%02d", hour, minute)

            val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("saved_time_for_${locationCoordinates}", time)
            editor.apply()

            val intent = Intent(this, SavedLocationsActivity::class.java)
            startActivity(intent)
        }
    }
}

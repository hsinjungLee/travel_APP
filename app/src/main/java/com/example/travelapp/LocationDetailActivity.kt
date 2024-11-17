package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class LocationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_detail)

        // 获取传递的地点坐标和名称
        val locationCoordinates = intent.getStringExtra("location_coordinates") ?: "Unknown"
        val locationName = intent.getStringExtra("location_name") ?: "Location Name"

        val locationTextView: TextView = findViewById(R.id.locationNameTextView)
        locationTextView.text = locationName

        val timePicker: TimePicker = findViewById(R.id.timePicker)
        val saveButton: Button = findViewById(R.id.saveButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)


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


        deleteButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
            val editor = sharedPreferences.edit()


            editor.remove("saved_time_for_${locationCoordinates}")
            val savedLocationsSet = sharedPreferences.getStringSet("saved_locations", mutableSetOf()) ?: mutableSetOf()
            savedLocationsSet.remove(locationCoordinates)
            editor.putStringSet("saved_locations", savedLocationsSet)
            editor.apply()


            Toast.makeText(this, "Location Deleted", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, SavedLocationsActivity::class.java)
            startActivity(intent)
        }
    }
}

package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView


class SavedLocationsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var savedLocations: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations)

        listView = findViewById(R.id.savedLocationsList)

        savedLocations = loadSavedLocations()


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, savedLocations)
        listView.adapter = adapter
    }

    private fun loadSavedLocations(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
        val savedLocationsSet = sharedPreferences.getStringSet("saved_locations", mutableSetOf()) ?: mutableSetOf()

        return savedLocationsSet.map {
            val parts = it.split("|")
            if (parts.isNotEmpty()) {
                parts[0] 
            } else {
                "Unknown Location"
            }
        }.toMutableList()
    }
}


package com.example.travelapp

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var searchView: SearchView
    private var lastSearchedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        searchView = findViewById(R.id.searchView)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    showLocationOnMap(it)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        findViewById<Button>(R.id.finishButton).setOnClickListener {
            val intent = Intent(this, SavedLocationsActivity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.SaveLocationButton).setOnClickListener {
            lastSearchedLocation?.let {
                saveLocation(it)
                Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "No location to save", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val locationName = intent.getStringExtra("location_name")
        if (!locationName.isNullOrEmpty()) {
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocationName(locationName, 1)
                if (addresses!!.isNotEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)

                    mMap.addMarker(MarkerOptions().position(latLng).title(locationName))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error finding location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLocationOnMap(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList!!.isNotEmpty()) {
                val location = addressList[0]
                val latLng = LatLng(location.latitude, location.longitude)
                lastSearchedLocation = latLng  // Store the last searched location


                mMap.clear()


                mMap.addMarker(MarkerOptions().position(latLng).title(address))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveLocation(location: LatLng) {
        val sharedPreferences = getSharedPreferences("maps_pref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val savedLocations = sharedPreferences.getStringSet("saved_locations", mutableSetOf()) ?: mutableSetOf()
        savedLocations.add("${location.latitude},${location.longitude}")


        editor.putStringSet("saved_locations", savedLocations)
        editor.apply()
    }
}


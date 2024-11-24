package com.example.travelapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationEditText : EditText = findViewById(R.id.location)

        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val locationName = locationEditText.text.toString()

            if(locationName.isNotBlank()){
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("location_name", locationName)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Please enter a location name", Toast.LENGTH_SHORT).show()
            }
            }


        }
    }



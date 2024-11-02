package com.example.travelapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val finishButton2: Button = findViewById(R.id.finishButton2)
        finishButton2.setOnClickListener {
            val intent = Intent(this, transportationActivity::class.java)
            startActivity(intent)
        }
    }
}
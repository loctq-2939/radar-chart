package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val radar = findViewById<MyCanvasView>(R.id.radar)
        radar.setLabel(
            "味",
            "見た目",
            "接客",
            "居心地",
            "価格"
        )
        radar.setData(listOf(1f, 2f, 3.5f, 4f, 5f))
    }
}
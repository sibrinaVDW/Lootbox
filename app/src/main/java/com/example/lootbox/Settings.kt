package com.example.lootbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

class Settings : AppCompatActivity() {
    var data = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val intent = intent
        data = intent.getStringExtra("user").toString()

        var btnSettings : ImageButton = findViewById(R.id.btnSettings2)
        btnSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Settings,Settings::class.java).apply{}
                startActivity(intent)
            }
        })

        var btnGoals : ImageButton = findViewById(R.id.btnGoals2)
        btnGoals.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Settings,Goals::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var btnProfile : ImageButton = findViewById(R.id.btnProfile3)
        btnProfile.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Settings,Profile::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var btnHome : ImageButton = findViewById(R.id.btnHome2)
        btnHome.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Settings,CollectionsView::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })
    }
}
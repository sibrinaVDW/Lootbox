package com.example.lootbox

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ClickedItemActivity : AppCompatActivity() {
    private var data = " "
    private var itemName : String? = ""
    private var catFrom : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clicked_item)

        val intent = intent
        data = intent.getStringExtra("user").toString()
        itemName = intent.getStringExtra("Item")
        catFrom = intent.getStringExtra("Category")

        var btnProfile : ImageButton = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@ClickedItemActivity,Profile::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection(data).document("categories").collection(catFrom!!)
            .document("items").collection(itemName!!).document("itemInfo")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    val title = document.getString("tilte") as String
                    var desc = document.getString("description")as String
                    var image = document.getLong("image")!!.toInt()
                    var accuiredDate = "Date Accuired : " + document.getString("date")as String
                    var publisher = "Publisher : " + document.getString("publisher")as String
                    var releasedDate = "Date Released : " + document.getString("release date")as String

                    val titleDisp : TextView = findViewById(R.id.txtItemName)
                    titleDisp.text = title

                    val descDisp : TextView = findViewById(R.id.lblGameDescription)
                    descDisp.text = desc

                    val imgDisp : ImageView = findViewById(R.id.imgGameCoverDisplay)
                    imgDisp.setImageResource(image)

                    val accDateDisp : TextView = findViewById(R.id.lblDateCollected)
                    accDateDisp.text = accuiredDate

                    val publisherDisp : TextView = findViewById(R.id.lblPublisher)
                    publisherDisp.text = publisher

                    val relDateDisp : TextView = findViewById(R.id.lblReleaseDate)
                    relDateDisp.text = releasedDate

                    val backImg : ImageView = findViewById(R.id.imgCoverImage)
                    backImg.setImageResource(image)

                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }
    }
}
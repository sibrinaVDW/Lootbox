package com.example.lootbox

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
//import com.google.firebase.firestore.FirebaseFirestore

private const val REQUEST_CODE = 42
//private lateinit var filePhoto: File
//private const val FILE_NAME = "photo.jpg"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val db = FirebaseFirestore.getInstance()
        val takeAPicture  = findViewById<Button>(R.id.btnTakePicture)
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ID

        takeAPicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val viewImage = findViewById<ImageView>(R.id.imageView01)
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ID
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            viewImage.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, requestCode, data)
        }
    }
}
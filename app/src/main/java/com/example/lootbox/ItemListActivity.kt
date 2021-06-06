package com.example.lootbox

import android.app.Activity
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog.Builder
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
private const val REQUEST_CODE = 42
class ItemListActivity : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var descriptionList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)


        var rcvItemList : RecyclerView = findViewById(R.id.rcvItemList)
        rcvItemList.layoutManager = LinearLayoutManager(this)
        rcvItemList.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList)

//for taking a picture
        val takeAPicture = findViewById<ImageButton>(R.id.imgGameImage)
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ID

        takeAPicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_LONG).show()
            }
        }

        val btnAdd : Button = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v:View){
                val diagPopUp = LayoutInflater.from(this@ItemListActivity).inflate(R.layout.itempopup,null)
                val alertBuilder = AlertDialog.Builder(this@ItemListActivity).setView(diagPopUp).setTitle("Add Game")
                val alertDialog = alertBuilder.show()

                var add : Button = diagPopUp.findViewById(R.id.btnAddGameToList)

                add.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v : View){
                        val gameName = diagPopUp.findViewById<EditText>(R.id.txtEnterGameName).text.toString()
                        val gameDescription = diagPopUp.findViewById<EditText>(R.id.txtEnterGameDescription).text.toString()
                        addToList(gameName,gameDescription,R.mipmap.ic_launcher_round)
                        alertDialog.dismiss()

                    }
                })



            }
        })


    }
    public fun addToList(title: String, description: String, image:Int){
        titlesList.add(title)
        descriptionList.add(description)
        imageList.add(image)
    }
    public fun postToItemList(gameName : String, gameDescription: String, gameImage : Int){

        val gameNameTextView : TextView = findViewById(R.id.txtGameName)
        var gameName : String = gameNameTextView.text.toString()
        for(i:Int in 1..3){
         //   addToList(gameName,gameDescription,gameImage)
            addToList("Game Name $i","Description $i",R.mipmap.ic_launcher_round)
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
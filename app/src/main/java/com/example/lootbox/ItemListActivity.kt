package com.example.lootbox

import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog.Builder
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog

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


        val btnAdd : ImageButton = findViewById(R.id.imgAdd)
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

}
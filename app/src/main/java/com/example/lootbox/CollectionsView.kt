package com.example.lootbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CollectionsView : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf< String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections_view)
        postToList()

        var recView :RecyclerView = findViewById(R.id.rcvCategoryList)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList)
    }

    private fun addToList(title: String, desc: String , image: Int){
        titlesList.add(title)
        descList.add(desc)
        imagesList.add(image)
    }

    private fun postToList(){
        for (i in 1..25){
            addToList("Title$i", "Description $i", R.mipmap.ic_launcher)
        }
    }
}
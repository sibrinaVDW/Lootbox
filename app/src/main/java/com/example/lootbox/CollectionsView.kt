package com.example.lootbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CollectionsView : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf< String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections_view)
        //postToList()

        var recView :RecyclerView = findViewById(R.id.rcvCategoryList)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList)

        registerForContextMenu(recView)

        addToList("Playstation","All my Playstation bois",R.drawable.launcher_icon)
        addToList("Xbox","All my Xbox bois",R.drawable.launcher_icon)

        var showPopUp : Button = findViewById<Button>(R.id.btnAdd)

        showPopUp.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val diagView = LayoutInflater.from(this@CollectionsView).inflate(R.layout.new_cat_popup,null)
                val alertBuild = AlertDialog.Builder(this@CollectionsView).setView(diagView).setTitle("Create category")
                val alertDiag = alertBuild.show()

                var create : Button = diagView.findViewById<Button>(R.id.btnCreate)
                create.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                            val catName = diagView.findViewById<EditText>(R.id.edtCatName).text.toString()
                            val catDesc = diagView.findViewById<EditText>(R.id.edtCatDesc).text.toString()
                            alertDiag.dismiss()
                            addToList(catName,catDesc,R.drawable.launcher_icon)
                        }
                })

                var cancel : Button = diagView.findViewById<Button>(R.id.btnCancel)
                cancel.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        alertDiag.dismiss()
                    }
                })
            }})
    }


    private fun addToList(title: String, desc: String , image: Int){
        titlesList.add(title)
        descList.add(desc)
        imagesList.add(image)
    }

    private fun postToList(){
        for (i in 1..3){
            addToList("Title$i", "Description $i", R.drawable.launcher_icon)
        }
    }
}
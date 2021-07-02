package com.example.lootbox

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


private const val REQUEST_CODE = 42
class CollectionsView : AppCompatActivity() {
    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf< String>()
    private var imagesList = mutableListOf<Int>()
    private var goalList = mutableListOf<String>()

    var viewImage: ImageView? = null


    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections_view)

        var recView :RecyclerView = findViewById(R.id.rcvCategoryList)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList,goalList)

        addToList("Playstation","All my Playstation bois",R.drawable.launcher_icon,"Goal: 10")
        addToList("Xbox","All my Xbox bois",R.drawable.launcher_icon, "Goal: 5")

        var btnSettings : ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@CollectionsView,Settings::class.java).apply{}
                startActivity(intent)
            }
        })

        var showPopUp : ImageButton = findViewById<ImageButton>(R.id.btnAdd)

        showPopUp.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val diagView = LayoutInflater.from(this@CollectionsView).inflate(R.layout.new_cat_popup,null)
                val alertBuild = AlertDialog.Builder(this@CollectionsView).setView(diagView).setTitle("Create category")
                val alertDiag = alertBuild.show()

                var captureImg: ImageView = diagView.findViewById<ImageView>(R.id.imgThumb)
                captureImg.setOnClickListener {
                    viewImage = diagView.findViewById<ImageView>(R.id.imgThumb)

                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }

                var create : ImageButton = diagView.findViewById<ImageButton>(R.id.btnCreate)
                create.setOnClickListener(object : View.OnClickListener {
                    @RequiresApi(Build.VERSION_CODES.P)
                    override fun onClick(v: View?) {
                        val catName = diagView.findViewById<EditText>(R.id.edtCatName).text.toString()
                        val catDesc = diagView.findViewById<EditText>(R.id.edtCatDesc).text.toString()
                        addToList(catName,catDesc,R.drawable.launcher_icon,"Goal: 0")
                        recView.layoutManager = LinearLayoutManager(this@CollectionsView)
                        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList,goalList)
                        alertDiag.dismiss()
                    }
                })

                var cancel : ImageButton = diagView.findViewById<ImageButton>(R.id.btnCancel)
                cancel.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        alertDiag.dismiss()
                    }
                })
            }})
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            viewImage?.setImageURI(imageUri)
        }
    }

    private fun addToList(title: String, desc: String , image: Int, goal : String){
        titlesList.add(title)
        descList.add(desc)
        imagesList.add(image)
        goalList.add(goal)
    }


}
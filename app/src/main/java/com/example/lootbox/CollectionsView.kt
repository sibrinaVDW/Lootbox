package com.example.lootbox

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toIcon
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


private const val REQUEST_CODE = 42
class CollectionsView : AppCompatActivity() {
    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf< String>()
    private var imagesList = mutableListOf<Int>()
    private var goalList = mutableListOf<Int>()
    var viewImage: ImageView? = null
    var bitmap: Bitmap? = null

    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections_view)
        //postToList()

        var recView :RecyclerView = findViewById(R.id.rcvCategoryList)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList,goalList)


        addToList("Playstation","All my Playstation bois",R.drawable.launcher_icon,10)
        addToList("Xbox","All my Xbox bois",R.drawable.launcher_icon,10)

        var showPopUp : Button = findViewById<Button>(R.id.btnAdd)

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
                    captureImg.buildDrawingCache()
                    bitmap = captureImg.getDrawingCache()

                    /*val intent = Intent(this, NewActivity::class.java)
                    intent.putExtra("BitmapImage", bitmap)*/

                    /*val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    viewImage  = diagView.findViewById<ImageView>(R.id.imgThumb)
                    if (takePictureIntent.resolveActivity(this@CollectionsView.packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_CODE)
                    } else {
                        Toast.makeText(this@CollectionsView, "Unable to open camera", Toast.LENGTH_LONG).show()
                    }*/
                }

                var create : Button = diagView.findViewById<Button>(R.id.btnCreate)
                create.setOnClickListener(object : View.OnClickListener {
                    @RequiresApi(Build.VERSION_CODES.P)
                    override fun onClick(v: View?) {
                        val catName = diagView.findViewById<EditText>(R.id.edtCatName).text.toString()
                        val catDesc = diagView.findViewById<EditText>(R.id.edtCatDesc).text.toString()
                        //val catImg = diagView.findViewById<ImageView>(R.id.imgThumb).imageAlpha
                        //val catImage : Int = viewImage?.drawable!!.alpha
                        //val image = (catImg.getDrawable() as BitmapDrawable).bitmap
                        //addToList(catName,catDesc,bitmap?.toIcon()!!.resId,0)
                        alertDiag.dismiss()
                        //addToList(catName,catDesc,catImg,0)
                        //addToList(catName,catDesc,image = viewImage!!.)
                        //addToList(catName,catDesc,viewImage!!.imageAlpha)

                        addToList(catName,catDesc,R.drawable.launcher_icon,0)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            viewImage?.setImageURI(imageUri)
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ID

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val takenImage = data?.extras?.get("data") as Bitmap
            viewImage?.setImageBitmap(takenImage)
        } else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }*/


    private fun addToList(title: String, desc: String , image: Int, goal:Int){
        titlesList.add(title)
        descList.add(desc)
        imagesList.add(image)
        goalList.add(goal)
    }

    private fun postToList(){
        for (i in 1..3){
            addToList("Title$i", "Description $i", R.drawable.launcher_icon,0)
        }
    }
}
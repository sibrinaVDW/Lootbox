package com.example.lootbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import java.lang.Integer.parseInt

public class Goal_RecAdapter (private var title: List<String>, private var details: List<String>, private var images:List<Int>, private var status : List<String>) :
    RecyclerView.Adapter<Goal_RecAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        public val itemTitle: TextView = itemView.findViewById(R.id.txtTitle)
        public val itemDesc: TextView = itemView.findViewById(R.id.txtDescription)
        public val itemThumbnail : ImageView = itemView.findViewById(R.id.imgThumbnail)
        public val itemStatusDisp : TextView = itemView.findViewById(R.id.txtDate)

        val dbItems = FirebaseFirestore.getInstance()
        init {

        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_list_item,parent,false)
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.itemDesc.text = details[position]
        holder.itemThumbnail.setImageResource(images[position])
        //Picasso.get().load(images[position]).into(holder.itemThumbnail)
        //holder.itemThumbnail.setImageURI(images[position])
        holder.itemStatusDisp.text = status[position]
    }

    override fun getItemCount(): Int {
        return title.size
    }
}
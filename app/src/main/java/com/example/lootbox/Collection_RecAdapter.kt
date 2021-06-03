package com.example.lootbox

import android.media.Image
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView

class Collection_RecAdapter(private var title: List<String>, private var details: List<String>, private var images:List<Int>) :
RecyclerView.Adapter<Collection_RecAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val itemDesc: TextView = itemView.findViewById(R.id.txtDescription)
        val itemThumbnail : ImageView = itemView.findViewById(R.id.imgThumbnail)

        init {
            itemView.setOnClickListener{v:View ->
                val pos: Int = adapterPosition
                Toast.makeText(itemView.context,"You clicked on item number ${pos+1}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row,parent,false)
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.itemDesc.text = details[position]
        holder.itemThumbnail.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return title.size
    }
}
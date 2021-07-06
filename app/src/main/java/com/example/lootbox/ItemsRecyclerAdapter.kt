package com.example.lootbox

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ItemsRecyclerAdapter(
    private var gameTitles: List<String>,
    private var gameDescriptions: List<String>,
    private var gameImages: List<Int>,
    private var gameDates : List<String>, private var catPass : String,private var dbData : String
) :
    RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val itemDescription: TextView = itemView.findViewById(R.id.txtDate)
        val itemPicture: ImageView = itemView.findViewById(R.id.imgThumbnail)
        val itemDate : TextView = itemView.findViewById(R.id.txtDate)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                //open clicked item activity
                val intent = Intent(v?.context,ClickedItemActivity::class.java).apply{}
                intent.putExtra("Item", itemTitle.text)
                intent.putExtra("Category", catPass)
                intent.putExtra("user", dbData)
                v?.context.startActivity(intent)
                Toast.makeText(itemView.context, "You clicked on item # ${position + 1}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return gameTitles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = gameTitles[position]
        holder.itemDescription.text = gameDescriptions[position]
        holder.itemPicture.setImageResource(gameImages[position])
        holder.itemDate.text = gameDates[position]
    }

}
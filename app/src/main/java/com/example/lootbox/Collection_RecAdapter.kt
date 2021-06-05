package com.example.lootbox

import android.view.*
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView


class Collection_RecAdapter(private var title: List<String>, private var details: List<String>, private var images:List<Int>) :
RecyclerView.Adapter<Collection_RecAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        val itemTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val itemDesc: TextView = itemView.findViewById(R.id.txtDescription)
        val itemThumbnail : ImageView = itemView.findViewById(R.id.imgThumbnail)
        var itemGoal : Int = 0
        //store in list, view list
        init {
            itemView.setOnClickListener{v:View ->
                val pos: Int = adapterPosition
                Toast.makeText(itemView.context,"You clicked on item number ${pos+1}",Toast.LENGTH_SHORT).show()
            }


            itemView.setOnLongClickListener(
                object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        val pos: Int = adapterPosition
                        val popupMenu = PopupMenu(v?.context, v)
                        popupMenu.inflate(R.menu.add_goal_contextmenu)
                        popupMenu.setOnMenuItemClickListener(this@ViewHolder)
                        popupMenu.show()
                        return true
                    }
                },
            )
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.btnGoalAdd -> {
                            val diagView = LayoutInflater.from(this@ViewHolder.itemView.context).inflate(R.layout.new_goal_pop,null)
                            val alertBuild = AlertDialog.Builder(this@ViewHolder.itemView.context).setView(diagView).setTitle("Create category")
                            val alertDiag = alertBuild.show()

                            var create : Button = diagView.findViewById<Button>(R.id.btnGoalCreate)
                            create.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View?) {
                                    val goalNum = diagView.findViewById<EditText>(R.id.edtGoalNum).text

                                    itemDesc.text = "You need to create $goalNum items for this category"
                                    alertDiag.dismiss()
                                }
                            })

                            var cancel : Button = diagView.findViewById<Button>(R.id.btnGoalCancel)
                            cancel.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View?) {
                                    alertDiag.dismiss()
                                }
                            })
                    true
                }
                R.id.btnEdit -> {
                    Toast.makeText(itemView.context, "Option 2 selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.btnDelete -> {
                    Toast.makeText(itemView.context, "Option 2 selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }


        override fun onClick(v: View?) {
            TODO("Not yet implemented")
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
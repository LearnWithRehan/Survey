package com.bmac.surwayapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bmac.surwayapp.R

class GrowerAdapter(var context: Context, var growerList :List<String>):
    RecyclerView.Adapter<myGrowerViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myGrowerViewHolder {

        var  layoutInflater= LayoutInflater.from(parent.context)
        var view = layoutInflater.inflate(R.layout.design_saved_survay_list,parent,false)

        return myGrowerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return growerList.size
    }

    override fun onBindViewHolder(holder: myGrowerViewHolder, position: Int) {

        holder.txtGrowerDat.text = growerList[position]
        holder.txtGrowerDat.setOnClickListener {

            Toast.makeText(context, "pos " + growerList[position], Toast.LENGTH_SHORT).show()
        }
    }
}

class myGrowerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    var txtGrowerDat:TextView = itemView.findViewById(R.id.txtVillageCode)
}

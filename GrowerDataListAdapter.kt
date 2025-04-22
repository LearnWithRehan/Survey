package com.bmac.surwayapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bmac.surwayapp.Model.GrowerDataListModel
import com.bmac.surwayapp.Model.VillageListModel
import com.bmac.surwayapp.databinding.DesignCustomListBinding

class GrowerDataListAdapter(var context: Context, var growerDataList :List<GrowerDataListModel>,
                            private var listener: onItemClickListeners):
    RecyclerView.Adapter<myGrowersViewHolder>()
{
    private  var rowIndex = 0
    var pos = 0

    val filteredlist = ArrayList<GrowerDataListModel>()

    init {
        filteredlist.addAll(growerDataList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myGrowersViewHolder {
        val binding = DesignCustomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return myGrowersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredlist.size
    }

    override fun onBindViewHolder(holder: myGrowersViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.binding.tvVillageCode.text = filteredlist[position].VillageCode + ", " +
                filteredlist[position].FarmerCode + ", " +
                filteredlist[position].First_FarmerName + ", " +
                filteredlist[position].Second_FarmerName


        holder.binding.root.setOnClickListener {
            rowIndex = position
            notifyDataSetChanged()
            listener.onClick(position)
            pos = 1
        }
    }
    interface onItemClickListeners {
        fun onClick(position1: Int)

    }

    fun filter(query : String){

        filteredlist.clear()
        if(query.isEmpty())
        {
            filteredlist.addAll(growerDataList)
        }
        else
        {
            val lowercasequery = query.toLowerCase()
            for(item in growerDataList)
            {
                if(item.VillageCode.toLowerCase().contains(lowercasequery))
                {
                    filteredlist.add(item)
                }
            }

        }
        notifyDataSetChanged()
    }

}

class myGrowersViewHolder(val binding: DesignCustomListBinding): RecyclerView.ViewHolder(binding.root) {

}

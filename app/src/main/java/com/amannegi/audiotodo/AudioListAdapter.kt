package com.amannegi.audiotodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AudioListAdapter(private val list: Array<out File>?,
                       private val listener: RecyclerViewClickListener)
    : RecyclerView.Adapter<AudioListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view, listener)
    }

    override fun getItemCount(): Int = list!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(list?.get(position))
    }


    class MyViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            recyclerViewClickListener.onClick(v!!, adapterPosition)
        }

        private val txtTitle = itemView.findViewById<TextView>(R.id.itemTitle)

        fun setData(f: File?) {
            txtTitle.text = f?.name
        }
    }
}
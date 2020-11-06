package com.rajesh.imagecomprassor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_zip.view.*
import java.io.File

/**
 * Created on 05-11-2020.
 */
class ZipAdapter(context: Context, val modelList: ArrayList<File>) :
    RecyclerView.Adapter<ZipAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.tv_zip_name
        val ivDelete = view.iv_zip_delete

        init {
            ivDelete.setOnClickListener {
                val position = adapterPosition

                modelList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(0, modelList.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_zip, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = modelList[position].name
    }

    override fun getItemCount(): Int {
        return modelList.size
    }
}
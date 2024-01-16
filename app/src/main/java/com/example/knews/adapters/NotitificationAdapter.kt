package com.example.knews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.knews.R
import com.example.knews.models.Notice

class NotitificationAdapter () : RecyclerView.Adapter<NotitificationAdapter.NoticeViewHolder>(){
    inner class NoticeViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)

    private val  differCallBack = object : DiffUtil.ItemCallback<Notice>(){
        override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)
    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = differ.currentList[position]
        holder.itemView.apply {
            val tvDate = findViewById<TextView>(R.id.tvDate)
            val tvHeading = findViewById<TextView>(R.id.tvHeading)
            val tvBody = findViewById<TextView>(R.id.tvBody)

            tvDate.text = notice.date
            tvHeading.text = notice.heading
            tvBody.text = notice.body
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
     return NoticeViewHolder(
         LayoutInflater.from(parent.context).inflate(
             R.layout.item_notification_preview,
             parent,
             false
         )
     )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
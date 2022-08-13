package com.example.todayweather.ui.daily

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todayweather.databinding.RcvDailyNavBinding
import com.example.todayweather.ui.home.model.Daily

class DailyNavAdapter : RecyclerView.Adapter<DailyNavAdapter.DailyNavViewHolder>() {

    // init list data
    var dataList = listOf<Daily>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // create ViewHolder with args are binding layout
    class DailyNavViewHolder(private val rcvNavDailyBinding: RcvDailyNavBinding) :
        RecyclerView.ViewHolder(rcvNavDailyBinding.root) {

        fun bind(item: Daily?) {
            if (item == null) return
            rcvNavDailyBinding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyNavViewHolder {
        return DailyNavViewHolder(
            RcvDailyNavBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DailyNavViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
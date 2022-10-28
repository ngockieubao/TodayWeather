package com.example.todayweather.ui.hourly

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todayweather.databinding.RcvHourlyElementBinding
import com.example.todayweather.data.model.Hourly

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    // init list data
    var dataList = listOf<Hourly>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // create ViewHolder with args are binding layout
    class HourlyViewHolder(private val rcvHourlyElementBinding: RcvHourlyElementBinding) :
        RecyclerView.ViewHolder(rcvHourlyElementBinding.root) {

        fun bind(item: Hourly?) {
            if (item == null) return
            rcvHourlyElementBinding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        return HourlyViewHolder(
            RcvHourlyElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
package com.example.todayweather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todayweather.R
import com.example.todayweather.databinding.RcvDetailElementBinding
import com.example.todayweather.data.model.DetailHomeModel

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    // init list data
    var dataList = listOf<DetailHomeModel>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // create ViewHolder with args are binding layout
    class DetailViewHolder(private val rcvDetailElementBinding: RcvDetailElementBinding) :
        RecyclerView.ViewHolder(rcvDetailElementBinding.root) {
        fun bind(item: DetailHomeModel?) {
            if (item == null) return

            rcvDetailElementBinding.imgViewIcHomeDetail.setImageResource(
                when (item.icon) {
                    1 -> R.drawable.ic_temperature
                    2 -> R.drawable.ic_water
                    3 -> R.drawable.ic_sun
                    4 -> R.drawable.ic_uchiha_eyes_24
                    5 -> R.drawable.ic_dew_point
                    6 -> R.drawable.ic_pressure
                    else -> R.drawable.ic_temperature
                }
            )
            rcvDetailElementBinding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            RcvDetailElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,     // add parent arg
                false  // add false arg
            )
        )
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
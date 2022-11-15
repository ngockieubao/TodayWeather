package com.example.todayweather.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todayweather.data.model.City
import com.example.todayweather.databinding.RcvSearchElementBinding

class SearchListAdapter(
    private val selectCity: SelectCity
) :
    ListAdapter<City, SearchListAdapter.CityViewHolder>(DiffCallBack) {

    class CityViewHolder(private val binding: RcvSearchElementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: City?) {
            if (item == null) return
            else {
                binding.item = item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CityViewHolder(RcvSearchElementBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            selectCity.selectItem(item)
        }
        holder.bind(item)
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem == newItem
            }
        }
    }
}


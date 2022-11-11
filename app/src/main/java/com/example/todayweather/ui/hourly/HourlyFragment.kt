package com.example.todayweather.ui.hourly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.todayweather.databinding.FragmentNavHourlyBinding
import com.example.todayweather.ui.WeatherViewModel

class HourlyFragment : Fragment() {

    private lateinit var bindingHourlyNavBinding: FragmentNavHourlyBinding
    private lateinit var hourlyNavAdapter: HourlyNavAdapter
    private val sharedViewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingHourlyNavBinding = FragmentNavHourlyBinding.inflate(inflater, container, false)

        bindingHourlyNavBinding.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = sharedViewModel
        }

        hourlyNavAdapter = HourlyNavAdapter()
        sharedViewModel.listDataHourly.observe(this.viewLifecycleOwner) {
            if (it == null) return@observe
            else hourlyNavAdapter.dataList = it
        }
        bindingHourlyNavBinding.rcvNavHourly.adapter = hourlyNavAdapter

        return bindingHourlyNavBinding.root
    }
}
package com.example.todayweather.ui.daily

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.todayweather.R
import com.example.todayweather.databinding.FragmentNavDailyBinding
import com.example.todayweather.ui.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DailyFragment : Fragment() {
    private lateinit var bindingDailyNavBinding: FragmentNavDailyBinding
    private lateinit var dailyNavAdapter: DailyNavAdapter

    // ShareViewModel
    private val sharedViewModel: WeatherViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingDailyNavBinding = FragmentNavDailyBinding.inflate(inflater, container, false)

        bindingDailyNavBinding.imageViewBackDailyNav.setOnClickListener {
            findNavController().navigate(R.id.action_dailyFragment_to_homeFragment)
        }

        return bindingDailyNavBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingDailyNavBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }

        dailyNavAdapter = DailyNavAdapter()

        sharedViewModel.listDataDaily.observe(this.viewLifecycleOwner) {
            dailyNavAdapter.dataList = it
        }

        bindingDailyNavBinding.rcvNavDaily.adapter = dailyNavAdapter
    }
}
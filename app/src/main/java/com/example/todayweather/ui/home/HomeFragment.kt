package com.example.todayweather.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todayweather.R
import com.example.todayweather.data.model.City
import com.example.todayweather.databinding.FragmentHomeBinding
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.ui.hourly.HourlyAdapter
import com.example.todayweather.util.Constants

class HomeFragment : Fragment() {

    private lateinit var bindingHome: FragmentHomeBinding
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var detailAdapter: DetailAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private var getBundle: City? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bindingHome = FragmentHomeBinding.inflate(inflater, container, false)

        weatherViewModel.networkError.observe(this.viewLifecycleOwner) {
            if (it == null) return@observe
            if (weatherViewModel.networkError.value == false)
                bindingHome.constraintStatusNetwork.visibility = View.GONE
            if (weatherViewModel.networkError.value == true)
                bindingHome.constraintStatusNetwork.visibility = View.VISIBLE
        }

        weatherViewModel.showLocation.observe(this.viewLifecycleOwner) {
            if (it != null && it != "") {
                bindingHome.tvHomeLocation.text = it
            }
        }
        weatherViewModel.listCurrent.observe(this.viewLifecycleOwner) {
            if (it == null) return@observe
            else bindingHome.item = it
        }

        detailAdapter = DetailAdapter()
        hourlyAdapter = HourlyAdapter()

        bindingHome.recyclerViewDetailContainerElement.recyclerViewDetail.adapter = detailAdapter
        bindingHome.recyclerViewHourlyContainerElement.recyclerViewHourly.adapter = hourlyAdapter

        weatherViewModel.listDataDetail.observe(this.viewLifecycleOwner) {
            detailAdapter.dataList = it
        }
        weatherViewModel.listDataHourly.observe(this.viewLifecycleOwner) {
            hourlyAdapter.dataList = it
        }

        searchCity()

        bindingHome.apply {
            imageBtnSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
            tvHomeStatusDescription.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_dailyFragment)
            }
            recyclerViewHourlyContainerElement.constraintHeaderHourly.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_hourlyFragment)
            }
            imageBtnRefresh.setOnClickListener {
                Toast.makeText(requireActivity(), "Update UI", Toast.LENGTH_SHORT).show()
            }
        }

        return bindingHome.root
    }

    private fun searchCity() {
        getBundle = arguments?.getParcelable(Constants.KEY_BUNDLE_SELECT_CITY)
        if (getBundle == null) return
        else {
            weatherViewModel.showLocation(getBundle!!.name)
            // Pass lat-lon args after allow position
            weatherViewModel.loadAPI(getBundle!!.lat, getBundle!!.lon)
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}

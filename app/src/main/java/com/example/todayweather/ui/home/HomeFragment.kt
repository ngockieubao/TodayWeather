package com.example.todayweather.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Inflate the layout for this fragment
        bindingHome = FragmentHomeBinding.inflate(inflater, container, false)

        weatherViewModel.networkError.observe(this.viewLifecycleOwner) {
            if (it == null) return@observe
            else {
                if (weatherViewModel.networkError.value == false)
                    bindingHome.constraintStatusNetwork.visibility = View.GONE
                else if (weatherViewModel.networkError.value == true)
                    bindingHome.constraintStatusNetwork.visibility = View.VISIBLE
            }
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
            if (it == null) return@observe
            else detailAdapter.dataList = it
        }
        weatherViewModel.listDataHourly.observe(this.viewLifecycleOwner) {
            if (it == null) return@observe
            else hourlyAdapter.dataList = it
        }

        return bindingHome.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchCity()

        bindingHome.apply {
            tvHomeStatusDescription.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_dailyFragment)
            }
            recyclerViewHourlyContainerElement.constraintHeaderHourly.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_hourlyFragment)
            }
            imageBtnRefresh.setOnClickListener {
                weatherViewModel.locationChange()
            }
        }
    }

    private fun searchCity() {
        getBundle = arguments?.getParcelable(Constants.KEY_BUNDLE_SELECT_CITY)
        if (getBundle == null) return
        else {
            weatherViewModel.showLocation(getBundle!!.name)
            // Pass lat-lon args after allow position
            weatherViewModel.loadApi(getBundle!!.lat, getBundle!!.lon)
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}

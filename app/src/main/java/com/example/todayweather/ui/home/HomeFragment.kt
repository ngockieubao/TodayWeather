package com.example.todayweather.ui.home

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.todayweather.R
import com.example.todayweather.ui.hourly.HourlyAdapter
import com.example.todayweather.databinding.FragmentHomeBinding
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.ui.home.model.City
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.IOException

class HomeFragment : Fragment() {
    private val weatherViewModel: WeatherViewModel by sharedViewModel()
    private lateinit var bindingHome: FragmentHomeBinding
    private lateinit var detailAdapter: DetailAdapter
    private lateinit var hourlyAdapter: HourlyAdapter

    // Init var location
    private var getPosition: String = ""
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var getBundle: City? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingHome = FragmentHomeBinding.inflate(inflater)

        Log.d(TAG, "onCreateView: HomeFragment")

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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

        // Set adapter
        bindingHome.recyclerViewDetailContainerElement.recyclerViewDetail.adapter = detailAdapter
        bindingHome.recyclerViewHourlyContainerElement.recyclerViewHourly.adapter = hourlyAdapter

        // Set data
        weatherViewModel.listDataDetail.observe(this.viewLifecycleOwner) {
            detailAdapter.dataList = it
        }
        weatherViewModel.listDataHourly.observe(this.viewLifecycleOwner) {
            hourlyAdapter.dataList = it
        }

        // Handle key bundle
        searchCity()

        // Navigate
        bindingHome.imageBtnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        bindingHome.tvHomeStatusDescription.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dailyFragment)
        }
        bindingHome.recyclerViewHourlyContainerElement.constraintHeaderHourly.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_hourlyFragment)
        }

        return bindingHome.root
    }

    private fun searchCity() {
        getBundle = arguments?.getParcelable(Constants.KEY_BUNDLE_SELECT_CITY)
        if (getBundle == null) return
        else {
            weatherViewModel.showLocation(getBundle!!.name)
            // Pass lat-lon args after allow position
            weatherViewModel.getWeatherProperties(getBundle!!.lat, getBundle!!.lon)
        }
    }

    // Get last location
    private fun getLastLocation() {
        try {
            try {
                fusedLocationClient!!.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            val latUpdate = location.latitude
                            val lonUpdate = location.longitude

                            getLocation(latUpdate, lonUpdate)
                        } else {
                            startLocationUpdates()
                        }
                    }
                    .addOnFailureListener {}
                    .addOnCompleteListener {}
            } catch (ex: SecurityException) {
                ex.printStackTrace()
            }
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    // Get location
    private fun getLocation(lat: Double, lon: Double) {
        try {
            val geocoder = Geocoder(requireContext())
            val position = geocoder.getFromLocation(lat, lon, 1)

            // Assign location
            getPosition = position[0].getAddressLine(0)
            // Pass lat-lon args after allow position
            weatherViewModel.getWeatherProperties(lat, lon)
            // Display location
            weatherViewModel.showLocation(Utils.formatLocation(requireContext(), getPosition))
//            weatherViewModel.showLocation(getPosition)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    // Update location
    private fun startLocationUpdates() {
        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: HomeFragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: HomeFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: HomeFragment")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: HomeFragment")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: HomeFragment")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: HomeFragment")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: HomeFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: HomeFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: HomeFragment")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: HomeFragment")
    }

    companion object {
        private const val TAG = "Home"
    }
}

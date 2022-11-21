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
import com.example.todayweather.databinding.FragmentSettingBinding
import com.example.todayweather.ui.WeatherViewModel
import com.example.todayweather.util.Constants
import com.example.todayweather.util.SharedPrefs
import com.example.todayweather.util.Utils

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val key: String = Constants.SHARED_PREFS
    private val keyLat: String = Constants.SHARED_PREFS_LAT
    private val keyLon: String = Constants.SHARED_PREFS_LON
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        val getSharedPrefs: String? = SharedPrefs.instance.getStringValue(key)
        if (getSharedPrefs != null) {
            when (getSharedPrefs) {
                Constants.CELCIUS -> binding.radioBtnCelcius.isChecked = true
                Constants.FAHRENHEIT -> binding.radioBtnFahrenheit.isChecked = true
            }
        } else Toast.makeText(requireActivity(), "Key - null", Toast.LENGTH_SHORT).show()

        return binding.root
    }

    private fun getNewApi() {
        val getSharedPrefsLat = SharedPrefs.instance.getStringValue(keyLat)
        val getSharedPrefsLon = SharedPrefs.instance.getStringValue(keyLon)
        if (getSharedPrefsLat != null || getSharedPrefsLon != null) {
            lat = getSharedPrefsLat()
            lon = getSharedPrefsLon()
        }

        if (lat == null || lon == null) {
            // Get lat-lon current location
            weatherViewModel.getLastLocation()
        } else {
            weatherViewModel.loadApi(lat!!, lon!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintSettingContent.setOnClickListener {
            weatherViewModel.getLastLocation()
            this.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        }
        binding.imageButtonBack.setOnClickListener {
            this.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        }

        binding.radioGroupConvert.setOnCheckedChangeListener { radioGroup, _ ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.radio_btn_celcius -> {
                    Utils.status = Constants.CELCIUS
                    SharedPrefs.instance.putStringValue(key, Constants.CELCIUS)
                    getNewApi()
                    this.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
                }
                R.id.radio_btn_fahrenheit -> {
                    Utils.status = Constants.FAHRENHEIT
                    SharedPrefs.instance.putStringValue(key, Constants.FAHRENHEIT)
                    getNewApi()
                    this.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
                }
            }
        }
    }

    private fun getSharedPrefsLat(): Double {
        return SharedPrefs.instance.getStringValue(keyLat)!!.toDouble()
    }

    private fun getSharedPrefsLon(): Double {
        return SharedPrefs.instance.getStringValue(keyLon)!!.toDouble()
    }
}
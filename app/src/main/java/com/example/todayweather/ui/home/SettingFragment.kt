package com.example.todayweather.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todayweather.R
import com.example.todayweather.databinding.FragmentSettingBinding
import com.example.todayweather.ui.WeatherViewModel

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val weatherViewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
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
                    weatherViewModel.setStatusConvert("celcius")
                    Toast.makeText(requireActivity(), "Celcius", Toast.LENGTH_SHORT).show()
                    this.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
                }
                R.id.radio_btn_fahrenheit -> {
                    weatherViewModel.setStatusConvert("fah")
                    Toast.makeText(requireActivity(), "Fah", Toast.LENGTH_SHORT).show()
                    this.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
                }
            }
        }
    }
}
package com.example.todayweather.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.todayweather.R
import com.example.todayweather.databinding.FragmentSearchBinding
import com.example.todayweather.data.model.City
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.example.todayweather.util.Utils.fromJsonToLocation

class SearchFragment : Fragment(), SelectCity {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter

    private var listCity = ArrayList<City>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Read list cities json
        listCity = Utils.readJSONFromAsset(requireContext())?.fromJsonToLocation()!!

        var cityFilterList: List<City>

        binding.citySearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(requireContext(), getString(R.string.searching), Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                for (item in listCity) {
                    if (newText != null) {
                        cityFilterList = listCity.filter { it.name.contains(newText) }
                        searchAdapter.listCities = cityFilterList
                    }
                }
                return true
            }
        })

        searchAdapter = SearchAdapter(requireContext(), this)
        searchAdapter.listCities = listCity
        binding.rcvFragmentSearch.adapter = searchAdapter

        return binding.root
    }

    override fun selectItem(city: City?) {
        val bundle = bundleOf(Constants.KEY_BUNDLE_SELECT_CITY to city)
        findNavController().navigate(R.id.action_searchFragment_to_homeFragment, bundle)
    }
}
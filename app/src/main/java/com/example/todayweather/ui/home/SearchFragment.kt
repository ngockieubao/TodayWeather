package com.example.todayweather.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.edtQuery.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                for (item in listCity) {
                    if (s != null) {
                        cityFilterList = listCity.filter { it.name.contains(s) }
                        searchAdapter.listCities = cityFilterList
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        searchAdapter = SearchAdapter(requireActivity(), this)
        searchAdapter.listCities = listCity
        binding.rcvFragmentSearch.adapter = searchAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonBack.setOnClickListener {
            this.findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    override fun selectItem(city: City?) {
        val bundle = bundleOf(Constants.KEY_BUNDLE_SELECT_CITY to city)
        findNavController().navigate(R.id.action_searchFragment_to_homeFragment, bundle)
    }
}
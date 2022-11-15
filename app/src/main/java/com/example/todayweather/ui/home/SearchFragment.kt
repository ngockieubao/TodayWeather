package com.example.todayweather.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.todayweather.R
import com.example.todayweather.data.model.City
import com.example.todayweather.databinding.FragmentSearchBinding
import com.example.todayweather.util.Constants
import com.example.todayweather.util.Utils
import com.example.todayweather.util.Utils.fromJsonToLocation
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(), SelectCity {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchListAdapter: SearchListAdapter
    private var listCity = ArrayList<City>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        // Read list cities json
        listCity = Utils.readJSONFromAsset(requireContext())?.fromJsonToLocation()!!
        searchListAdapter = SearchListAdapter(this)

        binding.citySearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(requireActivity(), getString(R.string.searching), Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filter(newText)
                }
                return true
            }
        })

        searchListAdapter.submitList(listCity)
        binding.rcvFragmentSearch.adapter = searchListAdapter

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

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredList: ArrayList<City> = ArrayList()

        // running a for loop to compare elements.
        for (item in listCity) {
            // checking if the entered string matched with any item of our recycler view.
            val input = item.name.lowercase(Locale.getDefault()).trim()
            val data = text.lowercase(Locale.getDefault()).trim()
            if (input.contains(data)) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireActivity(), "No Data Found...", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            searchListAdapter.submitList(filteredList)
        }
    }
}
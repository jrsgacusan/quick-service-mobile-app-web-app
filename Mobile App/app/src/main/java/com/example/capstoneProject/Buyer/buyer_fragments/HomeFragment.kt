package com.example.capstoneProject.Buyer.buyer_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.*
import com.example.capstoneProject.Buyer.BuyerActivity
import com.example.capstoneProject.Buyer.buyer_activities.ServiceCategoryActivity
import com.example.capstoneProject.Buyer.bottomNavigationBuyer
import com.example.capstoneProject.GroupieViews.ServiceCategoryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*


class HomeFragment : Fragment() {


    private lateinit var v: View
    private lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<ViewHolder>().apply {
        spanCount  = 2
    }
    private val i = adapter.spanSizeLookup



    val searchFragment = SearchFragment ()

    companion object {
        val SERVICECATEGORY = "serviceCategory"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)
        //Map everything here
        recyclerView = v.findViewById(R.id.recyclerView_fragmentHome)
        recyclerView.apply {
            layoutManager = GridLayoutManager(v.context, 2).apply {
                spanSizeLookup = i
            }
        }
        val btn = v.findViewById<Button>(R.id.searchBtn)
        //Button onclick listener
        btn.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.wrapper, searchFragment)
            transaction?.disallowAddToBackStack()
            transaction?.commit()
                    }
        fetchServiceCategory()

        return v
    }

    private fun fetchServiceCategory() {
        adapter.clear()
        //Get the array of service category
        val arrayList = ArrayList(listOf(*resources.getStringArray(R.array.services_category)))
        //Add the different categories to the adapter
        adapter.add(ServiceCategoryItem(arrayList[0], R.drawable.services_computer))
        adapter.add(ServiceCategoryItem(arrayList[1], R.drawable.services_homecleaning))
        adapter.add(ServiceCategoryItem(arrayList[2], R.drawable.services_plumbing))
        adapter.add(ServiceCategoryItem(arrayList[3], R.drawable.services_electrical))
        adapter.add(ServiceCategoryItem(arrayList[4], R.drawable.services_moving))
        adapter.add(ServiceCategoryItem(arrayList[5], R.drawable.services_delivery))
        adapter.add(ServiceCategoryItem(arrayList[6], R.drawable.services_aircon))
        adapter.add(ServiceCategoryItem(arrayList[7], R.drawable.services_homerepair))
        adapter.add(ServiceCategoryItem(arrayList[8], R.drawable.services_auto))
        //Attach the adapter to the recycler view
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(v.context, ServiceCategoryActivity::class.java)
            val category = item as ServiceCategoryItem
            Log.d("ServiceCategoryTitle", category.serviceTitle)
            intent.putExtra(SERVICECATEGORY, category.serviceTitle)
            startActivity(intent)


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as BuyerActivity?)?.setActionBarTitle("Services")
        bottomNavigationBuyer.menu.findItem(R.id.homePage).isChecked = true
    }



}
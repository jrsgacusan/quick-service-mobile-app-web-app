@file:Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")

package com.example.capstoneProject.buyer_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.capstoneProject.R
import com.example.capstoneProject.bottomNavigationBuyer
import com.example.capstoneProject.buyer_activities.DisplaySpecificServiceActivity
import com.example.capstoneProject.models.Service
import com.example.capstoneProject.views.ServiceItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    lateinit var v: View
    lateinit var searchView: SearchView
    lateinit var listView: ListView
    lateinit var adapter: ArrayAdapter<Service>
    var arrayList: ArrayList <Service> = ArrayList()



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v=  inflater.inflate(R.layout.fragment_search, container, false)
        //Map the Search View
        searchView = v.findViewById(R.id.searchView)
        listView = v.findViewById(R.id.listView_fragmentSearch)
        //automatically opens the search view. As soon as this fragment is used, the search view is already clicked.
        searchView.isIconified = false

        //Implement search view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                if (arrayList.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    Toast.makeText(v.context, "Not found", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
        listView.setOnItemClickListener { _, _, position, _ ->
            Log.i("ASDASDASDASD", "$position")
            val intent = Intent(v.context, DisplaySpecificServiceActivity::class.java)
            val service = arrayList[position]
            intent.putExtra("service", service)
            DisplaySpecificServiceActivity.viewOnlyMode = false
            startActivity(intent)

        }


        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Fetch services from firebase database
        fetchServices()
    }

    private fun fetchServices() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/services")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                snapshot.children.forEach {user->
                    if (user.key != currentUser){
                        user.children.forEach{ serviceListed->
                            val service = serviceListed.getValue(Service::class.java)!!
                            if (service.status == "ACTIVE") {
                                arrayList.add(service)
                            }
                        }

                    }
                }
                adapter = ArrayAdapter(v.context, android.R.layout.simple_list_item_1, arrayList)
                listView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //Whenever the fragment is changed to fragment_search, the action bar will disappear
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        bottomNavigationBuyer.menu.findItem(R.id.search).isChecked = true
    }
    //Whenever the fragment is not fragment_search, the action bar will appear
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

    }



}
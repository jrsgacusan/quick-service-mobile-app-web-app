package com.example.capstoneProject.ServiceProvider.seller_activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.R
import com.example.capstoneProject.Handlers.ServiceRequestHandler
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.GroupieViews.ServiceRequestItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class BuyersRequestActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var serviceRequestRecyclerView: RecyclerView
    private lateinit var serviceRequestArrayList: ArrayList<ServiceRequest>
    var currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
    private var serviceRequestHandler = ServiceRequestHandler()
    private lateinit var hideThisTextView: TextView
    private lateinit var hideThisImageView: ImageView
    var adapter = GroupAdapter<ViewHolder>()

    companion object {
        var serviceRequestToBeViewed: ServiceRequest? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyers_request)
        //Map and initilize everything here
        toolbar = findViewById(R.id.toolBar_activityBuyersRequest)
        serviceRequestRecyclerView = findViewById(R.id.serviceRequestRecyclerView_activityBuyersRequest)
        serviceRequestArrayList = ArrayList()
        hideThisTextView = findViewById(R.id.hideThisTextView_activityBuyersRequest)
        hideThisImageView = findViewById(R.id.hideThisImageView_activityBuyersRequest)
        serviceRequestRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        toolbar.title = "Buyers' Request"
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        //Gets all the Service Requests available
        val adapter = GroupAdapter<ViewHolder>()
        serviceRequestRecyclerView.adapter = adapter
        fetchServiceRequests()
    }

    private fun fetchServiceRequests() {
        serviceRequestHandler.serviceRequestRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                adapter.clear()
                p0.children.forEach {
                    val serviceRequest = it.getValue(ServiceRequest::class.java)
                    if (serviceRequest!!.userUid != currentUserUid){
                        adapter.add(ServiceRequestItem(serviceRequest))
                    }
                }
                serviceRequestRecyclerView.adapter = adapter

                adapter.setOnItemClickListener{ item, view ->
                    val serviceRequestItem = item as ServiceRequestItem
                    serviceRequestToBeViewed = serviceRequestItem.serviceRequest
                    var showRequestFragment = BottomShowRequestFragment()
                    showRequestFragment.show(supportFragmentManager, "TAG")



                }

                hideOrShowViews()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun hideOrShowViews() {
        if (adapter.itemCount == 0){
            hideThisTextView.isVisible = true
            hideThisImageView.isVisible = true
        } else {
            hideThisTextView.isVisible = false
            hideThisImageView.isVisible = false

        }
    }

}




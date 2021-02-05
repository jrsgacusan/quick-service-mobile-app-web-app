package com.example.capstoneProject.buyer_activities

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.R
import com.example.capstoneProject.buyer_fragments.HomeFragment
import com.example.capstoneProject.models.Service
import com.example.capstoneProject.models.User
import com.example.capstoneProject.models.UserSellerInfo
import com.example.capstoneProject.views.ServiceItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.lang.ArithmeticException


class ServiceCategoryActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var servicesRecyclerView: RecyclerView
    var servicesArrayList: ArrayList<Service> = ArrayList()
    var currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var category: String
    var user: User? = null
    val adapter = GroupAdapter<ViewHolder>()
    lateinit var hideThisTextView:TextView
    lateinit var hideThisImageView: ImageView
    lateinit var clickhereTextView: TextView
    lateinit var filterBtn : ImageButton


    companion object{
        const val TAG = "Random tag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_category)
        //get the category we would display
        category = intent.getStringExtra(HomeFragment.SERVICECATEGORY).toString()
        //Map everything
        toolbar = findViewById(R.id.toolBar_activityServiceCategory)
        servicesRecyclerView = findViewById(R.id.serviceRecyclerView_activityServiceCategory)
        hideThisTextView = findViewById(R.id.hideThisTextView_activityServiceCategory)
        hideThisImageView = findViewById(R.id.hideThisImageView_activityServiceCategory)
        clickhereTextView = findViewById(R.id.clickHereTextView_activityServiceCategory)
        filterBtn = findViewById(R.id.imageButton_activityServiceCategory)

        //Toolbar
        toolbar.title = category
        toolbar.setNavigationOnClickListener {
            Log.d(TAG, "It is clicked")
            finish()
        }
        //Groupie adapter. It is a 3rd party dependency used to easily create Recycler views.
        servicesRecyclerView.adapter = adapter
        servicesRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        //Gets the service using the category
        fetchService()

        clickhereTextView.setOnClickListener {
            createRequest()
        }

        filterBtn.setOnClickListener{
            showPopUp(it)
        }
    }

    private fun showPopUp(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_filter, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.descending-> {
                    fetchServiceDescending()
                }
                R.id.ascending-> {
                    fetchServiceAscending()
                }
                R.id.noFilter -> {
                    fetchService()
                }

                R.id.rating -> {

                }

                R.id.low -> {
                    Log.d(TAG, "Clicked.")
                    fetcherServiceUsingRating(0.0, 3.9)
                }

                R.id.high -> {
                    Log.d(TAG, "Clicked.")
                    fetcherServiceUsingRating(4.0,5.0)
                }
            }
            true
        }
        popup.show()
    }



    private fun fetcherServiceUsingRating(min: Double, max: Double){
        //Fetch the services provider that provides that service
        val serviceProviderArrayList: ArrayList<String> = ArrayList()
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val refFirst = FirebaseDatabase.getInstance().getReference("/services")
        refFirst.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                snapshot.children.forEach { user ->
                    if (user.key != currentUser) {
                        user.children.forEach { serviceListed ->
                            val service = serviceListed.getValue(Service::class.java)
                            if (service!!.category == category && service.status == "ACTIVE") {
                                serviceProviderArrayList.add(service.userUid!!)
                            }

                        }
                    }
                }
                //Filter those using min and max parameter
                val userFiltered: ArrayList <String> = ArrayList()
                serviceProviderArrayList.forEach { uid ->
                    val secRef = FirebaseDatabase.getInstance().getReference("user_seller_info/$uid")
                    secRef.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            adapter.clear()
                            val userData = snapshot.getValue(UserSellerInfo::class.java)!!
                            try {
                                val ttlrtng = userData.totalRating!!.toDouble()
                                val ttlcnt = userData.count!!.toDouble()
                                val rating = (ttlrtng/ttlcnt)
                                val solution = Math.round(rating * 10.0) / 10.0
                                if (solution  in min..max) {
                                    userFiltered.add(userData.uid!!)
                                }
                            } catch (e: ArithmeticException) {
                                userFiltered.add(userData.uid!!)
                            }

                            //Fetch the user filtered
                            userFiltered.forEach { uid ->
                                val ref = FirebaseDatabase.getInstance().getReference("/services/$uid")
                                ref.addListenerForSingleValueEvent(object: ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        adapter.clear()
                                        snapshot.children.forEach{ serviceListed ->
                                            val service = serviceListed.getValue(Service::class.java)!!
                                            if (service!!.category == category && service.status == "ACTIVE") {
                                                adapter.add(ServiceItem(service))
                                            }
                                        }

                                        adapter.setOnItemClickListener { item, view ->
                                            val serviceItem = item as ServiceItem
                                            val intent = Intent(view.context, DisplaySpecificServiceActivity::class.java)
                                            intent.putExtra("service", serviceItem.service)
                                            startActivity(intent)
                                            DisplaySpecificServiceActivity.viewOnlyMode = false
                                        }
                                        servicesRecyclerView.adapter = adapter
                                        hideOrShowViews()


                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                })
                            }

                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }




            }
            override fun onCancelled(error: DatabaseError) {
            }
        })





    }

    private fun fetchServiceDescending() {
        val arrayList: ArrayList<Service> = ArrayList()
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        var descending: List<Service>
        val refFirst = FirebaseDatabase.getInstance().getReference("/services")
        refFirst.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                adapter.clear()
                snapshot.children.forEach { user ->
                    Log.d("TAGWALALANG", user.key.toString())
                    if (user.key != currentUser) {
                        user.children.forEach { serviceListed ->
                            val service = serviceListed.getValue(Service::class.java)
                            if (service!!.category == category && service.status == "ACTIVE") {
                                arrayList.add(service)
                            }

                        }
                    }
                }
                descending = arrayList.sortedByDescending { service ->
                    service.price
                }
                descending.forEach {service ->
                    adapter.add(ServiceItem(service))
                }
                adapter.setOnItemClickListener { item, view ->
                    val serviceItem = item as ServiceItem
                    val intent = Intent(view.context, DisplaySpecificServiceActivity::class.java)
                    intent.putExtra("service", serviceItem.service)
                    startActivity(intent)
                    DisplaySpecificServiceActivity.viewOnlyMode = false
                }
                servicesRecyclerView.adapter = adapter
                hideOrShowViews()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun fetchServiceAscending() {
        val arrayList: ArrayList<Service> = ArrayList()
        var ascending: List<Service>
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val refFirst = FirebaseDatabase.getInstance().getReference("/services")
        refFirst.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                adapter.clear()
                snapshot.children.forEach { user ->
                    Log.d(TAG, user.key.toString())
                    if (user.key != currentUser) {
                        user.children.forEach { serviceListed ->
                            val service = serviceListed.getValue(Service::class.java)
                            if (service!!.category == category && service.status == "ACTIVE") {
                                arrayList.add(service)
                            }
                        }
                    }
                }
                ascending = arrayList.sortedBy { service ->
                    service.price
                }

                ascending.forEach {service ->
                    adapter.add(ServiceItem(service))
                }
                adapter.setOnItemClickListener { item, view ->
                    val serviceItem = item as ServiceItem
                    val intent = Intent(view.context, DisplaySpecificServiceActivity::class.java)
                    intent.putExtra("service", serviceItem.service)
                    startActivity(intent)
                    DisplaySpecificServiceActivity.viewOnlyMode = false
                }
                servicesRecyclerView.adapter = adapter




            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun createRequest() {
        val intent = Intent(applicationContext, RequestActivity::class.java)
        startActivity(intent)
    }


    private fun fetchService() {
        //fetch services
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val refFirst = FirebaseDatabase.getInstance().getReference("/services")
        refFirst.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                snapshot.children.forEach {
                    Log.d("TAGWALALANG", it.key.toString())
                    if (it.key != currentUser) {
                        it.children.forEach {
                            val service = it.getValue(Service::class.java)
                            if (service!!.category == category && service.status == "ACTIVE") {
                                adapter.add(ServiceItem(service))
                            }
                            adapter.setOnItemClickListener { item, view ->
                                val serviceItem = item as ServiceItem
                                val intent = Intent(view.context, DisplaySpecificServiceActivity::class.java)
                                intent.putExtra("service", serviceItem.service)
                                startActivity(intent)
                                DisplaySpecificServiceActivity.viewOnlyMode = false
                            }
                            servicesRecyclerView.adapter = adapter
                            hideOrShowViews()
                        }
                    }
                }



            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun hideOrShowViews() {
        if (adapter.getItemCount() == 0){
            hideThisTextView.isVisible = true
            hideThisImageView.isVisible = true
            clickhereTextView.isVisible= true
            filterBtn.isVisible = false

        } else {
            hideThisTextView.isVisible = false
            hideThisImageView.isVisible = false
            clickhereTextView.isVisible= false
            filterBtn.isVisible = true
        }
    }

}



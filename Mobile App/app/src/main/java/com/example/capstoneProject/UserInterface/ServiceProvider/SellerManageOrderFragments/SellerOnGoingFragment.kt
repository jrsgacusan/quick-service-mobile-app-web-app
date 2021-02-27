package com.example.capstoneProject.UserInterface.ServiceProvider.SellerManageOrderFragments

import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.GroupieViews.OrderItem
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*


class SellerOnGoingFragment : Fragment() {
    private lateinit var v: View
    private lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 2
    }
    private val i = adapter.spanSizeLookup

    companion object;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_manage_sellers_on_going, container, false)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView_fragmentManageSellersOnGoing)
        recyclerView.apply {
            layoutManager = GridLayoutManager(v.context, 2).apply {
                spanSizeLookup = i
            }
        }
        fetchOrders()
        statusListener()




        return v
    }




    private fun checkDate(date: String): Boolean{
        val values = date.split(" ").toTypedArray()
        val month = convertMonth(values[0])
        val day = values[1].substring(0,2)
        val year = values[2]
        val longDate = "$year$month$day".toLong()
        val currentDate = convertLongToTime(System.currentTimeMillis()).toLong()
        Log.d("INeedThisValue", "Month: ${values[0]}")
        Log.d("INeedThisValue", "Day: ${values[1]}")
        Log.d("INeedThisValue", "Year: ${values[2]}")
        Log.d("INeedThisValue", "Date Needed: $longDate")
        Log.d("INeedThisValue", "Current Date: $currentDate")
        return currentDate - 5 >= longDate
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyyMMdd")
        return format.format(date)
    }

    private fun convertMonth(s: String): String {
        var returnValue = "";
        when(s.toUpperCase()) {
            "JANUARY" -> { returnValue = "01" }
            "FEBRUARY" -> { returnValue = "02" }
            "MARCH" -> { returnValue = "03" }
            "APRIL" -> { returnValue = "04" }
            "MAY" -> { returnValue = "05" }
            "JUNE" -> { returnValue = "06" }
            "JULY" -> { returnValue = "07" }
            "AUGUST" -> { returnValue = "08" }
            "SEPTEMBER" -> { returnValue = "09" }
            "OCTOBER" -> { returnValue = "10" }
            "NOVEMBER" -> { returnValue = "11" }
            "DECEMBER" -> { returnValue = "12" }
        }

        return returnValue
    }


    private fun fetchOrders() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("booked_to/$currentUserUid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                snapshot.children.forEach { bookings ->
                    val order = bookings.getValue(Order::class.java)!!
                    if (order.status == "ACCEPTED") {
                        adapter.add(OrderItem(order, v.context))
                    }

                    if (order.status == "ACCEPTED") {
                        if (order.buyerConfirmation == "" && order.sellerConfirmation == "CONFIRMED") {
                            if(checkDate(order.date!!)){
                                val anotherRef = FirebaseDatabase.getInstance().getReference("booked_by/${order.userUid}/${order.uid}")
                                anotherRef.child("/status").setValue("COMPLETED")
                            }

                        }
                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    val orderItem = item as OrderItem
                    val tappedOrder = orderItem.order
                    var sellerOrderDetailsFragment = BottomFragmentSellerOrderDetails(tappedOrder)
                    sellerOrderDetailsFragment.show(fragmentManager!!, "TAG")
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun statusListener() {

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val anotherRef = FirebaseDatabase.getInstance().getReference("booked_to/$currentUser")
        anotherRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{ booking ->
                    val order = booking.getValue(Order::class.java)
                    val bookedTo = order!!.service_provider_uid!!
                    val orderUid = order.uid!!
                    val ref = FirebaseDatabase.getInstance().getReference("booked_to/$bookedTo/$orderUid")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                val order = snapshot.getValue(Order::class.java)!!
                                if (order.buyerConfirmation == "CONFIRMED" && order.sellerConfirmation == "CONFIRMED") {
                                    ref.child("/status").setValue("COMPLETED")

                                }
                            } catch (e: NullPointerException) {

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


}
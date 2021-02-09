@file:Suppress("DEPRECATION")

package com.example.capstoneProject.Buyer.buyer_manage_order_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import com.example.capstoneProject.R
import com.example.capstoneProject.Dialogs.ReviewDialog
import com.example.capstoneProject.Messages.ChatLogActivity
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.Models.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class BottomFragmentOrderDetails(orderPassed: Order) : BottomSheetDialogFragment() {
   private lateinit var v: View
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var price: TextView
    private lateinit var category: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var dateOrdered: TextView
    private lateinit var contactNum: TextView
    private lateinit var button: Button
    private lateinit var anotherButton: Button
    private lateinit var address: TextView
    private lateinit var mode: TextView
    val order = orderPassed





    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    companion object {
        const val CONFIRM_TEXT = "CONFIRM BOOKING"
        const val ADD_REVIEW_TEXT = "ADD A REVIEW"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_bottom_booking_details, container, false)
        mode = v.findViewById(R.id.mode_orderDetails)
        date = v.findViewById(R.id.date_orderDetails)
        time = v.findViewById(R.id.time_orderDetails)
        price = v.findViewById(R.id.price_orderDetails)
        category = v.findViewById(R.id.category_orderDetails)
        title = v.findViewById(R.id.title_orderDetails)
        description = v.findViewById(R.id.description_orderDetails)
        dateOrdered = v.findViewById(R.id.dateAndTimeOrdered_orderDetails)
        contactNum = v.findViewById(R.id.number_orderDetails)
        button = v.findViewById(R.id.button_orderDetails)
        anotherButton = v.findViewById(R.id.anotherButton_orderDetails)
        address = v.findViewById(R.id.address_fragmentBottomBookingDetails)



        date.text = "${date.text} ${order.date}"
        time.text = "${time.text} ${order.time}"
        price.text ="${price.text} ${order.price.toString()}"
        category.text = "${category.text} ${order.category}"
        title.text = "${title.text} ${order.title}"
        description.text = "${description.text} ${order.description}"
        dateOrdered.text = "${dateOrdered.text} ${convertLongToDate(order.dateOrdered)}"
        address.text = "${address.text} ${order.address}"
        mode.text = "Mode of Payment: ${order.modeOfPayment}"

        fetchNumber()
        checkStatus()
        statusListener()
        anotherButton.setOnClickListener {
            if(anotherButton.text == CONFIRM_TEXT){
                confirmTheOrder()
            } else if (anotherButton.text == ADD_REVIEW_TEXT) {
                addAReview()

            }
        }

        button.setOnClickListener {
            fetchUserAndGoToChatLogActivity()
        }
        return v
    }


    private fun statusListener() {
        val bookedBy = order.userUid!!
        val bookedTo = order.service_provider_uid!!
        val orderUid = order.uid!!
        val ref = FirebaseDatabase.getInstance().getReference("booked_by/$bookedBy/$orderUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
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



    private fun confirmTheOrder() {
        if (order.buyerConfirmation != "CONFIRMED") {
            val dialogBuilder = AlertDialog.Builder(v.context)
            dialogBuilder.setMessage("Do you want to confirm that this order is finished?")
                    .setCancelable(true)
                    .setPositiveButton("Confirm") { _, _ ->
                        val bookedBy = order.userUid!!
                        val bookedTo = order.service_provider_uid!!
                        val orderUid = order.uid!!
                        val ref = FirebaseDatabase.getInstance().getReference("booked_by/$bookedBy/$orderUid")
                        ref.child("buyerConfirmation").setValue("CONFIRMED")
                        val anotherRef = FirebaseDatabase.getInstance().getReference("booked_to/$bookedTo/$orderUid")
                        anotherRef.child("buyerConfirmation").setValue("CONFIRMED")
                        Toast.makeText(v.context, "Booking confirmed as completed.", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = dialogBuilder.create()
            alert.setTitle("Confirmation")
            alert.show()
        } else {
            Toast.makeText(v.context, "You already confirmed this booking.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addAReview() {
        if (order.reviewed == false){
            //close the bottom frafment here
            dismissBottomSheet()
            val reviewDialog = ReviewDialog(this@BottomFragmentOrderDetails, order)
            reviewDialog.showReviewDialog()
        } else if (order.reviewed == true) {
            Toast.makeText(v.context, "You already submitted a review for this order.", Toast.LENGTH_SHORT).show()
        }


    }
    private fun checkStatus() {
        when(order.status){
            "NEW" -> {
                anotherButton.isGone = true
            }
            "ACCEPTED" -> {
                anotherButton.isGone = false
                anotherButton.text = CONFIRM_TEXT
            }
            "COMPLETED" -> {
                anotherButton.isGone = false
                anotherButton.text = ADD_REVIEW_TEXT
            }
        }
    }

    private fun fetchUserAndGoToChatLogActivity() {
        try {
            val ref = FirebaseDatabase.getInstance().getReference("users/${order.service_provider_uid}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val serviceProvider = snapshot.getValue(User::class.java)!!
                    val intent = Intent(v.context, ChatLogActivity::class.java)
                    intent.putExtra("user", serviceProvider)
                    startActivity(intent)

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        } catch (e: Exception) {
            Toast.makeText(v.context, "Account is no longer Available.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchNumber() {
        try {
            val ref = FirebaseDatabase.getInstance().getReference("users/${order.service_provider_uid}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val serviceProvider = snapshot.getValue(User::class.java)!!
                    contactNum.text = "${contactNum.text} ${serviceProvider.mobileNumber}"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        } catch (e: Exception) {
            contactNum.text = "${contactNum.text} Account Deleted"
        }

    }

    private fun dismissBottomSheet(){
        val p = requireFragmentManager().findFragmentByTag(FinishedFragment.TAG)!!
        val df = p as BottomSheetDialogFragment
        df.dismiss()
    }


    fun convertLongToDate(long: Long): String{
        val resultdate = Date(long)
        return resultdate.toString()
    }
}
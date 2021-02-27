package com.example.capstoneProject.UserInterface.ServiceProvider.SellerManageOrderFragments

import android.content.DialogInterface
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
import com.example.capstoneProject.UserInterface.Messages.ChatLogActivity
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class BottomFragmentSellerOrderDetails(order: Order) : BottomSheetDialogFragment() {
    private var orderClicked = order
    private lateinit var v: View
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var address: TextView
    private lateinit var price: TextView
    private lateinit var name: TextView
    private lateinit var contactNumber: TextView
    private lateinit var category: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var dandtbooked: TextView
    private lateinit var messageButton: Button
    private lateinit var markButton: Button
    private lateinit var completeButton: Button
    private lateinit var modeEditText: TextView


    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_bottom_booking_details_seller, container, false)
        modeEditText = v.findViewById(R.id.mode_fragmentBottomBookingDetailsSeller)
        time = v.findViewById(R.id.time_fragmentBottomBookingDetailsSeller)
        address = v.findViewById(R.id.address_fragmentBottomBookingDetailsSeller)
        price = v.findViewById(R.id.price_fragmentBottomBookingDetailsSelle)
        name = v.findViewById(R.id.name_fragmentBottomBookingDetailsSeller)
        contactNumber = v.findViewById(R.id.number_fragmentBottomBookingDetailsSeller)
        category = v.findViewById(R.id.category_fragmentBottomBookingDetailsSeller)
        title = v.findViewById(R.id.title_fragmentBottomBookingDetailsSeller)
        description = v.findViewById(R.id.description_fragmentBottomBookingDetailsSeller)
        dandtbooked = v.findViewById(R.id.dandt_fragmentBottomBookingDetailsSeller)
        messageButton = v.findViewById(R.id.button_fragmentBottomBookingDetailsSeller)
        date = v.findViewById(R.id.date_fragmentBottomBookingDetailsSeller)
        markButton = v.findViewById(R.id.marButton_fragmentBottomBookingDetailsSeller)
        completeButton = v.findViewById(R.id.complete_fragmentBottomBookingDetailsSeller)

        checkStatus()

        time.text = "Time: ${orderClicked.time}"
        address.text = "Address: ${orderClicked.address}"
        price.text = "Price: ₱${orderClicked.price}"
        category.text = "Category: ${orderClicked.category}"
        title.text = "Title: ${orderClicked.title}"
        description.text = "Description: ${orderClicked.description}"
        dandtbooked.text = "Date and Time Booked: ${convertLongToDate(orderClicked.dateOrdered)}"
        date.text = "Date: ${orderClicked.date}"
        modeEditText.text = "Mode of Payment: ${orderClicked.modeOfPayment}"
        fetchNameAndNumber()

        messageButton.setOnClickListener {
            goToChatLogActivity()
        }

        markButton.setOnClickListener {
            acceptOrDeclineOrder()
        }

        completeButton.setOnClickListener {
            markAsComplete()
        }


        statusListener()

        return v
    }
    private fun convertLongToDate(long: Long): String {
        val resultdate = Date(long)
        return resultdate.toString()
    }

    private fun statusListener() {

        val bookedBy = orderClicked.userUid!!
        val bookedTo = orderClicked.service_provider_uid!!
        val orderUid = orderClicked.uid!!
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


    private fun markAsComplete() {
        if (orderClicked.sellerConfirmation == "CONFIRMED") {
            Toast.makeText(v.context, "You already confirmed this order.", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogBuilder = AlertDialog.Builder(v.context)
        dialogBuilder.setMessage("Do you want to confirm that this order is finished?")
                .setCancelable(true)
                .setPositiveButton("Confirm") { _, _ ->
                    val bookedBy = orderClicked.userUid!!
                    val bookedTo = orderClicked.service_provider_uid!!
                    val bookingUid = orderClicked.uid!!
                    val bookedByRef = FirebaseDatabase.getInstance().getReference("booked_by/$bookedBy/$bookingUid")
                    val bookedToRef = FirebaseDatabase.getInstance().getReference("booked_to/$bookedTo/$bookingUid")
                    bookedByRef.child("sellerConfirmation").setValue("CONFIRMED")
                    bookedToRef.child("sellerConfirmation").setValue("CONFIRMED")
                    Toast.makeText(v.context, "Booking confirmed as completed.", Toast.LENGTH_SHORT).show()
                    statusListener()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
        val alert = dialogBuilder.create()
        alert.setTitle("Confirmation")
        alert.show()

        if (orderClicked.sellerConfirmation == "CONFIRMED") {
            Toast.makeText(v.context, "You already confirmed this booking.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun acceptOrDeclineOrder() {
        val dialogBuilder = AlertDialog.Builder(v.context)
        dialogBuilder.setMessage("Do you want to Accept this order or Decline?")
                .setCancelable(true)
                .setPositiveButton("Accept", DialogInterface.OnClickListener { _, _ ->
                    val bookedBy = orderClicked.userUid!!
                    val bookedTo = orderClicked.service_provider_uid!!
                    val bookingUid = orderClicked.uid!!
                    val bookedByRef = FirebaseDatabase.getInstance().getReference("booked_by/$bookedBy/$bookingUid")
                    bookedByRef.child("status").setValue("ACCEPTED")
                    val bookedToRef = FirebaseDatabase.getInstance().getReference("booked_to/$bookedTo/$bookingUid")
                    bookedToRef.child("status").setValue("ACCEPTED")
                })
                .setNegativeButton("Decline", DialogInterface.OnClickListener { dialog, _ ->
                    val bookingUid = orderClicked.uid!!
                    val bookedBy = orderClicked.userUid!!
                    val bookedTo = orderClicked.service_provider_uid!!
                    val bookedByRef = FirebaseDatabase.getInstance().getReference("booked_by/$bookedBy/$bookingUid")
                    val bookedToRef = FirebaseDatabase.getInstance().getReference("booked_to/$bookedTo/$bookingUid")
                    bookedByRef.removeValue()
                    bookedToRef.removeValue()
                    dialog.cancel()
                })
        val alert = dialogBuilder.create()
        alert.setTitle("Order")
        alert.show()

    }

    private fun checkStatus() {
        if (orderClicked.status == "NEW") {
            markButton.isGone = false
            completeButton.isGone = true
        } else if (orderClicked.status == "ACCEPTED") {
            markButton.isGone = true
            completeButton.isGone = false
        } else {
            markButton.isGone = true
            completeButton.isGone = true
        }
    }

    private fun goToChatLogActivity() {
        val ref = FirebaseDatabase.getInstance().getReference("users/${orderClicked.userUid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val intent = Intent(v.context, ChatLogActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fetchNameAndNumber() {
        try {
            val ref = FirebaseDatabase.getInstance().getReference("users/${orderClicked.userUid}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!
                    name.text = "Name: ${user.firstName} ${user.lastName}"
                    contactNumber.text = "Contact Number: ${user.mobileNumber}"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        } catch (e: Exception) {
            name.text = "Name: Account Deleted"
            contactNumber.text = "Contact Number: Account Deleted"
        }

    }
}
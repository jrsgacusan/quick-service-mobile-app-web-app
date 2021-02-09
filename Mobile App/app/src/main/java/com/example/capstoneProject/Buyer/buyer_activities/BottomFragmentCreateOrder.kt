package com.example.capstoneProject.Buyer.buyer_activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.capstoneProject.Buyer.BuyerActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.Models.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BottomFragmentCreateOrder :BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme
    private lateinit var v: View
    private lateinit var dateButton: Button
    private lateinit var dateTextView : TextView
    private lateinit var timePicker: TimePicker
    private lateinit var addressEditText: EditText
    private lateinit var button: Button
    private lateinit var spinner:Spinner
    private lateinit var modeEditText: EditText

    companion object {
        const val TAG =  "JustSomeRandomTag"
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_bottom_create_booking, container, false)


        spinner = v.findViewById(R.id.spinnerModeOfPayment)
        modeEditText = v.findViewById(R.id.modeEditText_fragmentBottomSheet)
        dateButton = v.findViewById(R.id.dateButton_fragmentBottomSheet)
        dateTextView= v.findViewById(R.id.dateTextView_fragmentBottomSheet)
        timePicker = v.findViewById(R.id.timePicker_fragmentBottomSheet)
        addressEditText= v.findViewById(R.id.addressEditText_fragmentBottomSheet)
        button= v.findViewById(R.id.button_FragmentBottomCreateOrder)
        button.text = "Book Now (₱${DisplaySpecificServiceActivity.serviceToBeOrdered!!.price})"
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        dateButton.setOnClickListener {
            dateTextView.error = null
            dateTextView.text = null

            val dpd = DatePickerDialog(v.context, { _, year, month, dayOfMonth ->
                checkDatePicked(year, month, dayOfMonth)


            }, year, month, day)
            dpd.show()
        }

        button.setOnClickListener {
            checkEntries()
        }

        modeOfPayment()



        return v
    }

    private fun modeOfPayment() {
        //Add the different service categories to the spinner
        ArrayAdapter.createFromResource(v.context, R.array.modeOfPayment, android.R.layout.simple_spinner_item)
                .also {
                    adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
        val arrayList = resources.getStringArray(R.array.modeOfPayment)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View,
                                        arg2: Int, arg3: Long) {
                modeEditText.setText(arrayList[arg2])
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {
                modeEditText.text = null

            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDatePicked(yearPicked: Int, monthPicked: Int, dayOfMonthPicked: Int) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val formatted = current.format(formatter)

        val currentYear = formatted.substring(0,4).toInt()
        val currentMonth = formatted.substring(4, 6).toInt()
        val currentDay = formatted.substring(6).toInt()

        val monthPickedPlusOne = monthPicked + 1

        Log.d(TAG, "CURRENT_DATE : Year: $currentYear Month: $currentMonth Day: $currentDay")
        Log.d(TAG, "PICKED_DATE: Year: $yearPicked Month: $monthPickedPlusOne Day: $dayOfMonthPicked")

        if (yearPicked < currentYear){
            displayToast()
            return
        }
        if ((monthPickedPlusOne < currentMonth)&& (yearPicked >= currentYear) ) {
            displayToast()
            return
        }
        if  ((dayOfMonthPicked < currentDay) && (yearPicked >= currentYear && monthPickedPlusOne == currentMonth)){
            displayToast()
            return
        }

        dateTextView.text = "${month(monthPicked)} $dayOfMonthPicked, $yearPicked"




    }

    private fun showTheDialog() {
        val dialogBuilder = AlertDialog.Builder(v.context)
        dialogBuilder.setMessage("Book service now.")
                .setCancelable(true)
                .setPositiveButton("Continue(₱${DisplaySpecificServiceActivity.serviceToBeOrdered!!.price})") { _, _ ->
                    //This is the part where the payment comes in.
                    //Implement it here
                    //For now, just create the order.
                    createTheOrder()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
        val alert = dialogBuilder.create()
        alert.setTitle("Continue?")
        alert.show()
    }

    private fun createTheOrder() {

        //current user uid
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        // uid of the service provider
        val serviceProviderUid = DisplaySpecificServiceActivity.serviceToBeOrdered!!.userUid!!
        //reference
        val bookedByRef = FirebaseDatabase.getInstance().getReference("booked_by/$currentUserUid")
        val bookedToRef = FirebaseDatabase.getInstance().getReference("booked_to/$serviceProviderUid")
        val bookingUid = bookedByRef.push().key!!
        //get the name of the one who is ordering
        val userRef = FirebaseDatabase.getInstance().getReference("users/$currentUserUid")
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                val order = Order(
                        uid= bookingUid,
                        service_booked_uid = DisplaySpecificServiceActivity.serviceToBeOrdered!!.uid,
                        address = addressEditText.text.toString(),
                        date = dateTextView.text.toString(),
                        name = "${user.firstName} ${user.lastName}",
                        price =  DisplaySpecificServiceActivity.serviceToBeOrdered!!.price,
                        time = getTheTime(),
                        title = DisplaySpecificServiceActivity.serviceToBeOrdered!!.title,
                        category = DisplaySpecificServiceActivity.serviceToBeOrdered!!.category,
                        description = DisplaySpecificServiceActivity.serviceToBeOrdered!!.description,
                        service_provider_uid = serviceProviderUid,
                        userUid = currentUserUid,
                        modeOfPayment = modeEditText.text.toString()
                        )
                bookedByRef.child(bookingUid).setValue(order)
                bookedToRef.child(bookingUid).setValue(order)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val notificationsRef = FirebaseDatabase.getInstance().getReference("/notifications/")
        notificationsRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.child("$serviceProviderUid").getValue().toString().toInt()
                notificationsRef.child("$serviceProviderUid").setValue(count + 1)

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        val intent = Intent (v.context, BuyerActivity::class.java )
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(TAG, TAG)
        startActivity(intent)




    }

    private fun displayToast() {
        Toast.makeText(v.context, "Invalid date. You cannot enter a date from the past.", Toast.LENGTH_LONG).show()
    }

    private fun getTheTime(): String{
        var output: String
        var hr = 0
        var text = ""

        if  (timePicker.hour >= 13){
            hr = timePicker.hour - 12
            text = "PM"
        } else if (timePicker.hour == 12){
            hr = timePicker.hour
            text = "PM"
        } else {
            hr = timePicker.hour
            text = "AM"
        }

        output = "$hr:${timePicker.minute} $text"
        return output


    }

    private fun checkEntries() {
        if (dateTextView.text.isEmpty()){
            dateTextView.error = "Please pick a date."
            dateButton.requestFocus()
            return
        }

        if (addressEditText.text.isEmpty()) {
            addressEditText.error = "Please enter the address."
            addressEditText.requestFocus()
            return
        }

        if((timePicker.hour >= 0 && timePicker.hour <= 7) || (timePicker.hour >= 20 && timePicker.hour <= 23)){
            Toast.makeText(
                v.context,
                "You cannot set the time from 8 in the evening until 8 in the morning.",
                Toast.LENGTH_SHORT
            ).show()
            timePicker.requestFocus()
            return
        }

        if (modeEditText.text.isEmpty()) {
            modeEditText.error = "Choose your mode of payment for the Service Provider."
            modeEditText.requestFocus()
        }

        showTheDialog()
    }


    private fun month(monthOfYear: Int): String? {
        when (monthOfYear) {
            0 -> {
                return "January"
            }
            1 -> {
                return "February"
            }
            2 -> {
                return "March"
            }
            3 -> {
                return "April"
            }
            4 -> {
                return "May"
            }
            5 -> {
                return "June"
            }
            6 -> {
                return "July"
            }
            7 -> {
                return "August"
            }
            8 -> {
                return "September"
            }
            9 -> {
                return "October"
            }
            10 -> {
                return "November"
            }
            else -> {return "December"}
        }

    }
}
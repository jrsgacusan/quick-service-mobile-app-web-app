package com.example.capstoneProject.Dialogs

import android.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.example.capstoneProject.R
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.Models.UserReview
import com.example.capstoneProject.Models.UserSellerInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewDialog(fragment: Fragment, order: Order) {
    private val fragment: Fragment = fragment
    private val order: Order = order
    private var dialog: AlertDialog? = null
    private lateinit var ratingBar: RatingBar
    private lateinit var confirmButton: Button
    private lateinit var laterButton: Button
    private lateinit var editText: EditText
    lateinit var v: View


    fun showReviewDialog(){
        val builder = AlertDialog.Builder(fragment.context)
        val inflater = fragment!!.layoutInflater
        v  = inflater.inflate(R.layout.dialog_review, null)
        builder.setView(v)
        builder.setCancelable(false)

        ratingBar = v.findViewById(R.id.ratingBar_dialogReview)
        confirmButton = v.findViewById(R.id.confirmButton_dialogReview)
        laterButton = v.findViewById(R.id.laterButton_dialogReview)
        editText = v.findViewById(R.id.editText_dialogReview)

        laterButton.setOnClickListener {
            dismissDialog()
        }

        confirmButton.setOnClickListener {
            submitReview()
        }




        dialog = builder.create()
        dialog!!.show()








    }

    private fun submitReview() {
        val userBeingReviewed = order.service_provider_uid!!
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("reviews/$userBeingReviewed")
        val currentUserRef = FirebaseDatabase.getInstance().getReference("users/$currentUserUid")
        currentUserRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser  = snapshot.getValue(User::class.java)!!
                val id = ref.push().key!!
                val review = UserReview(
                        uid = id,
                        fname= currentUser.firstName,
                        lname = currentUser.lastName,
                        userImage = currentUser.profileImageUrl,
                        userUid = currentUserUid,
                        review= editText.text.toString(),
                        rating =  ratingBar.rating.toInt()
                )
                ref.child(id).setValue(review)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val bookedByRef = FirebaseDatabase.getInstance().getReference("booked_by/${order.userUid}/${order.uid}")
        bookedByRef.child("/reviewed").setValue(true)

        val bookedToRef = FirebaseDatabase.getInstance().getReference("booked_to/${order.service_provider_uid}/${order.uid}")
        bookedToRef.child("/reviewed").setValue(true)

        val refToTheSeller = FirebaseDatabase.getInstance().getReference("user_seller_info/${order.service_provider_uid}")
        refToTheSeller.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val sellerInfo = snapshot.getValue(UserSellerInfo::class.java)!!
                val count = sellerInfo.count!!
                val totalRating = sellerInfo.totalRating!!

                refToTheSeller.child("count").setValue(count + 1)
                refToTheSeller.child("totalRating").setValue(totalRating + ratingBar.rating.toInt())

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        Toast.makeText(v.context  , "Thank you for the review!", Toast.LENGTH_SHORT).show()
        dismissDialog()



    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }

}
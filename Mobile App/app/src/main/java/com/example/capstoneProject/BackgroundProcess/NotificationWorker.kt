package com.example.capstoneProject.BackgroundProcess

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.karn.notify.Notify

class NotificationsWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {

    override fun doWork(): Result {

        // Do the work here
        showNotification()
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun showNotification() {
        val currentUser: String = FirebaseAuth.getInstance().currentUser!!.uid
        val notificationsRef = FirebaseDatabase.getInstance().getReference("/notifications/")
        notificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                try {
                    val count = snapshot.child(currentUser).value.toString().toInt()
                    if (count > 0) {
                        Notify
                                .with(applicationContext)
                                .content { // this: Payload.Content.Default
                                    title = "New Booking"
                                    text = "You have a new booking, please check your orders."
                                }
                                .show()
                    }
                    notificationsRef.child(currentUser).setValue(0)
                } catch (e: Exception) {

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}







@file:Suppress("DEPRECATION")

package com.example.capstoneProject.Messages

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.Message
import com.example.capstoneProject.GroupieViews.LatestMessageRow
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class MessagesActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var hideThisTextView: TextView
    lateinit var hideThisImageView: ImageView

    val adapter = GroupAdapter<ViewHolder>()


    companion object{
        val TAG = "SAMPLE_TAG"
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        deleteNotifications()
        listenForLatestMessages()
        //map everything here
        toolbar = findViewById(R.id.toolbar_activityMessages)
        actionButton = findViewById(R.id.actionButton_activityMessages)
        recyclerView = findViewById(R.id.recylclerView_activityMessages)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        hideThisTextView = findViewById(R.id.hideThisTextView_activityMessages)
        hideThisImageView = findViewById(R.id.hideThisImageView_activityMessages)
        //action button
        actionButton.isVisible = false
        actionButton.setOnClickListener{
            val intent = Intent(this, CreateNewMessageActivity::class.java)
            startActivity(intent)
        }
        //Toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setTitle("Mesages")

        recyclerView.adapter = adapter
        //set item click listener to the adapter
        adapter.setOnItemClickListener { item, _ ->
            Log.d(TAG, item.toString())
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra("user", row.chatPartnerUser)
            startActivity(intent)
        }
        //On item long click is for deleting a message
        adapter.setOnItemLongClickListener { item, _ ->
            val row = item as LatestMessageRow
            showDialogFun(row)

            return@setOnItemLongClickListener(true)
        }
    }

    private fun showDialogFun(row: LatestMessageRow) {
        //Dialog before sign out
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete this conversation?")
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton("Delete", DialogInterface.OnClickListener {
                        _, _ ->
                    //pERFORM THE DELETION
                    val myUid = FirebaseAuth.getInstance().currentUser!!.uid
                    val partnerUid = row.chatPartnerUser!!.uid
                    val refToUserMessages = FirebaseDatabase.getInstance().getReference("/user_messages/$myUid/$partnerUid")
                    val refToLatestMessages = FirebaseDatabase.getInstance().getReference("/latest_messages/$myUid/$partnerUid")
                    Log.d(TAG, myUid)
                    Log.d(TAG, partnerUid!!)
                    refToUserMessages.removeValue()
                    refToLatestMessages.removeValue()
                    finish()
                    startActivity(Intent(this, MessagesActivity::class.java))
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.cancel()
                })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("DELETE CONVERSATION")
        // show alert dialog
        alert.show()

    }
    val latestMessagesMap = HashMap<String, Message>()
    private fun refreshRecyclerView(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
        hideOrShowViews()
    }
    fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            //New user messages us
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = message
                refreshRecyclerView()
                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            }

            //old user messages us again with a new message.
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = message
                refreshRecyclerView()
                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
//                val key = snapshot.key
//                latestMessagesMap.remove(latestMessagesMap[key])
//                refreshRecyclerView()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun hideOrShowViews() {
        if(adapter.getItemCount() == 0){
            hideThisTextView.isVisible = true
            hideThisImageView.isVisible =  true

        } else {
            hideThisTextView.isVisible = false
            hideThisImageView.isVisible =  false
        }
    }

    private fun deleteNotifications() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val notificationsRef = FirebaseDatabase.getInstance().getReference("notifications/")
        notificationsRef.child("$currentUser").setValue(0)
    }



}



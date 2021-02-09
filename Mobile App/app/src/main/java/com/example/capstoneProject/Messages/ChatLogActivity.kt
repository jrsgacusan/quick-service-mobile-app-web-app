@file:Suppress("DEPRECATION")

package com.example.capstoneProject.Messages

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.Buyer.BuyerActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.Message
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.GroupieViews.ChatFromItem
import com.example.capstoneProject.GroupieViews.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    lateinit var toolbar: Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var button: Button
    lateinit var editText: EditText
    val adapter = GroupAdapter<ViewHolder>()


    var toUser: User? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra<User>("user")
        //Map everything here
        toolbar= findViewById(R.id.toolbar_activityChatLog)
        recyclerView = findViewById(R.id.recyclerView_activityChatLog)
        button = findViewById(R.id.button_activityChatLog)
        editText = findViewById(R.id.editText_activityChatLog)
        //Attach the adapter to recycler view
        recyclerView.adapter = adapter

        //Toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setTitle(toUser!!.firstName + " " + toUser!!.lastName)

        //Handle the recycler view here
        //setUpDummyData()
        listenForMessages()

        //button
        button.setOnClickListener {
            Log.d(TAG, "Attempted to send message.")
            performSendMessage()

        }


    }


    private fun listenForMessages() {
        val toId = toUser!!.uid
        val fromId = FirebaseAuth.getInstance().currentUser!!.uid
       // val messageHandler = MessageHandler()
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {

                    if (message.fromId == FirebaseAuth.getInstance().currentUser!!.uid) {
                        val currentUser = BuyerActivity.currentUser ?: return
                        adapter.add(ChatFromItem(message.text!!, currentUser, message.timeStamp!!))
                    } else {
                        adapter.add(ChatToItem(message.text!!, toUser!!, message.timeStamp!!))
                    }
                }
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun performSendMessage() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val getNameRef = FirebaseDatabase.getInstance().getReference("users/$currentUser")
        getNameRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val name =  "${user!!.firstName} ${user.lastName}"

                if (editText.text.toString().isEmpty()) return
                val toId = toUser!!.uid
                val fromId = FirebaseAuth.getInstance().currentUser!!.uid
                val text = editText.text.toString()
                val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()
                val toRef = FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()
                val message = Message(uid = ref.key, text = text, fromId = fromId, toId = toId, timeStamp = System.currentTimeMillis(), senderName = BuyerActivity.currentUser!!.firstName + BuyerActivity.currentUser!!.lastName)
                ref.setValue(message)
                    .addOnSuccessListener { editText.text.clear()
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
                toRef.setValue(message)

                val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest_messages/$fromId/$toId")
                latestMessageRef.setValue(message)

                val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest_messages/$toId/$fromId")
                latestMessageToRef.setValue(message)



            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }



}




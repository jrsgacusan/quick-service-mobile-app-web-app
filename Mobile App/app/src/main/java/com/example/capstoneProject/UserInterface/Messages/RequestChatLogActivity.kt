package com.example.capstoneProject.UserInterface.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.GroupieViews.ChatFromItem
import com.example.capstoneProject.GroupieViews.ChatToItem
import com.example.capstoneProject.Models.Message
import com.example.capstoneProject.Models.RequestMessage
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.R
import com.example.capstoneProject.UserInterface.Buyer.BuyerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class RequestChatLogActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var button: Button
    lateinit var editText: EditText
    val adapter = GroupAdapter<ViewHolder>()
    private var toUser: User? = null
    private var request: ServiceRequest? = null

    companion object {
        const val USER_EXTRA_TAG = "USERINTENTTAG"
        const val REQUEST_EXTRA_TAG = "REQUESTEXTRATAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_chat_log)

        toUser = intent.getParcelableExtra(USER_EXTRA_TAG)
        request = intent.getParcelableExtra(REQUEST_EXTRA_TAG)
        //Map everything here
        toolbar = findViewById(R.id.toolbar_request)
        recyclerView = findViewById(R.id.recyclerView_request)
        button = findViewById(R.id.button_request)
        editText = findViewById(R.id.editText_request)
        //Attach the adapter to recycler view
        recyclerView.adapter = adapter

        //Toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.title = request!!.title.toString()


        button.setOnClickListener {
            if (editText.text.isEmpty()) {
                Toast.makeText(this, "Add text", Toast.LENGTH_SHORT).show()
                editText.requestFocus()
            } else {
                performSendMessage()
            }
        }

        messagesListener()


    }

    private fun messagesListener() {
        val toId = toUser!!.uid
        val fromId = FirebaseAuth.getInstance().currentUser!!.uid
        // val messageHandler = MessageHandler()
        val ref = FirebaseDatabase.getInstance().getReference("/request_messages/$fromId/${request!!.uid}/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    //If it's the message of the person in conversation with
                    if (message.fromId == FirebaseAuth.getInstance().currentUser!!.uid) {
                        val currentUser = BuyerActivity.currentUser ?: return
                        adapter.add(ChatFromItem(message.text!!, currentUser, message.timeStamp!!))
                    }
                    //If it's a from you.
                    else {
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
        //Current user UID
        val fromId = FirebaseAuth.getInstance().currentUser!!.uid
        val dataRef = FirebaseDatabase.getInstance().getReference("users/$fromId")
        dataRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserData = snapshot.getValue(User::class.java)
                //Send message to this UID
                val toId = toUser!!.uid
                //The message
                val text = editText.text.toString()
                val ref = FirebaseDatabase.getInstance().getReference("/request_messages/$fromId/${request!!.uid}/$toId").push()
                val toRef = FirebaseDatabase.getInstance().getReference("/request_messages/$toId/${request!!.uid}/$fromId").push()
                val message = RequestMessage(
                    uid = ref.key,
                    text = text,
                    fromId = fromId,
                    toId = toId,
                    timeStamp = System.currentTimeMillis(),
                    requestUid = request!!.uid )
                ref.setValue(message)
                    .addOnSuccessListener {
                        editText.text.clear()
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
                toRef.setValue(message)

                val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest_messages_request/$fromId/${request!!.uid}/$toId")
                latestMessageRef.setValue(message)

                val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest_messages_request/$toId/${request!!.uid}/$fromId")
                latestMessageToRef.setValue(message)


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



    }
}
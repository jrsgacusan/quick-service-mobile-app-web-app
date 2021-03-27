package com.example.capstoneProject.UserInterface.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.GroupieViews.LatestMessageRow
import com.example.capstoneProject.GroupieViews.LatestMessagesRowRequest
import com.example.capstoneProject.Models.Message
import com.example.capstoneProject.Models.RequestMessage
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_login.*

class MessagesRequestForSP : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var rv: RecyclerView
    val adapter = GroupAdapter<ViewHolder>()

    companion object{
        const val TAG = "ASDASDASDASDASDDSADAV"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_request_for_s_p)
        toolbar = findViewById(R.id.toolbar5)
        rv = findViewById(R.id.rv)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.adapter = adapter

        clickListeners()
        listenAgain()

    }

    private fun clickListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, RequestChatLogActivity::class.java)
            val row = item as LatestMessagesRowRequest

            val ref = FirebaseDatabase.getInstance().getReference("service_requests/${row.message.requestUid}")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val requestData = snapshot.getValue(ServiceRequest::class.java)
                    intent.putExtra(RequestChatLogActivity.USER_EXTRA_TAG, row.chatPartnerUser)
                    intent.putExtra(RequestChatLogActivity.REQUEST_EXTRA_TAG, requestData)
                    startActivity(intent)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    }


    val latestMessagesMap = HashMap<String, RequestMessage>()
    private fun refreshRecyclerView() {
        adapter.clear()
        latestMessagesMap.values.forEach { message ->
            adapter.add(LatestMessagesRowRequest(message))
        }

    }


    fun listenAgain(){
        val fromId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages_request/$fromId/")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { requestUid ->
                    requestUid.children.forEach { latestMessage ->
                        val latestMessageValue = latestMessage.getValue(RequestMessage::class.java)
                        Log.i(TAG, latestMessageValue!!.uid!!)
                        val message = latestMessage.getValue(RequestMessage::class.java) ?: return
                        latestMessagesMap[latestMessageValue.uid.toString()] = message

                    }


                }
                refreshRecyclerView()

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}
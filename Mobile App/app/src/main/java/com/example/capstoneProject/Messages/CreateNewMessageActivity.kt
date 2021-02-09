package com.example.capstoneProject.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.R
import com.example.capstoneProject.Handlers.UserHandler
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.GroupieViews.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class CreateNewMessageActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView

    lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_message)
        recyclerView = findViewById(R.id.recyclerView_activityCreateNewMessage)
        toolbar= findViewById(R.id.toolbar_activityCreateNewMessage)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        fetchUser()
    }

    private fun fetchUser() {
        val userHandler = UserHandler()
        userHandler.userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach { it ->
                    val user = it.getValue(User::class.java)
                    if (user!!.uid != currentUserUid ){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{ item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra("user",item.user)
                    startActivity(intent)
                    finish()
                }
                recyclerView.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })

    }

}


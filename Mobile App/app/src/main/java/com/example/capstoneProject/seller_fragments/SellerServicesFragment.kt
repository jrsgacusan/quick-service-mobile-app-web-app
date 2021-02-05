package com.example.capstoneProject.seller_fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.seller_activities.CreateServicesActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.SellerActivity
import com.example.capstoneProject.bottomNavigationSeller
import com.example.capstoneProject.buyer_activities.DisplaySpecificServiceActivity
import com.example.capstoneProject.models.Service
import com.example.capstoneProject.seller_activities.ImageSliderModel
import com.example.capstoneProject.views.ServiceCreatedItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder


class SellerServicesFragment : Fragment() {
    lateinit var v: View
    var currentUserUid = FirebaseAuth.getInstance().uid
    lateinit var hideThisTextView: TextView
    lateinit var hideThisImageView: ImageView
    lateinit var clickHereTextView: TextView
    lateinit var recyclerView: RecyclerView
    var adapter = GroupAdapter<ViewHolder>()

    companion object {
        var serviceGettingEdited: Service? = null
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_seller_services, container, false)
        //Map the view
        recyclerView = v.findViewById(R.id.recyclerView_fragmentSellerServices)
        recyclerView.addItemDecoration(DividerItemDecoration(v.context, DividerItemDecoration.VERTICAL))
        hideThisTextView = v.findViewById(R.id.hideThisTextView_fragmentSellerServices)
        hideThisImageView = v.findViewById(R.id.hideThisImageView_fragmentSellerServices)
        clickHereTextView = v.findViewById(R.id.clickHereTextView_fragmentSellerServices)
        //Register the list view for context menu. Context menu for editing and deleting.
        //register the recycler view later
       // registerForContextMenu()
        clickHereTextView.setOnClickListener {
            startActivity(Intent(v.context, CreateServicesActivity::class.java))
        }

        fetchServices()
        return v
    }



    private fun hideOrShowViews() {
        if (adapter.getItemCount()== 0){
            hideThisTextView.isVisible = true
            hideThisImageView.isVisible = true
            clickHereTextView.isVisible= true
        } else {
            hideThisTextView.isVisible = false
            hideThisImageView.isVisible = false
            clickHereTextView.isVisible= false
        }
    }

    fun fetchServices(){
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/services/$currentUser")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                adapter.clear()
                p0.children.forEach {
                    val service = it.getValue(Service::class.java)
                    adapter.add(ServiceCreatedItem(service!!,v.context))
                    //Listener for every click
                    adapter.setOnItemClickListener { item, view ->
                        val row = item as ServiceCreatedItem
                        val serviceClicked = row.service
                        Log.d("ServiceClickedTag", serviceClicked.status.toString())
                        showMenu( view, serviceClicked.status.toString(), serviceClicked)
                    }
                    recyclerView.adapter = adapter
                    hideOrShowViews()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }


    fun showMenu(view: View, status:String, service: Service){
        val popupMenu: PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_edit_delete,popupMenu.menu)
        val pause = popupMenu.menu.findItem(R.id.pause)
        val setAsActive = popupMenu.menu.findItem(R.id.setAsActive)
        val preview = popupMenu.menu.findItem(R.id.preview)
        preview.isVisible = true
        if (status == "PENDING" ){
            pause.isVisible = false
            setAsActive.isVisible = false
        } else if (status =="ACTIVE"){
            pause.isVisible = true
            setAsActive.isVisible = false
        } else if (status == "PAUSED"){
            pause.isVisible = false
            setAsActive.isVisible = true
        }
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit -> {
                    serviceGettingEdited = service
                    startActivity(Intent(v.context, CreateServicesActivity::class.java))
                }
                R.id.delete -> {
                    //Dialog before sign out
                    val dialogBuilder = AlertDialog.Builder(v.context)
                    // set message of alert dialog
                    dialogBuilder.setMessage("Do you want to delete this Service?")
                            // if the dialog is cancelable
                            .setCancelable(true)
                            // positive button text and action
                            .setPositiveButton("Continue", DialogInterface.OnClickListener { _, _ ->
                                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                                val ref = FirebaseDatabase.getInstance().getReference("/services/$currentUser/")
                                ref.child(service.uid!!).removeValue()
                                deleteImagesFromFirebase(service.uid!!)
                                Toast.makeText(v.context, "Service Deleted", Toast.LENGTH_SHORT).show()
                            })
                            // negative button text and action
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                                dialog.cancel()
                            })
                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Delete")
                    // show alert dialog
                    alert.show()
                }
                R.id.pause -> {
                    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                    val ref = FirebaseDatabase.getInstance()
                            .getReference("/services/$currentUser/${service.uid}")
                    ref.child("status").setValue("PAUSED")
                }
                R.id.setAsActive -> {
                    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                    val ref = FirebaseDatabase.getInstance()
                            .getReference("/services/$currentUser/${service.uid}")
                    ref.child("status").setValue("ACTIVE")
                }
                R.id.preview -> {
                        val intent = Intent(view.context, DisplaySpecificServiceActivity::class.java)
                        intent.putExtra("service", service)
                        startActivity(intent)
                        DisplaySpecificServiceActivity.viewOnlyMode = true
                }
            }
            true
        })
        //show the icons
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
        } catch (e: Exception) {
            //Log the exception here
        } finally {
            popupMenu.show()
        }


    }

    override fun onResume() {
        super.onResume()
        // Set title bar
        (activity as SellerActivity?)?.setActionBarTitle("Services")
        bottomNavigationSeller.menu.findItem(R.id.Seller_servicesPage).setChecked(true)
        (activity as SellerActivity).menuToHide.setVisible(true)
        serviceGettingEdited = null
    }
    override fun onStop() {
        super.onStop()
        (activity as SellerActivity).menuToHide.setVisible(false)


    }

    private fun deleteImagesFromFirebase(serviceUid: String) {
        val deleteRef = FirebaseDatabase.getInstance().getReference("/Sliders/$serviceUid")
        deleteRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val i =  it.getValue(ImageSliderModel::class.java)!!
                    var url = i.url!!
                    Log.i("sadasdasdasdas", url)
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                    storageRef.delete()
                    deleteRef.child(i.uid!!).removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


    }
}


package com.example.capstoneProject.UserInterface.ServiceProvider.SellerFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.capstoneProject.UserInterface.Buyer.BuyerActivity
import com.example.capstoneProject.UserInterface.Buyer.BuyerFragments.ProfileFragment
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.R
import com.example.capstoneProject.UserInterface.ServiceProvider.SellerActivity
import com.example.capstoneProject.UserInterface.ServiceProvider.bottomNavigationSeller
import com.example.capstoneProject.UserInterface.ServiceProvider.SellerActivities.AboutMeAsSellerActivity
import com.example.capstoneProject.UserInterface.ServiceProvider.SellerActivities.BuyersRequestActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class SellerProfileFragment : Fragment() {

    lateinit var switchBtn: Switch
    val sellerServices = SellerServicesFragment()
    lateinit var profileImage: ImageView
    lateinit var nameTextView: TextView
    lateinit var services: CardView
    lateinit var buyersRequestCardView: CardView
    lateinit var aboutMeAsASellerCardView: CardView
    lateinit var v: View
    var uid = FirebaseAuth.getInstance().uid

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_seller_profile, container, false)

        //map the views of the layout file
        aboutMeAsASellerCardView = v.findViewById(R.id.seller_aboutMeCardView)
        switchBtn = v.findViewById<Switch>(R.id.seller_modeSwitch)
        services = v.findViewById<CardView>(R.id.seller_myServicesCardView)
        profileImage = v.findViewById(R.id.profileImage_fragmentSellerProfile)
        nameTextView = v.findViewById(R.id.userName_fragmentSellerProfile)
        buyersRequestCardView = v.findViewById(R.id.buyersRequestCardView_fragmentSellerProfile)

        //Fetch user data
        fetchUserData()

        //Add listeners
        //Switch button view
        switchBtn.setOnClickListener {
            changeMode(switchBtn.isChecked)
            Toast.makeText(this.context, "Switched to Buyer Mode", Toast.LENGTH_LONG).show()
        }
        //Services card view
        services.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.wrapper, sellerServices)
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }
        //Buyers Request
        buyersRequestCardView.setOnClickListener {
            val intent = Intent(v.context, BuyersRequestActivity::class.java)
            startActivity(intent)
        }
        //About me as a seller
        aboutMeAsASellerCardView.setOnClickListener {
            val intent = Intent(v.context, AboutMeAsSellerActivity::class.java)
            startActivity(intent)

        }



        return v
    }

    private fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                Picasso.get().load(user!!.profileImageUrl).into(profileImage)
                nameTextView.text = "${user.firstName} ${user.lastName}"
            }

            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }


    //Change the mode based from the switch view
    private fun changeMode(state: Boolean) {
        if (state == false) {
            switchBtn.isChecked = true
            val intent = Intent(this.context, BuyerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            ProfileFragment.isBuyerMode = false
        }
    }

    companion object;

    override fun onResume() {
        super.onResume()
        // Set title bar
        (activity as SellerActivity?)?.setActionBarTitle("Profile Page")
        (activity as SellerActivity).menuItem =
                bottomNavigationSeller.menu.findItem(R.id.Seller_profilePage).setChecked(true)

    }


}
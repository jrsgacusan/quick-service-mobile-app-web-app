package com.example.capstoneProject.ServiceProvider.seller_activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.Service
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.ServiceProvider.seller_fragments.SellerServicesFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class CreateServicesActivity : AppCompatActivity(){
    private lateinit var spinner: Spinner
    private lateinit var titleEditText : EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var button: Button
    private var currentUserUid = FirebaseAuth.getInstance().uid
    private lateinit var categoryEditText: EditText
    private lateinit var toolBar: Toolbar
    private lateinit var counterTextView: TextView
    private lateinit var imagesListView: ListView
    private lateinit var addButton: Button
    private val arrayListImagesToBeUploaded: ArrayList<Uri> = ArrayList()
    private lateinit var imagesArrayAdapter: ArrayAdapter<Uri>



    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_services)

        //Check if the seller info is edited
        checkIfSellerInfoIsEdited()

        //Map the views from the layout
        imagesArrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, arrayListImagesToBeUploaded)
        imagesListView = findViewById(R.id.imagesListView_activityCreateServices)
        addButton = findViewById(R.id.addPhotoButton_activityCreateServices)
        spinner = findViewById(R.id.spinner_createServices)
        titleEditText = findViewById(R.id.title_createServices)
        descriptionEditText = findViewById(R.id.description_createServices)
        priceEditText = findViewById(R.id.price_createServices)
        button = findViewById(R.id.button_createServices)
        categoryEditText = findViewById(R.id.category_activityCreateServices)
        toolBar = findViewById(R.id.toolBar_activityCreateServices)
        counterTextView = findViewById(R.id.copunter_activityCreateServices)
        //Toolbar
        toolBar.setNavigationOnClickListener {
            finish()
        }
        //For the list view
        imagesListView.setOnTouchListener { v, _ ->
            // Setting on Touch Listener for handling the touch inside ScrollView
            // Disallow the touch request for parent scroll on touch of child view
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        //Add the different service categories to the spinner
        ArrayAdapter.createFromResource(this, R.array.services_category, android.R.layout.simple_spinner_item)
            .also {
                    adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        val arrayList = resources.getStringArray(R.array.services_category)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View,
                                        arg2: Int, arg3: Long) {
                categoryEditText.setText(arrayList[arg2])
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {

            }
        }

        //Check if the value of Service getting edited is null or not
        checkFirst()
        //Button listener
        button.setOnClickListener{
            if (button.text.toString() == "Create" ){
                checkAndCreate()
            }
            if (button.text.toString() == "Update"){
                update()
            }
        }
        //Counter for the editText
        descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val length: Int = descriptionEditText.length()
                val convert = length.toString()
                counterTextView.text = "$convert/300"
            }

            override fun afterTextChanged(s: Editable) {}
        })
        counterTextView.text = "${descriptionEditText.text.toString().length}/300"


        //Listener for the add images
        addButton.setOnClickListener {
            pickImageFromGallery()
        }

        imagesListView.adapter = imagesArrayAdapter



    }
    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
        //Permission code
        // private const val PERMISSION_CODE = 1001;
        var isDialogShown: Boolean? = false
        const val TAG = "Justsomserandomtag"

    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    private var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Log.d(TAG, data!!.data.toString())
            selectedPhotoUri = data.data
            //Array list of images to be uploaded
            arrayListImagesToBeUploaded.add(selectedPhotoUri!!)
            imagesArrayAdapter.notifyDataSetChanged()
        }
    }
    private fun uploadImages(serviceUid: String) {
        if (arrayListImagesToBeUploaded.isNotEmpty()) {
            arrayListImagesToBeUploaded.forEach {
                val fileName = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference("/slider-images/$fileName")
                ref.putFile(it)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                val databaseRef = FirebaseDatabase.getInstance().getReference("/Sliders/$serviceUid")
                                val key = databaseRef.push().key
                                val image = ImageSliderModel(uid = key, url = uri.toString())
                                databaseRef.child(key!!).setValue(image)
                            }
                        }
            }
            finish()
        }
    }



    private fun checkIfSellerInfoIsEdited() {
        if(isDialogShown == false) {
            val dialogBuilder = AlertDialog.Builder(this)
            // set message of alert dialog
            dialogBuilder.setMessage("Make sure to edit your Information as a seller.")
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton("Edit Now") { _, _ ->
                    val intent = Intent(this, AboutMeAsSellerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                    .setNegativeButton("Later") { dialog, _ ->
                    dialog.cancel()
                }
            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Seller Informations")
            // show alert dialog
            alert.show()
            isDialogShown = true
        }


    }
    @SuppressLint("SetTextI18n")
    private fun checkAndCreate() {
        if(titleEditText.text.toString().isEmpty()){
            titleEditText.error = "Please fill this up"
            titleEditText.requestFocus()
            return
        }

        if(titleEditText.text.toString().length <= 14){
            titleEditText.error = "Title is too short, please include more than 15 characters."
            titleEditText.requestFocus()
            return
        }

        if(descriptionEditText.text.toString().isEmpty()){
            descriptionEditText.error = "Please fill this up"
            descriptionEditText.requestFocus()
            return
        }

        if(descriptionEditText.text.toString().length >= 301){
            descriptionEditText.error = "Exceeded 300 characters."
            descriptionEditText.requestFocus()
            return
        }
        if(priceEditText.text.toString().isEmpty()){
            priceEditText.error = "Please fill this up"
            priceEditText.requestFocus()
            return
        }

        //Dialog before sign out
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to create the service?")
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton("Proceed") { _, _ ->
                    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                    val ref = FirebaseDatabase.getInstance().getReference("/users/$currentUser")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            val serviceRef = FirebaseDatabase.getInstance().getReference("/services/$currentUser")
                            val uid = serviceRef.push().key
                            val service = Service(uid = uid,
                                    userImageUrl = user!!.profileImageUrl,
                                    title = titleEditText.text.toString(),
                                    description = descriptionEditText.text.toString(),
                                    price = priceEditText.text.toString().toInt(),
                                    category = spinner.selectedItem.toString(),
                                    userUid = currentUserUid)
                            serviceRef.child(service.uid!!).setValue(service)
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, "Service successfuly created", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(applicationContext, "Failed to create service, try again later", Toast.LENGTH_SHORT).show()
                                    }
                            if (arrayListImagesToBeUploaded.isNotEmpty()){
                                uploadImages(uid!!)
                            } else {
                                finish()
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
                // negative button text and action
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Create Service")
        // show alert dialog
        alert.show()

    }
    private fun update(){
        if(titleEditText.text.toString().isEmpty()){
            titleEditText.error = "Please fill this up"
            titleEditText.requestFocus()
            return
        }

        if(descriptionEditText.text.toString().isEmpty()){
            descriptionEditText.error = "Please fill this up"
            descriptionEditText.requestFocus()
            return
        }

        if(descriptionEditText.text.toString().length >= 301){
            descriptionEditText.error = "Exceeded 300 characters."
            descriptionEditText.requestFocus()
            return
        }
        if(priceEditText.text.toString().isEmpty()){
            priceEditText.error = "Please fill this up"
            priceEditText.requestFocus()
            return
        }

        //Dialog before sign out
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to update the service?")
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton("Update") { _, _ ->
                    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                    val servicesRef = FirebaseDatabase.getInstance().getReference("/services/$currentUser/${SellerServicesFragment.serviceGettingEdited!!.uid}")
                    servicesRef.child("/category").setValue(categoryEditText.text.toString())
                    servicesRef.child("/description").setValue(descriptionEditText.text.toString())
                    servicesRef.child("/price").setValue(priceEditText.text.toString().toLong())
                    servicesRef.child("/title").setValue(titleEditText.text.toString())
                    Toast.makeText(applicationContext, "Service updated", Toast.LENGTH_LONG).show()
                    //If there is an image to be uploaded
                    if(arrayListImagesToBeUploaded.isNotEmpty()){
                        deleteImagesFromFirebase(SellerServicesFragment.serviceGettingEdited!!.uid!!)
                        //upload again
                        uploadImages(SellerServicesFragment.serviceGettingEdited!!.uid!!)
                        finish()
                    } else {
                        finish()
                    }
                }
                // negative button text and action
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Update Service")
        // show alert dialog
        alert.show()


    }
    private fun deleteImagesFromFirebase(serviceUid: String) {
        val deleteRef = FirebaseDatabase.getInstance().getReference("/Sliders/$serviceUid")
        deleteRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val i =  it.getValue(ImageSliderModel::class.java)!!
                    val url = i.url!!
                    Log.i(TAG, url)
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                    storageRef.delete()
                    deleteRef.child(i.uid!!).removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun setCategory(category: String){
        val arrayList = resources.getStringArray(R.array.services_category)
        if (category == arrayList[0]){
            spinner.setSelection(0)
            return
        }
        if (category == arrayList[1]){
            spinner.setSelection(1)
            return
        }
        if (category == arrayList[2]){
            spinner.setSelection(2)
            return
        }
        if (category == arrayList[3]){
            spinner.setSelection(3)
            return
        }
        if (category == arrayList[4]){
            spinner.setSelection(4)
            return
        }
        if (category == arrayList[5]){
            spinner.setSelection(5)
            return
        }
        if (category == arrayList[6]){
            spinner.setSelection(6)
            return
        }
        if (category == arrayList[7]){
            spinner.setSelection(7)
            return
        }
        if (category == arrayList[8]){
            spinner.setSelection(8)
            return
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        SellerServicesFragment.serviceGettingEdited = null
    }
    private fun checkFirst(){
        if (SellerServicesFragment.serviceGettingEdited == null) {
            button.text = "Create"
        } else if (SellerServicesFragment.serviceGettingEdited != null) {
            button.text = "Update"
            //Set the values of the views using the global variable serviceGettingEdited
            titleEditText.setText(SellerServicesFragment.serviceGettingEdited!!.title)
            descriptionEditText.setText(SellerServicesFragment.serviceGettingEdited!!.description)
            priceEditText.setText(SellerServicesFragment.serviceGettingEdited!!.price.toString())
            setCategory(SellerServicesFragment.serviceGettingEdited!!.category!!)
            val snackbar = Snackbar.make(findViewById(R.id.view_activityCreateServices), "Adding new image/images will delete previously added ones.", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Ok") {
                snackbar.dismiss()
            }
            snackbar.show()
            arrayListImagesToBeUploaded.clear()
        }

    }




}

@IgnoreExtraProperties
class ImageSliderModel(val uid: String? = null , val url:String? = null)

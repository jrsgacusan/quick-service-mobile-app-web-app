package com.example.capstoneProject.UserInterface.General

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.capstoneProject.UserInterface.Buyer.BuyerActivity
import com.example.capstoneProject.UserInterface.Dialogs.LoadingDialog
import com.example.capstoneProject.Handlers.UserHandler
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.R
import com.example.capstoneProject.UserInterface.ServiceProvider.SellerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*
import kotlin.collections.ArrayList

class ProfileSettingsActivity : AppCompatActivity() {
    lateinit var userHandler: UserHandler
    lateinit var userArrayList: ArrayList<User>
    lateinit var uid: String
    lateinit var firstNameProfileEditText: EditText
    lateinit var lastNameProfileEditText: EditText
    lateinit var emailAddressProfileEditText: EditText
    lateinit var mobileNumberEditText: EditText
    lateinit var ageEditText: EditText
    lateinit var bioEditText: EditText
    lateinit var profileImageView: ImageView
    lateinit var button: Button
    lateinit var uploadNewImage: ImageView
    val loadingDialog = LoadingDialog(this, "Updating profile...")


    lateinit var profileSettingsToolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        val lastActivity = intent.getStringExtra("intent")
        userArrayList = ArrayList()
        //Map Everything
        uploadNewImage = findViewById(R.id.uploadNewImage_profileSettings)
        button = findViewById(R.id.btn_profileSettings)
        profileImageView = findViewById(R.id.profileImageView_profileSettings)
        firstNameProfileEditText = findViewById(R.id.firstNameEditText_profileSettings)
        profileSettingsToolbar = findViewById(R.id.profileSettingToolBar)
        lastNameProfileEditText = findViewById(R.id.lastNameEditText_profileSettings)
        emailAddressProfileEditText = findViewById(R.id.emailAddEditText_profileSettings)
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText_profileSettings)
        ageEditText = findViewById(R.id.ageEditText_profileSettings)
        bioEditText = findViewById(R.id.userBio_profileSettings)
        uploadNewImage.isVisible = false
        // Initialize Database Handler
        userHandler = UserHandler()
        //Get the current UID of the logged in user
        uid = FirebaseAuth.getInstance().uid!!
        //Set toolbar navigation Icon
        profileSettingsToolbar.setNavigationIcon(R.drawable.ic_back)
        profileSettingsToolbar.setNavigationOnClickListener {
            goBack(lastActivity)
        }
        //Read user data
        readUserData()

        //Button listener
        button.setOnClickListener {
            if (button.text.toString() == "Update") {
                executeUpdate()
            } else if (button.text.toString() == "Save") {
                executeSave()
            }

        }
        uploadNewImage.setOnClickListener {
            chooseImage()
        }

    }


    private fun executeUpdate() {
        //enable the views that will be edited
        firstNameProfileEditText.isEnabled = true
        lastNameProfileEditText.isEnabled = true
        mobileNumberEditText.isEnabled = true
        ageEditText.isEnabled = true
        bioEditText.isEnabled = true
        uploadNewImage.isVisible = true
        //Change the text of the button
        button.text = "Save"
    }

    private fun executeSave() {


        if (mobileNumberEditText.text.toString().length != 12) {
            mobileNumberEditText.error = "Invalid. Please use this format, 639999999999"
            mobileNumberEditText.requestFocus()
            return
        }

        if (firstNameProfileEditText.text.toString().isEmpty() || lastNameProfileEditText.text.toString().isEmpty() || ageEditText.text.toString().isEmpty()) {
            firstNameProfileEditText.error = "Please fill this up"
            firstNameProfileEditText.requestFocus()
            return
        }

        uploadNewImage.isVisible = false
        firstNameProfileEditText.isEnabled = false
        lastNameProfileEditText.isEnabled = false
        mobileNumberEditText.isEnabled = false
        ageEditText.isEnabled = false
        bioEditText.isEnabled = false



        loadingDialog.startLoadingAnimation()
        //Check if there is an image selected
        if (selectedPhotoUri != null) {
            uploadImageToFirebaseDatabase()
        } else {
            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$currentUser")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    val userImageUrl: String = user!!.profileImageUrl.toString()
                    saveUserToFireBaseDatabase(userImageUrl)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
        //Sets the selected photo to null again
        button.text = "Update"
    }

    //Uploading an image to image view override function
    private var selectedPhotoUri: Uri? = null
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, SignUpAcitivity.IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SignUpAcitivity.IMAGE_PICK_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data!!.data.let {
                        launchImageCrop(it)
                    }
                } else {
                    //Log error hgere
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri)
                    selectedPhotoUri = result.uri
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //error message here
                }
            }
        }
    }

    private fun uploadImageToFirebaseDatabase() {
        if (selectedPhotoUri != null) {
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/profile-images/$fileName")
            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            deleteThePreviousImageFromFirebaseDatabase()
                            saveUserToFireBaseDatabase(it.toString())
                            loadingDialog.dismissDialog()
                        }
                    }
        }
    }

    private fun deleteThePreviousImageFromFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user!!.profileImageUrl != SignUpAcitivity.DEFAULT_IMG_URL) {
                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImageUrl!!)
                    storageRef.delete()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun saveUserToFireBaseDatabase(profileImageUrl: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$currentUserUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                val insertUser = User(
                        uid = currentUserUid,
                        bio = bioEditText.text.toString(),
                        emailAddress = emailAddressProfileEditText.text.toString(),
                        profileImageUrl = profileImageUrl,
                        firstName = firstNameProfileEditText.text.toString(),
                        lastName = lastNameProfileEditText.text.toString(),
                        age = ageEditText.text.toString().toInt(),
                        mobileNumber = mobileNumberEditText.text.toString(),
                        verified = user.verified
                )
                if (userHandler.update(insertUser)) {
                    Toast.makeText(applicationContext, "Account info updated", Toast.LENGTH_SHORT).show()
                }
                loadingDialog.dismissDialog()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun goBack(lastActivity: String?) {
        if (lastActivity == "seller") {
            startActivity(Intent(this, SellerActivity::class.java))
        }
        if (lastActivity == "buyer") {
            startActivity(Intent(this, BuyerActivity::class.java))
        }
        finish()
    }

    private fun readUserData() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val userData = p0.getValue(User::class.java)
                firstNameProfileEditText.setText(userData!!.firstName)
                lastNameProfileEditText.setText(userData.lastName)
                emailAddressProfileEditText.setText(userData.emailAddress)
                mobileNumberEditText.setText(userData.mobileNumber)
                ageEditText.setText(userData.age.toString())
                bioEditText.setText(userData.bio)
                //Picasso is a caching manager for the image
                Picasso.get().load(userData.profileImageUrl).into(profileImageView)
            }

            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    override fun onStop() {
        super.onStop()
        selectedPhotoUri = null
    }

    private fun setImage(selectedPhotoUri: Uri) {
        Picasso.get().load(selectedPhotoUri).into(profileImageView)
    }

    private fun launchImageCrop(it: Uri?) {
        CropImage.activity(it)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(500, 500)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setOutputCompressQuality(25)
                .start(this)
    }


}
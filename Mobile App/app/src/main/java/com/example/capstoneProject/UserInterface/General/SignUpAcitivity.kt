package com.example.capstoneProject.UserInterface.General


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.capstoneProject.UserInterface.Dialogs.LoadingDialog
import com.example.capstoneProject.Handlers.UserHandler
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.Models.UserSellerInfo
import com.example.capstoneProject.R
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*


class SignUpAcitivity : AppCompatActivity() {

    lateinit var profileImageView: ImageView
    lateinit var uploadImageView: ImageButton

    lateinit var fname: EditText
    lateinit var lname: EditText
    lateinit var email: EditText
    lateinit var mobileNum: EditText
    lateinit var password: EditText
    lateinit var retypePass: EditText
    lateinit var age: TextView
    private lateinit var auth: FirebaseAuth
    lateinit var userHandler: UserHandler
    var user: FirebaseUser? = null
    private val loadingDialog = LoadingDialog(this, "Creating Account...")


    companion object {
        //image pick code
        const val IMAGE_PICK_CODE: Int = 1000

        //Permission code
        private val PERMISSION_CODE = 1001

        const val DEFAULT_IMG_URL: String = "https://firebasestorage.googleapis.com/v0/b/course-project-88fec.appspot.com/o/default-images%2Fprofile_image_default.webp?alt=media&token=652ce853-0e3a-49ff-9fd7-980d02c6bb32"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        userHandler = UserHandler()
        auth = FirebaseAuth.getInstance()





        //Map the layout file views to the Kotlin file
        fname = findViewById(R.id.firstNameEditText)
        lname = findViewById(R.id.lastNameEditText)
        email = findViewById(R.id.emailAddEditText)
        mobileNum = findViewById(R.id.mobileNumberEditText)
        password = findViewById(R.id.passWordEditText)
        retypePass = findViewById(R.id.retypePasswordEditText)
        age = findViewById(R.id.ageTextView)
        val seekBar: SeekBar = findViewById(R.id.ageSeekBar)
        val signUpBtn = findViewById<Button>(R.id.signUpBtn)
        profileImageView = findViewById(R.id.profileImageView) //Image Views within the layout file
        uploadImageView = findViewById(R.id.uploadImageView) //Image Views within the layout file

        //popup menu for the upload image view listener
        uploadImageView.setOnClickListener {
            chooseImage()
            if (selectedPhotoUri != null) {
                profileImageView.isVisible = true
                uploadImageView.isGone = true
            }
            Log.d("TAGASFDASD", selectedPhotoUri.toString())
        }
        profileImageView.setOnClickListener {
            chooseImage()
        }
        //seek bar
        //age seek bar 18-60
        val step = 1 // 1 step per scroll
        val max = 60 //Maximum age
        val min = 18 //Minimum age
        seekBar.max = (max - min) / step
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
                age.text = (min + (i * step)).toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        //signUpBtn onclick listener
        signUpBtn.setOnClickListener {
            authenticate()
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IMAGE_PICK_CODE -> {
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

    private fun authenticate() {
        if (fname.text.toString().isEmpty()) {
            fname.error = "Please fill this up"
            fname.requestFocus()
            return
        }
        if (lname.text.toString().isEmpty()) {
            lname.error = "Please fill this up"
            lname.requestFocus()
            return
        }
        if (email.text.toString().isEmpty()) {
            email.error = "Please enter email"
            email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Please enter valid email"
            email.requestFocus()
            return
        }
        if (mobileNum.text.toString().length != 12) {
            mobileNum.error = "Invalid. Please use this format, 639999999999"
            mobileNum.requestFocus()
            return
        }
        if (password.text.toString().isEmpty()) {
            password.error = "Please enter password"
            password.requestFocus()
            return
        }
        if ((password.text.toString().isNotEmpty()) && (password.text.toString() != retypePass.text.toString())) {
            retypePass.error = "Please retype password"
            retypePass.requestFocus()
            return
        }
        //If there are no problems, create the user.
        //Dialog before sign up
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("Please make sure that the Email and Mobile Number is still active.")
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton("Create Account", DialogInterface.OnClickListener { _, _ ->
                    loadingDialog.startLoadingAnimation()
                    auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    //Email Verification
                                    user = auth.currentUser
                                    user!!.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    //Upload the image to firebase database, inside this function, the save user to firebase database will also be called.
                                                    uploadImageToFirebaseDatabase()

                                                }
                                            }
                                } else {
                                    try {
                                        val text = task.result.toString()
                                    } catch (e: RuntimeExecutionException) {
                                        val message = e.message!!
                                        Toast.makeText(
                                                this,
                                                message.substringAfter(':', "Try again later."),
                                                Toast.LENGTH_LONG
                                        ).show()
                                        loadingDialog.dismissDialog()

                                    }
                                }
                            }
                })
                // negative button text and action
                .setNegativeButton("Edit", DialogInterface.OnClickListener { dialog, _ ->
                    email.requestFocus()
                    dialog.cancel()
                })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Continue?")
        // show alert dialog
        alert.show()
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/${user!!.uid}")
        val user = User(
                uid = user!!.uid,
                emailAddress = email.text.toString(),
                firstName = fname.text.toString(),
                lastName = lname.text.toString(),
                age = age.text.toString().toInt(),
                profileImageUrl = profileImageUrl,
                mobileNumber = mobileNum.text.toString()
        )
        ref.setValue(user)
        //Create the seller info
        val anotherRef = FirebaseDatabase.getInstance().getReference("user_seller_info/${user.uid}")
        val userSellerInfo = UserSellerInfo(
                uid = user.uid
        )
        anotherRef.setValue(userSellerInfo)
        //create the notification for that user
        val notificationsRef = FirebaseDatabase.getInstance().getReference("notifications/")
        notificationsRef.child("${user.uid}").setValue(0)

        loadingDialog.dismissDialog()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    private fun uploadImageToFirebaseDatabase() {
        if (selectedPhotoUri != null) {
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/profile-images/$fileName")
            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            saveUserToFirebaseDatabase(it.toString())
                            loadingDialog.dismissDialog()
                        }
                    }
            selectedPhotoUri = null
        } else if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase(DEFAULT_IMG_URL)
        }

    }


}


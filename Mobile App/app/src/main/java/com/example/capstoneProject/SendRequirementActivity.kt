@file:Suppress("DEPRECATION")

package com.example.capstoneProject

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.capstoneProject.dialogs.LoadingDialog
import com.example.capstoneProject.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class SendRequirementActivity : AppCompatActivity() {


    private lateinit var tbar: Toolbar
    private lateinit var submitButton : Button
    private lateinit var id: CardView
    private lateinit var selfie: CardView
    private lateinit var docu: CardView
    private lateinit var idImageView: FloatingActionButton
    private lateinit var selfieImageView: FloatingActionButton
    private lateinit var docuImageView: FloatingActionButton

    //
    private var selfieImage: Bitmap? = null
    private var idImage: Bitmap? = null
    private var docuImage: Bitmap? = null



    val dialog = LoadingDialog(this, "Submitting requirements...")

    companion object{
        const val SELFIE = 0
        const val ID = 1
        const val DOCU = 2
        val TAG: String = "SAMPLETAG"


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_requirement)

        tbar = findViewById(R.id.toolbar_sendRequirementsActivity)
        submitButton = findViewById(R.id.submitButton_activitySendRequirements)
        id = findViewById(R.id.validId_activitySendRequirements)
        selfie = findViewById(R.id.selfieActivitySendRequirements)
        docu = findViewById(R.id.certification_activitySendRequirements)
        idImageView = findViewById(R.id.validIdImageView_activitySendRequiresments)
        selfieImageView = findViewById(R.id.imageViewselfieImageView_activitySendRequirements)
        docuImageView = findViewById(R.id.imageViewdocu__activitySendRequirements)


        tbar.setNavigationOnClickListener {
            finish()
        }

        selfie.setOnClickListener {
            takeASelfie()
        }

        id.setOnClickListener {
            takeIdPic()
        }
        docu.setOnClickListener {
            takeDocuPic()
        }

        submitButton.setOnClickListener {

            checkImages()

        }



    }

    private fun changeColors() {
        val color = this.resources.getColor(R.color.done)

        if (docuImage != null) {
            docuImageView.backgroundTintList = ColorStateList.valueOf(color)
        }
        if (idImage != null) {
            idImageView.backgroundTintList = ColorStateList.valueOf(color)
        }
        if (selfieImage != null) {
            selfieImageView.backgroundTintList = ColorStateList.valueOf(color)
        }

    }

    private fun checkImages() {
        if (idImage != null && selfieImage != null && docuImage != null){
            uploadToFirebaseDatabase()
        } else {
            Toast.makeText(this, "Submit the requirements needed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeDocuPic() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, DOCU)
    }
    private fun takeIdPic() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, ID)
    }
    private fun takeASelfie() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, SELFIE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELFIE && resultCode == RESULT_OK  ){
            selfieImage = data!!.extras!!.get("data") as Bitmap
            changeColors()
        } else if (requestCode == ID && resultCode == RESULT_OK) {
            idImage = data!!.extras!!.get("data") as Bitmap
            changeColors()
        } else if (requestCode == DOCU && resultCode == RESULT_OK) {
            docuImage = data!!.extras!!.get("data") as Bitmap
            changeColors()
        }
    }


    private fun uploadToFirebaseDatabase() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val selfieName = UUID.randomUUID().toString()
        val IDName = UUID.randomUUID().toString()
        val DocuName = UUID.randomUUID().toString()

        val streamSelfie = ByteArrayOutputStream()
        selfieImage!!.compress(Bitmap.CompressFormat.JPEG, 100, streamSelfie)
        val streamId = ByteArrayOutputStream()
        idImage!!.compress(Bitmap.CompressFormat.JPEG, 100, streamId)
        val streamDocu = ByteArrayOutputStream()
        docuImage!!.compress(Bitmap.CompressFormat.JPEG, 100, streamDocu)

        val selfieByte: ByteArray = streamSelfie.toByteArray()
        val idByte: ByteArray = streamId.toByteArray()
        val docuByte: ByteArray = streamDocu.toByteArray()

        val ref = FirebaseStorage.getInstance().getReference("/verification-images/$currentUserUid/selfie")
        dialog.startLoadingAnimation()
        ref.putBytes(selfieByte!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveToDatabase(it.toString(), SELFIE)
                    }
                }

        val refTwo = FirebaseStorage.getInstance().getReference("/verification-images/$currentUserUid/id")
        dialog.startLoadingAnimation()
        refTwo.putBytes(idByte!!)
                .addOnSuccessListener {
                    refTwo.downloadUrl.addOnSuccessListener {
                        saveToDatabase(it.toString(), ID)
                    }
                }

        val refThree = FirebaseStorage.getInstance().getReference("/verification-images/$currentUserUid/docu")
        dialog.startLoadingAnimation()
        refThree.putBytes(docuByte!!)
                .addOnSuccessListener {
                    refThree.downloadUrl.addOnSuccessListener {
                        saveToDatabase(it.toString(), DOCU)
                    }
                }




    }

    private fun saveToDatabase(url: String, type: Int) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("for-verification/$currentUserUid")
        val accountRef = FirebaseDatabase.getInstance().getReference("/users/$currentUserUid")
        accountRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!

                ref.child("number").setValue(user.mobileNumber)
                ref.child("uid").setValue(user.uid)
                ref.child("image").setValue(user.profileImageUrl)
                ref.child("name").setValue("${user.firstName} ${user.lastName}")
                ref.child("age").setValue(user.age)

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        if(type == SELFIE) {
            ref.child("selfie").setValue(url)
        } else if ( type == ID) {
            ref.child("id").setValue(url)
        } else if (type == DOCU) {
            ref.child("docu").setValue(url)
        }
        accountRef.child("/verified").setValue("PENDING")

        dialog.dismissDialog()
        finish()
        Toast.makeText(this, "Requirement submitted. Wait for account to be verified by the admin. Thank you.", Toast.LENGTH_LONG).show()

    }











}
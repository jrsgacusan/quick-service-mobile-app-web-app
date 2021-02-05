package com.example.capstoneProject.seller_activities


import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.capstoneProject.DisplayReviewsActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.handlers.UserSellerInfoHandler
import com.example.capstoneProject.handlers.UserSkillsHandler
import com.example.capstoneProject.models.Order
import com.example.capstoneProject.models.UserSellerInfo
import com.example.capstoneProject.models.UserSkills
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ArithmeticException


class AboutMeAsSellerActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var toolBar: Toolbar
    lateinit var descriptionEditText: EditText
    lateinit var addNewSkillsTextView: TextView
    lateinit var previousSchoolEditText: EditText
    lateinit var skillsListView: ListView
    lateinit var addSkillButton: ImageButton
    lateinit var cancelButton: ImageButton
    lateinit var addSkillEditText: EditText
    lateinit var expertiseSpinner : Spinner
    lateinit var educationStatusSpinner: Spinner
    lateinit var skillsContainer: ConstraintLayout
    lateinit var mainButton: Button
    lateinit var totalCompletedJobs: TextView
    lateinit var userRating:TextView
    var userSellerInfoHandler = UserSellerInfoHandler()
    var userSellerInfoArrayList : ArrayList<UserSellerInfo> = ArrayList()
    var userSkillsArrayList: ArrayList<UserSkills> = ArrayList()
    lateinit var userSkillsArrayAddapter: ArrayAdapter<UserSkills>
    var userSkillsHandler = UserSkillsHandler()
    lateinit var educationalAttainmentEditText:EditText
    lateinit var displayReviewsButton: Button

    companion object {
        var isSnackbarClicked: Boolean? = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me_as_seller)
        //Map everything
        totalCompletedJobs = findViewById(R.id.totalCompletedJobs_activityAboutMeAsASeller)
        userRating = findViewById(R.id.userRating_activityAboutMeAsASeller)
        toolBar = findViewById(R.id.toolBar_activityAboutMeAsSeller)
        descriptionEditText = findViewById(R.id.description_activityAboutMeAsSeller)
        addNewSkillsTextView = findViewById(R.id.addNewSkills_activityAboutMeAsSeller)
        previousSchoolEditText = findViewById(R.id.schoolEditText_activityAboutMeAsSeller)
        skillsListView = findViewById(R.id.skillsListView_activityAboutMeAsSeller)
        skillsListView.isEnabled = false
        addSkillButton = findViewById(R.id.addImageButton_activityAboutMeAsSeller)
        cancelButton = findViewById(R.id.cancleImageButton_activityAboutMeAsSeller)
        addSkillEditText = findViewById(R.id.addSkillsEditText_acitivityAboutMeAsSeller)
        expertiseSpinner = findViewById(R.id.expertiseLevelSpinner_acitivityAboutMeAsSeller)
        educationStatusSpinner = findViewById(R.id.educationStatus_activityAboutMeAsSeller)
        skillsContainer = findViewById(R.id.skillsContainer)
        mainButton = findViewById(R.id.mainButton_activityAboutMeAsSeller)
        educationalAttainmentEditText = findViewById(R.id.educationalAttainmentEditText_activityAboutMeAsSeller)
        displayReviewsButton = findViewById(R.id.viewReviewsButton_activityAboutMeAsSeller)

        //
        fetchSellerInfo()

        //Add the spinners
        //Skill level
        ArrayAdapter.createFromResource(this, R.array.skillLevel, android.R.layout.simple_spinner_item)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                expertiseSpinner.adapter = adapter
            }
        expertiseSpinner.onItemSelectedListener = this
        //Education Status
        ArrayAdapter.createFromResource(this, R.array.educationStatus, android.R.layout.simple_spinner_item)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                educationStatusSpinner.adapter = adapter
            }
        educationStatusSpinner.onItemSelectedListener = this
        val educationalStatusArrayList = getResources().getStringArray(R.array.educationStatus)
        educationStatusSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View,
                                        arg2: Int, arg3: Long) {
                educationalAttainmentEditText.setText(educationalStatusArrayList.get(arg2))
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
        //Toolbar
        toolBar.setTitle("Seller Informations")
        toolBar.setNavigationOnClickListener {
            finish()
        }
        //add new skills clickable text view
        addNewSkillsTextView.setOnClickListener {
            hideSomeViews()
        }
        cancelButton.setOnClickListener{
            showSomeViews()
        }
        //Add skill button
        addSkillButton.setOnClickListener{
           addSkill()
        }

        mainButton.setOnClickListener {
            performAction()
        }

        registerForContextMenu(skillsListView)
        //SnackBar
        if (isSnackbarClicked == false){
            val snackbar = Snackbar.make(mainButton, "To begin updating, press the UPDATE button", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Ok", View.OnClickListener {
                snackbar.dismiss()
                isSnackbarClicked = true

            })
            snackbar.show()
        }

        displayReviewsButton.setOnClickListener {
            val intent = Intent(applicationContext, DisplayReviewsActivity::class.java)
            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            intent.putExtra("userUid", currentUser)
            startActivity(intent)
        }

    }


    private fun performAction() {
        if (mainButton.text.toString() == "Update"){
            previousSchoolEditText.isEnabled = true
            descriptionEditText.isEnabled = true
            addSkillEditText.isEnabled = true
            skillsListView.isEnabled = true
            addNewSkillsTextView.isEnabled = true
            mainButton.text = "Save"
            Toast.makeText(this, "You can now update", Toast.LENGTH_SHORT).show()
        } else if (mainButton.text.toString() == "Save") {
            checkFirstAndSaveToFirebaseDatabase()

        }
    }



    private fun addSkill() {
        if (addSkillEditText.text.toString().isEmpty()){
            addSkillEditText.error = "Enter skill before adding."
            addSkillEditText.requestFocus()
        } else {
            val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
            val skill = UserSkills(
                    userUid = currentUserUid,
                    skillExpertise = expertiseSpinner.selectedItem.toString(),
                    skill = addSkillEditText.text.toString()
            )
            if (userSkillsHandler.createSkill(skill)) {
                Toast.makeText(this, "Skill added", Toast.LENGTH_SHORT).show()
            }
            addSkillEditText.text.clear()
            expertiseSpinner.setSelection(0)
        }
    }

    private fun showSomeViews() {
        skillsContainer.isGone = true
        addNewSkillsTextView.isVisible = true
        addSkillEditText.text.clear()
    }

    private fun hideSomeViews() {
        skillsContainer.isGone = false
        addNewSkillsTextView.isVisible = false
        addSkillEditText.requestFocus()
    }


    override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_edit_delete, menu)
        val menuItem = menu!!.findItem(R.id.edit)
        menuItem.setVisible(false)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.delete -> {
                if (userSkillsHandler.deleteSkill(userSkillsArrayList[info.position])) {
                    Toast.makeText(this, "Skill deleted", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        val userSkillsHandler = UserSkillsHandler()
        userSkillsHandler.userSkillsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
                userSkillsArrayList.clear()
                p0.children.forEach {
                    val userSkills = it.getValue(UserSkills::class.java)
                    if (userSkills!!.userUid == currentUserUid) {
                        userSkillsArrayList.add(userSkills)
                    }

                }
                userSkillsArrayAddapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, userSkillsArrayList)
                skillsListView.adapter = userSkillsArrayAddapter
            }
            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })

    }

    private fun fetchSellerInfo(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_seller_info/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val userData = p0.getValue(UserSellerInfo::class.java)!!
                descriptionEditText.setText(userData.description)
                previousSchoolEditText.setText(userData.previousSchool)
                totalCompletedJobs.text = "Jobs completed: " + userData.totalJobsFinished.toString()
                try {
                    val ttlrtng = userData.totalRating!!.toDouble()
                    val ttlcnt = userData.count!!.toDouble()
                    val rating = (ttlrtng/ttlcnt)
                    val solution = Math.round(rating * 10.0) / 10.0
                    userRating.text = "$solution/5"
                } catch (e: ArithmeticException) {
                    userRating.text = "No user ratings"
                }

                val educationalStatusArrayList = getResources().getStringArray(R.array.educationStatus)
                if (userData.educationalAttainment == educationalStatusArrayList[0]){
                    educationStatusSpinner.setSelection(0)
                } else if (userData.educationalAttainment == educationalStatusArrayList[1]){
                    educationStatusSpinner.setSelection(1)
                } else if (userData.educationalAttainment == educationalStatusArrayList[2]){
                    educationStatusSpinner.setSelection(2)
                } else if (userData.educationalAttainment == educationalStatusArrayList[3]){
                    educationStatusSpinner.setSelection(3)
                }  else if (userData.educationalAttainment == educationalStatusArrayList[4]){
                    educationStatusSpinner.setSelection(4)
                }  else {educationStatusSpinner.setSelection(5)}

            }
            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun checkFirstAndSaveToFirebaseDatabase() {
        if (descriptionEditText.text.toString().isEmpty()){
            descriptionEditText.requestFocus()
            descriptionEditText.error = "Add a description"
            return
        }
        if (previousSchoolEditText.text.toString().isEmpty()){
            previousSchoolEditText.requestFocus()
            previousSchoolEditText.error = "Add your previous school"
            return
        }
        if (previousSchoolEditText.text.toString().isEmpty()){
            previousSchoolEditText.requestFocus()
            previousSchoolEditText.error = "Add your previous school"
            return
        }
        previousSchoolEditText.isEnabled = false
        descriptionEditText.isEnabled = false
        addSkillEditText.isEnabled = false
        skillsListView.isEnabled = false
        addNewSkillsTextView.isEnabled = false
        mainButton.text = "Update"
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("/user_seller_info/$uid")
            ref.child("description").setValue(descriptionEditText.text.toString())
            ref.child("educationalAttainment").setValue(educationalAttainmentEditText.text.toString())
            ref.child("previousSchool").setValue(previousSchoolEditText.text.toString())
        Toast.makeText(this, "Account updated", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}
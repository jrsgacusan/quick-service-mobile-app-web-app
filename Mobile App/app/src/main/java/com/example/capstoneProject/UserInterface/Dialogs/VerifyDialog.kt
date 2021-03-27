package com.example.capstoneProject.UserInterface.Dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.widget.Button
import com.example.capstoneProject.UserInterface.General.SendRequirementActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.UserInterface.General.ChooseActivity

class VerifyDialog(activity: Activity, mode: String) {
    private val activity: Activity? = activity
    private var dialog: AlertDialog? = null
    private val modeValue: String = mode

    fun showDialog() {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_verify_as_service_provider, null)
        builder.setView(view)
        builder.setCancelable(true)

        val submitButton = view.findViewById<Button>(R.id.submitButton_dialog_verify)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton_dialog_verify)
        submitButton.setOnClickListener {
            val intent = Intent(activity, SendRequirementActivity::class.java)
            intent.putExtra(ChooseActivity.TAG, modeValue)
            activity.startActivity(intent)
            dismissDialog()

        }
        cancelButton.setOnClickListener {
            dismissDialog()
        }


        dialog = builder.create()
        dialog!!.show()


    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }


}


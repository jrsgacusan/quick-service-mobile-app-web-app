package com.example.capstoneProject.Dialogs

import android.app.AlertDialog
import android.content.Intent
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.capstoneProject.R
import com.example.capstoneProject.Buyer.SendRequirementActivity

class VerifyDialog(activity: Fragment) {
    private val activity: Fragment? = activity
    private var dialog: AlertDialog? = null

    fun showDialog(){
        val builder = AlertDialog.Builder(activity!!.context)
        val inflater = activity!!.layoutInflater
        val view  = inflater.inflate(R.layout.dialog_verify_as_service_provider, null)
        builder.setView(view)
        builder.setCancelable(true)

        val submitButton = view.findViewById<Button>(R.id.submitButton_dialog_verify)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton_dialog_verify)
        submitButton.setOnClickListener {
            val intent = Intent(activity.context, SendRequirementActivity::class.java)
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


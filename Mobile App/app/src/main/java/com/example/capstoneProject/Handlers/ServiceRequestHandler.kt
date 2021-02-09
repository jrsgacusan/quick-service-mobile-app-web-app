package com.example.capstoneProject.Handlers

import com.example.capstoneProject.Models.ServiceRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ServiceRequestHandler {

    var database: FirebaseDatabase
    var serviceRequestRef: DatabaseReference

    init {
        database = FirebaseDatabase.getInstance()
        serviceRequestRef = database.getReference("service_requests")
    }

    fun createServiceRequest(serviceRequest: ServiceRequest): Boolean {
        val id = serviceRequestRef.push().key
        serviceRequest.uid = id
        serviceRequestRef.child(id!!).setValue(serviceRequest)
        return true
    }

    fun updateServiceRequest(serviceRequest: ServiceRequest): Boolean {
        serviceRequestRef.child(serviceRequest.uid!!).setValue(serviceRequest)
        return true
    }

    fun deleteServiceRequest(serviceRequest: ServiceRequest): Boolean {
        serviceRequestRef.child(serviceRequest.uid!!).removeValue()
        return true
    }


}
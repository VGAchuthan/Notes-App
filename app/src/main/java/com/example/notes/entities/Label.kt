package com.example.notes.entities

import android.os.Parcelable
import com.example.notes.operations.LabelOperationHandler
import com.example.notes.operations.LabelOperations
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Label( var name : String) : Parcelable{
    var id : Int
    init {
        val labelHandler : LabelOperationHandler= LabelOperations
        id= labelHandler.getNextId()
    }

    override fun toString(): String {
        return "id= $id, title=$name"
    }
}
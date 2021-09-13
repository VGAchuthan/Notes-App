package com.example.notes.managers

import com.example.notes.datastore.LabelDataStoreHandler
import com.example.notes.datastore.NotesDataStore
import com.example.notes.entities.Label

object LabelManager {
    private val labelDataStore : LabelDataStoreHandler = NotesDataStore()
    fun addLabel(label : Label) : Boolean{
        return labelDataStore.addLabel(label)
    }
    fun getLabelById(id : Int) : Label?{
        return labelDataStore.getLabel(id)
    }
    fun editLabel(label : Label, newName : String) : Boolean{
        return labelDataStore.editLabel(label.id, label.name, newName)
    }
    fun deleteLabel(label : Label) : Boolean{
        return labelDataStore.deleteLabel(label)
    }
    fun getAllLabels() : List<Label>{
        return labelDataStore.getAllLabel()
    }
}
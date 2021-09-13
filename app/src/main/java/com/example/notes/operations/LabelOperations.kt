package com.example.notes.operations

import android.util.Log
import com.example.notes.entities.Label
import com.example.notes.managers.LabelManager

interface LabelOperationHandler{
    fun addLabel(label : Label) : Boolean
    fun editLabel(label : Label, newName: String) : Boolean
    fun deleteLabel(label: Label) : Boolean
    fun getAllLabels() : List<Label>?
    fun getLabelById(id:Int) : Label?
    fun getNextId() : Int
    fun checkIfLabelExist(labelName : String) : Boolean
}
object LabelOperations : LabelOperationHandler{
    private val labelManager : LabelManager = LabelManager
    override fun addLabel(label: Label): Boolean {
//       return labelManager.addLabel(label)
        return if(!checkIfLabelExist(label.name)) labelManager.addLabel(label) else false
    }

    override fun getLabelById(id:Int): Label? {
        return labelManager.getLabelById(id)
    }

    override fun editLabel(label: Label, newName : String): Boolean {
       // val _label = getLabelById(label.id)
        //_label?.name = newName
        return labelManager.editLabel(label, newName)
    }

    override fun deleteLabel(label: Label): Boolean {
        val notesOperationHandler : NotesOperationHandler = NotesOperation()
        notesOperationHandler.removeLabelFromNotes(label)
        return labelManager.deleteLabel(label)
    }

    override fun getAllLabels(): List<Label> {
        return labelManager.getAllLabels()
    }

    override fun getNextId(): Int {
        Log.e("IN LAB OP ","GEN NEXT ID :${getAllLabels()?.size}")
        return getAllLabels()?.size?.plus(1)
    }

    override fun checkIfLabelExist(labelName: String): Boolean {

        val label = this.getAllLabels()?.filter { it.name.equals(labelName, true) }
        println("in check label : $label")
        return label?.size == 1
    }
}
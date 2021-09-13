package com.example.notes.datastore

import android.util.Log
import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.enums.NotesType

interface LabelDataStoreHandler{
    fun addLabel(label : Label) : Boolean
    fun editLabel(id : Int, oldName : String, newName : String) : Boolean
    fun deleteLabel(label : Label) : Boolean
    fun getLabel(id : Int) : Label?
    fun getAllLabel() : List<Label>
}
interface NotesDataStoreHandler{
    fun  addNote(note : Notes) : Boolean
    fun editNote(id:Int, oldNotes : Notes, newNotes: Notes, notesType: NotesType) : Boolean
    fun deleteNote(note : Notes) : Boolean
    fun addToTrash(note : Notes) : Boolean
//    fun retrieveNote(note : Notes) : Boolean
    fun deleteNoteForever(note : Notes) : Boolean
    fun archiveNote(note : Notes) : Boolean
    fun unArchiveNote(note : Notes) : Boolean
    fun pinNote(note : Notes) : Boolean
    fun unPinNote(note : Notes) : Boolean
//    fun getAllNotes() : HashMap<String, List<Notes>>
    fun getAllUnArchivedNotes() : List<Notes>?
    fun getAllArchivedNotes() : List<Notes>?
    fun getAllPinnedNotes() : List<Notes>?
    fun getAllTrashNotes() : List<Notes>?
//    fun getAllNotesByLabel(label : Label) :  HashMap<NotesType, List<Notes>?>
}
class NotesDataStore : LabelDataStoreHandler, NotesDataStoreHandler {
    private val listOfUnArchiveNotes : ArrayList<Notes> = ArrayList()
    private val listOfArchiveNotes : ArrayList<Notes> = ArrayList()
    private val listOfTrashNotes : ArrayList<Notes> = ArrayList()
    private val listOfPinnedNotes : ArrayList<Notes> = ArrayList()
    private val listOfLabels : ArrayList<Label> = ArrayList()
    override fun addLabel(label: Label): Boolean {
        return this.listOfLabels.add(label)
    }

    override fun getLabel(id: Int): Label? {
        var label  : Label?= null
        label =  this.listOfLabels.filter { it.id == id }.firstOrNull()
        return label?.copy()
    }
    override fun editLabel(id: Int,oldName: String, newName: String): Boolean {
        var label  : Label?= null
        label =  this.listOfLabels.filter { it.id == id }.firstOrNull()

        this.listOfLabels.remove(label)
        Log.e("IN EDI LAL NOTES STORE","label before edit L : $label - $newName, $oldName")
        var flag = false
        if(label?.name.equals(oldName)){
            label?.name = newName
            flag = true
        }
        this.listOfLabels.add(label!!)
        Log.e("IN EDI LAL NOTES STORE","label after edit L : $label")
//        this.listOfLabels.re
        return flag
    }

    override fun deleteLabel(label: Label): Boolean {
        return this.listOfLabels.remove(label)
    }

    override fun getAllLabel(): List<Label> {
        return this.listOfLabels.sortedBy { it.name }.toList()
    }

    override fun addNote(note: Notes): Boolean {
        Log.e("ADD NOTE","ADD NOTE")
        return this.listOfUnArchiveNotes.add(note)
    }

    override fun editNote(id:Int, oldNotes : Notes, newNotes: Notes, notesType: NotesType): Boolean {
        return when(notesType){
            NotesType.ARCHIVED -> {
                this.listOfArchiveNotes.remove(oldNotes) && this.listOfArchiveNotes.add(newNotes)
            }
            NotesType.TRASH -> {
                false
            }
            NotesType.UNARCHIVE -> {
                this.listOfUnArchiveNotes.remove(oldNotes) && this.listOfUnArchiveNotes.add(newNotes)
            }
            NotesType.PINNED -> {
                this.listOfPinnedNotes.remove(oldNotes) && this.listOfPinnedNotes.add(newNotes)
            }
            NotesType.ALL -> TODO()
        }
    }

    override fun deleteNote(note: Notes): Boolean {
        return this.listOfUnArchiveNotes.remove(note)
    }

    override fun addToTrash(note: Notes): Boolean {
        return this.listOfTrashNotes.add(note)
    }

    override fun deleteNoteForever(note: Notes): Boolean {
        return this.listOfTrashNotes.remove(note)
    }

    override fun archiveNote(note: Notes): Boolean {
        return this.listOfArchiveNotes.add(note)
    }

    override fun unArchiveNote(note: Notes): Boolean {
        return this.listOfArchiveNotes.remove(note)
    }

    override fun pinNote(note: Notes): Boolean {
        return this.listOfPinnedNotes.add(note)
    }

    override fun unPinNote(note: Notes): Boolean {
        return this.listOfPinnedNotes.remove(note)
    }

//    override fun getAllNotes(): HashMap<String, List<Notes>> {
//        TODO("Not yet implemented")
//    }

    override fun getAllUnArchivedNotes(): List<Notes>? {
        return this.listOfUnArchiveNotes.sortedBy { it.id }.toList()
    }

    override fun getAllArchivedNotes(): List<Notes>?{
        return this.listOfArchiveNotes.sortedBy { it.id }.toList()
    }

    override fun getAllPinnedNotes(): List<Notes>? {
        return this.listOfPinnedNotes.sortedBy { it.id }.toList()
    }

    override fun getAllTrashNotes(): List<Notes>? {
        return this.listOfTrashNotes.sortedBy { it.id }.toList()
    }

//    override fun getAllNotesByLabel(label: Label): HashMap<NotesType, List<Notes>?> {
//        val list : HashMap<NotesType, List<Notes>?> = HashMap()
//
//        val unArchivedList = this.getAllUnArchivedNotes()?.filter { (it.listOfLabels?.contains(label) == true) }
//        val pinnedList = this.getAllPinnedNotes()?.filter { it.listOfLabels?.contains(label) == true }
//        list.put(NotesType.UNARCHIVE, unArchivedList)
//        list.put(NotesType.PINNED,pinnedList)
//        return list
//    }
}
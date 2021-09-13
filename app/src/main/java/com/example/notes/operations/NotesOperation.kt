package com.example.notes.operations

import android.util.Log
import com.example.notes.entities.ArchiveInformation
import com.example.notes.entities.DeleteInformation
import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.enums.NotesType
import com.example.notes.managers.NotesManager
import java.util.*
import kotlin.collections.HashMap

interface NotesOperationHandler{
    fun addNote(note : Notes) : Boolean
    fun editNote(id:Int, oldNotes : Notes, newNotes: Notes, notesType: NotesType) : Boolean
    fun deleteNote(note : Notes) : Boolean
    fun retrieveNote(note : Notes) : Boolean
    fun deleteNoteForever(note : Notes) : Boolean
    fun archiveNote(note : Notes) : Boolean
    fun unArchiveNote(note : Notes) : Boolean
    fun pinNote(note : Notes) : Boolean
    fun unPinNote(note : Notes ) : Boolean
    fun getNoteById(id : Int,notesType: NotesType) : Notes?
    fun getAllNotes() : HashMap<NotesType, List<Notes>?>
    fun getNextNoteId() : Int
    fun getPinnedAndUnArchiveNotes() : List<Notes>?
    fun getAllUnArchivedNotes() : List<Notes>?
    fun getAllArchivedNotes() : List<Notes>?
    fun getAllPinnedNotes() : List<Notes>?
    fun getAllTrashNotes() : List<Notes>?
    fun getAllNotesByLabel(label : Label) :  List<Notes>?
    fun removeLabelFromNotes(label: Label) : Boolean

}
class NotesOperation  : NotesOperationHandler{
    private val notesManager : NotesManager = NotesManager
    override fun addNote(note: Notes): Boolean {
        return notesManager.addNote(note)
    }

    override fun editNote(id:Int, oldNotes : Notes, newNotes: Notes, notesType: NotesType): Boolean {
        return when(notesType) {
            NotesType.ARCHIVED -> {
                notesManager.editNote(id,oldNotes, newNotes, notesType)
            }
            NotesType.TRASH -> {
                false
            }
            NotesType.UNARCHIVE -> {
                notesManager.editNote(id,oldNotes, newNotes, notesType)
            }
            NotesType.PINNED -> {
                notesManager.editNote(id,oldNotes, newNotes, notesType)
            }
            NotesType.ALL -> TODO()
        }
    }

    override fun deleteNote(note: Notes): Boolean {
        return if(note.title=="" || note.content == ""){
             notesManager.deleteNote(note)
        }
        else{
            note.isDeleted = true
            note.notesType = NotesType.TRASH
            val date = Date()
            val deleteInformation = DeleteInformation(date)
            note.deleteInformation = deleteInformation
            if(note.isArchived){
                note.isArchived = false
                notesManager.unArchiveNote(note)
            }
             notesManager.addToTrash(note) && notesManager.deleteNote(note)
        }

    }

    override fun retrieveNote(note: Notes): Boolean {
        note.isDeleted = false
        note.notesType = NotesType.UNARCHIVE
       return notesManager.addNote(note) && notesManager.deleteNoteForever(note)
    }

    override fun deleteNoteForever(note: Notes): Boolean {
        return notesManager.deleteNoteForever(note)
    }

    override fun archiveNote(note: Notes): Boolean {
        note.isArchived = true
        note.notesType = NotesType.ARCHIVED
        val date = Date()
        val archiveInformation = ArchiveInformation(date, date)
        note.archiveInformation = archiveInformation
        return notesManager.addToArchive(note) && notesManager.deleteNote(note)
    }

    override fun unArchiveNote(note: Notes): Boolean {
        note.isArchived = false
        note.notesType = NotesType.UNARCHIVE
        note.archiveInformation = null
        return notesManager.unArchiveNote(note) && notesManager.addNote(note)
    }

    override fun pinNote(note: Notes): Boolean {
        note.isPinned = true
        note.notesType = NotesType.PINNED
        return notesManager.addToPin(note) && notesManager.deleteNote(note)
    }

    override fun unPinNote(note: Notes): Boolean {
        note.isPinned = false
        note.notesType = NotesType.UNARCHIVE
        return notesManager.unPinNote(note) && notesManager.addNote(note)
    }

    override fun getNoteById(id: Int, notesType: NotesType): Notes? {
        return  when(notesType){
            NotesType.UNARCHIVE -> {
                getAllUnArchivedNotes()?.filter { it.id == id }?.firstOrNull()
            }
            NotesType.ARCHIVED -> {
                getAllArchivedNotes()?.filter { it.id == id }?.firstOrNull()
            }
            NotesType.TRASH -> {
                getAllTrashNotes()?.filter { it.id == id }?.firstOrNull()
            }
            NotesType.PINNED -> {
                getAllPinnedNotes()?.filter { it.id == id }?.firstOrNull()
            }
            NotesType.ALL -> TODO()
        }
    }
    private fun mergeAllNotes(vararg lists : List<Notes>) : List<Notes>{
        return listOf(*lists).flatten()
    }

    override fun getNextNoteId(): Int {

        val unArchiveSize = this.getAllUnArchivedNotes()?.size ?: 0
        val archiveSize = this.getAllArchivedNotes()?.size ?: 0
        val pinnedSize = this.getAllPinnedNotes()?.size ?: 0
        val trashSize = this.getAllTrashNotes()?.size ?: 0
        val nextId = (unArchiveSize+ archiveSize+pinnedSize+trashSize +1)
        Log.e("IN NOTE OP","NEXT ID : $nextId ")

        return nextId
    }

    override fun getPinnedAndUnArchiveNotes(): List<Notes>? {
        val listOfPinnedNotes = this.getAllPinnedNotes()
        val listOfUnArchivedNotes = this.getAllUnArchivedNotes()
        val allNotes = this.mergeAllNotes(listOfPinnedNotes!!,listOfUnArchivedNotes!!)
        return allNotes
    }

    override fun getAllNotes(): HashMap<NotesType, List<Notes>?> {
        val listOfAllNotes : HashMap<NotesType, List<Notes> ?> = HashMap()
        val listOfUnArchivedNotes = this.getAllUnArchivedNotes()
        val listOfArchivedNotes = this.getAllArchivedNotes()
        val listOfTrashNotes = this.getAllTrashNotes()
        val listOfPinnedNotes = this.getAllPinnedNotes()
        val allNotes = this.mergeAllNotes(listOfArchivedNotes!!,listOfUnArchivedNotes!!, listOfTrashNotes!!, listOfPinnedNotes!!)
        println("all notes : ${allNotes.groupBy { it.notesType }}")
        for(note in allNotes){
            println(note)
        }

        listOfAllNotes.put(NotesType.UNARCHIVE, listOfUnArchivedNotes)
        listOfAllNotes.put(NotesType.ARCHIVED, listOfArchivedNotes)
        listOfAllNotes.put(NotesType.PINNED, listOfPinnedNotes)
        listOfAllNotes.put(NotesType.TRASH, listOfTrashNotes)


            return listOfAllNotes
    }

    override fun getAllUnArchivedNotes(): List<Notes>? {
        return notesManager.getAllUnArchivedNotes()
    }

    override fun getAllArchivedNotes(): List<Notes>? {
        return notesManager.getAllArchivedNotes()
    }

    override fun getAllPinnedNotes(): List<Notes>? {
        return notesManager.getAllPinnedNotes()
    }

    override fun getAllTrashNotes(): List<Notes>? {
        return notesManager.getAllTrashNotes()
    }

    override fun removeLabelFromNotes(label: Label): Boolean {
        var removeFromPinned = this.getAllPinnedNotes()?.filter { (it.listOfLabels?.contains(label) == true) &&(it.listOfLabels?.remove(label)) }
        var removeFromArchived = this.getAllArchivedNotes()?.filter { (it.listOfLabels?.contains(label) == true) &&(it.listOfLabels?.remove(label)) }
        var removeFromrTrash = this.getAllTrashNotes()?.filter { (it.listOfLabels?.contains(label) == true) &&(it.listOfLabels?.remove(label)) }
        var removeFromUnArchived = this.getAllUnArchivedNotes()?.filter { (it.listOfLabels?.contains(label) == true) &&(it.listOfLabels?.remove(label)) }
        return true
    }


    override fun getAllNotesByLabel(label: Label): List<Notes>? {
        val list : HashMap<NotesType, List<Notes>?> = HashMap()

        val unArchivedList = this.getAllUnArchivedNotes()?.filter { (it.listOfLabels?.contains(label) == true) }
        val pinnedList = this.getAllPinnedNotes()?.filter { it.listOfLabels?.contains(label) == true }
        val archivedList = this.getAllArchivedNotes()?.filter { it.listOfLabels?.contains(label) == true }
        list.put(NotesType.UNARCHIVE, unArchivedList)
        list.put(NotesType.PINNED,pinnedList)
        list.put(NotesType.ARCHIVED, archivedList)
        return mergeAllNotes(pinnedList!!,unArchivedList!!,archivedList!!)
    }
}
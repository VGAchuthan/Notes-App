package com.example.notes.managers

import com.example.notes.datastore.NotesDataStore
import com.example.notes.datastore.NotesDataStoreHandler
import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.enums.NotesType

object NotesManager {
    private  val notesDataStore : NotesDataStoreHandler = NotesDataStore()
    fun addNote(note : Notes) : Boolean{
        return notesDataStore.addNote(note)
    }
    fun editNote(id:Int, oldNotes : Notes, newNotes: Notes, notesType: NotesType) : Boolean{
        return notesDataStore.editNote(id, oldNotes, newNotes, notesType)
    }
    fun addToTrash(note : Notes) : Boolean{
        return notesDataStore.addToTrash(note)
    }
    fun addToPin(note : Notes) : Boolean{
        return notesDataStore.pinNote(note)
    }
    fun addToArchive(note : Notes) : Boolean{
        return notesDataStore.archiveNote(note)
    }
    fun deleteNote(note : Notes) : Boolean{
        return notesDataStore.deleteNote(note)
    }
    fun deleteNoteForever(note : Notes) : Boolean{
        return notesDataStore.deleteNoteForever(note)
    }
    fun unArchiveNote(note : Notes):Boolean{
        return notesDataStore.unArchiveNote(note)
    }
    fun unPinNote(note : Notes) : Boolean{
        return notesDataStore.unPinNote(note)
    }
//    fun retrieveNote(note : Notes) : Boolean{
//        return false
//    }

    fun getAllUnArchivedNotes() : List<Notes>?{
        return notesDataStore.getAllUnArchivedNotes()
    }
    fun getAllArchivedNotes() : List<Notes>?{
        return notesDataStore.getAllArchivedNotes()
    }

    fun getAllPinnedNotes() : List<Notes>?{
        return notesDataStore.getAllPinnedNotes()
    }
    fun getAllTrashNotes() : List<Notes>?{
        return notesDataStore.getAllTrashNotes()
    }
//    fun getAllNotesByLabel(label : Label) : HashMap<NotesType, List<Notes>?>? {
//        val list : HashMap<NotesType, List<Notes>?> = HashMap()
//
//        val unArchivedList = this.getAllUnArchivedNotes()?.filter { (it.listOfLabels?.contains(label) == true) }
//        val pinnedList = this.getAllPinnedNotes()?.filter { it.listOfLabels?.contains(label) == true }
//        list.put(NotesType.UNARCHIVE, unArchivedList)
//        list.put(NotesType.PINNED,pinnedList)
//        return list
//    }
}
package com.example.notes.operations

import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.enums.NotesType

object SearchOperations {
    private var fetchOperation : NotesOperationHandler = NotesOperation()
    private fun mergeAllNotes(vararg lists : List<Notes>) : List<Notes>{
        return listOf(*lists).flatten()
    }
    fun searchInNotes(value : String) : List<Notes>?{
        val unarchivedList = fetchOperation.getAllUnArchivedNotes()?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) }
        val archivedList = fetchOperation.getAllArchivedNotes()?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) }
        val pinnedList = fetchOperation.getAllPinnedNotes()?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) }


        return mergeAllNotes(pinnedList!!,unarchivedList!!,archivedList!!)
    }
    fun searchInLabeledNotes(label : Label, value : String) : List<Notes>?{
        val list = fetchOperation.getAllNotesByLabel(label)
        println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
        println("list : $list")
        var searchedLists = HashMap<NotesType, List<Notes>?>()
//        searchedLists.put(NotesType.ARCHIVED,list?.get(NotesType.ARCHIVED)?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) })
//        searchedLists.put(NotesType.UNARCHIVE,list?.get(NotesType.UNARCHIVE)?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) })
//        searchedLists.put(NotesType.PINNED,list?.get(NotesType.PINNED)?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) })
//        println("Searched List : $searchedLists")
        return list?.filter { it.title.contains(value, true) || it.content.contains(value, true) || it.listOfLabels.map{it.name}.contains(value) }
    }
}
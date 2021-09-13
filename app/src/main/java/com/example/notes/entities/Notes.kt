package com.example.notes.entities

import com.example.notes.enums.NotesType
import com.example.notes.operations.NotesOperation
import com.example.notes.operations.NotesOperationHandler
import java.util.*
import kotlin.collections.ArrayList
val notesHandler: NotesOperationHandler = NotesOperation()
data class Notes(var title : String = "", var content: String = "", var listOfLabels : ArrayList<Label> = ArrayList(),
                 var isArchived : Boolean = false, var archiveInformation : ArchiveInformation? = null,
                 var isDeleted : Boolean = false, var deleteInformation : DeleteInformation? = null,
                 var isPinned : Boolean = false, var created : Date?=null,
                 var modified : Date?=null, var notesType: NotesType = NotesType.UNARCHIVE


) {
    var createdBy : String = "abc@gmail.com"
    var id : Int

    init{
        val date = Date()
        created = date
        modified = date
//        listOfLabels = ArrayList()
        id= notesHandler.getNextNoteId()

    }
}

data class ArchiveInformation(var archivedTime : Date, var modifiedTime : Date) {

}
data class DeleteInformation(var deleteTime : Date)

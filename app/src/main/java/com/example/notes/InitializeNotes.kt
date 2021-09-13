package com.example.notes

import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.enums.NotesType
import com.example.notes.operations.*
import java.util.*

class InitializeNotes {


//    companion object{
        private val searchHandler =  SearchOperations
        private val notesHandler : NotesOperationHandler = NotesOperation()
        val label1 = Label("Label1")
        val label2 = Label("Label2")
        private fun searchFunctionsCheck(){
//        searchInNotes("this")
            searchInNotes("edit")
            searchInLabeledNotes(label1, "this")

        }
        private fun searchInLabeledNotes(label:Label, value : String){
            println("_____________________________________________SEARCH IN LABEL NOTES")
            println(searchHandler.searchInLabeledNotes(label, value))
        }
        private fun searchInNotes(value : String){
            println("______________________________________________________SEARCH IN NOTES_________________________________________")
            println(searchHandler.searchInNotes(value))
        }
        fun notesFunctionsCheck(){
            val note1 = Notes("Title 1", "this is firts notes\nthis is firts notes\nthis is firts notes\nthis is firts notes\nthis is firts notes\nthis is firts notes\nthis is firts notes\nthis is firts notes\n")
            addNotes(note1)
            val note2 = Notes("Title 2", "this is first notes")
            addNotes(note2)
            val note3 = Notes("Title 3", "this is firss notes")
            addNotes(note3)
            val note4 = Notes("Title 4", "this is fir notes")
            addNotes(note4)
            note1.listOfLabels?.add(label1)
            note2.listOfLabels?.add(label1)






//            getAllUnArchiveNotes()
//            archiveNote(note1)
//            getAllUnArchiveNotes()
//            getAllArchivedNotes()
////        unarchiveNote(note1)
//            println("________________________________________________________________________________")
//            getAllUnArchiveNotes()
//            getAllArchivedNotes()
//            println("_____________________________________PINNED NOTES___________________________________________")
//            pinNote(note3)
//            getAllPinnedNotes()
////        unpinNote(note3)
//            getAllPinnedNotes()
//            getAllUnArchiveNotes()
//            println("_____________________________________TRASH NOTES___________________________________________")
//            deleteNote(note4)
//            getAllTrashNotes()
//            getAllUnArchiveNotes()
////        retrieveNote(note4)
//            getAllTrashNotes()
//            getAllUnArchiveNotes()
//            println("_____________________________________DELETE NOTES FOREVER ___________________________________________")
////        deleteNoteForever(note4)
//            println("_____________________________________ Get notes by label ___________________________________________")
//            getNotesByLabel(label1)
//            println("_____________________________________ Get all notes ___________________________________________")
//            getAllNotes()
//            getNoteById(2,NotesType.UNARCHIVE)
//            Thread.sleep(3000)
//            editNote(2, NotesType.UNARCHIVE)
//            getAllNotes()

        }
        private fun editNote(id: Int, notesType: NotesType){
            if(notesType == NotesType.TRASH){
                println("Edit is not valid for trash notes")
                // return
            }
            else{
                println("IN EDIT NOTE")
                val oldNotes = notesHandler.getNoteById(id, notesType)
                println(oldNotes)
                var noteToBeEDited = oldNotes?.copy()
                noteToBeEDited!!.content = "EDited conettn "
                noteToBeEDited.listOfLabels.add(Label("Fourteen Tag"))
                noteToBeEDited.modified = Date()
                println(noteToBeEDited)
                println(oldNotes)
                if(notesHandler.editNote(id,oldNotes!!, noteToBeEDited!!,notesType)){
                    println("edited success")
                }
                else
                    println("edit fails")
            }
        }
        private fun getNoteById(id : Int, notesType: NotesType){
            println("get note by id")
            val notes = (notesHandler.getNoteById(id, notesType))
            if(notes == null){
                println("no note found")
            }else
                println("Note found\n $notes")
        }
        private fun getAllNotes(){
            println(notesHandler.getAllNotes())
        }
        private fun getNotesByLabel(label: Label){
            println("get notes by label")
            println(notesHandler.getAllNotesByLabel(label))
        }
        private fun deleteNoteForever(note : Notes){
            if(notesHandler.deleteNoteForever(note)){
                println("notes deleted forever success")
            }else
                println("delete forever fails")
        }
        private fun deleteNote(note : Notes){
            if(notesHandler.deleteNote(note)){
                println("moved to bin success")
            }else
                println("move to bin fails")
        }
        private fun retrieveNote(note: Notes){
            if(notesHandler.retrieveNote(note)){
                println("note retrieed sucess")
            }else
                println("retrieve fails")
        }
        private fun getAllTrashNotes(){
            println("Tash notes")
            println(notesHandler.getAllTrashNotes())
        }
        private fun getAllPinnedNotes(){
            println("PINNED NOTES")
            println(notesHandler.getAllPinnedNotes())
        }

        private fun pinNote(note : Notes){
            if(notesHandler.pinNote(note)){
                println("ponned success")
            }
            else
                println("pin fail")
        }
        private fun unpinNote(note: Notes){
            if(notesHandler.unPinNote(note)){
                println("un pin success")
            }
            else
                println("un pin fails")
        }
        private fun unarchiveNote(note: Notes){
            if(notesHandler.unArchiveNote(note))
            {
                println("unarchive successfully")
            }else
                println("unarchive failed")
        }
        private fun getAllArchivedNotes(){
            println("Archived Notes")
            println(notesHandler.getAllArchivedNotes())
        }
        private fun archiveNote(note: Notes){

            if(notesHandler.archiveNote(note)){
                println("archive successfully")

            }else
                println("archive not success")
        }
        private fun addNotes(note: Notes){
            if(notesHandler.addNote(note)){
                println("notes added")
            }else
                println("notes not added")
        }
        private fun getAllUnArchiveNotes(){
            println(" UN Archived Notes")
            println(notesHandler.getAllUnArchivedNotes())
        }
        public fun labelFunctionsCheck(){
            addLabel(label1)
            addLabel(label2)
            val label3 = Label("a".capitalize())

            addLabel(label3)
            val label4 = Label("H")
            addLabel(label4)

            val label5 = Label("G")
            addLabel(label5)
            val label6 = Label("F")
            addLabel(label6)








//            editLabel(label2,"Achuthan")
//            printAllLabels()
//            deleteLabel(label4)
//            printAllLabels()
//            deleteById(5)
//            printAllLabels()
//            addLabel(label1.copy(id=9))
//            printAllLabels()
            //println(labelHandler.getAllLabels())
        }
        private fun deleteById(id : Int){
            val labelHandler : LabelOperationHandler = LabelOperations
            val label = labelHandler.getLabelById(id)
            println("Delete by Id ")
            label?.let { deleteLabel(it) }
        }
        private fun deleteLabel(label : Label){
            val labelHandler : LabelOperationHandler = LabelOperations
            labelHandler.deleteLabel(label)
        }
        private fun printAllLabels(){
            val labelHandler : LabelOperationHandler = LabelOperations
            println(labelHandler.getAllLabels())
        }
        private fun editLabel(label: Label, newName : String){
            val labelHandler : LabelOperationHandler = LabelOperations
            if(labelHandler.editLabel(label, newName)){
                println("Label edited ")
            }else
                println("Not Edited Successfully")

        }
        private fun addLabel(label : Label){
            val labelHandler : LabelOperationHandler = LabelOperations
            if(labelHandler.addLabel(label)){
                println("Label Added")
            }
            else{
                println("Label Exists")
            }


        }


}
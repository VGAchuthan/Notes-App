package com.example.notes.fragments.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.entities.Notes

class NotesViewModel : ViewModel() {
    private var _note = MutableLiveData<Notes>()
    private var _listOfUnarchivedNotes = MutableLiveData<ArrayList<Notes>>()
    fun setNote(notes: Notes){
        _note.value =  notes
    }

}
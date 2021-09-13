package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.get
import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.enums.LabelsViewType
import com.example.notes.enums.NotesType
import com.example.notes.fragments.secondary.BottomDialogFragment
import com.example.notes.operations.NotesOperation
import com.example.notes.operations.NotesOperationHandler
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*
import kotlin.collections.ArrayList

class NotesDetailActivity : AppCompatActivity() {
    private lateinit var note : Notes
    val notesOperationHandler : NotesOperationHandler = NotesOperation()
    private lateinit var titleView : EditText
    private lateinit var contentsView : EditText
    private lateinit var modifiedView :TextView
    private lateinit var wholeView : RelativeLayout
    lateinit var toolbar: Toolbar
    private lateinit var moreActions : View
    val bottomSheetDialogFragment = BottomDialogFragment.newInstance()
    private val REQUEST_FOR_LABEL = 2
    private lateinit var chipGroup : ChipGroup
    private lateinit var chipContainer : FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_notes_detail)
        wholeView = findViewById(R.id.layout_notes_details)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intent = intent
        val notesId = intent.extras?.get("notesId").toString().toInt()
        val notesTypeOrdinal = intent.extras?.get("notesType").toString().toInt()
        val notesType = NotesType.values().get(index = notesTypeOrdinal)
        Log.e("FRAM DETAILS ACTI","notesId : $notesId : Type : $notesType")
        note = getNotes(notesId, notesType)
        Log.e("FRAOM DETAILS ACTI","NOTE : ${note.listOfLabels}")
        titleView = findViewById(R.id.notes_detail_edittext_title) as EditText
        contentsView = findViewById(R.id.notes_detail_edittext_context)
        modifiedView = findViewById(R.id.notes_detail_edittext_modified)
        moreActions = findViewById(R.id.label_view_more_actions)
        titleView.setText(note?.title,TextView.BufferType.EDITABLE)
        contentsView.setText(note?.content, TextView.BufferType.EDITABLE)
        modifiedView.text = note?.modified.toString()

        chipGroup = findViewById(R.id.notes_detail_chip_group)
        fillChipView()
        chipGroup.setOnCheckedChangeListener { group, checkedId -> callLabelActivity() }
        moreActions.setOnClickListener { //callBottomSheetDialog()
            callBottomDialog()
             }
        if(note.isDeleted){
//            titleView.isEnabled = true
            titleView.isFocusable = false
            titleView.isClickable = false
            contentsView.isClickable = false
            contentsView.isFocusable = false
//            contentsView.isEnabled = true
            wholeView.setOnClickListener {
                Toast.makeText(this,"Retrive Note to Edit", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun fillChipView() {
        chipGroup.removeAllViews()
        for(label in note.listOfLabels){
            val chip = Chip(this)
            chip.text = label.name
//            chip.paddingStart = 2
            chipGroup.addView(chip)
        }
    }

    private fun callBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.layout_bottom_dialog)
        val layoutAddLabel = bottomSheetDialog.findViewById<RelativeLayout>(R.id.bottom_sheet_layout_add_label)
        layoutAddLabel?.setOnClickListener {
            callLabelActivity()

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
    private fun callLabelActivity(){
        val intent = Intent(this,LabelActivity::class.java)
        intent.putExtra("viewType", LabelsViewType.SELECT.ordinal)
        intent.putParcelableArrayListExtra("listOfLabel",this.note.listOfLabels)
        startActivityForResult(intent, REQUEST_FOR_LABEL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_FOR_LABEL){
            if(resultCode== RESULT_OK  && data != null){
                val list = data.getParcelableArrayListExtra<Label>("selectedLabelList")
                println("ON ACT RES")
                println(list)
//                this.note.listOfLabels = (this.note.listOfLabels + (list as ArrayList<Label>)) as ArrayList<Label>
//                this.note.listOfLabels = list as ArrayList<Label>
                this.note.listOfLabels.clear()
                this.note.listOfLabels.addAll(list as ArrayList)
                println(this.note.listOfLabels)
                fillChipView()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val newNote = note.copy()
        newNote.id = note.id
        newNote.created = note.created
        newNote.title = titleView.text.toString()
        newNote.content = contentsView.text.toString()
        newNote.listOfLabels = note.listOfLabels
//        Thread.sleep(5000L)
        var now = Date(System.currentTimeMillis() + 50000L)
        //now.time = now.time + 50000L
        Log.e("N DETAILS","now : $now ${note.listOfLabels} \n ${newNote.listOfLabels}")
        newNote.modified = now
        if(newNote.title !="" || newNote.content != "" ){

            saveNote(newNote)
        }
        else{
            notesOperationHandler.deleteNote(note)
//            notesOperationHandler.deleteNoteForever(note)
            Toast.makeText(this,"Empty Notes Discarded",Toast.LENGTH_SHORT).show()
        }



    }
    private fun callBottomSheetDialog(){

//        supportFragmentManager.beginTransaction().show(bottomSheetDialogFragment).commitNow()
        bottomSheetDialogFragment.show(supportFragmentManager, BottomDialogFragment.MY_TAG)
    }
    private fun saveNote(newNote: Notes) {

        if(notesOperationHandler.editNote(note.id, note,newNote,NotesType.UNARCHIVE)){
            println("edit done : ${note.id} : ${newNote.id}")
        }else
            println("edit fails")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notes_details_menu, menu)

        if(menu != null){
            if(note.isArchived){
                menu.get(1).setIcon(R.drawable.ic_outline_unarchive_24)
            }
            if(note.isPinned){
                menu.get(0).setIcon(R.drawable.ic_baseline_push_pin_24)
            }
            if(note.isDeleted){
                menu.get(0).setVisible(false)
                menu.get(1).setVisible(false)
                menu.get(2).setIcon(R.drawable.ic_outline_restore_24)
                menu.add(Menu.NONE,3,Menu.NONE,"Delete Forever").setIcon(R.drawable.ic_outline_delete_forever_24).setShowAsAction(2)

            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item?.itemId){
        android.R.id.home ->{
            onBackPressed()
//            overridePend

            true
        }
        R.id.item_notes_details_archive ->{
            if(note.isArchived){
                //item.setIcon(R.drawable.ic_outline_unarchive_24)
                    Toast.makeText(this,"Notes is  Un Archived", Toast.LENGTH_SHORT).show()
                if(notesOperationHandler.unArchiveNote(note))
                    println("un archive success")
                else
                    println("un arc dails")
            }
            else{
                Toast.makeText(this,"Notes is Archived", Toast.LENGTH_SHORT).show()
                if(notesOperationHandler.archiveNote(note)){
                println("archived successfully")
            }
            else
                println("archive fails")

            }

            //clearContentProvider()
            onBackPressed()
            true
        }
        R.id.item_notes_details_pin ->{
            if(notesOperationHandler.pinNote(note)){
                println("pinned successfully")
            }
            else
                println("pinned fails")
            onBackPressed()
            true
        }
        R.id.item_notes_details_trash->{
            if(note.isDeleted){
                Toast.makeText(this,"Notes is Retrived", Toast.LENGTH_SHORT).show()
                if (notesOperationHandler.retrieveNote(note)) {
                    println("retrieve  successfully")
                } else
                    println("retrieve fails")

            }
            else {
                Toast.makeText(this,"Notes Moved to Bin", Toast.LENGTH_SHORT).show()
                if (notesOperationHandler.deleteNote(note)) {
                    println("move to bin  successfully")
                } else
                    println("move to bin fails")
            }
            onBackPressed()
            true
        }
        3 ->{
            Toast.makeText(this,"Note Deleted Forever", Toast.LENGTH_SHORT).show()
            if(notesOperationHandler.deleteNoteForever(note)){
                println("note deleted forever")
            }else
                println("note delete forever fails")
            onBackPressed()
            true
        }
        else ->
            super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun getNotes(id : Int, notesType: NotesType) : Notes{
        return notesOperationHandler.getNoteById(id, notesType)!!
    }
}
package com.example.notes

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*

import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.MenuItemCompat
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.core.view.size
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity


import com.example.notes.fragments.main.NotesFragment
import androidx.fragment.app.setFragmentResultListener
import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.entities.notesHandler
import com.example.notes.enums.LabelsViewType
import com.example.notes.enums.NotesType
import com.example.notes.operations.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var drawerToggle : ActionBarDrawerToggle
    lateinit var navView : NavigationView
    lateinit var toolbar: Toolbar
    lateinit var fragmentContainer : View
    lateinit var fab_main : FloatingActionButton
    private  var listOfLabel : List<Label>? =null
    private lateinit var selectedLabel: Label
    private var viewMode : String? =null
    private var notesFragment = NotesFragment.newInstance(/*ViewModes.GENERAL_MODE*/)
    private lateinit var searchIcon : View
    private lateinit var searchBar : EditText
    private lateinit var searchToolBar : Toolbar


    init {
        Log.e("InMAIN","INIT")
        var initializeNotes = InitializeNotes()
        initializeNotes.notesFunctionsCheck()
        initializeNotes.labelFunctionsCheck()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("IN MAIN","ON CREATE")
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        fab_main = findViewById(R.id.main_fab_add_note)
        fab_main.setOnClickListener {  addNewNote()}
        fragmentContainer = findViewById(R.id.main_container)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
//        createMenuItemsFromLabels()
        drawerToggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()




        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_notes->{
                    viewMode = ViewModes.GENERAL_MODE
                    fab_main.visibility = View.VISIBLE
                    Toast.makeText(this,"notes clieck",Toast.LENGTH_SHORT).show()
                    supportFragmentManager.setFragmentResult("viewType", bundleOf("viewType" to ViewModes.GENERAL_MODE))
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.item_archive ->{
                    viewMode = ViewModes.ARCHIVE_MODE
                    fab_main.visibility = View.INVISIBLE
                    Toast.makeText(this,"archive clieck",Toast.LENGTH_SHORT).show()
                    supportFragmentManager.setFragmentResult("viewType", bundleOf("viewType" to ViewModes.ARCHIVE_MODE))
                    drawerLayout.closeDrawers()

                    true
                }
                R.id.item_trash ->{
                    viewMode  = ViewModes.TRASH_MODE
                    Toast.makeText(this,"click : ${it.itemId}",Toast.LENGTH_SHORT).show()
                    fab_main.visibility = View.INVISIBLE
//                    Toast.makeText(this,"trash clieck",Toast.LENGTH_SHORT).show()
                    supportFragmentManager.setFragmentResult("viewType", bundleOf("viewType" to ViewModes.TRASH_MODE))
                    drawerLayout.closeDrawers()
                    true
                }
                 0 ->{//This is for create new label button
//                     viewMode = ViewModes.GENERAL_MODE
                     if(it.itemId == 0){
                         val intent = Intent(this,LabelActivity::class.java)
                         intent.putExtra("viewType",LabelsViewType.EDIT.ordinal)
                         intent.putParcelableArrayListExtra("listOfLabel", ArrayList())
                         startActivity(intent)
                     }
                     drawerLayout.closeDrawers()
                     true
                 }

                else->{//This is for other labels
                    Log.e("IN MAIN","DRAWER MENU CLICK : ${listOfLabel?.get(it.itemId - 1)}")
                    selectedLabel =listOfLabel?.get(it.itemId-1)!!
                    viewMode = ViewModes.SEARCH_IN_LABEL
                    supportFragmentManager.setFragmentResult("viewType", bundleOf("viewType" to ViewModes.LABELED, "label" to selectedLabel))
                    Toast.makeText(this,"click : ${it}",Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawers()
                    false
                }

//                drawerLayout.closeDrawer(Gravity.START, false)
//                        true
            }


        }
        if(!notesFragment.isAdded){
            supportFragmentManager.beginTransaction().add(R.id.main_container,notesFragment).commitNow()
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
    }


    private fun searchNotesWithText(text: CharSequence?) {
        val searchHandler: SearchOperations = SearchOperations
        var list : List<Notes>?
        if(viewMode == ViewModes.SEARCH_IN_LABEL){
            Log.e("IN MAIN","search with label : $selectedLabel")

            supportFragmentManager.setFragmentResult("searchType",
                bundleOf("searchType" to ViewModes.SEARCH_IN_LABEL, "text" to text.toString(), "label" to selectedLabel))
        }
        else{

            supportFragmentManager.setFragmentResult("searchType",
                bundleOf("searchType" to ViewModes.SEARCH_MODE, "text" to text.toString()))
        }

    }


    override fun onResume() {
        super.onResume()
        createMenuItemsFromLabels()



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchMenuItem = menu?.findItem(R.id.search_bar)
        val searchView =  searchMenuItem?.actionView as SearchView
//        val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(viewMode == ViewModes.SEARCH_IN_LABEL)
                    searchNotesWithText(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                    searchNotesWithText(newText)
                return false
            }
        })
        searchView.setOnCloseListener {
//            setTitle()
            Log.e("IN MAIN","search view x cleick")
             false
        }
        return super.onCreateOptionsMenu(menu)
    }
    private fun setFragmentInContainer(){
        supportFragmentManager.beginTransaction().replace(R.id.main_container,notesFragment)
    }
    private fun createMenuItemsFromLabels(){
        println("*************************************************************create menu items")
        println(navView.menu.size)
        var menu = navView.menu
        if(menu.size >4 ){
            menu.removeGroup(2)
        }

        var labels_menu = menu.addSubMenu(2,123,2,"Labels")
        if(labels_menu.isNotEmpty()){
            labels_menu.clear()
        }
        val labelOperationHandler : LabelOperationHandler = LabelOperations
        listOfLabel = labelOperationHandler.getAllLabels()
        var index =1
        for(label in listOfLabel!!.iterator()){
            labels_menu.add(Menu.NONE,index,Menu.NONE,label.name).setIcon(R.drawable.ic_outline_label_24)
            index++

        }
        labels_menu.add("Create New Label").setIcon(R.drawable.ic_baseline_add_24)

        println(labels_menu)
        println("menu : % : ${menu[4]}")
        println("*************************************************************create menu items")
        println(navView.menu.size)
    }
    private fun addNewNote(){
        val newNote = Notes()
        val addNoteHandler : NotesOperationHandler = NotesOperation()
        addNoteHandler.addNote(newNote)
        val intent = Intent(this, NotesDetailActivity::class.java)
        intent.putExtra("notesId", newNote.id)
        intent.putExtra("notesType",NotesType.UNARCHIVE.ordinal)
        startActivity(intent)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return false;
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
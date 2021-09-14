package com.example.notes.fragments.main

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.ViewModes
import com.example.notes.adapters.NotesAdapter
import com.example.notes.adapters.SectionedNotesAdapter
import com.example.notes.entities.Label
import com.example.notes.entities.Notes
import com.example.notes.operations.NotesOperation
import com.example.notes.operations.NotesOperationHandler
import com.example.notes.operations.SearchOperations
import androidx.recyclerview.widget.RecyclerView.ViewHolder




class NotesFragment : Fragment() {
    private var rootView: View? = null
    private var viewType : String? =ViewModes.GENERAL_MODE
    private lateinit var recyclerView  : RecyclerView
    private lateinit var adapter : NotesAdapter
    private   var selectedLabel : Label?=null
    private var searchedText : String? = null
    private var swipeListener : ItemTouchHelper?=null
    private var listOfNotes : List<Notes>? =null
    private val notesOperationHandler  : NotesOperationHandler = NotesOperation()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Receives From Main Activity for Display Notes like Archived, Trash, etc
        setFragmentResultListener("viewType"){ requestKey, bundle ->  
            viewType = bundle.getString("viewType")
            if(viewType == ViewModes.LABELED){
                selectedLabel = bundle.getParcelable<Label>("label")
                Log.e("IN NOT FRAG","selected label $selectedLabel")
            }
            initializeViews()
        }
        //REceives From Main Activity for showing search results
        setFragmentResultListener("searchType"){ requestKey, bundle ->
            viewType = bundle.getString("searchType")
            searchedText = bundle.getString("text")
            if(viewType == ViewModes.SEARCH_IN_LABEL){
                selectedLabel = bundle.getParcelable<Label>("label")
                Log.e("IN NOT FRAG","selected label $selectedLabel")
            }
            Log.e("IN NOT FRAG 2 Lsit"," text : $searchedText")
            initializeViews()
        }




        retainInstance = true
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        Log.e("NOTES FRAGMENT","ON CREATE VIEW")
        rootView =inflater?.inflate(R.layout.fragment_notes, container, false)
        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("viewType",viewType)
        super.onSaveInstanceState(outState)
    }
    override fun onResume() {
        super.onResume()
        Log.e("NOTES FRAGMENT","ON RESUME")
//        val listOfNotes = notesOperationHandler.getAllUnArchivedNotes()
//        adapter = NotesAdapter(this.requireContext(),listOfNotes)
//        recyclerView.adapter = adapter
//        adapter.notifyDataSetChanged()
        initializeViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        recyclerView = view?.findViewById(R.id.notes_recycler_view)
        if(savedInstanceState != null){
            Log.e("NOTES FRAGMENT","SAVED INSTANCE NOT NULL")
        }

        initializeViews()
        Log.e("NOTES FRAGMENT","ON VIEW CREATE")
        super.onViewCreated(view, savedInstanceState)


    }

    private fun initializeViews(){
        Log.e("NOTES FRAGMENT","INITIALIZE VIEWS : $viewType")
        activity?.setTitle(viewType)
        var dividerItemDecoration = DividerItemDecoration(recyclerView.context, Configuration.ORIENTATION_PORTRAIT)
        listOfNotes =getNotesList(viewType!!)
        adapter = NotesAdapter(this.requireContext(),listOfNotes)
        var sectionedadapter = SectionedNotesAdapter(this.requireContext())
        sectionedadapter.groupTheList(listOfNotes)
        recyclerView.adapter = sectionedadapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        swipeListener = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,ItemTouchHelper.RIGHT or
                        ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder, target: ViewHolder
                ): Boolean {
                    return false // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition - 1
                    Log.e("ON SWIPE","layout position ${position}")
                    var notes = listOfNotes?.get(position)
                    // remove from adapter
                    when(direction){
                        ItemTouchHelper.RIGHT,ItemTouchHelper.LEFT->{
                            notesOperationHandler.archiveNote(notes!!)
                            (listOfNotes as ArrayList).removeAt(position)
//                            adapter.notifyItemRemoved(position)

//                            sectionedadapter.notifyItemRemoved(position + 1)
                            sectionedadapter.groupTheList(listOfNotes)
//                            sectionedadapter.notifyDataSetChanged()

                            return

                        }

                    }
                    Toast.makeText(requireContext(),"swipe detecttor : $direction",Toast.LENGTH_SHORT).show()
                }
            })
        swipeListener?.attachToRecyclerView(recyclerView)




//        recyclerView.addItemDecoration(dividerItemDecoration)
    }
    private fun getNotesList(viewType: String) : List<Notes>?{
        return when(viewType) {
            ViewModes.GENERAL_MODE -> {
                println("___________________________________________________________________________")
                println(notesOperationHandler.getPinnedAndUnArchiveNotes())
                notesOperationHandler.getPinnedAndUnArchiveNotes()

            }
            ViewModes.ARCHIVE_MODE -> {
                notesOperationHandler.getAllArchivedNotes()
            }
            ViewModes.TRASH_MODE -> {
                println(notesOperationHandler.getAllTrashNotes())
                notesOperationHandler.getAllTrashNotes()
            }
            ViewModes.LABELED->{
                activity?.title = "Notes Contains: "+selectedLabel?.name
                println( notesOperationHandler.getAllNotesByLabel(selectedLabel!!))
                notesOperationHandler.getAllNotesByLabel(selectedLabel!!)
            }
            ViewModes.SEARCH_MODE ->{
                var searchHandler = SearchOperations
                searchHandler.searchInNotes(searchedText!!)
            }
            else ->{
//                val selectedLabel =
                Log.e("IN NOTES FRAG","else in viewmode")
                var searchHandler = SearchOperations
                println(searchHandler.searchInLabeledNotes(selectedLabel!!, searchedText!!))
                searchHandler.searchInLabeledNotes(selectedLabel!!, searchedText!!)
//
            }
        }

    }

    override fun onPause() {
        Log.e("NOTES FRAGMENT","ON APUSE")
        super.onPause()
    }

    companion object{
        fun newInstance(/*viewType : String*/) : NotesFragment{
            var fragment = NotesFragment()
//            var args = Bundle()
//            args.putString("viewTYpe", viewType)
//            fragment.arguments = args
            return fragment
        }
    }
}
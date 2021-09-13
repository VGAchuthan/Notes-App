package com.example.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.adapters.LabelAdapter
import com.example.notes.entities.Label
import com.example.notes.enums.LabelsViewType
import com.example.notes.operations.LabelOperationHandler
import com.example.notes.operations.LabelOperations

class LabelActivity : AppCompatActivity() {
    private val labelOperationHandler : LabelOperationHandler = LabelOperations
    private lateinit var recyclerView: RecyclerView
    private lateinit var newLabel: EditText
    private lateinit var viewType: LabelsViewType
    private lateinit var labelAdapter: LabelAdapter
    private lateinit var errorMessageView : TextView
    lateinit var toolbar: Toolbar
    private val REQUEST_CODE_FOR_LABELS = 1
    lateinit private var listOfSelectedLabels:ArrayList<Label>


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_label)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val viewTypeOrdinal = intent.extras?.get("viewType").toString().toInt()
        this.listOfSelectedLabels = intent.extras?.getParcelableArrayList<Label>("listOfLabel") as ArrayList<Label>
        viewType = LabelsViewType.values()[viewTypeOrdinal]
        recyclerView = findViewById(R.id.label_activity_recyclerview)
        newLabel = findViewById(R.id.label_activity_add_label)
        errorMessageView = findViewById(R.id.label_activity_error_view)
        initializeViews()

        newLabel.doOnTextChanged { text, start, before, count ->
            if(checkIfLabelExists(text.toString())){
                errorMessageView.visibility = View.VISIBLE
            }
            else
                errorMessageView.visibility = View.GONE
        }
        newLabel.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (newLabel.right - newLabel.compoundDrawables[2].bounds
                        .width())
                ) {
                    addNewLabel()
                    newLabel.text.clear()

                    true
                }
                false
            } else if (motionEvent.rawX <= (newLabel.left + newLabel.compoundDrawables[0].bounds
                    .width())
            ) {

                Toast.makeText(this, "left drawable click", Toast.LENGTH_SHORT).show()
                newLabel.text.clear()
                newLabel.clearFocus()

                true
            }
            false
        }
        false


    }

    private fun checkIfLabelExists(labelName : String) : Boolean{
        return labelOperationHandler.checkIfLabelExist(labelName)
    }

    private fun addNewLabel() {
        val labelName = newLabel.text.toString().trim()
        if(labelName.trim() != ""){
            val newLabel = Label(labelName)
            labelOperationHandler.addLabel(newLabel)
            initializeViews()
        }

    }

    private fun initializeViews() {
        println("____________________________________________ADATER LABEL ${labelOperationHandler.getAllLabels()}")
        when (viewType) {

            LabelsViewType.EDIT -> {
                setTitle(R.string.title_edit_label)
                println(labelOperationHandler.getAllLabels())
                labelAdapter =
                    LabelAdapter(this, labelOperationHandler.getAllLabels()!!, LabelsViewType.EDIT)
                recyclerView.adapter = labelAdapter
                recyclerView.layoutManager = LinearLayoutManager(this)

            }
            LabelsViewType.SELECT -> {
//                listOfSelectedLabels = ArrayList<Label>()
                setTitle(R.string.title_select_label)
                newLabel.visibility = View.GONE
                labelAdapter =
                    LabelAdapter(this, labelOperationHandler.getAllLabels()!!, LabelsViewType.SELECT, this.listOfSelectedLabels)
                recyclerView.adapter = labelAdapter
                recyclerView.layoutManager = LinearLayoutManager(this)

            }
        }

    }
    fun addSelectedLabels(label : Label){
        Log.e("IN LABEL ACT","lable : $label")
        this.listOfSelectedLabels.add(label)
    }
    fun removeSelectedLabels(label : Label){
        this.listOfSelectedLabels.remove(label)
    }

    override fun onPause() {
        Log.e("in labe ac","on pauese")
        super.onPause()

//        finish()
    }

    override fun onBackPressed() {
        val resultIntent = Intent(this, NotesDetailActivity::class.java)
        resultIntent.putParcelableArrayListExtra("selectedLabelList",this.listOfSelectedLabels)
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
//            overridePend

            true
        }
        else ->
            false
    }
}

interface LabelOnClickListener{
    fun updateLabel(oldLabel : Label, position : Int )
}
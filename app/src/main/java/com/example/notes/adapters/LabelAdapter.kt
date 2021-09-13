package com.example.notes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.LabelActivity
import com.example.notes.R
import com.example.notes.entities.Label
import com.example.notes.enums.LabelsViewType
import com.example.notes.operations.LabelOperationHandler
import com.example.notes.operations.LabelOperations
import com.example.notes.operations.NotesOperation
import com.example.notes.operations.NotesOperationHandler

class LabelAdapter(private val context: Context, labelList : List<Label>, private val type: LabelsViewType,
private var listOfExistingLabels : List<Label> = ArrayList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val list = labelList
    private var currentSelectedItemIndex : Int = -1
    private var previousSelectedItemIndex : Int = -1
    private var listOfSelectedLabel  = ArrayList<Label>()
    private var labelOperation : LabelOperationHandler = LabelOperations
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LabelEditViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_label_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LabelEditViewHolder).bind(list?.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private inner class LabelEditViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private var isSelected : Boolean = false
        private val labelName = itemView.findViewById<EditText>(R.id.label_view_edittext)
        private val leftDrawable = itemView.findViewById<View>(R.id.label_view_left_icon_view)
        private val rightDrawable = itemView.findViewById<View>(R.id.label_view_right_icon_view)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.label_view_select_checkbox)

        private lateinit var current_label : Label
        init {


            if(type == LabelsViewType.EDIT){
                checkBox.visibility = View.INVISIBLE
            }else
            {
                checkBox.visibility = View.VISIBLE
                rightDrawable.visibility = View.INVISIBLE
            }


        }
        fun onCLickFunctionalities(){
            if(!isSelected)
                isSelected = true
            previousSelectedItemIndex = currentSelectedItemIndex
            currentSelectedItemIndex = position
            Log.e("IN L A","$previousSelectedItemIndex : $currentSelectedItemIndex")
            if(previousSelectedItemIndex != -1)
                notifyItemChanged(previousSelectedItemIndex)

            selectLabelFunctionalities()
        }
        fun bind(label : Label){
            this.current_label = label
            if(listOfExistingLabels.contains(current_label)){
                checkBox.isChecked = true
            }
            labelName.setText(label.name,TextView.BufferType.EDITABLE)
            if(type == LabelsViewType.EDIT){
//                labelName.isClickable = false
//                labelName.isFocusable = false

                labelName.setOnClickListener {
                    onCLickFunctionalities()
                }
                itemView.setOnClickListener {
                    onCLickFunctionalities()
                }
                rightDrawable.setOnClickListener {
                    if(isSelected){
                        previousSelectedItemIndex = currentSelectedItemIndex
                        currentSelectedItemIndex = -1
                        isSelected=false

                        Log.e("IN L A","editd draw click")
                        updateLabel()
                        notifyItemChanged(this.layoutPosition)


                    }


                }

                selectLabelFunctionalities()
            }
            else
            {
                checkBox.setOnCheckedChangeListener{buttonView, isChecked ->
                    if(isChecked){
                        println("${this.labelName}")
                        listOfSelectedLabel.add(current_label)
                        if(context is LabelActivity){
                                if(!listOfExistingLabels.contains(current_label)){
                                    (context as LabelActivity).addSelectedLabels(current_label)
                                }
//
                        }
                        println("$listOfSelectedLabel")

                    }
                    else{
                        if(context is LabelActivity){
                            (context as LabelActivity).removeSelectedLabels(current_label)
                        }
                        listOfSelectedLabel.remove(current_label)
                    }

                }

                if(this.checkBox.isSelected){
                    println("${this.labelName}")
                }

            }




        }
        private fun updateLabel(){
            Log.e("IN L A","update label : ${current_label.name}")
            val newName = labelName.text.toString()
            Log.e("IN L A","update label : ${current_label.name} , $newName")
            if(current_label.name.equals(newName)){
                Toast.makeText(context,"Labels are same", Toast.LENGTH_SHORT).show()
            }
            println("_______________________________________________________________________$newName")
            println(labelOperation.editLabel(current_label, newName))
        }
        @SuppressLint("UseCompatLoadingForDrawables")
        private fun selectLabelFunctionalities(){
            if(currentSelectedItemIndex == this.layoutPosition){
                Log.e("IN LABEL ADAPTER","CURENTLY LCIKEC : $currentSelectedItemIndex - $position - $layoutPosition")
                itemView.setBackgroundColor(context.resources.getColor(R.color.label_selected))
                leftDrawable.background = context.getDrawable(R.drawable.ic_baseline_delete_outline_24)
                rightDrawable.background = context.getDrawable(R.drawable.ic_baseline_done_24)
                this.labelName.isClickable = true
                this.labelName.isFocusable = true
                this.labelName.isFocusableInTouchMode = true
                this.labelName.requestFocus()

                leftDrawable.setOnClickListener {
                    if(isSelected)
                        deleteSelectedLabel(this.current_label)
                }

            }
            else{
                isSelected = false
                itemView.setBackgroundColor(context.resources.getColor(R.color.white))
                leftDrawable.background = context.getDrawable(R.drawable.ic_outline_label_24)
                rightDrawable.background = context.getDrawable(R.drawable.ic_baseline_edit_24)
//                this.labelName.isClickable = false
                this.labelName.isFocusable = false

            }



//            labelName.isClickable = true




        }

        private fun deleteSelectedLabel(currentLabel: Label) {
            labelOperation.deleteLabel(currentLabel)
//            notifyItemChanged(this.layoutPosition)

            (list as ArrayList).remove(currentLabel)
            notifyItemRemoved(this.adapterPosition)

        }

    }

}
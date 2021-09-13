package com.example.notes.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.NotesDetailActivity
import com.example.notes.R
import com.example.notes.entities.Notes
import com.example.notes.enums.NotesType
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class NotesAdapter(private val context: Context, dataSet : List<Notes>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = dataSet
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SimpleNotesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_notes_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SimpleNotesViewHolder).bind(list?.get(position))
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
    private inner class SimpleNotesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val titleView = itemView.findViewById<TextView>(R.id.notes_card_title_text)
        val contentView = itemView.findViewById<TextView>(R.id.notes_card_content_text)
        val chipGroup = itemView.findViewById<ChipGroup>(R.id.notes_detail_chip_group)
        val chipContainer = itemView.findViewById<FrameLayout>(R.id.notes_card_chip)
        private lateinit var current_note : Notes
        init{

            this.itemView.setOnClickListener {

                this.callDetailsActivity(this.current_note.id!!) }

        }

        fun bind(notes : Notes?){
            this.current_note = notes!!
            fillLabelsInChip()
            if(notes.isPinned){
                titleView.text = "isPonned"
            }
            else{
                titleView.text = notes?.title + notes?.id//viewModel?.title
            }

//            Log.e("FROM BIND","NOTE ID :, $itemView")
            val viewModel = list?.get(position)

            contentView.text = notes?.content//viewModel?.content
//            itemView.setOnClickListener{ Log.e("FRAM", "GTASDS")}


            itemView.setOnLongClickListener {

                Toast.makeText(this.itemView.context,"long click",Toast.LENGTH_LONG).show()
                true
            }

        }

        private fun fillLabelsInChip() {
            Log.e("NOTES ADAPT","${chipContainer.childCount} != ${current_note.listOfLabels.size}")
            chipContainer.removeAllViews()
            if(chipContainer.childCount != current_note.listOfLabels.size)
            if(!current_note.listOfLabels.isEmpty())
            {
                chipContainer.visibility = View.VISIBLE
                var index = 0
                for(label in current_note.listOfLabels){
                    if(index >1){
                        val chip = Chip(this.itemView.context)
                        chip.text = "+" + (current_note.listOfLabels.size - 2)
                        chipGroup.addView(chip)
                        break
                    }
                    else
                    {
                        val chip = Chip(this.itemView.context)
                        chip.text = label.name
                        chipGroup.addView(chip)
                        index++

                    }

                }
            }
        }

        private fun callDetailsActivity(id : Int){
            //Log.e("RECYCLER","in call mehos")
            var notesTypeOrdinal = getSelectedNotesType()
//            println("RECYCLER LA : $notesTypeOrdinal")
            val intent = Intent(this.itemView.context,NotesDetailActivity::class.java)
            intent.putExtra("notesId",id)
            intent.putExtra("notesType",notesTypeOrdinal)
//            intent.put
            this.itemView.context.startActivity(intent)
        }
        private fun getSelectedNotesType(): Int {
            return if(current_note.isArchived){
                NotesType.ARCHIVED.ordinal
            }
            else if (current_note.isDeleted){
                NotesType.TRASH.ordinal
            }
            else if(current_note.isPinned){
                NotesType.PINNED.ordinal
            }

            else
                NotesType.UNARCHIVE.ordinal
        }
    }


}
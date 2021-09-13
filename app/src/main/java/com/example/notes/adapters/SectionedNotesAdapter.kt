package com.example.notes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.entities.Notes
import androidx.recyclerview.widget.ListAdapter
import com.example.notes.NotesDetailActivity
import com.example.notes.R
import com.example.notes.enums.NotesType
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

private val ITEM_VIEW_TYPE_HEADER = -1
private val ITEM_VIEW_TYPE_ITEM = -2
class NotesDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class SectionedNotesAdapter(private val context : Context,dataSet : List<Notes>?)  : ListAdapter<DataItem,RecyclerView.ViewHolder>(NotesDiffCallback()){
    private val list = dataSet
    init {
        val items = when(list){
            null -> { listOf(DataItem.Header("Loading"))}
            else ->{
                val gropuByList = list.groupBy { it.notesType }
                var myList = ArrayList<DataItem>()
                for(key in gropuByList.keys){
                    myList.add(DataItem.Header(key.toString().toLowerCase()))
                    for(items in gropuByList.getValue(key)){
                        myList.add(DataItem.NotesItem(items))
                    }
                }
                myList
            }
        }
        submitList(items)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_item_header, parent, false)
            )
            ITEM_VIEW_TYPE_ITEM -> NotesViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_notes_card, parent, false)
            )
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.NotesItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is HeaderViewHolder-> {
                holder.bind(getItem(position) as DataItem.Header)

            }
            is NotesViewHolder ->{
                holder.bind(getItem(position) as DataItem.NotesItem)
            }
        }
    }
    private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val headerTitle = itemView.findViewById<TextView>(R.id.layout_header_title)
            fun bind(item : DataItem.Header){
                if(item.typeName == NotesType.UNARCHIVE.toString().toLowerCase()){
                    headerTitle.text = "OTHERS"
                }
                else
                    headerTitle.text = item.typeName.toUpperCase()
            }
    }

    private inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleView = itemView.findViewById<TextView>(R.id.notes_card_title_text)
        val contentView = itemView.findViewById<TextView>(R.id.notes_card_content_text)
        val chipGroup = itemView.findViewById<ChipGroup>(R.id.notes_detail_chip_group)
        val chipContainer = itemView.findViewById<FrameLayout>(R.id.notes_card_chip)
        private lateinit var current_note : Notes
        init{

            this.itemView.setOnClickListener {

                this.callDetailsActivity(this.current_note.id!!) }

        }

        fun bind(_notes : DataItem.NotesItem?){
            val notes = _notes!!.note
            this.current_note = notes
            fillLabelsInChip()

                titleView.text = notes?.title + notes?.id//viewModel?.title

            contentView.text = notes?.content//viewModel?.content
            itemView.setOnLongClickListener {

                Toast.makeText(this.itemView.context,"long click", Toast.LENGTH_LONG).show()
                true
            }

        }

        private fun fillLabelsInChip() {
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

            var notesTypeOrdinal = getSelectedNotesType()
            val intent = Intent(this.itemView.context, NotesDetailActivity::class.java)
            intent.putExtra("notesId",id)
            intent.putExtra("notesType",notesTypeOrdinal)

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


sealed class DataItem {
    data class NotesItem(val note: Notes): DataItem() {
        override val id = note.id
    }

    data class Header(val typeName: String): DataItem() {
        override val id = typeName.hashCode()
    }

    abstract val id: Int
}
package com.rawlin.notesapp.ui.notes_lists.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.rawlin.notesapp.R
import com.rawlin.notesapp.domain.Note

class PinnedNotesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            TODO("not implemented")
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            TODO("not implemented")
        }

    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Note>) {
        differ.submitList(list)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Note) = with(itemView) {
            TODO("bind view with data")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.notes_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotesViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Note) -> Unit)? = null

    fun setOnItemClickListener(listener: (Note) -> Unit) {
        onItemClickListener = listener
    }

}


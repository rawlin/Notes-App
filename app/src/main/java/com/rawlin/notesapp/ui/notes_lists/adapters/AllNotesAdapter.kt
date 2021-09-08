package com.rawlin.notesapp.ui.notes_lists.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.rawlin.notesapp.R
import com.rawlin.notesapp.databinding.NotesItemBinding
import com.rawlin.notesapp.domain.Note

class AllNotesAdapter : RecyclerView.Adapter<AllNotesAdapter.NotesViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Note>) {
        differ.submitList(list)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = NotesItemBinding.bind(itemView)
        fun bind(item: Note) = with(binding) {
            itemTitleTextView.text = item.title
            itemMessageTextView.text = item.message
            itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {

        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.notes_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Note) -> Unit)? = null

    fun setOnItemClickListener(listener: (Note) -> Unit) {
        onItemClickListener = listener
    }

}


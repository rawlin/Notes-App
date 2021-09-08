package com.rawlin.notesapp.ui.notes_lists.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rawlin.notesapp.R
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.databinding.NotesItemBinding


class PinnedNotesAdapter : RecyclerView.Adapter<PinnedNotesAdapter.NotesViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<PinnedNote>() {

        override fun areItemsTheSame(oldItem: PinnedNote, newItem: PinnedNote): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PinnedNote, newItem: PinnedNote): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<PinnedNote>) {
        differ.submitList(list)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = NotesItemBinding.bind(itemView)

        fun bind(item: PinnedNote) = with(binding) {
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

    private var onItemClickListener: ((PinnedNote) -> Unit)? = null

    fun setOnItemClickListener(listener: (PinnedNote) -> Unit) {
        onItemClickListener = listener
    }

}


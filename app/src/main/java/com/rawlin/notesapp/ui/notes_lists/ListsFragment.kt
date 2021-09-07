package com.rawlin.notesapp.ui.notes_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isEmpty
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.rawlin.notesapp.databinding.FragmentListsBinding
import com.rawlin.notesapp.utils.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListsFragment : BindingFragment<FragmentListsBinding>() {

    private val viewModel by viewModels<NotesListsViewModel>()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentListsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            createNoteButton.setOnClickListener {
                val directions = ListsFragmentDirections.actionListsFragmentToNotesDetailsFragment()
                findNavController().navigate(directions)
            }
            if (recyclerView.isEmpty())
        }
    }
}
package com.rawlin.notesapp.ui.notes_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.rawlin.notesapp.databinding.FragmentListsBinding
import com.rawlin.notesapp.ui.notes_lists.adapters.AllNotesAdapter
import com.rawlin.notesapp.ui.notes_lists.adapters.PinnedNotesAdapter
import com.rawlin.notesapp.utils.BindingFragment
import com.rawlin.notesapp.utils.Resource
import com.rawlin.notesapp.utils.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ListsFragment : BindingFragment<FragmentListsBinding>() {

    private val viewModel by viewModels<NotesListsViewModel>()
    private val pinnedNotesAdapter = PinnedNotesAdapter()
    private val allNotesAdapter = AllNotesAdapter()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentListsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllNotes()
        setupAllNotes()
        setupPinnedNotes()
        binding.apply {
            createNoteButton.setOnClickListener {
                val directions =
                    ListsFragmentDirections.actionListsFragmentToNotesDetailsFragment(null, null)
                findNavController().navigateSafely(directions)
            }

            settingsButton.setOnClickListener {
                val directions = ListsFragmentDirections.actionGlobalSettingsFragment()
                findNavController().navigateSafely(directions)
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.allNotes.collect { allNotesState ->
                        when (allNotesState) {
                            is Resource.Success -> {
                                allNotesAdapter.submitList(allNotesState.data ?: emptyList())
                                binding.notesVisiblityTextView.isVisible = allNotesState.data.isNullOrEmpty()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    allNotesState.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> Unit
                        }
                    }
                }

                launch {
                    viewModel.allPinnedNotes.collect { pinnedNotes ->
                        when (pinnedNotes) {
                            is Resource.Success -> {
                                pinnedNotesAdapter.submitList(pinnedNotes.data ?: emptyList())
                                binding.noPinnedNotesTextView.isVisible = pinnedNotes.data.isNullOrEmpty()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    pinnedNotes.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> Unit
                        }
                    }
                }

                launch {
                    viewModel.pinMode.collect { isEnabled ->
                        binding.apply {
                            pinnedTextView.isVisible = isEnabled
                            pinnedHolder.isVisible = isEnabled

                        }

                    }
                }

            }
        }

        allNotesAdapter.setOnItemClickListener { note ->
            val destination = ListsFragmentDirections.actionListsFragmentToNotesDetailsFragment(
                note = note,
                pinnedNote = null
            )
            findNavController().navigateSafely(destination)
        }

        pinnedNotesAdapter.setOnItemClickListener { pinnedNote ->
            val destination = ListsFragmentDirections.actionListsFragmentToNotesDetailsFragment(
                note = null,
                pinnedNote = pinnedNote
            )
            findNavController().navigateSafely(destination)
        }

    }

    private fun setupPinnedNotes() {
        binding.pinnedRecyclerView.apply {
            adapter = pinnedNotesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupAllNotes() {
        binding.allNotesRecyclerView.apply {
            adapter = allNotesAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllNotes()
    }
}
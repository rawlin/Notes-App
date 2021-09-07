package com.rawlin.notesapp.ui.notes_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.rawlin.notesapp.databinding.FragmentNotesDetailBinding
import com.rawlin.notesapp.utils.BindingFragment
import com.rawlin.notesapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesDetailFragment : BindingFragment<FragmentNotesDetailBinding>() {

    private val viewModel by viewModels<NotesDetailViewModel>()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNotesDetailBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteTitleEditText.doAfterTextChanged { titleText ->
                noteTitleTextView.text = titleText.toString()
            }


            saveNoteButton.setOnClickListener {
                val title = noteTitleEditText.text.toString()
                val message = noteMessageEditText.text.toString()
                viewModel.createNote(title, message)
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createNoteState.collect { state->
                    when (state) {
                        is Resource.Success -> {
                            Toast.makeText(requireContext(), "Note Created", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            //Show loading
                        }
                    }
                }
            }
        }


    }
}
package com.rawlin.notesapp.ui.notes_details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import coil.Coil
import coil.load
import com.rawlin.notesapp.R
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.databinding.FragmentNotesDetailBinding
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.utils.BindingFragment
import com.rawlin.notesapp.utils.Constants.PICK_FROM_GALLARY
import com.rawlin.notesapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "NotesDetailFragment"

@AndroidEntryPoint
class NotesDetailFragment : BindingFragment<FragmentNotesDetailBinding>() {

    private val viewModel by viewModels<NotesDetailViewModel>()
    private var note: Note? = null
    private var pinnedNote: PinnedNote? = null

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNotesDetailBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        note = arguments?.let { NotesDetailFragmentArgs.fromBundle(it).note }
        pinnedNote = arguments?.let { NotesDetailFragmentArgs.fromBundle(it).pinnedNote }
        setView()

        binding.apply {
            noteTitleEditText.doAfterTextChanged { titleText ->
                noteTitleTextView.text = titleText.toString()
            }


            saveNoteButton.setOnClickListener {
                val title = noteTitleEditText.text.toString()
                val message = noteMessageEditText.text.toString()
                if (pinnedNote != null) {
                    pinnedNote!!.id?.let { it1 ->
                        viewModel.updatePinnedNote(
                            title = title,
                            message = message,
                            id = it1
                        )
                    }
                    return@setOnClickListener
                }
                if (note != null) {
                    note!!.id?.let { it1 ->
                        viewModel.updateNote(
                            title = title,
                            message = message,
                            id = it1
                        )
                    }
                    return@setOnClickListener
                }
                viewModel.createNote(title, message)
            }

            detailsSettingsButton.setOnClickListener {
                val destination = NotesDetailFragmentDirections.actionGlobalSettingsFragment()
                findNavController().navigate(destination)
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.createNoteState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Note Created", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is Resource.Loading -> {
                                //Show loading
                            }
                        }
                    }
                }

                launch {
                    viewModel.updateNoteState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Note Updated", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                                Log.d(TAG, "Note Updated")
                            }
                            is Resource.Loading -> Unit
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

            }
        }


    }

    private fun setView() = with(binding) {
        if (pinnedNote != null) {
            noteTitleEditText.setText(pinnedNote!!.title)
            noteMessageEditText.setText(pinnedNote!!.message)
            return@with
        }
        if (note != null) {
            noteTitleEditText.setText(note!!.title)
            noteMessageEditText.setText(note!!.message)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_addImage -> {
                val gallaryIntent = Intent().apply {
                    action = Intent.ACTION_GET_CONTENT
                    type = "image/*"
                }
                Log.d(TAG, "onOptionsItemSelected: Add Image")
                Toast.makeText(requireContext(), "HELLLOOOO", Toast.LENGTH_SHORT).show()
                startActivityForResult(gallaryIntent, PICK_FROM_GALLARY)
                return true
            }
            R.id.action_delete -> {
                viewModel.deleteNote()
                return true
            }
            R.id.action_pin -> {
                viewModel.pinNote()
                return true
            }
            R.id.action_share -> {
//                viewModel.share
                return true
            }
            else -> false

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_FROM_GALLARY){
            val imageUri = data?.data ?: return
            updateImage(imageUri)
        }
    }

    private fun updateImage(imageUri: Uri) {
        binding.imageView.load(imageUri)
    }
}
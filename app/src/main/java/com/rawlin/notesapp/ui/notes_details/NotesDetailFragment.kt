package com.rawlin.notesapp.ui.notes_details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rawlin.notesapp.R
import com.rawlin.notesapp.database.PinnedNote
import com.rawlin.notesapp.databinding.FragmentNotesDetailBinding
import com.rawlin.notesapp.domain.Note
import com.rawlin.notesapp.utils.BindingFragment
import com.rawlin.notesapp.utils.Constants.PICK_FROM_GALLARY
import com.rawlin.notesapp.utils.Resource
import com.rawlin.notesapp.utils.isValidInput
import com.rawlin.notesapp.utils.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "NotesDetailFragment"

@AndroidEntryPoint
class NotesDetailFragment : BindingFragment<FragmentNotesDetailBinding>() {

    private val viewModel by viewModels<NotesDetailViewModel>()
    private var note: Note? = null
    private var pinnedNote: PinnedNote? = null
    private var isSharingEnabled = false
    private var imageUri: Uri? = null

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNotesDetailBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        note = arguments?.let { NotesDetailFragmentArgs.fromBundle(it).note }
        pinnedNote = arguments?.let { NotesDetailFragmentArgs.fromBundle(it).pinnedNote }
        setViewAndData()

        binding.apply {
            noteTitleEditText.doAfterTextChanged { titleText ->
                noteTitleTextView.text = titleText.toString()
            }


            saveNoteButton.setOnClickListener {
                val title = noteTitleEditText.text.toString()
                val message = noteMessageEditText.text.toString()
                if (pinnedNote != null) {
                    pinnedNote?.id?.let { it1 ->
                        viewModel.updatePinnedNote(
                            title = title,
                            message = message,
                            id = it1,
                            imageUri = imageUri
                        )
                    }
                    return@setOnClickListener
                }
                if (note != null) {
                    note?.id?.let { it1 ->
                        viewModel.updateNote(
                            title = title,
                            message = message,
                            id = it1,
                            imageUri = imageUri,
                            createdTime = note?.createdTime
                        )
                    }
                    return@setOnClickListener
                }
                viewModel.createNote(title, message, imageUri)
            }

            detailsSettingsButton.setOnClickListener {
                val destination = NotesDetailFragmentDirections.actionGlobalSettingsFragment()
                findNavController().navigateSafely(destination)
            }

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_addImage -> {
                        handleAddAndRemoveImage()
                        true
                    }
                    R.id.action_delete -> {
                        handleDelete()
                        Log.d(TAG, "Delete")
                        true
                    }
                    R.id.action_pin -> {
                        handlePinAndUnpin()
                        Log.d(TAG, "Pin")
                        true
                    }
                    R.id.action_share -> {
                        if (isSharingEnabled) {
                            handleShare()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.enable_sharing_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.d(TAG, "Share")
                        true
                    }
                    else -> true
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.createNoteState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.note_created_message),
                                    Toast.LENGTH_SHORT
                                )
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
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.note_updated_success),
                                    Toast.LENGTH_SHORT
                                )
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

                launch {
                    viewModel.isSharingEnabled.collect { isEnabled ->
                        isSharingEnabled = isEnabled
                    }
                }

                launch {
                    viewModel.deleteNoteStatus.collect { deleteStatus ->
                        when (deleteStatus) {
                            is Resource.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.note_deleted_message),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    deleteStatus.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> Unit
                        }
                    }
                }

                launch {
                    viewModel.pinnedState.collect { pinnedState ->
                        when (pinnedState) {
                            is Resource.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.note_pinned_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.toolbar.menu.findItem(R.id.action_pin).title =
                                    getString(R.string.unpin_note)
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    pinnedState.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> {

                            }
                        }
                    }
                }

                launch {
                    viewModel.unPinnedState.collect { unPinnedState ->
                        when (unPinnedState) {
                            is Resource.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.unpinned_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.toolbar.menu.findItem(R.id.action_pin).title =
                                    getString(R.string.pin_note)
                            }
                            is Resource.Loading -> {

                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    unPinnedState.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }

            }
        }


    }

    private fun handleAddAndRemoveImage() {
        if (imageUri == null) {
            val gallaryIntent = Intent().apply {
                action = Intent.ACTION_OPEN_DOCUMENT
                type = "image/*"
            }
            Log.d(TAG, "onOptionsItemSelected: Add Image")
            startActivityForResult(gallaryIntent, PICK_FROM_GALLARY)
        } else {
            removeImage()
        }
    }

    private fun handlePinAndUnpin() {
        if (note == null && pinnedNote == null) {
            Toast.makeText(requireContext(), getString(R.string.invalid_pin), Toast.LENGTH_SHORT)
                .show()
            return
        }
        note?.let { pinNote(it) }
        pinnedNote?.let { unPinNote(it) }
    }

    private fun handleShare() {
        val title = binding.noteTitleEditText.text.toString()
        val message = binding.noteMessageEditText.text.toString()
        var imageUri = note?.imageUri
        if (imageUri == null)
            imageUri = pinnedNote?.imageUri
        if (title.isValidInput() && message.isValidInput()) {
            shareNote(title, message, imageUri)
        } else {
            Toast.makeText(requireContext(), getString(R.string.invalid_share), Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun handleDelete() {
        if (note == null && pinnedNote == null) {
            Toast.makeText(requireContext(), getString(R.string.invalid_delete), Toast.LENGTH_SHORT)
                .show()
            return
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setPositiveButton(R.string.yes) { _, _ ->
                note?.let { viewModel.deleteNote(it) }
                pinnedNote?.let { viewModel.deletePinnedNote(it) }
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
            }
            .show()
    }

    private fun shareNote(title: String, message: String, imageUri: String?) {

        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = SHARE_INTENT_TYPE
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
//        if (imageUri != null) {
//            val uri = Uri.parse(imageUri)
//            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        }
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)))

    }

    private fun unPinNote(pinnedNote: PinnedNote) {
        viewModel.unPinNote(pinnedNote)
    }

    private fun pinNote(note: Note) {
        viewModel.pinNote(note)
    }

    private fun removeImage() {
        binding.imageView.isVisible = false
        note = note?.copy(imageUri = null)
        pinnedNote = pinnedNote?.copy(imageUri = null)
        binding.toolbar.menu.findItem(R.id.action_addImage).title = getString(R.string.add_image)
        imageUri = null
    }

    private fun setViewAndData() = with(binding) {
        if (pinnedNote != null) {
            noteTitleEditText.setText(pinnedNote?.title)
            noteTitleTextView.text = pinnedNote?.title
            noteMessageEditText.setText(pinnedNote?.message)
            if (pinnedNote?.imageUri != null) {
                imageUri = Uri.parse(pinnedNote?.imageUri)
            }
            binding.toolbar.menu.findItem(R.id.action_pin).title = getString(R.string.unpin_note)
            if (pinnedNote?.imageUri != null) {
                setImage(Uri.parse(pinnedNote?.imageUri))
            }
            return@with
        }
        if (note != null) {
            noteTitleEditText.setText(note?.title)
            noteTitleTextView.text = note?.title
            if (note?.imageUri != null) {
                imageUri = Uri.parse(note?.imageUri)
            }
            noteMessageEditText.setText(note?.message)
            if (note?.imageUri != null) {
                setImage(Uri.parse(note?.imageUri))
            }
        }

    }

    private fun setImage(imageUri: Uri?) {
        binding.imageView.load(imageUri) {
            this.listener({},{}, { _, throwable ->
                Log.e(TAG, "Could not set image",throwable)
            },{ _, _ ->
                Log.d(TAG, "Image set")
            })
        }
        binding.imageView.isVisible = true
        binding.toolbar.menu.findItem(R.id.action_addImage).title = getString(R.string.remove_image)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_FROM_GALLARY) {
            imageUri = data?.data
            updateImage(imageUri ?: return)
        }
    }

    private fun updateImage(imageUri: Uri) {
        setImage(imageUri)
        val newNote = note?.copy(imageUri = imageUri.toString())
        note = newNote
        val newPinnedNote = pinnedNote?.copy(imageUri = imageUri.toString())
        pinnedNote = newPinnedNote
    }

    companion object {
        private const val SHARE_INTENT_TYPE = "text/*"
    }
}
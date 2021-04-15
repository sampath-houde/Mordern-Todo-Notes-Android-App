package com.example.mordernnotesandtodo.fragment.Notes.AddOrUpdateNoteFragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.viewModel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_add_note.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.InputStream


class AddOrUpdateNoteFragment : Fragment() {

    private val date = FormatDate().date
    private lateinit var mNotesViewModel: NotesViewModel
    var selectedImagePath: ByteArray? = null
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private val args by navArgs<AddOrUpdateNoteFragmentArgs>()
    private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_note, container, false)

        mNotesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        view.dateTime.text = date


        if (args.notesData != null) {
            view.shareButton.visibility = View.VISIBLE
            view.deleteNoteLayout.visibility = View.VISIBLE

            view.noteTitle.setText(args.notesData!!.notesTitle)
            view.noteDescription.setText(args.notesData!!.notesDescription)

            if (args.notesData!!.imagePath != null) {
                view.layout.visibility = View.VISIBLE
                view.imageNote.setImageBitmap(Convertors().toBitmap(args.notesData!!.imagePath!!))
                selectedImagePath = args.notesData!!.imagePath
            }


        }

        view.save_button.setOnClickListener {

            if (args.notesData == null) {
                defaultScope.launch {
                    insertNoteToDatabase()
                }


            } else {
                defaultScope.launch {
                    updateNoteToDatabase()
                }

            }

        }

        view.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        bottomSheet(view)

        view.addimagelayout.setOnClickListener {
            defaultScope.launch {
                requestPermission()
            }

        }

        view.deleteImageButton.setOnClickListener {
            view.imageNote.setImageBitmap(null)
            view.layout.visibility = View.GONE
            selectedImagePath = null
        }

        view.deleteNoteLayout.setOnClickListener {
            createAlertDialog()
        }

        view.shareButton.setOnClickListener {
            shareNoteToOtherApps()

        }

        return view
    }

    private fun shareNoteToOtherApps() {
        val intent1 = Intent()
        intent1.action = Intent.ACTION_SEND
        intent1.putExtra(Intent.EXTRA_TEXT, "Title: ${noteTitle.text.toString()} \n Description: ${noteDescription.text.toString()}")
        intent1.type = "text/plain"

        startActivity(intent1)
    }

    private fun createAlertDialog() {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete")
                .setConfirmText("Delete")
                .setCancelText("Cancel")
                .setConfirmButtonBackgroundColor(Color.parseColor("#ffff4444"))
                .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                    override fun onClick(sweetAlertDialog: SweetAlertDialog?) {
                        defaultScope.launch {
                            deleteNoteFromDataBase()
                            findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
                            sweetAlertDialog!!.dismissWithAnimation()
                        }
                    }

                })
                .setCancelClickListener(object : SweetAlertDialog.OnSweetClickListener {
                    override fun onClick(sweetAlertDialog: SweetAlertDialog?) {
                        sweetAlertDialog!!.dismissWithAnimation()
                    }
                })
                .show()

    }

    private fun insertNoteToDatabase() {
        val id = 0
        val noteTitle = noteTitle.text.toString().trim()
        val noteDescription = noteDescription.text.toString().trim()
        val imagePath = selectedImagePath

        if (inputCheck(noteTitle, noteDescription)) {

            val note = UserNotes(id, noteTitle, noteDescription, date, imagePath)
            mNotesViewModel.addNote(note)

            mainScope.launch {
                Toast.makeText(context, "Successfully Added", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
            }
        } else {
            mainScope.launch {
                Toast.makeText(context, "Enter all details", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateNoteToDatabase() {
        val noteTitle = noteTitle.text.toString().trim()
        val noteDescription = noteDescription.text.toString().trim()
        val imagePath = selectedImagePath

        if (inputCheck(noteTitle, noteDescription)) {

            val note = UserNotes(args.notesData!!.id, noteTitle, noteDescription, date, imagePath)
            mNotesViewModel.updateNote(note)

            mainScope.launch {
                Toast.makeText(context, "Successfully Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
            }

        } else {
            mainScope.launch {
                Toast.makeText(context, "Enter all details", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun deleteNoteFromDataBase() {
        mNotesViewModel.deleteNote(args.notesData!!)

    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteDescription))
    }

    private fun bottomSheet(view: View) {
        val bottomSheet: RelativeLayout = view.bottomLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = 120
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        view.bottomSheetButton.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMISSION)
        } else {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImage: Uri? = data.data
                if (selectedImage != null) {
                    try {
                        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedImage)
                        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                        layout.visibility = View.VISIBLE
                        imageNote.setImageBitmap(bitmap)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        selectedImagePath = Convertors().fromBitmap(bitmap)
                        deleteImageButton.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
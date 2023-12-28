package com.sampath.mordernnotesandtodo.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.data.model.UserNotes
import com.sampath.mordernnotesandtodo.databinding.FragmentAddNoteBinding
import com.sampath.mordernnotesandtodo.databinding.RecordAudioDialogBinding
import com.sampath.mordernnotesandtodo.ui.viewModel.NotesViewModel
import com.sampath.mordernnotesandtodo.utils.Convertors
import com.sampath.mordernnotesandtodo.utils.Utils
import com.sampath.mordernnotesandtodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.util.Objects
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class AddOrUpdateNoteFragment : Fragment(R.layout.fragment_add_note) {

    @Inject @Named("currentDate")
    lateinit var date: String
    private val mNotesViewModel: NotesViewModel by activityViewModels()
    var selectedImagePath: ByteArray? = null
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2
    private val binding by viewBinding(FragmentAddNoteBinding::bind)
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private var noteData: UserNotes? = null
    private val args by navArgs<AddOrUpdateNoteFragmentArgs>()

    @Inject @Named("defaultCoroutineScope")
    lateinit var defaultScope: CoroutineScope

    @Inject @Named("mainCoroutineScope")
    lateinit var mainScope: CoroutineScope

    private var file: File? = null
    private var audioPath: String? = null

    @Inject
    lateinit var mr: MediaRecorder
    private var i = 1
    
    @Inject
    lateinit var handler: Handler
    @Inject
    lateinit var mp: MediaPlayer
    private lateinit var timer : TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.dateTime.text = date
        initClickListeners()

        if (args.noteId != -1) {
            mNotesViewModel.getNote(args.noteId)
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                mNotesViewModel.readNote.collect { noteData ->
                    if(noteData != null) {
                        binding.shareButton.visibility = View.VISIBLE
                        binding.deleteNoteLayout.visibility = View.VISIBLE
                        binding.noteTitle.setText(noteData.notesTitle)
                        binding.noteDescription.setText(noteData.notesDescription)

                        if (noteData.imagePath != null) {
                            binding.layout.visibility = View.VISIBLE
                            binding.imageNote.setImageBitmap(Convertors().toBitmap(noteData.imagePath))
                            selectedImagePath = noteData.imagePath
                        }

                        if (noteData.audio != null) {
                            binding.playAudioNote.visibility = View.VISIBLE
                            binding.addRecordingLayout.visibility = View.GONE
                            audioPath = noteData.audio
                            prepareAudioFile(noteData)
                        }
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.saveButton.setOnClickListener {
            if (noteData == null) {
                defaultScope.launch {
                    insertNoteToDatabase()
                }
            } else {
                defaultScope.launch {
                    updateNoteToDatabase()
                }

            }

        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        initBottomSheet()

        binding.addimagelayout.setOnClickListener {
            defaultScope.launch {
                requestPermissionForImage()
            }

        }

        binding.deleteImageButton.setOnClickListener {
            binding.imageNote.setImageBitmap(null)
            binding.layout.visibility = View.GONE
            selectedImagePath = null
        }

        binding.deleteNoteLayout.setOnClickListener {
            createDeleteNoteAlertDialog()
        }

        binding.addRecordingLayout.setOnClickListener {
            initRecordDialog()
        }

        binding.shareButton.setOnClickListener {
            shareNoteToOtherApps()

        }
    }

    private fun initRecordDialog() {

        val view = requireActivity().layoutInflater.inflate(R.layout.record_audio_dialog, null)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        val dialog = builder.create()
        val binding = RecordAudioDialogBinding.bind(view)

        timer = binding.timer

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.saveButton.isEnabled = false

        binding.recordButton.setOnClickListener {
            requestPermissionForAudio()
            binding.recordButton.visibility = View.GONE
            Toast.makeText(requireContext(), "Recording Started", Toast.LENGTH_SHORT).show()
            binding.stopButton.visibility = View.VISIBLE
            timer.visibility = View.VISIBLE
            countDownTimer.start()
        }

        binding.stopButton.setOnClickListener {
            mr.stop()
            binding.saveButton.isEnabled = true
            binding.stopButton.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.textHintColor
                )
            )
            binding.frameLayout.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.textHintColor
                )
            )
            Toast.makeText(requireContext(), "Recording Stopped", Toast.LENGTH_SHORT).show()
            binding.stopButton.isEnabled = false
            countDownTimer.cancel()
            binding.cancelButton.isEnabled = false
        }

        binding.saveButton.setOnClickListener {
            dialog.dismiss()
            this.binding.playAudioNote.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            prepareAudioFile(noteData!!)
        }

        binding.cancelButton.setOnClickListener {
            mr.stop()
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun shareNoteToOtherApps() {
        val intent1 = Intent()
        intent1.action = Intent.ACTION_SEND
        intent1.putExtra(
            Intent.EXTRA_TEXT,
            "Title: ${binding.noteTitle.text.toString()} \n Description: ${binding.noteDescription.text.toString()}"
        )
        intent1.type = "text/plain"

        startActivity(intent1)
    }

    private val countDownTimer = object : CountDownTimer(360000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val string = Utils.startTimer(360000 - millisUntilFinished)
            timer.text = string
        }

        override fun onFinish() {}
    }

    private val runnableAudio = Runnable { updateProgressBar() }

    private fun prepareAudioFile(noteData: UserNotes) {
        setOutDataForAudioFile(noteData)
        mp.prepare()
        binding.playPauseAudioButton.setOnClickListener {
            if (mp.isPlaying) {
                handler.removeCallbacks(runnableAudio)
                mp.pause()
                binding.playPauseAudioButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_circle_outline_24))
                binding.playPauseAudioButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.titleColor))

            } else {
                mp.start()
                binding.playPauseAudioButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_pause_circle_outline_24))
                binding.playPauseAudioButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.doneButtonColor))
                updateProgressBar()
            }
        }

        binding.deleteAudioButton.setOnClickListener {
            createDeleteAudioAlertDialog()
        }

        mp.setOnCompletionListener {
            mp.stop()
            mp.reset()
            setOutDataForAudioFile(noteData)
            mp.prepare()
            binding.progress.progress = mp.currentPosition
            binding.playPauseAudioButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_circle_outline_24))
            binding.playPauseAudioButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.titleColor))
        }

    }

    private fun createDeleteAudioAlertDialog() {
        val view = requireActivity().layoutInflater.inflate(R.layout.delete_note_alert_dialog, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteTitle = view.findViewById<TextView>(R.id.deleteTitle)
        val deleteDesc = view.findViewById<TextView>(R.id.deleteNoteDesc)
        val delete = view.findViewById<TextView>(R.id.deleteButton)
        val cancel = view.findViewById<TextView>(R.id.cancelButton)
        deleteTitle.text = resources.getText(R.string.deleteAudio)
        deleteDesc.text = resources.getText(R.string.deleteAudioDesc)

        delete.setOnClickListener {
            mp.stop()
            binding.playAudioNote.visibility = View.GONE
            file = null
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setOutDataForAudioFile(noteData: UserNotes) {
        if (noteData.audio != null) {
            mp.setDataSource(noteData.audio)
        } else {
            mp.setDataSource(file!!.absolutePath)
        }

    }

    private fun updateProgressBar() {
        if (mp.isPlaying) {
            binding.progress.progress = ((mp.currentPosition.toFloat() / mp.duration.toFloat()) * 100).toInt()
            handler.postDelayed(runnableAudio, 10)
        }
    }



    private fun requestPermissionForAudio() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) +
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 111
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                recordAudioStart()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun recordAudioStart() {
        file = File(requireContext().filesDir, "Audio${i++}")
        mr.setAudioSource(MediaRecorder.AudioSource.MIC)
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mr.setOutputFile(file)
        mr.prepare()
        mr.start()
    }


     private fun createDeleteNoteAlertDialog() {

             val view = requireActivity().layoutInflater.inflate(R.layout.delete_note_alert_dialog, null)
             val builder = AlertDialog.Builder(activity)
             builder.setView(view)
             builder.setCancelable(false)
             val dialog = builder.create()
             dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

             val delete = view.findViewById<TextView>(R.id.deleteButton)
             val cancel = view.findViewById<TextView>(R.id.cancelButton)
             delete.setOnClickListener {
                 defaultScope.launch {
                     deleteNoteFromDataBase()
                     findNavController().navigateUp()
                     dialog.dismiss()
                 }
             }

             cancel.setOnClickListener {
                 dialog.dismiss()
                 bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
             }

             dialog.show()
    }

    private fun insertNoteToDatabase() {
        val id = 0
        val noteTitle = binding.noteTitle.text.toString().trim()
        val noteDescription = binding.noteDescription.text.toString().trim()
        val imagePath = selectedImagePath
        if(file!=null) { audioPath = file!!.absolutePath }

        if (inputCheck(noteTitle, noteDescription)) {

            val note = UserNotes(id, noteTitle, noteDescription, date, imagePath, audioPath)
            mNotesViewModel.addNote(note)

            mainScope.launch {
                Snackbar.make(binding.saveButton, "Sucessfully Added", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
            }
        } else {
            mainScope.launch {
                Snackbar.make(binding.saveButton, "Enter All Details", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateNoteToDatabase() {
        val noteTitle = binding.noteTitle.text.toString().trim()
        val noteDescription = binding.noteDescription.text.toString().trim()
        val imagePath = selectedImagePath
        if(file!=null) {audioPath = file!!.absolutePath}

        if (inputCheck(noteTitle, noteDescription)) {
            val noteId = noteData?.id!!
            val note = UserNotes(noteId, noteTitle, noteDescription, date, imagePath, audioPath)
            mNotesViewModel.updateNote(note)

            mainScope.launch {
                Snackbar.make(binding.saveButton, "Sucessfully Updated", Snackbar.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
            }

        } else {
            mainScope.launch {
                Snackbar.make(binding.saveButton, "Enter All Details", Snackbar.LENGTH_SHORT).show()
            }

        }
    }


    private fun deleteNoteFromDataBase() {
        noteData?.let { it ->  mNotesViewModel.deleteNote(it)}
    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteDescription))
    }

    private fun initBottomSheet() {
        val bottomSheet: RelativeLayout = binding.bottomLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = 120
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.bottomSheetButton.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }

    private fun requestPermissionForImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMISSION)
        } else {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            requireActivity().startActivityFromFragment(this, intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        selectImage()
                    }
                }
            }

            111 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        recordAudioStart()
                    }
                }
            }

            else -> {
                Toast.makeText(context, "Permission Deinied", Toast.LENGTH_SHORT).show()
            }
        }
        /*if ((requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.size > 0) || (requestCode == 111 && grantResults.size>0)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImage: Uri? = data.data
                Log.d("Sampath", selectedImage.toString())
                if (selectedImage != null) {
                    try {

                        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedImage)
                        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.layout.visibility = View.VISIBLE
                        Log.d("Sampath", bitmap.toString())
                        binding.imageNote.setImageBitmap(bitmap)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        selectedImagePath = Convertors().fromBitmap(bitmap)
                        binding.deleteImageButton.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(Objects.nonNull(mp)) mp.stop()
    }

}
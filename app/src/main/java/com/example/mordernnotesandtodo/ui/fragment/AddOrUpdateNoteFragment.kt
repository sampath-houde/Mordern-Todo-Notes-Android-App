package com.example.mordernnotesandtodo.fragment.Notes.AddOrUpdateNoteFragment

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mordernnotesandtodo.R
import com.example.mordernnotesandtodo.databinding.FragmentAddNoteBinding
import com.example.mordernnotesandtodo.databinding.RecordAudioDialogBinding
import com.example.mordernnotesandtodo.model.UserNotes
import com.example.mordernnotesandtodo.viewModel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.File
import java.io.InputStream


@Suppress("DEPRECATION")
class AddOrUpdateNoteFragment : Fragment() {

    private val date = FormatDate().date
    private lateinit var mNotesViewModel: NotesViewModel
    var selectedImagePath: ByteArray? = null
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2
    private var binding: FragmentAddNoteBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private val args by navArgs<AddOrUpdateNoteFragmentArgs>()
    private val defaultScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var file: File? = null
    private lateinit var mr: MediaRecorder
    private var i = 1
    private val handler = Handler()
    private var mp = MediaPlayer()
    private lateinit var timer : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentAddNoteBinding.bind(view)

        mr = MediaRecorder()
        mNotesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        binding!!.dateTime.text = date



        if (args.notesData != null) {
            binding!!.shareButton.visibility = View.VISIBLE
            binding!!.deleteNoteLayout.visibility = View.VISIBLE
            binding!!.noteTitle.setText(args.notesData!!.notesTitle)
            binding!!.noteDescription.setText(args.notesData!!.notesDescription)



            if (args.notesData!!.imagePath != null) {
                binding!!.layout.visibility = View.VISIBLE
                binding!!.imageNote.setImageBitmap(Convertors().toBitmap(args.notesData!!.imagePath!!))
                selectedImagePath = args.notesData!!.imagePath
            }

            if (args.notesData!!.audio != null) {
                binding!!.playAudioNote.visibility = View.VISIBLE
                binding!!.addRecordingLayout.visibility = View.GONE
                prepareAudioFile()
            }


        }



        binding!!.saveButton.setOnClickListener {

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

        binding!!.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        bottomSheet()

        binding!!.addimagelayout.setOnClickListener {
            defaultScope.launch {
                requestPermissionForImage()
            }

        }

        binding!!.deleteImageButton.setOnClickListener {
            binding!!.imageNote.setImageBitmap(null)
            binding!!.layout.visibility = View.GONE
            selectedImagePath = null
        }

        binding!!.deleteNoteLayout.setOnClickListener {
            createDeleteNoteAlertDialog()
        }

        binding!!.addRecordingLayout.setOnClickListener {
            createRecordDialog()
        }

        binding!!.shareButton.setOnClickListener {
            shareNoteToOtherApps()

        }
    }


    private fun shareNoteToOtherApps() {
        val intent1 = Intent()
        intent1.action = Intent.ACTION_SEND
        intent1.putExtra(
            Intent.EXTRA_TEXT,
            "Title: ${binding!!.noteTitle.text.toString()} \n Description: ${binding!!.noteDescription.text.toString()}"
        )
        intent1.type = "text/plain"

        startActivity(intent1)
    }

    private fun createRecordDialog() {
        val view = requireActivity().layoutInflater.inflate(R.layout.record_audio_dialog, null)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        val dialog = builder.create()
        val dialogBinding = RecordAudioDialogBinding.bind(view)
        val stop = dialogBinding.stopButton
        val record = dialogBinding.recordButton
        val ring = dialogBinding.frameLayout
        val saveAudioButton = dialogBinding.saveButton
        val cancelAudioButton = dialogBinding.cancelButton
        timer = dialogBinding.timer

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        saveAudioButton.isEnabled = false

        record.setOnClickListener {
            requestPermissionForAudio()
            record.visibility = View.GONE
            Toast.makeText(view.context, "Recording Started", Toast.LENGTH_SHORT).show()
            stop.visibility = View.VISIBLE
            timer.visibility = View.VISIBLE
            countDownTimer.start()
        }

        stop.setOnClickListener {
            mr.stop()
            saveAudioButton.isEnabled = true
            stop.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.textHintColor
                )
            )
            ring.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.textHintColor
                )
            )
            Toast.makeText(view.context, "Recording Stopped", Toast.LENGTH_SHORT).show()
            stop.isEnabled = false
            countDownTimer.cancel()
            cancelAudioButton.isEnabled = false
        }

        saveAudioButton.setOnClickListener {
            dialog.dismiss()
            binding!!.playAudioNote.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            prepareAudioFile()
        }

        cancelAudioButton.setOnClickListener {
            mr.stop()
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()


    }

    private fun startTimer(milliseconds: Long) : String {
        var timerString : String = ""
        var secondsString : String = ""

        val hours : Int = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes: Int = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
        val seconds: Int = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()

        if(hours > 0) {
            timerString = "$hours:"
        }

        if(seconds < 10) {
            secondsString = "0${seconds}"
        } else {
            secondsString = "$seconds"
        }

        timerString = "${timerString} ${minutes} : ${secondsString}"
        return timerString
    }

    val countDownTimer = object : CountDownTimer(360000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val string = startTimer(360000 - millisUntilFinished)
            timer.setText(string)
        }

        override fun onFinish() {

        }

    }

    val runnableAudio = object : Runnable {
        override fun run() {
            updateProgressBar()
        }

    }

    private fun prepareAudioFile() {
        setOutDataForAudioFile()
        mp.prepare()
        binding!!.playPauseAudioButton.setOnClickListener {
            if (mp.isPlaying) {
                handler.removeCallbacks(runnableAudio)
                mp.pause()
                binding!!.playPauseAudioButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_circle_outline_24))
                binding!!.playPauseAudioButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            } else {
                mp.start()
                binding!!.playPauseAudioButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_pause_circle_outline_24))
                binding!!.playPauseAudioButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.doneButtonColor))
                updateProgressBar()
            }
        }

        binding!!.deleteAudioButton.setOnClickListener {
            createDeleteAudioAlertDialog()
        }

        mp.setOnCompletionListener {
            mp.stop()
            mp.reset()
            setOutDataForAudioFile()
            mp.prepare()
            binding!!.progress.setProgress(mp.currentPosition)
            binding!!.playPauseAudioButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_circle_outline_24))
            binding!!.playPauseAudioButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
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
        deleteTitle.setText(resources.getText(R.string.deleteAudio))
        deleteDesc.setText(resources.getText(R.string.deleteAudioDesc))

        delete.setOnClickListener {
            mp.stop()
            binding!!.playAudioNote.visibility = View.GONE
            file = null
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setOutDataForAudioFile() {
        if (args.notesData?.audio != null) {
            mp.setDataSource(args.notesData!!.audio)
        } else {
            mp.setDataSource(file!!.absolutePath)
        }

    }

    private fun updateProgressBar() {
        if (mp.isPlaying) {
            binding!!.progress.setProgress(((mp.currentPosition.toFloat() / mp.duration.toFloat()) * 100).toInt())
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
        val noteTitle = binding!!.noteTitle.text.toString().trim()
        val noteDescription = binding!!.noteDescription.text.toString().trim()
        val imagePath = selectedImagePath
        var audioPath: String? = null
        if(file!=null) { audioPath = file!!.absolutePath }

        if (inputCheck(noteTitle, noteDescription)) {

            val note = UserNotes(id, noteTitle, noteDescription, date, imagePath, audioPath)
            mNotesViewModel.addNote(note)

            mainScope.launch {
                Snackbar.make(binding!!.saveButton, "Sucessfully Added", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
            }
        } else {
            mainScope.launch {
                Snackbar.make(binding!!.saveButton, "Enter All Details", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateNoteToDatabase() {
        val noteTitle = binding!!.noteTitle.text.toString().trim()
        val noteDescription = binding!!.noteDescription.text.toString().trim()
        val imagePath = selectedImagePath
        var audioPath: String? = null
        if(file!=null) {audioPath = file!!.absolutePath}

        if (inputCheck(noteTitle, noteDescription)) {

            val note = UserNotes(args.notesData!!.id, noteTitle, noteDescription, date, imagePath, audioPath)
            mNotesViewModel.updateNote(note)

            mainScope.launch {
                Snackbar.make(binding!!.saveButton, "Sucessfully Updated", Snackbar.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_addNewNoteFragment_to_viewPagerFragment)
            }

        } else {
            mainScope.launch {
                Snackbar.make(binding!!.saveButton, "Enter All Details", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun deleteNoteFromDataBase() {
        mNotesViewModel.deleteNote(args.notesData!!)

    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteDescription))
    }

    private fun bottomSheet() {
        val bottomSheet: RelativeLayout = binding!!.bottomLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.peekHeight = 120
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding!!.bottomSheetButton.setOnClickListener {
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
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
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
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        selectImage()
                    }
                }
            }

            111 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
                if (selectedImage != null) {
                    try {
                        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedImage)
                        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                        binding!!.layout.visibility = View.VISIBLE
                        binding!!.imageNote.setImageBitmap(bitmap)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        selectedImagePath = Convertors().fromBitmap(bitmap)
                        binding!!.deleteImageButton.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
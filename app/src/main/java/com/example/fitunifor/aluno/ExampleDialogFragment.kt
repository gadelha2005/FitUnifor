package com.example.fitunifor.aluno

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import com.example.fitunifor.R
import com.google.firebase.storage.FirebaseStorage

class ExampleDialogFragment : DialogFragment() {

    private lateinit var firebaseStorage: FirebaseStorage
    private var exercicioNome: String? = null
    private var videoFileName: String? = null

    companion object {
        private const val ARG_EXERCICIO_NOME = "arg_exercicio_nome"
        private const val ARG_VIDEO_FILE_NAME = "arg_video_file_name"

        fun newInstance(exercicioNome: String, videoFileName: String) =
            ExampleDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EXERCICIO_NOME, exercicioNome)
                    putString(ARG_VIDEO_FILE_NAME, videoFileName)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseStorage = FirebaseStorage.getInstance()
        exercicioNome = arguments?.getString(ARG_EXERCICIO_NOME)
        videoFileName = arguments?.getString(ARG_VIDEO_FILE_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_example_dialog, container, false)

        val backIcon = view.findViewById<ImageView>(R.id.iconBack)
        val videoView = view.findViewById<VideoView>(R.id.dialogVideoView2)
        val titleView = view.findViewById<TextView>(R.id.dialogTitle1)

        titleView.text = exercicioNome ?: "Exercício"

        videoFileName?.let { original ->
            // garante extensão .mp4
            val fileName = if (original.lowercase().endsWith(".mp4")) original else "$original.mp4"

            // -------------  ALTERAÇÃO AQUI  -------------
            // não usamos mais “videos/”; o arquivo está na raiz
            val videoRef = firebaseStorage.reference.child(fileName)
            // ---------------------------------------------

            videoRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    videoView.setVideoURI(uri)
                    val mediaController = MediaController(requireContext()).apply {
                        setAnchorView(videoView)
                        setMediaPlayer(videoView)
                    }
                    videoView.setMediaController(mediaController)
                    videoView.setOnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.start()
                    }
                }
                .addOnFailureListener { e ->
                    toast("Falha ao carregar vídeo: ${e.message}")
                    dismiss()
                }
        } ?: toast("Arquivo do vídeo não informado")

        backIcon.setOnClickListener {
            videoView.stopPlayback()
            dismiss()
        }
        return view
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

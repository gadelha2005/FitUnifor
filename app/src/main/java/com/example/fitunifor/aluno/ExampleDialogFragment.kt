import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import android.widget.MediaController
import androidx.fragment.app.DialogFragment
import com.example.fitunifor.R

class ExampleDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_example_dialog, container, false)

        val backIcon = view.findViewById<ImageView>(R.id.iconBack)
        val videoView = view.findViewById<VideoView>(R.id.dialogVideoView2)

        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.supino_inclinado_video}")
        videoView.setVideoURI(videoUri)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setOnPreparedListener { it.isLooping = true }
        videoView.start()

        backIcon.setOnClickListener {
            videoView.stopPlayback()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

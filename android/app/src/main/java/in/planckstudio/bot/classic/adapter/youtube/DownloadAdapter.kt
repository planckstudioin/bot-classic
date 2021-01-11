package `in`.planckstudio.bot.classic.adapter.youtube

import `in`.planckstudio.bot.classic.R
import `in`.planckstudio.bot.classic.model.youtube.DownloadType
import `in`.planckstudio.bot.classic.ui.youtube.YoutubeDownloadActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class DownloadAdapter(
    private val dataSet: ArrayList<DownloadType>,
    private val clickListner: YoutubeDownloadActivity
) :
    RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: AppCompatTextView = view.findViewById(R.id.recent_title)
        val captionText: AppCompatTextView = view.findViewById(R.id.recent_caption)
        private val downloadBtn: MaterialButton = view.findViewById(R.id.yt_save_video_btn)
        val downloadImageType: ImageView = view.findViewById(R.id.yt_image_type)

        fun initialize(item: DownloadType, action: OnDownloadButtonClickListner) {
            downloadBtn.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_youtube_download, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.titleText.text = dataSet[position].getDownloadType()

        val vType = dataSet[position].getDownloadType()

        if (vType == "mp4" || vType == "webm") {
            viewHolder.downloadImageType.setImageResource(R.drawable.ic_baseline_videocam_24)
        } else if (vType == "m4a" || vType == "mp3") {
            viewHolder.downloadImageType.setImageResource(R.drawable.ic_baseline_audiotrack_24)
        } else {
            viewHolder.downloadImageType.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
        }

        viewHolder.captionText.text = vType
        viewHolder.titleText.text = dataSet[position].getDownloadSize()

        viewHolder.initialize(dataSet[position], clickListner)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

interface OnDownloadButtonClickListner {
    fun onItemClick(item: DownloadType, position: Int)
}
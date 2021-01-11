package `in`.planckstudio.bot.classic.ui.youtube

import `in`.planckstudio.bot.classic.R
import `in`.planckstudio.bot.classic.adapter.youtube.DownloadAdapter
import `in`.planckstudio.bot.classic.adapter.youtube.OnDownloadButtonClickListner
import `in`.planckstudio.bot.classic.helper.MuxHelper
import `in`.planckstudio.bot.classic.model.youtube.DownloadType
import `in`.planckstudio.bot.classic.util.LocalStorage
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class YoutubeDownloadActivity : AppCompatActivity(), OnDownloadButtonClickListner {

    private lateinit var mFileUri: Uri
    private lateinit var ls: LocalStorage

    var msg: String? = ""
    var lastMsg = ""

    private lateinit var mProgressBar: ProgressBar
    private lateinit var mInputUrl: TextInputEditText
    private lateinit var mActionBtn: MaterialButton
    private lateinit var mInputBox: MaterialCardView
    private lateinit var mProfileImg: NetworkImageView
    private lateinit var mImageUrl: String
    private lateinit var appUrl: String
    private lateinit var appToken: String
    private lateinit var appKey: String
    private lateinit var appRequest: String

    private lateinit var mRequestQueue: RequestQueue

    lateinit var mUrl: String

    private lateinit var mDownloadRecyclerView: RecyclerView

    private val mDownloadList = java.util.ArrayList<DownloadType>()
    private val downloadAdapter = DownloadAdapter(mDownloadList, this)
    private lateinit var mDownloadItem: DownloadType

    lateinit var mAudioUrl: String
    lateinit var mVideoTitle: String
    lateinit var mVideoId: String
    lateinit var mVideoType: String

    val handler = Handler()

    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_download)

        this.ls = LocalStorage(this)
        this.mFileUri = Uri.parse("")

        this.appUrl = getResources().getString(R.string.yt_dl_api_endpoint);
        this.appToken = ""
        this.appKey = ""
        this.appRequest = "igprofile"

        this.mRequestQueue = Volley.newRequestQueue(this)

        this.mProgressBar = findViewById(R.id.yt_dl_progressbar)
        this.mInputUrl = findViewById(R.id.input_yt_url)
        this.mActionBtn = findViewById(R.id.media_btn)
        this.mInputBox = findViewById(R.id.yt_input_box)
        this.mProfileImg = findViewById(R.id.imageholder)
        this.mDownloadRecyclerView = findViewById(R.id.yt_video_list)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        this.mDownloadRecyclerView.layoutManager = mLayoutManager

        this.mDownloadRecyclerView.adapter = downloadAdapter

        this.mActionBtn.setOnClickListener {
            this.mUrl = this.mInputUrl.text.toString()

            if (this.mUrl.contains("https://www.youtube.com/watch?v=")) {
                this.mUrl.replace("https://www.youtube.com/watch?v=", "https://youtu.be/")
            }

            if (this.mUrl.isEmpty() || !this.mUrl.contains("https://youtu.be/")) {
                this.mInputUrl.text?.clear()
                Toast.makeText(this, "Enter youtube video url", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fetching video data", Toast.LENGTH_SHORT).show()
                fetchVideoData()
                downloadAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchVideoData() {

        val stringRequest = object : StringRequest(
            Method.POST,
            this.appUrl,
            Response.Listener<String> {

                val jsonObject = JSONObject(it)
                val rCode = jsonObject.getString("code")
                val msg = jsonObject.getString("message")
                val res = jsonObject.getJSONObject("result")
                if (rCode == "200") {
                    Toast.makeText(this, "Data fetched", Toast.LENGTH_SHORT).show()
                    val rImageUrl = res.getString("thumbnail")
                    val fSplit = rImageUrl.split("/")
                    val fVideoCode = fSplit[4]
                    this.mVideoId = fVideoCode
                    addSearchHistory(fVideoCode, rImageUrl)
                    var videoTitle = res.getString("title").toString()
                    videoTitle.replace("|", "-")
                    videoTitle.replace(",", " ")
                    this.mVideoTitle = videoTitle
                    this.mImageUrl = rImageUrl

                    val links = res.getJSONArray("links")

                    if (links.length() != 0) {
                        for (i in 0 until links.length()) {
                            val linkItem = links.getJSONObject(i)
                            val mytype = linkItem.getString("type")
                            val ismute = linkItem.getBoolean("mute")

                            if (mytype == "m4a") {
                                this.mAudioUrl = linkItem.getString("url")
                            }

                            if (mytype != "webm") {
                                this.mDownloadItem = DownloadType(
                                    linkItem.getInt("itag"),
                                    linkItem.getString("type"),
                                    linkItem.getString("quality"),
                                    linkItem.getString("url"),
                                    linkItem.getBoolean("mute")
                                )
                                this.mDownloadList.add(this.mDownloadItem)
                                downloadAdapter.notifyDataSetChanged()
                            }
                        }

                        val mImageLoader = ImageLoader(
                            mRequestQueue,
                            object : ImageLoader.ImageCache {
                                override fun putBitmap(key: String?, value: Bitmap?) {}
                                override fun getBitmap(key: String?): Bitmap? {
                                    return null
                                }
                            })

                        mProfileImg.setImageUrl(rImageUrl, mImageLoader)
                        this.mInputBox.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Something goes wrong", Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val stringMap = HashMap<String, String>()
                stringMap["url"] = mUrl
                return stringMap
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(15000, 3, 1F)
        stringRequest.setShouldCache(false)
        mRequestQueue.add(stringRequest).retryPolicy = retryPolicy
    }

    private fun addSearchHistory(key: String, value: String) {
        val stringRequest = object : StringRequest(
            Method.POST,
            "https://bot.planckstudio.in/api/app/v1/searchhistory.php",
            Response.Listener<String> {
                val jsonObject = JSONObject(it)
                Log.e("BOT", ls.getValueString("device_uid"))
            },
            Response.ErrorListener {
                Toast.makeText(this, "Something goes wrong", Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val stringMap = HashMap<String, String>()
                stringMap["request"] = "add"
                stringMap["search_uid"] = ls.getValueString("device_uid")
                stringMap["search_type"] = "yt_dl"
                stringMap["search_key"] = key
                stringMap["search_value"] = value
                return stringMap
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(15000, 3, 1F)
        stringRequest.setShouldCache(false)
        mRequestQueue.add(stringRequest).retryPolicy = retryPolicy
    }

    private fun downloadYoutube(item: DownloadType) {

        var myType = ""

        if (!item.getDownloadMute()) {
            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show()
            commonDownload(item.getDownloadUrl(), item.getDownloadType())
        } else {
            var vTitle = mVideoTitle
            vTitle.replace('|', '-')
            vTitle.replace(',', ' ')
            mProgressBar.visibility = View.VISIBLE
            val mh =
                MuxHelper(this, vTitle, item.getDownloadSize(), mAudioUrl, item.getDownloadUrl())
            mh.joinVideo()
            mProgressBar.visibility = View.GONE
        }
    }

    private fun commonDownload(url: String, type: String, mux: Boolean = false): String {

        var title = ""
        var dest = ""
        val saveDir = File(Environment.DIRECTORY_DOWNLOADS)

        if (!(saveDir.exists())) {
            saveDir.mkdirs()
        }

        val timestamp = Date().time.toString()

        if (mux) {
            title = ("$mVideoId.$type")
        } else {
            title = ("$mVideoTitle.$type")
        }

        dest = saveDir.toString()

        val downloadManager: DownloadManager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(mVideoTitle)
                .setDescription("Using PlanckBot")
                .setDestinationInExternalPublicDir(
                    dest,
                    title
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    this.mFileUri = downloadManager.getUriForDownloadedFile(downloadId)
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                msg = statusMessage(status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                            this.mProgressBar.visibility = View.INVISIBLE
                        } else {
                            this.mProgressBar.visibility = View.VISIBLE
                        }

                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }.start()

        return dest.toString()
    }

    fun statusMessage(status: Int): String {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download failed"
            DownloadManager.STATUS_SUCCESSFUL -> "Saved in Download folder"
            else -> "Processing"
        }
        return msg
    }

    override fun onItemClick(item: DownloadType, position: Int) {
        downloadYoutube(item)
    }
}
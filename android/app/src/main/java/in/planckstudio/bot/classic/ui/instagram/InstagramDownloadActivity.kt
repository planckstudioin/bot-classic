package `in`.planckstudio.bot.classic.ui.instagram

import `in`.planckstudio.bot.classic.R
import `in`.planckstudio.bot.classic.util.LocalStorage
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class InstagramDownloadActivity : AppCompatActivity() {

    private lateinit var mMediaType: String
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mInputUrl: TextInputEditText
    private lateinit var mActionBtn: MaterialButton
    private lateinit var mSaveBtn: MaterialButton
    private lateinit var mProfileBox: MaterialCardView
    private lateinit var mProfileImg: NetworkImageView
    private lateinit var mImageUrl: String
    private lateinit var mSidecar: ArrayList<String>

    private lateinit var appUrl: String
    private lateinit var appUrlAll: String

    private lateinit var mUrl: String
    private lateinit var mRequestQueue: RequestQueue
    lateinit var mPostUrl: String
    lateinit var mDownloadType: String

    private lateinit var mJsonArray: JSONArray
    private lateinit var mSidecarType: JSONArray
    private lateinit var mType: String
    private lateinit var mshortcode: String
    private lateinit var mUsername: String

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var clipData: ClipData
    private lateinit var mCaption: String

    var msg: String? = ""
    var lastMsg = ""

    private lateinit var ls: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instagram_download)

        this.ls = LocalStorage(this)

        this.appUrl = resources.getString(R.string.ig_dl_single_api_endpoint)
        this.appUrlAll = resources.getString(R.string.ig_dl_all_api_endpoint)

        this.mRequestQueue = Volley.newRequestQueue(this)

        this.mProgressBar = findViewById(R.id.ig_dl_progressbar)
        this.mInputUrl = findViewById(R.id.input_url)
        this.mActionBtn = findViewById(R.id.media_btn)
        this.mSaveBtn = findViewById(R.id.save_post_btn)
        this.mProfileBox = findViewById(R.id.media_box)
        this.mProfileImg = findViewById(R.id.imageholder)
        this.mMediaType = ""

        this.mActionBtn.setOnClickListener {
            this.mPostUrl = this.mInputUrl.text.toString()
            if (this.mPostUrl.isEmpty()) {
                Toast.makeText(this, "Enter url/username", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fetching data", Toast.LENGTH_SHORT).show()
                if (mPostUrl.contains("https://")) {
                    val fSplit = mPostUrl.split("?")
                    val fUrl = fSplit[0]
                    fetchPost(fUrl)
                } else {
                    fetchAllPost(this.mPostUrl)
                }
            }
        }

        this.mSaveBtn.setOnClickListener {

            mType = this.mMediaType

            if (mDownloadType == "all") {
                val l = this.mJsonArray.length()
                showConfirmDownloadDialog(l + 1)
            } else {
                if (mType == "GraphSidecar") {
                    val l = this.mJsonArray.length()
                    var i = 0
                    while (i < l) {
                        downloadMedia(
                            this.mJsonArray.getString(i),
                            this.mSidecarType.getString(i),
                            mshortcode,
                            mUsername
                        )
                        i++
                    }
                } else {
                    downloadMedia(mImageUrl, mType, mshortcode, mUsername)
                }
            }
        }
    }

    private fun fetchAllPost(username: String) {
        val stringRequest = object : StringRequest(
            Method.POST,
            appUrlAll,
            Response.Listener<String> {
                val jsonObject = JSONObject(it)
                val status = jsonObject.getString("status")

                if (status == "success") {

                    val rImageUrl = jsonObject.getString("dp")

                    val mImageLoader = ImageLoader(mRequestQueue, object : ImageLoader.ImageCache {
                        override fun putBitmap(key: String?, value: Bitmap?) {}
                        override fun getBitmap(key: String?): Bitmap? {
                            return null
                        }
                    })

                    mProfileImg.setImageUrl(rImageUrl, mImageLoader)
                    this.mProfileBox.visibility = View.GONE
                    this.mSaveBtn.visibility = View.VISIBLE

                    mJsonArray = jsonObject.getJSONArray("media")

                    this.mImageUrl = rImageUrl
                    this.mDownloadType = "all"
                }

            },
            Response.ErrorListener {
                //
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val stringMap = HashMap<String, String>()
                stringMap["username"] = username
                return stringMap
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(15000, 3, 1F)
        stringRequest.setShouldCache(false)
        mRequestQueue.add(stringRequest).retryPolicy = retryPolicy
    }

    private fun fetchPost(postUrl: String) {
        val stringRequest = object : StringRequest(Method.POST, appUrl, Response.Listener<String> {
            val jsonObject = JSONObject(it)
            val rCode = jsonObject.getString("code")
            val rMessage = jsonObject.getString("message")

            if (rCode == "200") {
                mType = jsonObject.getString("type")
                Toast.makeText(this, "Image fetched", Toast.LENGTH_SHORT).show()
                val rDisplayUrl = jsonObject.getString("display_url")
                val rImageUrl = jsonObject.getString("media_url")
                this.mshortcode = jsonObject.getString("shortcode")
                this.mUsername = jsonObject.getString("username")
                this.mCaption = jsonObject.getString("caption")
                this.clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                this.clipData = ClipData.newPlainText("caption", this.mCaption)
                this.clipboardManager.setPrimaryClip(clipData)
                this.mMediaType = mType
                this.mDownloadType = "single"

                Toast.makeText(this, "Caption copied", Toast.LENGTH_LONG).show()

                val mImageLoader = ImageLoader(mRequestQueue, object : ImageLoader.ImageCache {
                    override fun putBitmap(key: String?, value: Bitmap?) {}
                    override fun getBitmap(key: String?): Bitmap? {
                        return null
                    }
                })

                mProfileImg.setImageUrl(rDisplayUrl, mImageLoader)
                this.mProfileBox.visibility = View.GONE
                this.mSaveBtn.visibility = View.VISIBLE

                this.mImageUrl = rImageUrl

                if (mType == "GraphSidecar") {
                    mJsonArray = jsonObject.getJSONArray("sidecar")
                    mSidecarType = jsonObject.getJSONArray("sidecar_type")
                }
            } else {
                Toast.makeText(this, rMessage, Toast.LENGTH_SHORT).show()
            }

        }, Response.ErrorListener {
            Toast.makeText(this, "Something goes wrong", Toast.LENGTH_SHORT).show()
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val stringMap = HashMap<String, String>()
                stringMap["url"] = postUrl
                return stringMap
            }
        }
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(15000, 3, 1F)
        stringRequest.setShouldCache(false)
        mRequestQueue.add(stringRequest).retryPolicy = retryPolicy
    }

    private fun downloadMedia(
        mediaUrl: String,
        mediaType: String,
        mediaShortcode: String,
        mediaUsername: String
    ) {
        val rootDir = File(Environment.DIRECTORY_DOWNLOADS)
        var myType = ""

        if (mediaType == "GraphImage") {
            myType = ".jpg"
        } else if (mediaType == "GraphVideo") {
            myType = ".mp4"
        }

        val myDir = File("$rootDir")

        if (!(myDir.exists())) {
            myDir.mkdirs()
        }

        val timestamp = Date().time.toString()

        val title = (mediaUsername + "__" + mediaShortcode + "__" + timestamp + myType)

        val downloadManager: DownloadManager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = Uri.parse(mediaUrl)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(mediaShortcode)
                .setDescription("Using PlanckBot")
                .setDestinationInExternalPublicDir(
                    myDir.toString(),
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
    }

    private fun downloadAllMedia(
        username: String,
        media: JSONObject,
        sidecar: Boolean,
        code: String,
        no: Int = 0
    ) {
        val rootDir = File(Environment.DIRECTORY_DOWNLOADS)
        var myType = ""
        var shortcode = ""
        var nos = ""

        val hasSidecar = media.getBoolean("hasSidecar")

        nos = if (no == 0) {
            ""
        } else {
            no.toString()
        }

        if (hasSidecar) {
            val vsidecar = media.getJSONArray("sidecar")
            val l = vsidecar.length()
            var i = 0
            while (i < l) {
                var count = i + 1
                downloadAllMedia(username, vsidecar.getJSONObject(i), true, code, (i + 1))
                i++
            }
        } else {
            when (media.getString("type")) {
                "GraphImage" -> {
                    myType = ".jpg"
                }
                "GraphVideo" -> {
                    myType = ".mp4"
                }
            }

            val myDir = File("$rootDir")

            if (!(myDir.exists())) {
                myDir.mkdirs()
            }

            val timestamp = Date().time.toString()

            val title = (username + "__" + timestamp + "__" + code + "__" + nos + myType)

            val downloadManager: DownloadManager =
                this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(media.getString("src"))

            val request = DownloadManager.Request(downloadUri).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(code)
                    .setDescription("Using PlanckBot")
                    .setDestinationInExternalPublicDir(
                        myDir.toString(),
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
                            //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                        lastMsg = msg ?: ""
                    }
                    cursor.close()
                }
            }.start()
        }
    }


    private fun statusMessage(status: Int): String {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download failed"
            DownloadManager.STATUS_SUCCESSFUL -> "Saved in Download folder"
            else -> "Processing"
        }
        return msg
    }

    private fun showConfirmDownloadDialog(total: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm download all media")
        builder.setMessage("We found $total posts")
        builder.setPositiveButton("DOWNLOAD") { dialogInterface, which ->
            startDownloadAllMedia()
        }
        builder.setNegativeButton("CANCEL") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun startDownloadAllMedia() {
        val l = mJsonArray.length()
        var i = 0
        while (i < l) {
            val mycode = this.mJsonArray.getJSONObject(i).getString("code")
            downloadAllMedia(this.mPostUrl, this.mJsonArray.getJSONObject(i), false, mycode)
            i++
        }
    }
}
package `in`.planckstudio.bot.classic.ui.instagram

import `in`.planckstudio.bot.classic.R
import `in`.planckstudio.bot.classic.util.LocalStorage
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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


class ProfilePictureActivity : AppCompatActivity() {

    private lateinit var mProgressBar: ProgressBar
    private lateinit var mInputUsername: TextInputEditText
    private lateinit var mActionBtn: MaterialButton
    private lateinit var mSaveBtn: MaterialButton
    private lateinit var mProfileBox: MaterialCardView
    private lateinit var mProfileImg: NetworkImageView
    private lateinit var mImageUrl: String
    private lateinit var appUrl: String

    private lateinit var mRequestQueue: RequestQueue

    lateinit var mUsername: String

    var msg: String? = ""
    var lastMsg = ""

    private lateinit var ls: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

        this.ls = LocalStorage(this)

        this.appUrl = getResources().getString(R.string.ig_dp_api_endpoint);

        this.mRequestQueue = Volley.newRequestQueue(this)

        this.mProgressBar = findViewById(R.id.ig_dp_progressbar)
        this.mInputUsername = findViewById(R.id.input_ig_username)
        this.mActionBtn = findViewById(R.id.profile_btn)
        this.mSaveBtn = findViewById(R.id.save_ig_profile_btn)
        this.mProfileBox = findViewById(R.id.ig_profile_box)
        this.mProfileImg = findViewById(R.id.imageholder)

        this.mActionBtn.setOnClickListener {
            this.mUsername = this.mInputUsername.text.toString()

            if (this.mUsername.isEmpty()) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fetching Profile Picture", Toast.LENGTH_SHORT).show()
                fetchProfile()
            }
        }

        this.mSaveBtn.setOnClickListener {
            this.mProgressBar.visibility = View.INVISIBLE
            downloadMedia(mImageUrl, mUsername)
        }

    }

    private fun fetchProfile() {

        val stringRequest = object : StringRequest(
            Method.POST,
            this.appUrl,
            Response.Listener<String> {

                val jsonObject = JSONObject(it)
                val rCode = jsonObject.getString("code")
                val msg = jsonObject.getString("message")
                if (rCode == "200") {

                    Toast.makeText(this, "Profile fetched", Toast.LENGTH_SHORT).show()
                    val rImageUrl = jsonObject.getString("media_url")
                    this.mImageUrl = rImageUrl

                    val mImageLoader = ImageLoader(mRequestQueue, object : ImageLoader.ImageCache {
                        override fun putBitmap(key: String?, value: Bitmap?) {}
                        override fun getBitmap(key: String?): Bitmap? {
                            return null
                        }
                    })
                    mProfileImg.setImageUrl(rImageUrl, mImageLoader)
                    this.mProfileBox.visibility = View.GONE
                    this.mSaveBtn.visibility = View.VISIBLE
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
                stringMap["username"] = mUsername
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

    private fun downloadMedia(
        mediaUrl: String,
        mediaUsername: String
    ) {
        val rootDir = File(Environment.DIRECTORY_DOWNLOADS)
        val myType: String = ".jpg"
        val timestamp = Date().time.toString()

        val myDir = File("$rootDir")

        if (!(myDir.exists())) {
            myDir.mkdirs()
        }

        val downloadManager: DownloadManager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = Uri.parse(mediaUrl)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("$mediaUsername-dp")
                .setDescription("Using PlanckBot")
                .setDestinationInExternalPublicDir(
                    myDir.toString(),
                    mediaUsername + "__" + timestamp + myType
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(mediaUrl, myDir, status)
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
        }).start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download failed"
            DownloadManager.STATUS_SUCCESSFUL -> "Saved in Download folder"
            else -> "Processing"
        }
        return msg
    }
}
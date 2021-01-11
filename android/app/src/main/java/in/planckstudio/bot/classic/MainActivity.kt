package `in`.planckstudio.bot.classic

import `in`.planckstudio.bot.classic.ui.instagram.InstagramDownloadActivity
import `in`.planckstudio.bot.classic.ui.instagram.ProfilePictureActivity
import `in`.planckstudio.bot.classic.ui.youtube.YoutubeDownloadActivity
import `in`.planckstudio.bot.classic.util.LocalStorage
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var mContactusBtn: MaterialCardView
    private lateinit var mOpenIgDpBtn: MaterialCardView
    private lateinit var mOpenIgDlBtn: MaterialCardView
    private lateinit var mOpenYtDlBtn: MaterialCardView
    private lateinit var ls: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.mContactusBtn = findViewById(R.id.contactus_btn)
        this.mOpenIgDpBtn = findViewById(R.id.ig_items_dp)
        this.mOpenIgDlBtn = findViewById(R.id.ig_items_dl)
        this.mOpenYtDlBtn = findViewById(R.id.yt_items_dl)

        this.ls = LocalStorage(this)

        this.mContactusBtn.setOnClickListener {
            val uri: Uri = Uri.parse("https://instagram.com/plancksupport")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        }

        this.mOpenIgDpBtn.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(this, ProfilePictureActivity::class.java)
                startActivity(intent)
            } else {
                requestPermission()
            }
        }

        this.mOpenIgDlBtn.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(this, InstagramDownloadActivity::class.java)
                startActivity(intent)
            } else {
                requestPermission()
            }
        }

        this.mOpenYtDlBtn.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(this, YoutubeDownloadActivity::class.java)
                startActivity(intent)
            } else {
                requestPermission()
            }
        }

    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val result: Int = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED
        } else true
    }

    @Throws(Exception::class)
    fun requestPermission() {
        try {
            val code = 27
            ActivityCompat.requestPermissions(
                (this as Activity?)!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                code
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
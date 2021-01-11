package `in`.planckstudio.bot.classic.model.youtube

class DownloadType {
    private var DownloadId: Int = 0
    private var DownloadType: String = ""
    private var DownloadSize: String = ""
    private var DownloadUrl: String = ""
    private var DownloadMute: Boolean = false


    constructor()

    constructor(id: Int, type: String, size: String, url: String, mute: Boolean) {
        this.DownloadId = id
        this.DownloadType = type
        this.DownloadSize = size
        this.DownloadUrl = url
        this.DownloadMute = mute
    }

    fun setDownloadId(id: Int) {
        this.DownloadId = id
    }

    fun setDownloadType(type: String) {
        this.DownloadType = type
    }

    fun setDownloadSize(key: String) {
        this.DownloadSize = key
    }

    fun setDownloadUrl(value: String) {
        this.DownloadUrl = value
    }

    fun setDownloadMute(value: Boolean) {
        this.DownloadMute = value
    }

    fun getDownloadId(): Int {
        return this.DownloadId
    }

    fun getDownloadType(): String {
        return this.DownloadType
    }

    fun getDownloadSize(): String {
        return this.DownloadSize
    }

    fun getDownloadUrl(): String {
        return this.DownloadUrl
    }

    fun getDownloadMute(): Boolean {
        return this.DownloadMute
    }
}
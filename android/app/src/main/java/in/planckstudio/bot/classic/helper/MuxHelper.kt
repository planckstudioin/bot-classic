package `in`.planckstudio.bot.classic.helper

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer

class MuxHelper(
    context: Context,
    outputTitle: String,
    outputSize: String,
    audioFile: String,
    videoFile: String
) {

    private val mOutputTitle = outputTitle
    private val mOutputSize = outputSize
    private val mAudioFile = audioFile
    private val mVideoFile = videoFile
    private val mContext = context

    fun joinVideo(): String {

        GlobalScope.launch(Dispatchers.IO) {

            val outputFormat = ".mp4"
            val outputFileName = mOutputSize + "__" + mOutputTitle + outputFormat

            val myDir = File(Environment.getExternalStorageDirectory(), "/Download")

            if (!(myDir.exists())) {
                myDir.mkdirs()
            }

            val outputFile = File(myDir, outputFileName)

            val muxer = MediaMuxer(outputFile.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            launch(Dispatchers.Main) {
                Toast.makeText(mContext, "Download started", Toast.LENGTH_SHORT).show()
            }

            val audioExtractor = MediaExtractor()
            audioExtractor.setDataSource(mAudioFile)
            val audioIndexMap = hashMapOf<Int, Int>()
            for (i in 0 until audioExtractor.trackCount) {
                audioExtractor.selectTrack(i)
                val audioFormat = audioExtractor.getTrackFormat(i)
                audioIndexMap[i] = muxer.addTrack(audioFormat)
            }

            val videoExtractor = MediaExtractor()
            videoExtractor.setDataSource(mVideoFile)
            val videoIndexMap = hashMapOf<Int, Int>()
            for (i in 0 until videoExtractor.trackCount) {
                videoExtractor.selectTrack(i)
                val videoFormat = videoExtractor.getTrackFormat(i)
                videoIndexMap[i] = muxer.addTrack(videoFormat)
            }

            var finished = false
            val bufferSize: Int = 1024 * 1024 // increase buffer size if readSampleData fails
            val offset = 0
            val audioBuffer = ByteBuffer.allocate(bufferSize)
            val videoBuffer = ByteBuffer.allocate(bufferSize)
            val audioBufferInfo = MediaCodec.BufferInfo()
            val videoBufferInfo = MediaCodec.BufferInfo()

            muxer.start()

            launch(Dispatchers.Main) {
                Toast.makeText(mContext, "Merging audio and video", Toast.LENGTH_SHORT).show()
            }

            while (!finished) {
                audioBufferInfo.offset = offset
                audioBufferInfo.size = audioExtractor.readSampleData(audioBuffer, offset)
                audioBufferInfo.flags = audioExtractor.sampleFlags
                audioBufferInfo.presentationTimeUs = audioExtractor.sampleTime

                videoBufferInfo.offset = offset
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuffer, offset)
                videoBufferInfo.flags = videoExtractor.sampleFlags
                videoBufferInfo.presentationTimeUs = videoExtractor.sampleTime
                when {
                    audioBufferInfo.size < 0 && videoBufferInfo.size < 0 -> {
                        finished = true
                        audioBufferInfo.size = 0
                        videoBufferInfo.size = 0
                    }
                    else -> {
                        audioIndexMap[audioExtractor.sampleTrackIndex]?.apply {
                            muxer.writeSampleData(
                                this,
                                audioBuffer,
                                audioBufferInfo
                            )
                        }
                        audioExtractor.advance()

                        videoIndexMap[videoExtractor.sampleTrackIndex]?.apply {
                            muxer.writeSampleData(
                                this,
                                videoBuffer,
                                videoBufferInfo
                            )
                        }
                        videoExtractor.advance()
                    }
                }
            }
            muxer.stop()
            muxer.release()

            launch(Dispatchers.Main) {
                Toast.makeText(mContext, "Saved on Download folder", Toast.LENGTH_SHORT).show()
            }
        }
        return "true"
    }
}
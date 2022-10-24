package com.evan.img2mp4

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl
import java.io.RandomAccessFile
import java.nio.ByteBuffer

object MuxAudioVideo {
    fun muxAccToMp4(videoPath: String, audioPath: String, outputPath: String): Boolean {
        return try {
            val movie = Movie()
            val audioTracker = AACTrackImpl(FileDataSourceImpl(audioPath))
            // val videoTracker = Mp4TrackImpl(
            //     1,
            //     IsoFile(videoFilePath),
            //     FileRandomAccessSourceImpl(RandomAccessFile(videoFilePath, "r")),
            //     "video"
            // )
            val videoTracker = MovieCreator.build(videoPath).tracks
            movie.addTrack(AppendTrack(audioTracker))
            videoTracker.forEach {
                if ("vide" == it.handler) {
                    movie.addTrack(AppendTrack(it))
                }
            }
            movie.addTrack(AppendTrack(audioTracker))
            // movie.addTrack(AppendTrack(videoTracker))
            val result = DefaultMp4Builder().build(movie)
            val fileChannel = RandomAccessFile(outputPath, "rw").channel
            result.writeContainer(fileChannel)
            fileChannel.close()
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    fun muxMp3ToMp4(videoPath: String, audioPath: String, outputPath: String): Boolean {
        return try {
            val movie = Movie()
            val audioTracker = MP3TrackImpl(FileDataSourceImpl(audioPath))
            // val videoTracker = Mp4TrackImpl(
            //     1,
            //     IsoFile(videoFilePath),
            //     FileRandomAccessSourceImpl(RandomAccessFile(videoFilePath, "r")),
            //     "video"
            // )
            val videoTracker = MovieCreator.build(videoPath).tracks
            movie.addTrack(AppendTrack(audioTracker))
            videoTracker.forEach {
                if ("vide" == it.handler) {
                    movie.addTrack(AppendTrack(it))
                }
            }
            movie.addTrack(AppendTrack(audioTracker))
            // movie.addTrack(AppendTrack(videoTracker))
            val result = DefaultMp4Builder().build(movie)
            val fileChannel = RandomAccessFile(outputPath, "rw").channel
            result.writeContainer(fileChannel)
            fileChannel.close()
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    @SuppressLint("WrongConstant")
    fun muxByMediaMuxer(videoPath: String, audioPath: String, outputPath: String): Boolean {
        return try {
            val videoExtractor = MediaExtractor()
            videoExtractor.setDataSource(videoPath)
            val audioExtractor = MediaExtractor()
            audioExtractor.setDataSource(audioPath)
            val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            videoExtractor.selectTrack(0)
            val videoFormat = videoExtractor.getTrackFormat(0)
            val videoTrack = muxer.addTrack(videoFormat)
            audioExtractor.selectTrack(0)
            val audioFormat = audioExtractor.getTrackFormat(0)
            val audioTrack = muxer.addTrack(audioFormat)
            var sawEOS = false
            var frameCount = 0
            val offset = 0
            val sampleSize = 1024 * 1024
            val videoBuf = ByteBuffer.allocate(sampleSize)
            val audioBuf = ByteBuffer.allocate(sampleSize)
            val videoBufferInfo = MediaCodec.BufferInfo()
            val audioBufferInfo = MediaCodec.BufferInfo()
            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            muxer.start()
            while (!sawEOS) {
                videoBufferInfo.offset = offset
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset)
                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    sawEOS = true
                    videoBufferInfo.size = 0
                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.sampleTime
                    videoBufferInfo.flags = videoExtractor.sampleFlags
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo)
                    videoExtractor.advance()
                    frameCount++
                }
            }
            var sawEOS2 = false
            var frameCount2 = 0
            while (!sawEOS2) {
                frameCount2++
                audioBufferInfo.offset = offset
                audioBufferInfo.size = audioExtractor.readSampleData(audioBuf, offset)
                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    sawEOS2 = true
                    audioBufferInfo.size = 0
                } else {
                    audioBufferInfo.presentationTimeUs = audioExtractor.sampleTime
                    audioBufferInfo.flags = audioExtractor.sampleFlags
                    muxer.writeSampleData(audioTrack, audioBuf, audioBufferInfo)
                    audioExtractor.advance()
                }
            }
            muxer.stop()
            muxer.release()
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }
}
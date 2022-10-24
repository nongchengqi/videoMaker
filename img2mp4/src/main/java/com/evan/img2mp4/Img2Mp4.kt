package com.evan.img2mp4

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream

object Img2Mp4 {
    fun makeImageToVideo(
        imageFile: File,
        outFile: File,
        fps: Int = 1,
        frames: Int = 1,
        bitrate: Int = 1800000
    ) {
        Log.e("Img2Mp4", "-----start: fps:$fps frames:$frames")
        val time = System.currentTimeMillis()
        val map = BitmapFactory.decodeFile(imageFile.absolutePath)
        val videoEncoder = VideoEncoder(
            map.width, map.height, bitrate,
            fps
        )
        videoEncoder.start(outFile.absolutePath)
        repeat(fps * frames) {
            videoEncoder.drainFrame(map.copy(Bitmap.Config.ARGB_8888, false), it)
        }
        videoEncoder.drainEnd()
        Log.e("Img2Mp4", "-----end: cost time:${System.currentTimeMillis() - time}")
    }

    private fun createImage(context: Context, icon: Int, outFile: File): Boolean {
        val bm = try {
            createBitmapFromCanvas(context, icon)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        } ?: return false
        return try {
            if (outFile.exists()) {
                outFile.delete()
            }
            outFile.parentFile?.mkdirs()
            outFile.createNewFile()
            val out = FileOutputStream(outFile)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    private fun createBitmapFromCanvas(
        context: Context,
        icon: Int
    ): Bitmap? {
        val width = 750
        val height = 1624
        val drawable =
            ResourcesCompat.getDrawable(
                context.resources,
                icon,
                null
            )?.toBitmap()
                ?: return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(
            drawable,
            Rect(0, 0, drawable.width, drawable.height),
            Rect(0, 0, width, height),
            null
        )

        val tips = "tips"
        val title = "title"
        val desc = "desc"
        val tipsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 36f
            color = Color.parseColor("#ccffffff")
            style = Paint.Style.FILL
        }
        val tipsBounds = Rect()
        tipsPaint.getTextBounds(tips, 0, tips.length, tipsBounds)
        canvas.drawText(
            tips, (width - tipsBounds.width()) / 2f, 470f + 36f, tipsPaint
        )
        val iconBitmap =
            ResourcesCompat.getDrawable(context.resources, icon, null)?.toBitmap()
        iconBitmap?.let {
            canvas.drawBitmap(
                iconBitmap,
                Rect(0, 0, it.width, it.height),
                Rect(278, 574, 478, 774),
                null
            )
        }
        if (title.isNotEmpty()) {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG).apply {
                textSize = 44f
                color = Color.parseColor("#FF02091C")
                style = Paint.Style.FILL
            }
            val textBounds = Rect()
            textPaint.getTextBounds(title, 0, title.length, textBounds)
            canvas.drawText(
                title, (width - textBounds.width()) / 2f, 808f + 44f, textPaint
            )
        }
        if (desc.isNotEmpty()) {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 28f
                color = Color.parseColor("#7d000000")
                style = Paint.Style.FILL
            }
            val textBounds = Rect()
            textPaint.getTextBounds(desc, 0, desc.length, textBounds)
            canvas.drawText(
                desc, (width - textBounds.width()) / 2f, 878f + 28f, textPaint
            )
        }

        val slogan = "slogan"
        val sloganPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 32f
            color = Color.parseColor("#ccffffff")
            style = Paint.Style.FILL
        }
        if (slogan.contains("\n")) {
            val slogans = slogan.split("\n")
            slogans.forEachIndexed { index, s ->
                canvas.drawText(
                    s, 238f, 1274f + 24f + (40 * index), sloganPaint
                )
            }
        } else {
            canvas.drawText(
                slogan, 238f, 1274f + 32f, sloganPaint
            )
        }
        return bitmap
    }
}
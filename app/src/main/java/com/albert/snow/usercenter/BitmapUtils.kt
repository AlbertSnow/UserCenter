package com.albert.snow.usercenter

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {

    fun getBitmapFromFile(f: File): Bitmap? {
        return if (!f.exists()) null else try {
            BitmapFactory.decodeFile(f.absolutePath)
        } catch (ex: java.lang.Exception) {
            null
        }
    }


    fun savePicture(
        bitmap: Bitmap, format: CompressFormat?,
        saveFilePath: String?, quality: Int
    ): Boolean {
        val file = File(saveFilePath)
        makeDir(file)
        try {
            val out = FileOutputStream(file)
            if (bitmap.compress(format, quality, out)) {
                out.flush()
                out.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun makeDir(file: File) {
        val tempPath = File(file.parent)
        if (!tempPath.exists()) {
            tempPath.mkdirs()
        }
    }
}
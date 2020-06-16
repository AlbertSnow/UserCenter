package com.albert.snow.usercenter

import android.os.Environment
import java.io.File


const val APP_EXTERNAL_DIR = "PictureDemo"
class MainRepository {

    var currentCropCacheFile: File?= null

    fun generateCropCacheFile() {
        val dir = File(Environment.getExternalStorageDirectory().path, APP_EXTERNAL_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        currentCropCacheFile = File(dir, "${System.currentTimeMillis()}-avatar.crop_cachedata_picutre")
    }

    var currentPickCacheFile: File?= null

    fun generatePickCacheFile() {
        val dir = File(Environment.getExternalStorageDirectory().path, APP_EXTERNAL_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        currentPickCacheFile = File(dir, "${System.currentTimeMillis()}-avatar.pick_cachedata_picutre")
    }
}
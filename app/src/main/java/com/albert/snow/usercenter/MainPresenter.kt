package com.albert.snow.usercenter

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

class MainPresenter(val view: MainActivity) {

    private val REQUEST_CODE_OPEN_ALBUM = 50
    private val REQUEST_CODE_OPEN_CROP_PICTURE = 51

    private val repository = MainRepository()

    fun goEditHeadPicture(activity: Activity) {
        startPhotoAlbum(activity)
    }
    private fun startPhotoAlbum(activity: Activity) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_ALBUM)
    }

    private fun startPhotoClip(sourceFile: File, activity: Activity): Boolean {
        var isFailure = true

        val intent = Intent("com.android.camera.action.CROP")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        repository.generateCropCacheFile()
        repository.currentCropCacheFile?.also {

            val cropDestFile =  Uri.fromFile(it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropDestFile)

            val sourceUri: Uri? = getSourceFileUri(activity, sourceFile, intent)
            intent.setDataAndType(sourceUri, "image/*")

            activity.startActivityForResult(intent, REQUEST_CODE_OPEN_CROP_PICTURE)
            isFailure = false
        }

        return isFailure
    }

    private fun getSourceFileUri(activity: Activity, sourceFile: File, intent: Intent): Uri? {
        var sourceUri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sourceUri = FileProvider.getUriForFile(activity,"${activity.packageName}.fileProvider", sourceFile);
            val resInfoList: List<ResolveInfo> ?= activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resInfoList?.forEach { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, sourceUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        } else {
            sourceUri = Uri.fromFile(sourceFile)
        }
        return sourceUri
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, intent: Intent?, activity: Activity): Boolean {
        var isInterruptHandle = true

        if (requestCode == REQUEST_CODE_OPEN_ALBUM) {
            handleAlbumResult(resultCode, intent, activity)
        } else if (requestCode == REQUEST_CODE_OPEN_CROP_PICTURE) {
            handleCropResult(resultCode, intent, activity)
        } else {
            isInterruptHandle = false
        }

        return isInterruptHandle
    }

    private fun handleAlbumResult(resultCode: Int, intent: Intent?, activity: Activity) {
        var isFailure = true

        intent?.data?.let {uri ->
            val contentResolver = activity.contentResolver;
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);//获得图片数据

            repository.generatePickCacheFile()
            repository.currentPickCacheFile?.also {bitmapFile ->
                BitmapUtils.savePicture(bitmap, Bitmap.CompressFormat.JPEG, bitmapFile.absolutePath , 80);
                isFailure = startPhotoClip(bitmapFile, activity)
            }
        }
    }

    private fun handleCropResult(resultCode: Int, intent: Intent?, activity: Activity) {

        repository.currentCropCacheFile?.also {
            val bitmap: Bitmap? = BitmapUtils.getBitmapFromFile(it)
            if (bitmap != null) {
                view.showBitmap(bitmap)
            } else {
                view.showToast("图片处理失败")
            }
        }
    }


}
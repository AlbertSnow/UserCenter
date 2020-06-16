package com.albert.snow.usercenter

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this)
    }

    fun onClickChangePicture(view: View) {
        presenter.goEditHeadPicture(this)
    }

    fun showToast(it: String) {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

    fun showBitmap(bitmap: Bitmap) {
        Glide.with(this).load(bitmap).centerCrop()
            .apply(RequestOptions.circleCropTransform()).into(imageView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (presenter.handleActivityResult(requestCode, resultCode, data, this)) {
            return
        }
    }

}

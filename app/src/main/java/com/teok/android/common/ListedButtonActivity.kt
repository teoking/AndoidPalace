package com.teok.android.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

import com.teok.android.R

abstract class ListedButtonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listed_button)
    }

    fun addButton(name: String, buttonClick: View.OnClickListener) {

    }
}

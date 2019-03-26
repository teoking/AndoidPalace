package com.teok.android.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.teok.android.R

abstract class ListedButtonActivity : AppCompatActivity() {

    private lateinit var mContainer: LinearLayout
    private lateinit var mInflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listed_button)
        mContainer = findViewById(R.id.root_container)
        mInflater = LayoutInflater.from(this)
    }

    fun addButton(name: String, onClickListener: View.OnClickListener) {
        val button = mInflater.inflate(R.layout.normal_button, mContainer, false) as Button
        button.text = name
        button.setOnClickListener(onClickListener)

        mContainer.addView(button)
    }
}

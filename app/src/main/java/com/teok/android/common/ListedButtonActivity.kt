package com.teok.android.common

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.teok.android.R

abstract class ListedButtonActivity : AppCompatActivity() {

    private lateinit var mContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listed_button)
        mContainer = findViewById(R.id.root_container)
    }

    fun addButton(name: String, onClickListener: View.OnClickListener) {
        val button = Button(this, null, R.style.NormalButton)
        button.text = name
        button.setOnClickListener(onClickListener)

        mContainer.addView(button)
    }
}

package com.teok.android.lab.dictionary;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.teok.android.R;

import java.util.zip.ZipFile;

/**
 * DictDemo activity
 */
public class DictDemo extends Activity {
    private EditText mWordBox;
    private TextView mWordExplaination;
    private Button mSearchBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dict_demo);

        mWordBox = (EditText) findViewById(R.id.word_box);
        mWordExplaination = (TextView) findViewById(R.id.word_explaination);
        mSearchBtn = (Button) findViewById(R.id.search_btn);
    }


}
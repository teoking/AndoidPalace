package com.teok.android.xposed;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.teok.android.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Created at 11:00 AM, 10/9/14</p>
 *
 * @author teo
 */
public class ShowInterpretLog extends Activity {

    private TextView mTextViewResults;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_xposed_interpret_log_layout);

        mTextViewResults = (TextView) findViewById(R.id.textview_results);
        findViewById(R.id.btn_show_letv_model).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        createLogFile();

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<String> logs = InnoLog.getCachedLogs();
                if (logs == null)
                    return;

                for (String log : logs) {
                    mTextViewResults.append(log + "\r\n");
                }
            }
        }.sendEmptyMessage(0);
    }

    private void createLogFile() {
        File logFile = new File(getDir("files", MODE_WORLD_WRITEABLE | MODE_WORLD_READABLE), "android_palace_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                logFile.setReadable(true, false);
                logFile.setWritable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
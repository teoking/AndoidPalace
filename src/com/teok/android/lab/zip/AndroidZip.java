package com.teok.android.lab.zip;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.teok.android.common.ULog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Test zip file read/write on Android
 */
public class AndroidZip extends Activity {
    private static final String TAG = "AndroidZip";

    private TextView mResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResult = new TextView(this);
        setContentView(mResult);

        ViewGroup.LayoutParams params = mResult.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mResult.setLayoutParams(params);

        new ReadAssertZipFileTask().execute("endict.zip");
    }

    class ReadAssertZipFileTask extends AsyncTask<String, Integer, Integer> {

        public static final int OK = 1;
        public static final int ERROR_PARAMS = -1;
        public static final int ERROR_UNKNOWN = -2;

        private long mStart = -1;
        private long mEnd = -1;
        private int mLineCount;

        @Override
        protected void onPreExecute() {
            mResult.setText("Start reading endict.zip in assert...");
            mResult.append("\r\n");
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (params == null || params.length > 2) {
                return ERROR_PARAMS;
            }

            mStart = System.currentTimeMillis();

            String zipFileName = params[0];
            if (!isFinishing()) {
                try {
                    AssetFileDescriptor afd = getAssets().openFd(zipFileName);
                    ZipInputStream zis = new ZipInputStream(afd.createInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(zis));

                    ZipEntry ze;
                    while ((ze = zis.getNextEntry()) != null) {
                        dumpZipEntry(ze);

                        String line;
                        while ((line = br.readLine()) != null) {
                            mLineCount ++;
                            ULog.d(TAG, line);
                        }

                        zis.closeEntry();
                    }
                    br.close();

                    mEnd = System.currentTimeMillis();
                    return OK;
                } catch (IOException e) {
                    ULog.e(TAG, "Read assert zip file failed", e);
                }
            }

            return ERROR_UNKNOWN;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == OK) {
                Toast.makeText(AndroidZip.this, "Read end, cost~" + (mEnd - mStart) + "", Toast.LENGTH_LONG).show();
                mResult.append("Read finished. cost ~ " + (mEnd - mStart) + " ms");
                mResult.append("\r\n");
                mResult.append("Lines count = " + mLineCount);
                mResult.append("\r\n");
            } else if (result < OK) {
                Toast.makeText(AndroidZip.this, "Read end with error " + result, Toast.LENGTH_LONG).show();
                mResult.append("Read finished with error, code = " + result);
                mResult.append("\r\n");
            }
        }

        void dumpZipEntry(ZipEntry ze) {
            if (ze != null) {
                ULog.d(TAG, "DumpZip name: " + ze.getName());
                ULog.d(TAG, "DumpZip Comment: " + ze.getComment());
                ULog.d(TAG, "DumpZip CompressedSize: " + ze.getCompressedSize());
                ULog.d(TAG, "DumpZip Crc: " + ze.getCrc());
                ULog.d(TAG, "DumpZip Time: " + ze.getTime());
            }
        }
    }
}
package com.teok.android.opengles.my;

import android.R;
import android.app.ListActivity;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.teok.android.common.ULog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * <p>Created at 10:38 AM, 9/4/13</p>
 *
 * @author teo
 */
public class GLESImplInfo extends ListActivity {

    private static final String TAG = "GLESImplInfo";
    ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListView = getListView();
        new InfoLoader().execute();
    }

    private class InfoLoader extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            ArrayList<String> infoList = new ArrayList<String>();

            addNotNull(infoList, "Float configs :");
            for (int i : FLOAT_CONFIGS) {
                addNotNull(infoList, getFloat_glGetFloatv(i));
            }

            addNotNull(infoList, "String configs :");
            for (int i : STRING_CONFIGS) {
                addNotNull(infoList, getString_glGetString(i));
            }

            return infoList;
        }

        @Override
        protected void onPostExecute(List<String> infoList) {
            if (infoList == null || infoList.isEmpty()) {
                TextView emptyText = new TextView(GLESImplInfo.this);
                mListView.setEmptyView(emptyText);
                emptyText.setText("Failed to get OpenGL ES information");
            } else {
                mListView.setAdapter(new ArrayAdapter<String>(GLESImplInfo.this, R.layout.simple_list_item_1, infoList));
            }

            mListView.setVisibility(View.VISIBLE);
        }
    }

    void addNotNull(List<String> list, String val) {
        if (!TextUtils.isEmpty(val))
            list.add(val);
    }

    String getFloat_glGetFloatv(int pName) {
        try {
            float[] f = new float[2];
            GLES20.glGetFloatv(pName, f, 0);
            String name = getParamName(pName);
            return name + ": [" + f[0] + "," + f[1] + "]";
        } catch (IllegalAccessException e) {
            ULog.w(TAG, "error to find 0x" + Integer.toHexString(pName));
        }

        return null;
    }

    String getString_glGetString(int pName) {
        try {
            return getParamName(pName) + " : " + GLES20.glGetString(pName);
        } catch (IllegalAccessException e) {
            ULog.w(TAG, "error to find 0x" + Integer.toHexString(pName));
        }

        return null;
    }

    String getParamName(int pName) throws IllegalAccessException {
        Field[] fields = GLES20.class.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.getInt(null) == pName) {
                return f.getName();
            }
        }

        return null;
    }

    int[] FLOAT_CONFIGS = {
        GLES20.GL_ALIASED_LINE_WIDTH_RANGE,
    };

    int[] STRING_CONFIGS = {
            GLES20.GL_EXTENSIONS,
    };
}
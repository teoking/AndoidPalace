package com.teok.android.opengles;

import android.opengl.GLSurfaceView;
import com.teok.android.common.ULog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * <p>Created at 11:09 AM, 8/6/13</p>
 *
 * @author teo
 */
public abstract class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = GLRenderer.class.getName();

    private boolean mFirstDraw;
    private boolean mSurfaceCreated;
    private int mWidth;
    private int mHeight;
    private long mLastTime;
    private int mFPS;

    public GLRenderer() {
        mFirstDraw = true;
        mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFPS = 0;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed,
                                 EGLConfig config) {
        if (ULog.DEBUG) {
            ULog.i(TAG, "Surface created.");
        }
        mSurfaceCreated = true;
        mWidth = -1;
        mHeight = -1;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width,
                                 int height) {
        if (!mSurfaceCreated && width == mWidth
                && height == mHeight) {
            if (ULog.DEBUG) {
                ULog.i(TAG,
                        "Surface changed but already handled.");
            }
            return;
        }
        if (ULog.DEBUG) {
            // Android honeycomb has an option to keep the
            // context.
            String msg = "Surface changed width:" + width
                    + " height:" + height;
            if (mSurfaceCreated) {
                msg += " context lost.";
            } else {
                msg += ".";
            }
            ULog.i(TAG, msg);
        }

        mWidth = width;
        mHeight = height;

        onCreate(mWidth, mHeight, mSurfaceCreated);
        mSurfaceCreated = false;
    }

    @Override
    public void onDrawFrame(GL10 notUsed) {
        onDrawFrame(mFirstDraw);

        if (ULog.DEBUG) {
            mFPS++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime >= 1000) {
                mFPS = 0;
                mLastTime = currentTime;
            }
        }

        if (mFirstDraw) {
            mFirstDraw = false;
        }
    }

    public int getFPS() {
        return mFPS;
    }

    public abstract void onCreate(int width, int height,
                                  boolean contextLost);

    public abstract void onDrawFrame(boolean firstDraw);
}

package com.teok.android.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import com.teok.android.R;
import com.teok.android.common.ULog;

public class FrameFocusTransitionActivity extends Activity {

    private static final String TAG = "FrameFocusTransition";

    private Button mButton1;
    private Button mButton2;
    private LinearLayout mContentLayout;

    private View mFocus;
    private static final int PADDING = 10;
    private static final int FRAME_FOCUS_GRADIENT_WIDTH = 52;

    private int mActionBarHeight = 0;
    private boolean isFrameFocusInited = false;
    private static final int TRANS_ANIM_DURATION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_focus_transition);

        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);

        mFocus = findViewById(R.id.frame_focus);

        mButton1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    animToAnchorView(v, mFocus, TRANS_ANIM_DURATION);
                }
            }
        });

        mButton2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    animToAnchorView(v, mFocus, TRANS_ANIM_DURATION);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Locations will be available when the activity is visible.
        int[] location = new int[2];
        mButton1.getLocationInWindow(location);
        mButton1.setText(printViewLocation("button 1# ", mButton1));

        mButton2.getLocationInWindow(location);
        mButton2.setText(printViewLocation("button 2# ", mButton2));

        if (getActionBar() != null) {
            mActionBarHeight = getActionBar().getHeight();
        }

        if (!isFrameFocusInited) {
            animToAnchorView(mButton1, mFocus, 100);
            mFocus.setVisibility(View.VISIBLE);
            isFrameFocusInited = true;
        }
    }

    private void animToAnchorView(final View anchor, final View frameFocus, int duration) {
        printViewLocation("MoveTo1", anchor);

        int[] position = new int[2];

        // Do animation
        // Transition
        float focusX = 0,
              toXDelta = 0,
              focusY = 0,
              toYDelta = 0;
        float anchorX = 0,
              anchorY = 0;

        anchor.getLocationInWindow(position);
        anchorX = position[0];
        anchorY = position[1];

        frameFocus.getLocationInWindow(position);
        focusX = position[0];
        focusY = position[1];

        toXDelta = anchorX - PADDING - FRAME_FOCUS_GRADIENT_WIDTH / 2 - focusX;
        toYDelta = anchorY - PADDING - FRAME_FOCUS_GRADIENT_WIDTH / 2 - focusY;

        ULog.d(TAG, "anim to x=" + toXDelta + " y=" + toYDelta);

        Animation anim = new TranslateAnimation(0, toXDelta, 0, toYDelta);
//        anim.setFillAfter(true);
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Fix the flicker problem with animation
                // See: http://www.mail-archive.com/android-developers@googlegroups.com/msg67535.html
                animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                animation.setDuration(1);
                frameFocus.startAnimation(animation);
                pinToAnchorView(anchor, frameFocus);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        frameFocus.startAnimation(anim);
    }

    /**
     * Move the focus to the anchor view without animation
     * @param anchor
     * @param frameFocus
     */
    private void pinToAnchorView(View anchor, final View frameFocus) {
        int[] position = new int[2];
        anchor.getLocationInWindow(position);
        float left = position[0];
        float top = position[1];
        float width = anchor.getWidth();
        float height = anchor.getHeight();

        final float realLeft = left - PADDING - FRAME_FOCUS_GRADIENT_WIDTH / 2;
        final float realTop = top - PADDING - mActionBarHeight - FRAME_FOCUS_GRADIENT_WIDTH / 2;
        final float realWidth = width + 2 * PADDING + FRAME_FOCUS_GRADIENT_WIDTH;
        final float realHeight = height + 2 * PADDING + FRAME_FOCUS_GRADIENT_WIDTH;

        frameFocus.setX(realLeft);
        frameFocus.setY(realTop);

        if (frameFocus.getWidth() != realWidth || frameFocus.getHeight() != realHeight) {
            ULog.d(TAG, "frame focus resize...");
            ViewGroup.LayoutParams params = frameFocus.getLayoutParams();
            params.width = (int) realWidth;
            params.height = (int) realHeight;
            frameFocus.setLayoutParams(params);

            printViewLocation("frame focus", frameFocus);
        }
    }

    private String printViewLocation(String viewName, View view) {
        int[] position = new int[2];
        view.getLocationInWindow(position);

        String log = "[" + viewName + "]\n x=" + position[0] + " y=" + position[1] + " w=" + view.getWidth() + " h=" + view.getHeight();

        ULog.d(TAG, log);
        return log;
    }
}

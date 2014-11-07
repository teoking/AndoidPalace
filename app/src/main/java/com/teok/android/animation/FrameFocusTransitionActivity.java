package com.teok.android.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
                    moveToAnchorView(v, mFocus);
                }
            }
        });

        mButton2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    moveToAnchorView(v, mFocus);
                }
            }
        });

        ViewTreeObserver observer = mContentLayout.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int[] location = new int[2];
                    mButton1.getLocationInWindow(location);
                    mButton1.setText(printViewLocation("button 1# ", mButton1));

                    mButton2.getLocationInWindow(location);
                    mButton2.setText(printViewLocation("button 2# ", mButton2));

                    if (getActionBar() != null) {
                        mActionBarHeight = getActionBar().getHeight();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void moveToAnchorView(View anchor, final View frameFocus) {
        int[] position = new int[2];
        anchor.getLocationInWindow(position);
        float left = position[0];
        float top = position[1];
        float width = anchor.getWidth();
        float height = anchor.getHeight();

        final float realLeft = (int) (left - PADDING - FRAME_FOCUS_GRADIENT_WIDTH / 2);
        final float realTop = (int) (top - PADDING - mActionBarHeight - FRAME_FOCUS_GRADIENT_WIDTH / 2);
        final float realWidth = (int) (width + 2 * PADDING + FRAME_FOCUS_GRADIENT_WIDTH);
        final float realHeight = (int) (height + 2 * PADDING + FRAME_FOCUS_GRADIENT_WIDTH);

        printViewLocation("focused view", anchor);

//        // Do animation
//        // Transition
//        float fromXDelta = 0,
//                toXDelta = 0,
//                fromYDelta = 0,
//                toYDelta = 0;
//        frameFocus.getLocationInWindow(position);
//        if (isFrameFocusInited) {
//            toXDelta = left - position[0];
//            toYDelta = top - position[1];
//        } else {
//            fromXDelta = position[0];
//            fromYDelta = position[1];
//            isFrameFocusInited = true;
//        }
//        Animation anim = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
//        anim.setFillAfter(true);
//        anim.setDuration(300);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
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
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        frameFocus.startAnimation(anim);
    }

    private String printViewLocation(String viewName, View view) {
        int[] position = new int[2];
        view.getLocationInWindow(position);

        String log = "[" + viewName + "]\n x=" + position[0] + " y=" + position[1] + " w=" + view.getWidth() + " h=" + view.getHeight();

        ULog.d(TAG, log);
        return log;
    }
}

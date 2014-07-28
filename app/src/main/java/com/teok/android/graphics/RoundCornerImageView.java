package com.teok.android.graphics;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.teok.android.R;
import com.teok.android.common.ULog;

/**
* Created by teo on 2014-7-26.
*/
public class RoundCornerImageView extends ImageView {

    private static final boolean DEBUG = true;
    private static final String TAG = RoundCornerImageView.class.getSimpleName();

    private Drawable mDrawable;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mDrawable = RoundCornerDrawable.fromDrawable(drawable);
        super.setImageDrawable(mDrawable);
        ULog.d(TAG, "setImageDrawable w=" + mDrawable.getIntrinsicWidth() + "  h=" + mDrawable.getIntrinsicHeight());
    }

    private static class RoundCornerDrawable extends Drawable {
        // Temporarily
        private static final float FIXED_CORNER_RADIUS = 20.0f;
        private static final float FIXED_MARGIN = 12.0f;

        private final float mCornerRadius;

        private BitmapShader mShader;
        private Paint mPaint;
        private RectF mRect;

        RoundCornerDrawable(Bitmap bitmap, float cornerRadius) {
            mCornerRadius = cornerRadius;

            mShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setShader(mShader);

            mRect = new RectF();
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
//            mRect.set(0.f, 0.f, bounds.width(), bounds.height());
            mRect.set(FIXED_MARGIN, FIXED_MARGIN, 800.f, 600.f);
            ULog.d(TAG, "bounds " + bounds.flattenToString());
            ULog.d(TAG, "w=" + bounds.width() + "  h=" + bounds.height());
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public static RoundCornerDrawable fromDrawable(Drawable drawable) {
            Bitmap bm;
            if (drawable instanceof BitmapDrawable) {
                bm = ((BitmapDrawable)drawable).getBitmap();
            } else {
                bm = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }

            ULog.d(TAG, "bitmap  w=" + bm.getWidth() + "  h=" + bm.getHeight());

            return new RoundCornerDrawable(bm, FIXED_CORNER_RADIUS);
        }
    }
}

package com.teok.android.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.teok.android.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * <p>Created at 11:05 AM, 6/17/13</p>
 *
 * @author teo
 */
public class ShrinkAndFadeInActivity extends Activity {

    GridView mGrid;
    private List<ResolveInfo> mApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadApps(); // do this in onresume?

        setContentView(R.layout.grid);
        mGrid = (GridView) findViewById(R.id.myGrid);
        mGrid.setAdapter(new AppsAdapter());
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shrinkAndFadeInIcon((ImageView) view);
            }
        });
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;

            if (convertView == null) {
                i = new ImageView(ShrinkAndFadeInActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new GridView.LayoutParams(50, 50));
            } else {
                i = (ImageView) convertView;
            }

            ResolveInfo info = mApps.get(position);
            i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

            return i;
        }


        public final int getCount() {
            return mApps.size();
        }

        public final Object getItem(int position) {
            return mApps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    private void shrinkAndFadeInIcon(final ImageView v) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 2.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 2.0f);

        final GridView grid = (GridView) v.getParent();
        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(v, alpha,
                scaleX, scaleY);
        oa.setDuration(300);
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scheduleFadeOutIcon(v);
            }
        });
        oa.start();
    }

    private void scheduleFadeOutIcon(final ImageView v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f);
                        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
                        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
                        ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(v, alpha,
                                scaleX, scaleY);
                        oa.setDuration(300);
                        oa.start();
                    }
                });
            }
        }, 500);
    }
}

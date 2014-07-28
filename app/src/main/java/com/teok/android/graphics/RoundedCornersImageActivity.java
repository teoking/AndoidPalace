package com.teok.android.graphics;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.teok.android.R;

public class RoundedCornersImageActivity extends Activity {

    private RoundCornerImageView mImageView;
    private RoundCornerImageView mImageView2;
    private RoundCornerImageView mImageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rounded_corners_image);

        mImageView = (RoundCornerImageView) findViewById(R.id.imageView);
        mImageView2 = (RoundCornerImageView) findViewById(R.id.imageView2);
        mImageView3 = (RoundCornerImageView) findViewById(R.id.imageView3);

        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.scene1));
        mImageView2.setImageDrawable(getResources().getDrawable(R.drawable.scene2));
        mImageView3.setImageDrawable(getResources().getDrawable(R.drawable.scene3));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rounded_corners_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

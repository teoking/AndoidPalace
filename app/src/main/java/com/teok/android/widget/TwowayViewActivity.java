package com.teok.android.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teok.android.R;
import com.teok.android.common.ULog;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class TwowayViewActivity extends Activity {

    private static final String TAG = TwowayViewActivity.class.getSimpleName();

    @BindView(R.id.leftAnchor)
    ImageView mLeftAnchor;

    @BindView(R.id.recyclerView)
    TwoWayView mRecyclerView;

    @BindView(R.id.rightAnchor)
    ImageView mRightAnchor;

    private List<ItemObject> mData;
    private int mCurrentPosition;
    private int mMinPosition;
    private int mMaxPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoway_view);

        mData = new ArrayList<ItemObject>();
        mData.add(new ItemObject(R.drawable.ic_launcher, "App1", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App2", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App3", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App4", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App5", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App6", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App7", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App8", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App9", "Bla bla bla bla bla bla"));
        mData.add(new ItemObject(R.drawable.ic_launcher, "App10", "Bla bla bla bla bla bla"));

        mRecyclerView.setAdapter(new LayoutAdapter(this, mRecyclerView, R.layout.item, mData));

        mMinPosition = 0;
        mMaxPosition = mData.size() - 1;

        mCurrentPosition = mMinPosition;

        mRecyclerView.smoothScrollToPosition(mCurrentPosition);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = recyclerView.findViewHolderForPosition(mCurrentPosition).itemView;
                    ULog.d(TAG, "current View(" + view.getLeft() + ", " + view.getTop() + ")");
                }
            }
        });

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ULog.d(TAG, v + "  " + hasFocus);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        ULog.d(TAG, "firstVisiblePosition " + mRecyclerView.getFirstVisiblePosition());
//        ULog.d(TAG, "lastVisiblePosition " + mRecyclerView.getLastVisiblePosition());

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mCurrentPosition = Math.max(mCurrentPosition - 1, mMinPosition);
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mCurrentPosition = Math.min(mCurrentPosition + 1, mMaxPosition);
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                ItemObject item = mData.get(mCurrentPosition);
                Toast.makeText(this, item.title + " clicked!", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private static class ItemObject {
        public int iconId;
        public String title;
        public String description;

        public ItemObject(int iconId, String title, String description) {
            this.iconId = iconId;
            this.title = title;
            this.description = description;
        }
    }

    private static class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

        private final Context mContext;
        private final TwoWayView mRecyclerView;
        private final int mLayoutId;
        private final List<ItemObject> mData;

        public LayoutAdapter(Context context, TwoWayView recyclerView, int layoutId, List<ItemObject> data) {
            mContext = context;
            mRecyclerView = recyclerView;
            mLayoutId = layoutId;
            mData = data;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            ItemObject item = mData.get(position);
            holder.icon.setImageResource(item.iconId);
            holder.title.setText(item.title);
            holder.description.setText(item.description);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        static class SimpleViewHolder extends RecyclerView.ViewHolder {
            public final ImageView icon;
            public final TextView title;
            public final TextView description;

            public SimpleViewHolder(View view) {
                super(view);
                icon = (ImageView) view.findViewById(R.id.icon);
                title = (TextView) view.findViewById(R.id.title);
                description = (TextView) view.findViewById(R.id.description);
            }
        }

    }
}

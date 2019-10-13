package com.libraries.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


public class MGTab extends LinearLayout implements View.OnClickListener{

    View previousView;
    int lastIndex = -1;
    View[] views;

    public MGTab(final Context context) {
        super(context);
    }

    public MGTab(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MGTab(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTabOrientation(boolean vertical) {

        if(vertical)
            this.setOrientation(LinearLayout.VERTICAL);
        else
            this.setOrientation(LinearLayout.HORIZONTAL);
    }

    OnTabListener mCallback;
    public interface OnTabListener {
        public void onTabCreated(MGTab tab, int index, View v);
        public void onTabSelected(MGTab tab, int index, View v);
        public void onTabPreviouslySelected(MGTab tab, int index, View v);
    }

    public void setOnTabListener(OnTabListener listener) {
        try {
            mCallback = (OnTabListener) listener;
        } catch (ClassCastException e)  {
            throw new ClassCastException(this.toString() + " must implement OnTabListener");
        }
    }

    public void createTab(int count, int resIdLayout, boolean vertical) {

        setTabOrientation(vertical);

        if(count <= 0) {
            throw new ArrayIndexOutOfBoundsException(this.toString() + " selected resid, unselected resid and tab names must have the same array length.");
        }

        views = new View[count];
        for(int x = 0; x < count; x++) {
            LayoutInflater inf = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inf.inflate(resIdLayout, null);
            v.setOnClickListener(this);
            v.setTag(x);
            views[x] = v;

            if(mCallback != null)
                mCallback.onTabCreated(this, x, v);

            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, 1.0f);

            addView(v, params);
        }
    }

    public void setSelectedTab(int index) {

        if(index >= 0 && index < views.length) {
            onClick(views[index]);
        }
        else {
            throw new ArrayIndexOutOfBoundsException(this.toString() + " tab index out of bounds");
        }
    }

    @Override
    public void onClick(View v) {

        if(lastIndex != -1) {
            if(mCallback != null)
                mCallback.onTabPreviouslySelected(this, lastIndex, previousView);
        }

        int index = Integer.parseInt(v.getTag().toString());
        View viewSelected = views[index];

        lastIndex = index;
        previousView = viewSelected;

        if(mCallback != null)
            mCallback.onTabSelected(this, index, viewSelected);
    }

    public int getSelectedTabIndex() {
        return lastIndex;
    }
}

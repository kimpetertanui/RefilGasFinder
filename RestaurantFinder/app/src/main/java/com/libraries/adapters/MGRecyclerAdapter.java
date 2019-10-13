package com.libraries.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mg on 30/07/16.
 */
public class MGRecyclerAdapter extends RecyclerView.Adapter<MGRecyclerAdapter.ViewHolder> {

    OnMGRecyclerAdapterListener mCallback;
    public interface OnMGRecyclerAdapterListener {
        public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, ViewHolder v, int position);
    }

    public void setOnMGRecyclerAdapterListener(OnMGRecyclerAdapterListener listener) {

        try {
            mCallback = (OnMGRecyclerAdapterListener) listener;
        } catch (ClassCastException e)  {
            throw new ClassCastException(this.toString() + " must implement OnMGRecyclerAdapterListener");
        }
    }

    private int resId;
    private int count;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MGRecyclerAdapter(int count, int resId) {
        this.count = count;
        this.resId = resId;
    }

    @Override
    public MGRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);
        if(mCallback != null) {
            mCallback.onMGRecyclerAdapterCreated(this, holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }
}

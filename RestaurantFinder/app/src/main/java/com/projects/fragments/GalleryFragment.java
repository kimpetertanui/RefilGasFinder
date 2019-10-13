package com.projects.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.application.MainApplication;
import com.config.UIConfig;
import com.db.Queries;
import com.etsy.android.grid.StaggeredGridView;
import com.libraries.adapters.MGListAdapter;
import com.libraries.adapters.MGListAdapter.OnMGListAdapterAdapterListener;
import com.libraries.imageview.StaggeredImageView;
import com.models.Photo;
import com.projects.activities.ImageViewerActivity;
import com.apps.restaurantfinder.R;
import java.util.ArrayList;

public class GalleryFragment extends Fragment implements OnItemClickListener {

	private View viewInflate = null;
	ArrayList<Photo> photoList;
	Queries q;
	SwipeRefreshLayout swipeRefresh;

	@Override
    public void onDestroyView()  {
        super.onDestroyView();
        if (viewInflate != null) {
            ViewGroup parentViewGroup = (ViewGroup) viewInflate.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// save the reference of the inflated view
		viewInflate = inflater.inflate(R.layout.fragment_gallery, container, false);
        return viewInflate;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// this is really important in order to save the state across screen
		// configuration changes for example
		setRetainInstance(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)  {
		super.onViewCreated(view, savedInstanceState);
		swipeRefresh = (SwipeRefreshLayout) viewInflate.findViewById(R.id.swipe_refresh);
		swipeRefresh.setClickable(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			swipeRefresh.setProgressViewOffset(false, 0,100);
		}

		q = MainApplication.getQueriesInstance(getActivity());
		swipeRefresh.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		showRefresh(false);


		showRefresh(true);
        photoList = q.getPhotos();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                setGalleries();

            }
        }, 300);
	}

	public void showRefresh(boolean show) {
		swipeRefresh.setRefreshing(show);
		swipeRefresh.setEnabled(show);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int pos, long resid) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this.getActivity(), ImageViewerActivity.class);
		i.putExtra("photoList", photoList);
		i.putExtra("index", pos);
        startActivity(i);
	}

    private void setGalleries() {

        MGListAdapter adapter = new MGListAdapter(getActivity(), photoList.size(), R.layout.gallery_entry);
        adapter.setOnMGListAdapterAdapterListener(new OnMGListAdapterAdapterListener() {

            @Override
            public void OnMGListAdapterAdapterCreated(MGListAdapter adapter, View v, int position, ViewGroup viewGroup) {
                // TODO Auto-generated method stub
                Photo p = photoList.get(position);
                String strUrl = p.getThumb_url();
                if(!strUrl.contains("http")) {
                    strUrl = "http://" + strUrl;
                }

                StaggeredImageView imgViewThumb = (StaggeredImageView) v.findViewById(R.id.imgViewThumb);
                imgViewThumb.setCornerRadius(UIConfig.BORDER_RADIUS);
                imgViewThumb.setBorderWidth(UIConfig.BORDER_WIDTH);
                imgViewThumb.setBorderColor(getResources().getColor(UIConfig.THEME_COLOR));
                imgViewThumb.setHeightRatio(1.0f);

				MainApplication.getImageLoaderInstance(getActivity())
						.displayImage(strUrl, imgViewThumb, MainApplication.getDisplayImageOptionsThumbInstance());
            }
        });

        StaggeredGridView gridView = (StaggeredGridView) viewInflate.findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(adapter);

        showRefresh(false);
    }
}

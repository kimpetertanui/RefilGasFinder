package com.projects.subdetails;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
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
import com.models.Restaurant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.projects.activities.ImageViewerActivity;
import com.apps.restaurantfinder.R;
import java.util.ArrayList;

public class SubDetailGalleryView implements OnItemClickListener {

	private View viewInflate = null;
	DisplayImageOptions options;
	private Restaurant restaurant;
	ArrayList<Photo> photoList;
	private FragmentActivity act;
	
	public SubDetailGalleryView(FragmentActivity act) {
		// TODO Auto-generated method stub
		this.act = act;
		LayoutInflater inflater = act.getLayoutInflater();
		viewInflate = inflater.inflate(R.layout.sub_detail_gallery, null, false);
	}

	public View showGalleries(Restaurant res) {
		this.restaurant = res;

        Queries q = MainApplication.getQueriesInstance(act);
        photoList = q.getPhotosByRestaurantId(restaurant.getRestaurant_id());

		MGListAdapter adapter = new MGListAdapter(act, photoList.size(), R.layout.gallery_entry);
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
				imgViewThumb.setBorderColor(act.getResources().getColor(UIConfig.THEME_COLOR));
				imgViewThumb.setHeightRatio(1.0f);

				MainApplication.getImageLoaderInstance(act)
						.displayImage(strUrl, imgViewThumb, MainApplication.getDisplayImageOptionsThumbInstance());
			}
		});

		StaggeredGridView gridView = (StaggeredGridView) viewInflate.findViewById(R.id.grid_view);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(adapter);
        return viewInflate;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int pos, long resid) {
		// TODO Auto-generated method stub
		Intent i = new Intent(act, ImageViewerActivity.class);
		i.putExtra("photoList", photoList);
		i.putExtra("index", pos);
		act.startActivity(i);
	}
	
	public View getView() {
		return viewInflate;
	}
}

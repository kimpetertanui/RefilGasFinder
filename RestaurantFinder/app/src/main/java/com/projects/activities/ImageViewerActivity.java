package com.projects.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.config.UIConfig;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.models.Photo;
import com.apps.gasfinder.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageViewerActivity extends AppCompatActivity {

	ArrayList<Photo> photos;
	ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.activity_imageviewer);
		setTitle(R.string.galleries);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		photos = (ArrayList<Photo>)this.getIntent().getSerializableExtra("photoList");
		int index = this.getIntent().getIntExtra("index", 0);

        mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(10);
		mPagerAdapter = new ScreenSlidePagerAdapter(this, photos);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(index, true);
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	public class ScreenSlidePagerAdapter extends PagerAdapter {

		private Context mContext;
		ArrayList<Photo> photos;

		public ScreenSlidePagerAdapter(Context context) {
			mContext = context;
		}

		public ScreenSlidePagerAdapter(Context context, ArrayList<Photo> photos) {
			mContext = context;
			this.photos = photos;
		}

		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View layout = inflater.inflate(R.layout.imageviewer_entry, collection, false);

			Photo p = photos.get(position);
			String strUrl = p.getPhoto_url();
			Log.e("PHOTO_URL", strUrl);

			final SubsamplingScaleImageView imgViewPhoto = (SubsamplingScaleImageView) layout.findViewById(R.id.imgViewPhoto);
			imgViewPhoto.setDebug(true);
//			imgViewPhoto.setParallelLoadingEnabled(true);
			imgViewPhoto.recycle();

			final TextView tvLoadingImage = (TextView) layout.findViewById(R.id.tvLoadingImage);
			tvLoadingImage.setVisibility(View.VISIBLE);

			final ImageView imgView = new ImageView(mContext);

			Picasso.with(ImageViewerActivity.this).load(strUrl).memoryPolicy(MemoryPolicy.NO_CACHE )
					.networkPolicy(NetworkPolicy.NO_CACHE).into(imgView, new Callback() {
				@Override
				public void onSuccess() {
					Log.e("Picasso Image", "Success");
					Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
					imgViewPhoto.setImage(ImageSource.bitmap(bitmap));
					tvLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onError() {
					Log.e("Picasso Image", "Error loading image");
					imgViewPhoto.setImage(ImageSource.resource(UIConfig.IMAGE_PLACEHOLDER));
					tvLoadingImage.setVisibility(View.GONE);
				}
			});

			collection.addView(layout);
			return layout;
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView((View) view);
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		// Handle action bar actions click
		switch (item.getItemId()) {
			default:
				finish();
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.menu_default, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
		// if nav drawer is opened, hide the action items
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onDestroy()  {
		super.onDestroy();
	}

}

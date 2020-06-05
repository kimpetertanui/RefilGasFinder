package com.projects.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.MainApplication;
import com.config.UIConfig;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.imageview.MGImageView;
import com.libraries.utilities.MGUtilities;
import com.models.Category;
import com.models.Favorite;
import com.models.Photo;
import com.models.Restaurant;
import com.apps.restaurantfinder.MainActivity;
import com.apps.restaurantfinder.R;

import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {

	public Queries q;
    Category category;
    SwipeRefreshLayout swipeRefresh;
    MGAsyncTaskNoDialog task;
    ArrayList<Restaurant> restaurants;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.fragment_list_swipe);
        setTitle(R.string.resuls);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		q = MainApplication.getQueriesInstance(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        showRefresh(true);
        category = (Category) this.getIntent().getSerializableExtra("category");
        getData();
	}

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    public void getData() {
        task = new MGAsyncTaskNoDialog(this);
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                showList();
                showRefresh(false);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(category != null)
                    restaurants = q.getRestaurantsByCategoryId(category.getCategory_id());
                else
                    restaurants = (ArrayList<Restaurant>) RestaurantActivity.this.getIntent().getSerializableExtra("searchList");
            }
        });
        task.execute();
    }

    private void showList() {
        MGRecyclerAdapter adapter = new MGRecyclerAdapter(restaurants.size(), R.layout.restaurant_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final Restaurant restaurant = restaurants.get(position);
                Photo p = q.getPhotoByRestaurantId(restaurant.getRestaurant_id());
                Favorite fave = q.getFavoriteByRestaurantId(restaurant.getRestaurant_id());

                MGImageView imgViewThumb = (MGImageView) v.view.findViewById(R.id.imgViewThumb);
                imgViewThumb.setCornerRadius(UIConfig.BORDER_RADIUS);
                imgViewThumb.setBorderWidth(UIConfig.BORDER_WIDTH);
                imgViewThumb.setBorderColor(getResources().getColor(UIConfig.THEME_COLOR));

                if(p != null) {
                    MainApplication.getImageLoaderInstance(RestaurantActivity.this)
                            .displayImage(p.getThumb_url(), imgViewThumb, MainApplication.getDisplayImageOptionsInstance());
                }
                else {
                    imgViewThumb.setImageResource(UIConfig.IMAGE_PLACEHOLDER);
                }

                ImageView imgListFave = (ImageView) v.view.findViewById(R.id.imgListFave);
                ImageView imgListFeatured = (ImageView) v.view.findViewById(R.id.imgListFeatured);

                imgListFave.setVisibility(View.INVISIBLE);
                imgListFeatured.setVisibility(View.INVISIBLE);

                if(restaurant.getFeatured() == 1)
                    imgListFeatured.setVisibility(View.VISIBLE);

                if(fave != null)
                    imgListFave.setVisibility(View.VISIBLE);

                Spanned name = Html.fromHtml(restaurant.getName());
                Spanned address = Html.fromHtml(restaurant.getAddress());

                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                tvTitle.setText(name);

                TextView tvSubtitle = (TextView) v.view.findViewById(R.id.tvSubtitle);
                tvSubtitle.setText(address);

                FrameLayout frameEntry = (FrameLayout) v.view.findViewById(R.id.frameEntry);
                frameEntry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(RestaurantActivity.this, DetailActivity.class);
                        i.putExtra("restaurant", restaurant);
                        startActivity(i);
                    }
                });
            }

        });
        mRecyclerView.setAdapter(adapter);

        if(restaurants.size() == 0) {
            MGUtilities.showNotifier(this, MainActivity.offsetY, R.string.failed_data);
            return;
        }
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

        if(task != null)
            task.cancel(true);
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



}

package com.projects.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.models.Favorite;
import com.models.Photo;
import com.models.Restaurant;
import com.projects.activities.DetailActivity;
import com.apps.restaurantfinder.MainActivity;
import com.apps.restaurantfinder.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FavoriteFragment extends Fragment {

    private View viewInflate;
    ArrayList<Restaurant> restaurants;
    MGAsyncTaskNoDialog task;
    Queries q;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public FavoriteFragment() { }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_list_swipe, null);
        return viewInflate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        restaurants = new ArrayList<Restaurant>();
        q = MainApplication.getQueriesInstance(getContext());

        mRecyclerView = (RecyclerView) viewInflate.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefresh = (SwipeRefreshLayout) viewInflate.findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        showRefresh(false);
        getData();
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    public void getData() {
        task = new MGAsyncTaskNoDialog(getActivity());
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
                restaurants = q.getFavoriteRestaurants();
                if (MainApplication.currentLocation != null) {
                    for (Restaurant restaurant : restaurants) {
                        Location locStore = new Location("Store");
                        locStore.setLatitude(restaurant.getLat());
                        locStore.setLongitude(restaurant.getLon());
                        double userDistanceFromStore = MainApplication.currentLocation.distanceTo(locStore) / 1000;
                        restaurant.setDistance(userDistanceFromStore);
                    }

                    Collections.sort(restaurants, new Comparator<Restaurant>() {
                        @Override
                        public int compare(Restaurant store, Restaurant t1) {
                            if (store.getDistance() < t1.getDistance())
                                return -1;
                            if (store.getDistance() > t1.getDistance())
                                return 1;
                            return 0;
                        }
                    });
                }
            }
        });
        task.execute();
    }

    @Override
    public void onDestroyView()  {
        super.onDestroyView();
        if (viewInflate != null) {
            ViewGroup parentViewGroup = (ViewGroup) viewInflate.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }

        if(task != null)
            task.cancel(true);
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
                    MainApplication.getImageLoaderInstance(getActivity())
                            .displayImage(p.getThumb_url(), imgViewThumb, MainApplication.getDisplayImageOptionsThumbInstance());
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
                frameEntry.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        i.putExtra("restaurant", restaurant);
                        startActivity(i);
                    }
                });
            }

        });
        mRecyclerView.setAdapter(adapter);

        if(restaurants.size() == 0) {
            MGUtilities.showNotifier(this.getActivity(), MainActivity.offsetY, R.string.failed_data);
            return;
        }
    }
}

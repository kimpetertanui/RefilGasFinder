package com.projects.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.MainApplication;
import com.config.Config;
import com.config.UIConfig;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.location.MGLocationManagerUtils;
import com.libraries.slider.MGSlider;
import com.libraries.slider.MGSlider.OnMGSliderListener;
import com.libraries.slider.MGSliderAdapter;
import com.libraries.slider.MGSliderAdapter.OnMGSliderAdapterListener;
import com.libraries.usersession.UserAccessSession;
import com.libraries.utilities.MGUtilities;
import com.models.Category;
import com.models.Data;
import com.models.Photo;
import com.models.Restaurant;
import com.projects.activities.DetailActivity;
import com.projects.activities.RestaurantActivity;
import com.apps.restaurantfinder.MainActivity;
import com.apps.restaurantfinder.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment implements MainApplication.OnLocationListener {

    private View viewInflate;
    ArrayList<Restaurant> restaurants;
    ArrayList<Category> categories;
    MGAsyncTaskNoDialog task;
    Queries q;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    MGSlider slider;

    public HomeFragment() { }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_home, null);
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
        categories = new ArrayList<Category>();

        slider = (MGSlider) viewInflate.findViewById(R.id.slider);
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

        if(!MGUtilities.isLocationEnabled(getActivity()) && MainApplication.currentLocation == null) {
            MGLocationManagerUtils utils = new MGLocationManagerUtils();
            utils.setOnAlertListener(new MGLocationManagerUtils.OnAlertListener() {
                @Override
                public void onPositiveTapped() {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Config.PERMISSION_REQUEST_LOCATION_SETTINGS);
                }

                @Override
                public void onNegativeTapped() {

                }
            });
            utils.showAlertView(
                    getActivity(),
                    R.string.location_error,
                    R.string.gps_not_on,
                    R.string.go_to_settings,
                    R.string.cancel);
        }
        else {
            refetch();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.PERMISSION_REQUEST_LOCATION_SETTINGS) {
            if(MGUtilities.isLocationEnabled(getActivity()))
                refetch();
            else
                Toast.makeText(getActivity(), R.string.location_error_not_turned_on, Toast.LENGTH_LONG).show();
        }
    }

    public void refetch() {
        showRefresh(true);
        MainApplication app = (MainApplication) getActivity().getApplication();
        app.setOnLocationListener(this, getActivity());
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
                if(!MGUtilities.hasConnection(getActivity())) {
                    restaurants = q.getFeaturedRestaurants();
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
                    categories = q.getCategories();
                }

                createSlider();
                showList();
                showRefresh(false);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if( MGUtilities.hasConnection(getActivity()) && MainApplication.currentLocation != null) {
                    try {
                        String strUrl = "";
                        UserAccessSession accessSession = UserAccessSession.getInstance(getActivity());
                        float radius = accessSession.getFilterDistance();
                        Location loc = MainApplication.currentLocation;
                        if(accessSession.getFilterDistance() == 0) {
                            strUrl = String.format("%s?api_key=%s&lat=%f&lon=%f&radius=%f&default_to_find_distance=%d&get_categories=1",
                                    Config.GET_DATA_URL,
                                    Config.API_KEY,
                                    loc.getLatitude(),
                                    loc.getLongitude(),
                                    radius,
                                    Config.DEFAULT_RESTAURANTS_COUNT_TO_FIND_DISTANCE);
                        }
                        else {
                            strUrl = String.format("%s?api_key=%s&lat=%f&lon=%f&radius=%f&default_to_find_distance=%d&get_categories=1",
                                    Config.GET_DATA_URL,
                                    Config.API_KEY,
                                    loc.getLatitude(),
                                    loc.getLongitude(),
                                    radius,
                                    Config.DEFAULT_RESTAURANTS_COUNT_TO_FIND_DISTANCE);
                        }

                        Log.e("URL", strUrl);
                        DataParser parser = new DataParser();
                        Data data = parser.getData(strUrl);
                        if (data == null)
                            return;

                        if(data.getMax_distance() > 0) {
                            UserAccessSession.getInstance(getActivity()).setFilterDistanceMax(data.getMax_distance());
                        }

                        if(Config.AUTO_ADJUST_DISTANCE) {
                            if(UserAccessSession.getInstance(getActivity()).getFilterDistance() == 0) {
                                UserAccessSession.getInstance(getActivity()).setFilterDistance(data.getDefault_distance());
                            }
                        }

                        if (data.getRestaurants() != null && data.getRestaurants().size() > 0) {
                            q.deleteTable("restaurants");
                            for(Restaurant restaurant : data.getRestaurants()) {
                                q.deleteRestaurant(restaurant.getRestaurant_id());
                                q.insertRestaurant(restaurant);
                                restaurants.add(restaurant);

                                if(restaurant.getPhotos() != null) {
                                    for(Photo photo : restaurant.getPhotos()) {
                                        q.deletePhoto(photo.getPhoto_id());
                                        q.insertPhoto(photo);
                                    }
                                }
                            }
                        }

                        if (data.getCategories() != null && data.getCategories().size() > 0) {
                            q.deleteTable("categories");
                            for(Category category : data.getCategories()) {
                                q.deleteCategory(category.getCategory_id());
                                q.insertCategory(category);
                                categories.add(category);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                slider.pauseSliderAnimation();
                parentViewGroup.removeAllViews();
            }
        }

        if(task != null)
            task.cancel(true);
    }

    private void showList() {
        MGRecyclerAdapter adapter = new MGRecyclerAdapter(categories.size(), R.layout.category_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {

                final Category category = categories.get(position);
                Spanned name = Html.fromHtml(category.getCategory());
                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                tvTitle.setText(name);

                FrameLayout frameCategory = (FrameLayout) v.view.findViewById(R.id.frameCategory);
                frameCategory.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slider.stopSliderAnimation();
                        Intent i = new Intent(getActivity(), RestaurantActivity.class);
                        i.putExtra("category", category);
                        startActivity(i);
                    }
                });
            }

        });
        mRecyclerView.setAdapter(adapter);
    }

    // Create Slider
    private void createSlider() {
        if(restaurants != null && restaurants.size() == 0 && categories != null && categories.size() == 0) {
            MGUtilities.showNotifier(this.getActivity(), MainActivity.offsetY, R.string.failed_data);
            return;
        }

        slider.setMaxSliderThumb(restaurants.size());
        MGSliderAdapter adapter = new MGSliderAdapter(R.layout.slider_entry, restaurants.size(), restaurants.size());
        adapter.setOnMGSliderAdapterListener(new OnMGSliderAdapterListener() {

            @Override
            public void onOnMGSliderAdapterCreated(MGSliderAdapter adapter, View v,
                                                   int position) {
                // TODO Auto-generated method stub
                final Restaurant entry = restaurants.get(position);
                Photo p = q.getPhotoByRestaurantId(entry.getRestaurant_id());
                ImageView imageViewSlider = (ImageView) v.findViewById(R.id.imageViewSlider);
                if(p != null) {
                    MainApplication.getImageLoaderInstance(getActivity())
                            .displayImage(p.getPhoto_url(), imageViewSlider, MainApplication.getDisplayImageOptionsInstance());
                }
                else {
                    imageViewSlider.setImageResource(UIConfig.IMAGE_PLACEHOLDER);
                }

                imageViewSlider.setTag(position);
                imageViewSlider.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        i.putExtra("restaurant", entry);
                        startActivity(i);
                    }
                });

                Spanned name = Html.fromHtml(entry.getName());
                name = Html.fromHtml(name.toString());

                Spanned address = Html.fromHtml(entry.getAddress());
                address = Html.fromHtml(address.toString());

                TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvTitle.setText(name);

                TextView tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
                tvSubtitle.setText(address);
            }
        });

        slider.setOnMGSliderListener(new OnMGSliderListener() {

            @Override
            public void onItemThumbSelected(MGSlider slider, ImageView[] buttonPoint, ImageView imgView, int pos) { }

            @Override
            public void onItemThumbCreated(MGSlider slider, ImageView imgView, int pos) { }


            @Override
            public void onItemPageScrolled(MGSlider slider, ImageView[] buttonPoint, int pos) { }

            @Override
            public void onItemMGSliderToView(MGSlider slider, int pos) { }

            @Override
            public void onItemMGSliderViewClick(AdapterView<?> adapterView, View v, int pos, long resid) { }

            @Override
            public void onAllItemThumbCreated(MGSlider slider, LinearLayout linearLayout) { }

        });
        slider.setOffscreenPageLimit(restaurants.size() - 1);
        slider.setAdapter(adapter);
        slider.setActivity(this.getActivity());
        slider.setSliderAnimation(5000);
        slider.resumeSliderAnimation();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        slider.resumeSliderAnimation();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        slider.pauseSliderAnimation();
    }

    @Override
    public void onLocationChanged(Location prevLoc, Location currentLoc) {
        MainApplication app = (MainApplication) getActivity().getApplication();
        app.setOnLocationListener(null, getActivity());
        getData();
    }

    @Override
    public void onLocationRequestDenied() {
        showRefresh(false);
        MGUtilities.showAlertView(
                getActivity(),
                R.string.permission_error,
                R.string.permission_error_details_location);
    }
}

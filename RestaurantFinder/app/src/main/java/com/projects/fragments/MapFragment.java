package com.projects.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.application.MainApplication;
import com.config.Config;
import com.db.Queries;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.directions.GMapV2Direction;
import com.libraries.utilities.MGUtilities;
import com.models.Restaurant;
import com.projects.activities.DetailActivity;
import com.apps.restaurantfinder.MainActivity;
import com.apps.restaurantfinder.R;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mg on 27/07/16.
 */
public class MapFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private View viewInflate;
    private GoogleMap googleMap;
    SwipeRefreshLayout swipeRefresh;
    private GMapV2Direction gMapV2;
    private ArrayList<Restaurant> restaurants;
    private HashMap<String, Restaurant> markers;
    private ArrayList<Marker> markerList;
    private Restaurant selecteRestaurant;

    Queries q;
    MGAsyncTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_map, null);
        return viewInflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(task != null)
            task.cancel(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
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

        Button btnAllPins = (Button)viewInflate.findViewById(R.id.btnAllPins);
        btnAllPins.setOnClickListener(this);

        Button btnCurrentLocation = (Button)viewInflate.findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(this);

        Button btnRoute = (Button)viewInflate.findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(this);

        showRefresh(true);

        restaurants = new ArrayList<Restaurant>();
        markers = new HashMap<String, Restaurant>();
        markerList = new ArrayList<Marker>();

        FragmentManager fManager = getActivity().getSupportFragmentManager();
        SupportMapFragment supportMapFragment = ((SupportMapFragment) fManager.findFragmentById(R.id.googleMap));
        if(supportMapFragment == null) {
            fManager = getChildFragmentManager();
            supportMapFragment = ((SupportMapFragment) fManager.findFragmentById(R.id.googleMap));
        }
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        googleMap = _googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        try {
            googleMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e) { }

        googleMap.setOnMapClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }
        });

        gMapV2 = new GMapV2Direction();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, Config.LOADING_DELAY_IN_MILLIS);
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO Auto-generated method stub
        selecteRestaurant = markers.get(marker.getId());
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra("restaurant", selecteRestaurant);
        startActivity(i);
    }

    @Override
    public void onMapClick(LatLng point) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()) {
            case R.id.btnAllPins:
                addStoreMarkers();
                break;
            case R.id.btnRoute:
                getDirections();
                break;
            case R.id.btnCurrentLocation:
                getMyLocation();
                break;
        }
    }


    public void addStoreMarkers() {
        if(googleMap != null)
            googleMap.clear();

        try {
            markers.clear();
            markerList.clear();
            for(Restaurant entry: restaurants) {
                if(entry.getLat() == 0 || entry.getLon() == 0)
                    continue;

                Marker mark = createMarker(entry);
                markerList.add(mark);
                markers.put(mark.getId(), entry);
            }
            showBoundedMap();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getDirections() {
        if(selecteRestaurant == null) {
            Toast.makeText(getActivity(), R.string.select_one_restaurant, Toast.LENGTH_SHORT).show();
            return;
        }

        MGAsyncTask asyncTask = new MGAsyncTask(getActivity());
        asyncTask.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            private ArrayList<ArrayList<LatLng>> allDirections;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                allDirections = new ArrayList<ArrayList<LatLng>>();
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                for(ArrayList<LatLng> directions : allDirections) {
                    PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
                    for(LatLng latLng : directions) {
                        rectLine.add(latLng);
                    }
                    googleMap.addPolyline(rectLine);
                }

                if(allDirections.size() <= 1) {
                    Toast.makeText(getActivity(), R.string.cannot_determine_direction, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                Location location = MainApplication.currentLocation;
                if(location != null && selecteRestaurant != null) {
                    LatLng marker1 = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng marker2 = new LatLng(selecteRestaurant.getLat(), selecteRestaurant.getLon());
                    Document doc = gMapV2.getDocument1(marker1, marker2, GMapV2Direction.MODE_DRIVING);
                    ArrayList<LatLng> directionPoint = gMapV2.getDirection(doc);
                    allDirections.add(directionPoint);
                }
            }
        });
        asyncTask.startAsyncTask();
    }

    private void getMyLocation() {
        Location location = MainApplication.currentLocation;
        if(location == null) {
            MGUtilities.showAlertView(
                    getActivity(),
                    R.string.location_error,
                    R.string.cannot_determine_location);

            return;
        }

        addStoreMarkers();
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_LEVEL);
        googleMap.moveCamera(zoom);
        CameraUpdate center = CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude()));

        googleMap.animateCamera(center);
    }



    private void showBoundedMap() {
        if(markerList == null && markerList.size() == 0 ) {
            MGUtilities.showNotifier(this.getActivity(), MainActivity.offsetY, R.string.failed_data);
            return;
        }

        if(markerList.size() > 0) {
            LatLngBounds.Builder bld = new LatLngBounds.Builder();
            for (int i = 0; i < markerList.size(); i++) {
                Marker marker = markerList.get(i);
                bld.include(marker.getPosition());
            }

            LatLngBounds bounds = bld.build();
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds,
                            this.getResources().getDisplayMetrics().widthPixels,
                            this.getResources().getDisplayMetrics().heightPixels,
                            70));
        }
        else {
            MGUtilities.showNotifier(this.getActivity(), MainActivity.offsetY, R.string.no_results_found);
            Location loc = MainApplication.currentLocation;
            if(loc != null) {
                googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 70));
            }
        }
    }

    private Marker createMarker(Restaurant restaurant) {
        final MarkerOptions markerOptions = new MarkerOptions();
        Spanned name = Html.fromHtml(restaurant.getName());
        name = Html.fromHtml(name.toString());
        Spanned storeAddress = Html.fromHtml(restaurant.getAddress());
        storeAddress = Html.fromHtml(storeAddress.toString());
        markerOptions.title( name.toString() );

        String address = storeAddress.toString();
        if(address.length() > 50)
            address = storeAddress.toString().substring(0,  50) + "...";

        markerOptions.snippet(address);
        markerOptions.position(new LatLng(restaurant.getLat(), restaurant.getLon()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));

        Marker mark = googleMap.addMarker(markerOptions);
        mark.setInfoWindowAnchor(Config.MAP_INFO_WINDOW_X_OFFSET, 0);
        return mark;
    }

    public void getData() {
        showRefresh(true);
        task = new MGAsyncTask(getActivity());
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.hide();
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                showRefresh(false);
                addStoreMarkers();
                showBoundedMap();
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                restaurants = q.getRestaurants();
            }
        });
        task.execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selecteRestaurant = markers.get(marker.getId());
        if(MainApplication.currentLocation != null) {
            Location loc = new Location("marker");
            loc.setLatitude(marker.getPosition().latitude);
            loc.setLongitude(marker.getPosition().longitude);

            double meters = MainApplication.currentLocation.distanceTo(loc);
            double miles = meters * 0.000621371f;
            String str = String.format("%.1f %s",
                    miles,
                    MGUtilities.getStringFromResource(getActivity(), R.string.km));

            TextView tvDistance = (TextView) viewInflate.findViewById(R.id.tvDistance);
            tvDistance.setText(str);
        }
        return false;
    }
}

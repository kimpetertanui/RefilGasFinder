package com.projects.subdetails;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.config.Config;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.models.Restaurant;
import com.apps.restaurantfinder.R;

public class SubDetailMapView implements OnMapReadyCallback {

	private View viewInflate = null;
	private SupportMapFragment mapFragment;
	private GoogleMap googleMap;
	private FragmentActivity act;
	Restaurant restaurant;
	
	public SubDetailMapView(FragmentActivity act) {
		// TODO Auto-generated method stub
		this.act = act;
		LayoutInflater inflater = act.getLayoutInflater();
		viewInflate = inflater.inflate(R.layout.sub_detail_map, null, false);
		setMap();
	}
	
	private void setMap() {
		mapFragment = new SupportMapFragment();
		FragmentTransaction fragmentTransaction = act.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameMapSubDetail, mapFragment);
        fragmentTransaction.commit();
	}

	public View loadMap(Restaurant restaurant) {

		this.restaurant = restaurant;
		mapFragment.getMapAsync(this);
		return viewInflate;
	}

	@Override
	public void onMapReady(GoogleMap _googleMap) {
		googleMap = _googleMap;
		googleMap.getUiSettings().setAllGesturesEnabled(false);

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(restaurant.getName());
		markerOptions.snippet(restaurant.getAddress());
		markerOptions.position(new LatLng(restaurant.getLat(), restaurant.getLon() ));

		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));
		Marker mark = googleMap.addMarker(markerOptions);
		mark.showInfoWindow();

		CameraUpdate zoom = CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_LEVEL);
		googleMap.moveCamera(zoom);
		CameraUpdate center = CameraUpdateFactory.newLatLng(markerOptions.getPosition());
		googleMap.animateCamera(center);
	}

	
	public View getView() {
		return viewInflate;
	}
}

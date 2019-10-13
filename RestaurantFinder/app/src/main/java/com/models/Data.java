package com.models;

import java.util.ArrayList;

public class Data {
	
	private ArrayList<Restaurant> restaurants;
	private ArrayList<Category> categories;

	private float max_distance;
	private float default_distance;

	public ArrayList<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(ArrayList<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public float getMax_distance() {
		return max_distance;
	}

	public void setMax_distance(float max_distance) {
		this.max_distance = max_distance;
	}

	public float getDefault_distance() {
		return default_distance;
	}

	public void setDefault_distance(float default_distance) {
		this.default_distance = default_distance;
	}
}

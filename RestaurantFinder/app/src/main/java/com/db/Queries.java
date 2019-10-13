package com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.models.Category;
import com.models.Favorite;
import com.models.Photo;
import com.models.Restaurant;

import java.util.ArrayList;

public class Queries {
	
	private SQLiteDatabase db;
	private DbHelper dbHelper;

	public Queries(SQLiteDatabase db, DbHelper dbHelper) {
		this.db = db;
		this.dbHelper = dbHelper;
	}
	
	public void deleteTable(String tableName) {
		db = dbHelper.getWritableDatabase();
		try{
			db.delete(tableName, null, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void insertRestaurant(Restaurant entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("address", entry.getAddress());
		values.put("amenities", entry.getAmenities());
		values.put("created_at", entry.getCreated_at());
		values.put("desc", entry.getDesc1());
		values.put("email", entry.getEmail());
		values.put("featured", entry.getFeatured());
		values.put("hours", entry.getHours());
		values.put("lat", entry.getLat());
		values.put("lon", entry.getLon());
		values.put("name", entry.getName());
		values.put("phone", entry.getPhone());
		values.put("website", entry.getWebsite());
		values.put("category_id", entry.getCategory_id());
		values.put("food_rating", entry.getFood_rating());
		values.put("price_rating", entry.getPrice_rating());
		values.put("restaurant_id", entry.getRestaurant_id());
		values.put("distance", entry.getDistance());

        db.insert("restaurants", null, values);
	}
	
	public void insertFavorite(int restaurantId) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("restaurant_id", restaurantId);
        db.insert("favorites", null, values);
	}

	public void deleteRestaurant(int restaurant_id) {
		db = dbHelper.getWritableDatabase();
		try{
			db.delete("restaurants", "restaurant_id = " + restaurant_id, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void deletePhoto(int photo_id) {
		db = dbHelper.getWritableDatabase();
		try{
			db.delete("photos", "photo_id = " + photo_id, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteCategory(int category_id) {
		db = dbHelper.getWritableDatabase();
		try{
			db.delete("categories", "category_id = " + category_id, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteFavorite(int favoriteId) {
		db = dbHelper.getWritableDatabase();
		try{
			db.delete("favorites", "favorite_id = " + favoriteId, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertPhoto(Photo entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("created_at", entry.getCreated_at());
		values.put("photo_url", entry.getPhoto_url());
		values.put("thumb_url", entry.getThumb_url());
		values.put("photo_id", entry.getPhoto_id());
		values.put("restaurant_id", entry.getRestaurant_id());
        db.insert("photos", null, values);
	}
	
	public void insertCategory(Category entry) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("category", entry.getCategory());
		values.put("created_at", entry.getCreated_at());
		values.put("category_id", entry.getCategory_id());
        db.insert("categories", null, values);
	}
	
	public Category getCategoryByCategoryId(int categoryId) {
		Category entry = null;
		String sql = String.format("SELECT * FROM categories WHERE category_id = %d", categoryId);
		ArrayList<Category> list = new ArrayList<Category>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				entry = formatCategory(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}
	
	public Category getCategoryByCategoryName(String category) {
		Category entry = null;
		String sql = String.format("SELECT * FROM categories WHERE category = '%s'", category);
		ArrayList<Category> list = new ArrayList<Category>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				entry = formatCategory(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}

	public ArrayList<Category> getCategories() {
		ArrayList<Category> list = new ArrayList<Category>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM categories ORDER BY category ASC", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				Category entry = formatCategory(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}
	
	private ArrayList<Restaurant> getRestaurantsUsingSQL(String sql) {
		ArrayList<Restaurant> list = new ArrayList<Restaurant>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				Restaurant entry = formatRestaurant(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Restaurant> getFeaturedRestaurants() {
		String sql = String.format("SELECT * FROM restaurants WHERE featured = 1 ORDER BY name ASC");
		ArrayList<Restaurant> list = getRestaurantsUsingSQL(sql);
		return list;
	}

	public ArrayList<Restaurant> getRestaurants() {
		ArrayList<Restaurant> list = getRestaurantsUsingSQL("SELECT * FROM restaurants ORDER BY name ASC");
		return list;
	}
	
	public ArrayList<Restaurant> getRestaurantsByCategoryId(int categoryId) {
		String sql = String.format("SELECT * FROM restaurants WHERE category_id = %d ORDER BY name ASC", categoryId);
		ArrayList<Restaurant> list = getRestaurantsUsingSQL(sql);
		return list;
	}
	
	public Restaurant getRestaurantByRestaurantId(int restaurantId) {
		String sql = String.format("SELECT * FROM restaurants WHERE restaurant_id = %d ORDER BY name ASC", restaurantId);
		ArrayList<Restaurant> list = getRestaurantsUsingSQL(sql);
		return list.size() == 0 ? null : list.get(0);
	}
	
	
	private ArrayList<Photo> getPhotosBySQL(String sql) {
		ArrayList<Photo> list = new ArrayList<Photo>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql, null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				Photo entry = formatPhoto(mCursor);
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Photo> getPhotos() {
		return getPhotosBySQL("SELECT * FROM photos");
	}
	
	public ArrayList<Photo> getPhotosByRestaurantId(int restaurantId) {
		String sql = String.format("SELECT * FROM photos WHERE restaurant_id = %d", restaurantId);
		return getPhotosBySQL(sql);
	}

	public Photo getPhotoByPhotoId(int photoId) {
		String sql = String.format("SELECT * FROM photos WHERE photo_id = %d", photoId);
		ArrayList<Photo> photos = getPhotosBySQL(sql);
		return photos.size() == 0 ? null : photos.get(0);
	}
	
	public Photo getPhotoByRestaurantId(int restaurantId) {
		Photo entry = null;
		String sql = String.format("SELECT * FROM photos WHERE restaurant_id = %d", restaurantId);
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql , null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				entry = formatPhoto(mCursor);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}
	
	public ArrayList<Favorite> getFavorites() {
		ArrayList<Favorite> list = new ArrayList<Favorite>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM favorites", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				Favorite entry = new Favorite();
				entry.favorite_id = mCursor.getInt(mCursor.getColumnIndex("favorite_id"));
				entry.restaurant_id = mCursor.getInt( mCursor.getColumnIndex("restaurant_id") );
				list.add(entry);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Restaurant> getFavoriteRestaurants() {
		String sql = String.format("SELECT * FROM restaurants INNER JOIN favorites ON restaurants.restaurant_id = favorites.restaurant_id ORDER BY name ASC");
		ArrayList<Restaurant> list = getRestaurantsUsingSQL(sql);
		return list;
	}
	
	public Restaurant getFavoriteRestaurantsByRestaurantId(int restaurantId) {
		String sql = String.format(
				"SELECT * FROM restaurants INNER JOIN favorites ON " 
						+ "restaurants.restaurant_id = favorites.restaurant_id " 
						+ "WHERE restaurants.restaurant_id = %d", restaurantId);
		
		ArrayList<Restaurant> list = getRestaurantsUsingSQL(sql);
		return list.size() == 0 ? null : list.get(0);
	}

	public Favorite getFavoriteByRestaurantId(int restaurantId) {
		Favorite entry = null;
		String sql = String.format("SELECT * FROM favorites WHERE restaurant_id = %d", restaurantId);
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery(sql , null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				entry = new Favorite();
				entry.favorite_id = mCursor.getInt( mCursor.getColumnIndex("favorite_id") );
				entry.restaurant_id = mCursor.getInt( mCursor.getColumnIndex("restaurant_id") );
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return entry;
	}
	
	public ArrayList<String> getCategoryNames() {
		ArrayList<String> list = new ArrayList<String>();
		db = dbHelper.getReadableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM categories", null);
		mCursor.moveToFirst();
		if (!mCursor.isAfterLast()) {
			do {
				String cat = mCursor.getString( mCursor.getColumnIndex("category") );
				list.add(cat);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public void closeDatabase(){
		db.close();
	}

	public Restaurant formatRestaurant(Cursor mCursor) {
		Restaurant entry = new Restaurant();
		entry.setAddress(mCursor.getString( mCursor.getColumnIndex("address") ));
		entry.setAmenities(mCursor.getString( mCursor.getColumnIndex("amenities") ));
		entry.setCategory_id(mCursor.getInt( mCursor.getColumnIndex("category_id") ));
		entry.setCreated_at(mCursor.getInt( mCursor.getColumnIndex("created_at") ));
		entry.setDesc1(mCursor.getString( mCursor.getColumnIndex("desc") ));
		entry.setEmail(mCursor.getString( mCursor.getColumnIndex("email") ));
		entry.setFeatured(mCursor.getInt( mCursor.getColumnIndex("featured") ));
		entry.setFood_rating(mCursor.getDouble( mCursor.getColumnIndex("food_rating") ));
		entry.setHours(mCursor.getString( mCursor.getColumnIndex("hours") ));
		entry.setLat(mCursor.getDouble( mCursor.getColumnIndex("lat") ));
		entry.setLon(mCursor.getDouble( mCursor.getColumnIndex("lon") ));
		entry.setName(mCursor.getString( mCursor.getColumnIndex("name") ));
		entry.setPhone(mCursor.getString( mCursor.getColumnIndex("phone") ));
		entry.setPrice_rating(mCursor.getDouble( mCursor.getColumnIndex("price_rating") ));
		entry.setRestaurant_id(mCursor.getInt( mCursor.getColumnIndex("restaurant_id") ));
		entry.setWebsite(mCursor.getString( mCursor.getColumnIndex("website") ));
		entry.setDistance(mCursor.getDouble( mCursor.getColumnIndex("distance") ));
		return  entry;
	}

	public Photo formatPhoto(Cursor mCursor) {
		Photo entry = new Photo();
		entry.setCreated_at(mCursor.getInt( mCursor.getColumnIndex("created_at") ));
		entry.setPhoto_id(mCursor.getInt( mCursor.getColumnIndex("photo_id") ));
		entry.setPhoto_url(mCursor.getString( mCursor.getColumnIndex("photo_url") ));
		entry.setRestaurant_id(mCursor.getInt( mCursor.getColumnIndex("restaurant_id") ));
		entry.setThumb_url(mCursor.getString( mCursor.getColumnIndex("thumb_url") ));
		return entry;
	}

	public Category formatCategory(Cursor mCursor) {
		Category entry = new Category();
		entry.setCategory(mCursor.getString( mCursor.getColumnIndex("category") ));
		entry.setCategory_id(mCursor.getInt( mCursor.getColumnIndex("category_id") ));
		entry.setCreated_at(mCursor.getInt( mCursor.getColumnIndex("created_at") ));
		return entry;
	}
}

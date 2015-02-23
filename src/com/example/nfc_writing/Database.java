package com.example.nfc_writing;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, "user.db", null, 1);

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String query;
		query = "CREATE TABLE WorkFlow (nombre TEXT, tipo TEXT, sincro INT)";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String query ="DROP TABLE IF EXISTS WorkFlow";
		db.execSQL(query);
		onCreate(db);
	}
	/**
	 * Inserts User into SQLite DB
	 * @param queryValues
	 */
	public void insert(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("nombre", queryValues.get("nombre"));
		values.put("tipo", queryValues.get("tipo"));
		values.put("sincro", queryValues.get("sincro"));
		database.insert("WorkFlow", null, values);
		database.close();
	}
	/**
	 * Get list of data from SQLite DB as Array List
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getAllData() {
		ArrayList<HashMap<String, String>> usersList;
		usersList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM users";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("nombre", cursor.getString(0));
				map.put("tipo", cursor.getString(1));
				map.put("sincro", cursor.getString(2));
				usersList.add(map);
			} while (cursor.moveToNext());
		}
		database.close();
		return usersList;
	}



}

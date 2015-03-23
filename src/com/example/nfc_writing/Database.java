package com.example.nfc_writing;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
		query ="CREATE TABLE Objetos (nombre TEXT)";
		db.execSQL(query);
		query ="CREATE TABLE Interaccion (nombre TEXT)";
		db.execSQL(query);
		query ="CREATE TABLE Relacion (nombre TEXT)";
		db.execSQL(query);
		query ="CREATE TABLE RCruzadas (relacion TEXT, objeto TEXT,interaccion TEXT)";
		db.execSQL(query);
		query ="CREATE TABLE Log (relacion TEXT, objetoPadre TEXT,objeto TEXT,interaccion TEXT,tiempo TEXT,sincro INTEGER)";
		db.execSQL(query);
		query="CREATE TABLE Conf_spinner (relacion TEXT, interaccion TEXT,PRIMARY KEY(interaccion))";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String query;
		query ="DROP TABLE IF EXISTS Objetos";
		db.execSQL(query);
		query ="DROP TABLE IF EXISTS Interaccion";
		db.execSQL(query);
		query ="DROP TABLE IF EXISTS Relacion";
		db.execSQL(query);
		query ="DROP TABLE IF EXISTS RCruzadas";
		db.execSQL(query);
		query ="DROP TABLE IF EXISTS Log";
		db.execSQL(query);
		query="DROP TABLE IF EXIST Conf_spinner";
		db.execSQL(query);
		onCreate(db);
	}
	/*
	 * Inserts Data into SQLite DB
	 */
	public void insert(HashMap<String, String> queryValues,String table) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		switch (table) {
		case "Objetos":
			values.put("nombre",queryValues.get("objeto"));
			database.insert("Objetos", null, values);
			database.close();
			break;
		case "Interaccion":
			values.put("nombre",queryValues.get("interaccion"));
			database.insert("Interaccion", null, values);
			database.close();
			break;
		case "Relacion":
			values.put("nombre",queryValues.get("relacion"));
			database.insert("Relacion", null, values);
			database.close();
			break;
		case "RCruzadas":
			values.put("relacion", queryValues.get("relacion"));
			values.put("objeto", queryValues.get("objeto"));
			values.put("interaccion", queryValues.get("interaccion"));
			database.insert("RCruzadas", null, values);
			database.close();
			break;
		case "Log":
			values.put("relacion", queryValues.get("relacion"));
			values.put("objetoPadre", queryValues.get("objetoPadre"));
			values.put("objeto", queryValues.get("objeto"));
			values.put("interaccion", queryValues.get("interaccion"));
			values.put("tiempo", queryValues.get("tiempo"));
			values.put("sincro", queryValues.get("sincro"));
			database.insert("Log", null, values);	
			database.close();
			break;
		case "Conf_spinner":
			values.put("relacion",queryValues.get("relacion"));
			values.put("interaccion",queryValues.get("interaccion"));
			database.insert("Conf_spinner", null, values);
			database.close();
			break;

		default:
			database.close();
			break;
		}
	}
	/*
	 * Delete data
	 */
	public void delete(HashMap<String, String> queryValues,String table) {
		SQLiteDatabase database = this.getWritableDatabase();
		switch (table) {
		case "Conf_spinner":		
			String query;
			query = "DELETE FROM Conf_spinner WHERE interaccion='"+queryValues.get("interaccion")+"'";
			database.execSQL(query);
			database.close();
			break;
		default:
			database.close();
			break;
		}

	}
	/*
	 * Get list of data from SQLite DB as Array List
	 */
	public ArrayList<HashMap<String, String>> getAllData() {
		ArrayList<HashMap<String, String>> usersList;
		usersList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM Log";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("relacion", cursor.getString(0));
				map.put("objetoPadre", cursor.getString(1));
				map.put("objeto", cursor.getString(2));
				map.put("interaccion", cursor.getString(3));
				map.put("tiempo", cursor.getString(4));
				map.put("sincro", cursor.getString(5));
				usersList.add(map);
			} while (cursor.moveToNext());
		}
		database.close();
		return usersList;
	}
	/*
	 * Compose JSON out of SQLite records
	 */
	public String composeJSONfromSQLite(){
		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM Log where sincro ='0'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("relacion", cursor.getString(0));
				map.put("objetoPadre", cursor.getString(1));
				map.put("objeto", cursor.getString(2));
				map.put("interaccion", cursor.getString(3));
				map.put("tiempo", cursor.getString(4));
				map.put("sincro", "1");
				wordList.add(map);
			} while (cursor.moveToNext());
		}
		database.close();
		Gson gson = new GsonBuilder().create();
		//Use GSON to serialize Array List to JSON
		return gson.toJson(wordList);
	}
	public int dbSyncCount(){
		int count = 0;
		String selectQuery = "SELECT * FROM Log where sincro ='0' ";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		count = cursor.getCount();
		database.close();
		return count;
	}

	/*
	 * Update Sync status against 
	 */
	public void updateSyncStatus(String relacion, String objetoPadre, String objeto, String interaccion, String tiempo){
		SQLiteDatabase database = this.getWritableDatabase();    

		String query = new String("Update Log set sincro = '1' where relacion='"+relacion+"'AND objetoPadre='"+objetoPadre+"'" +
				"AND objeto='"+objeto+"'AND interaccion='"+interaccion+"'AND tiempo='"+tiempo+"'");
		//String updateQuery = "Update users set sincro = '"+ status +"' where userId="+"'"+ id +"'";
		Log.d("query",query);        
		database.execSQL(query);
		database.close();
	}
}





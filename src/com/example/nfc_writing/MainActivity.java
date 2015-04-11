package com.example.nfc_writing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class MainActivity extends ActionBarActivity {

	private ProgressDialog prgDialog;
	private HashMap<String, String> queryValues;
	private Database mydatabase;
	private GoogleCloudMessaging gcmObj;
	private Context applicationContext;
	private String regId = "";
	private static final String REG_ID = "regId";
	private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button readButton = (Button)findViewById(R.id.ReadTag);
		final Button writeButton = (Button)findViewById(R.id.WriteTag);
		final Button quitButton = (Button)findViewById(R.id.Quit);
		final Button createIdentifier = (Button)findViewById(R.id.CreateIdentifier);
		prgDialog = new ProgressDialog(this);
		prgDialog.setMessage("Transferring Data between Remote MySQL DB and Squilite mobile phone DB.Please wait...");
		prgDialog.setCancelable(false);
		mydatabase =  new Database(this, "DB", null, 1);

		initGCM();//Initialize Service of Google Cloud Messaging


		/*NfcAdapter mNfcAdapter=NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) 
		{ 
			// Stop here, we definitely need NFC
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;
		} 
		else
		{
			Toast.makeText(this, "This device support NFC.", Toast.LENGTH_LONG).show();

		}*/

		/*
		 * Button options
		 */

		writeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(MainActivity.this,ActivityMenuWrite.class);
				startActivity(intent);

			}
		});

		readButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(MainActivity.this,ActivityMenuRead.class);
				startActivity(intent);

			}
		});

		createIdentifier.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,ActivityIdentifier.class);
				startActivity(intent);

			}
		});


		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				moveTaskToBack(true);
				System.exit(0);

			}
		});	

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(" "+getTitle());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.refresh:
			prgDialog.show();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					prgDialog.hide();
					Intent intent = new Intent(MainActivity.this,ActivityShowDB.class);
					startActivity(intent);
				}}, 3000); 
			syncMySQLDBSQLite(); //Sincronización web.móvil envía datos a la web. 
			syncSQLiteMySQLDB(); //Sincronización móvil.Web envía datos al movil.

			return true;
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this,ActivityShowSetting.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/****************************************************************************************************************************************************
	 * MySql to SQlite																																	*
	 ***************************************************************************************************************************************************/
	//Get data
	public void syncSQLiteMySQLDB() { 

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		client.post("http://192.168.0.13:80/nfc/getusers.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] content, byte[] arg2, Throwable error) {
				prgDialog.hide();
				if (statusCode == 404) {
					Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
				} else if (statusCode == 500) {
					Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
							Toast.LENGTH_LONG).show();
				}

			}
			@Override
			public void onSuccess(int arg0, Header[] response, byte[] arg2) {

				try {
					String cadena = new String(arg2,"UTF-8");
					updateSQLite(cadena);

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		});
	}
	//Update Database
	public void updateSQLite(String response){		
		ArrayList<HashMap<String, String>> usersynclist;
		usersynclist = new ArrayList<HashMap<String, String>>();
		// Create GSON object
		Gson gson = new GsonBuilder().create();
		try {

			JSONArray arr = new JSONArray(response);
			System.out.println("------TRAER-----------");
			System.out.println(arr.length());
			if(arr.length() != 0){
				for (int i = 0; i < arr.length(); i++) {
					// Get JSON object
					JSONObject obj = (JSONObject) arr.get(i);

					System.out.println(obj.get("relacion"));
					System.out.println(obj.get("objetoPadre"));
					System.out.println(obj.get("objeto"));
					System.out.println(obj.get("interaccion"));
					System.out.println(obj.get("tiempo"));
					System.out.println(obj.get("sincro"));

					// DB QueryValues Object to insert into SQLite
					queryValues = new HashMap<String, String>();
					queryValues.put("relacion", obj.get("relacion").toString());
					queryValues.put("objetoPadre", obj.get("objetoPadre").toString());
					queryValues.put("objeto", obj.get("objeto").toString());
					queryValues.put("interaccion", obj.get("interaccion").toString());
					queryValues.put("tiempo", obj.get("tiempo").toString());

					// Insert or delete data into SQLite DB
					if(obj.getInt("sincro") == 2)
					{
						mydatabase.delete(queryValues, "Log");
						queryValues.put("sincro", "3");
					}
					else
					{
						queryValues.put("sincro", "1");
						mydatabase.insert(queryValues,"Log");
					}
					// Add status for each User in Hashmap
					usersynclist.add(queryValues);
				}
				// Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
				updateMySQLSyncSts(gson.toJson(usersynclist));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Method to inform remote MySQL DB about completion of Sync activity
	public void updateMySQLSyncSts(String json) {
		System.out.println(json);
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("sincro", json);

		client.post("http://192.168.0.13:80/nfc/updatesyncsts.php", params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				prgDialog.hide();
				Toast.makeText(getApplicationContext(), "Error Occured to inform the MysQL DB", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub		
				String cadena;
				try {
					cadena = new String(arg2,"UTF-8");
					System.out.println(cadena);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		});
	}

	/***************************************************************************************************************************
	 * SQLite to MySql																										   *
	 ***************************************************************************************************************************/
	public void syncMySQLDBSQLite(){

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		ArrayList<HashMap<String, String>> userList =  mydatabase.getAllData();
		if(userList.size()!=0){
			if(mydatabase.dbSyncCount() != 0){
				prgDialog.show();
				params.put("usersJSON",mydatabase.composeJSONfromSQLite());
				client.post("http://192.168.0.13:80/nfc/insertuser_.php",params ,new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] arg1, byte[] arg2, Throwable arg3) {
						// TODO Auto-generated method stub
						prgDialog.hide();
						if(statusCode == 404){
							Toast.makeText(getApplicationContext(), "Requested resource not found 2", Toast.LENGTH_LONG).show();
						}else if(statusCode == 500){
							Toast.makeText(getApplicationContext(), "Something went wrong at server end 2", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "Unexpected Error occcured 2! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
						}

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						String cadena;
						try {
							cadena = new String(arg2,"UTF-8");
							System.out.println(cadena);
							try {
								JSONArray arr = new JSONArray(cadena);
								System.out.println("------ENVIAR-----------");
								System.out.println(arr.length());
								for(int i=0; i<arr.length();i++){
									JSONObject obj = (JSONObject)arr.get(i);
									System.out.println(obj.get("relacion"));
									System.out.println(obj.get("objetoPadre"));
									System.out.println(obj.get("objeto"));
									System.out.println(obj.get("interaccion"));
									System.out.println(obj.get("tiempo"));
									System.out.println(obj.get("sincro"));
									mydatabase.updateSyncStatus(obj.get("relacion").toString(),obj.get("objetoPadre").toString(),obj.get("objeto").toString(),
											obj.get("interaccion").toString(),obj.get("tiempo").toString());
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								prgDialog.hide();
								Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}

						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
			}
		}
	}
	/***************************************************************************************************************************
	 * GMC																								   					   *
	 ***************************************************************************************************************************/
	void initGCM()
	{
		SharedPreferences prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		long expirationTime =prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);
		applicationContext = getApplicationContext();
		
		if (registrationId == "") 
		{
			// Check if Google Play Service is installed in Device
			// Play services is needed to handle GCM stuffs
			if (checkPlayServices()) 
			{
				// Register Device in GCM Server
				registerInBackground();
			}
		}
		else
		{
			if(System.currentTimeMillis() > expirationTime)
			{
				/*try {
					gcmObj.unregister();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				registerInBackground();
			}
			else
			{
				Log.e("MyTag","Sincronizado ya");
			}
		}
	}

	private void registerInBackground() { 
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcmObj == null) {
						gcmObj = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regId = gcmObj.register(ApplicationConstants.GOOGLE_PROJ_ID);
					msg = "Registration ID :" + regId;
					System.out.println(msg);
					Log.e("MyTag",msg);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if (!TextUtils.isEmpty(regId)) {
					// Store RegId created by GCM Server in SharedPref
					storeRegIdinSharedPref(applicationContext, regId);
					Toast.makeText(applicationContext,"Registered with GCM Server successfully.\n\n"+ msg, Toast.LENGTH_SHORT).show();
				} else { 
					Toast.makeText(applicationContext,
							"Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
									+ msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute(null, null, null);
	}

	private void storeRegIdinSharedPref(Context context, String regId) {
		SharedPreferences prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putLong(PROPERTY_EXPIRATION_TIME, System.currentTimeMillis() + EXPIRATION_TIME_MS);
		editor.commit();
		storeRegIdinServer();
	}

	private void storeRegIdinServer() {
		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		params.put("regId", regId);
		client.post(ApplicationConstants.APP_SERVER_URL, params,new AsyncHttpResponseHandler() {
			// When the response returned by REST has Http
			// response code '200'
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Toast.makeText(applicationContext,"Reg Id shared successfully with Web App ",Toast.LENGTH_LONG).show();
			}

			// When the response returned by REST has Http
			// response code other than '200' such as '404',
			// '500' or '403' etc
			@Override
			public void onFailure(int statusCode, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(applicationContext,
							"Requested resource not found",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(applicationContext,
							"Something went wrong at server end",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							applicationContext,
							"Unexpected Error occcured! [Most common Error: Device might "
									+ "not be connected to Internet or remote server is not up and running], check for other errors as well",
									Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	// Check if Google Playservices is installed in Device or not
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// When Play services not found in device
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				// Show Error dialog to install Play services
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(
						applicationContext,
						"This device doesn't support Play services, App will not work normally",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		} else {
			Toast.makeText(
					applicationContext,
					"This device supports Play services, App will work normally",
					Toast.LENGTH_LONG).show();
		}
		return true;
	}
}





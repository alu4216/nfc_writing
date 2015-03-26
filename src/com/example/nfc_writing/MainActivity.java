package com.example.nfc_writing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class MainActivity extends ActionBarActivity {

	boolean  reciver;
	ProgressDialog prgDialog;
	HashMap<String, String> queryValues;
	Database mydatabase;
	Intent alarmIntent;
	PendingIntent pendingIntent;
	AlarmManager alarmManager;
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
		reciver = false;
		mydatabase =  new Database(this, "DB", null, 1);


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

		// BroadCase Receiver Intent Object
		// Alarm Manager Object
		alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
		// Pending Intent Object
		pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);



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
			syncMySQLDBSQLite(); //Sincronizaci�n web.m�vil env�a datos a la web. 
			syncSQLiteMySQLDB(); //Sincronizaci�n m�vil.Web env�a datos al movil.

			return true;
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this,ActivityShowSetting.class);
			startActivity(intent);
			return true;
		case R.id.reciver:
			if(reciver == false)
			{
				reciver = true;
				activeReciver(reciver);
			}
			else
			{
				reciver = false;
				activeReciver(reciver);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void activeReciver(boolean reciver)
	{
		if(reciver == true)
		{
			// Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in 
			// Remote MySQL DB
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000, pendingIntent);
			Toast.makeText(getApplicationContext(), "Reciver active", Toast.LENGTH_LONG).show();
		}
		else
		{
			alarmManager.cancel(pendingIntent);
			Toast.makeText(getApplicationContext(), "Disable active", Toast.LENGTH_LONG).show();
		}

	}
	/****************************************************************************************************************************************************
	 * MySql to SQlite																																	*
	 ***************************************************************************************************************************************************/
	//Get data
	public void syncSQLiteMySQLDB() { 
		
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
	
		client.post("http://192.168.0.10:80/nfc/getusers.php", params, new AsyncHttpResponseHandler() {
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
					queryValues.put("sincro", obj.get("sincro").toString());
					// Insert User into SQLite DB
					mydatabase.insert(queryValues,"Log");

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
		
		client.post("http://192.168.0.10:80/nfc/updatesyncsts.php", params, new AsyncHttpResponseHandler() {

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
				client.post("http://192.168.0.10:80/nfc/insertuser_.php",params ,new AsyncHttpResponseHandler() {

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
}





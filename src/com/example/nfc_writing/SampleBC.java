package com.example.nfc_writing;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SampleBC extends BroadcastReceiver {


	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//Toast.makeText(context, "BC Service Running for " + noOfTimes + " times", Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		// Checks if new records are inserted in Remote MySQL DB to proceed with Sync operation
		client.post("http://192.168.0.10:80/nfc/getdbrowcount.php",params ,new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				if(statusCode == 404){
					Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
				}else if(statusCode == 500){
					Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "Error occured in SAMPLEBC!!", Toast.LENGTH_SHORT).show();
				}

			}
	
			@Override
			public void onSuccess(int arg0, Header[] response, byte[] arg2) {
				// TODO Auto-generated method stub
				System.out.println(response);
				try {
					String cadena = new String(arg2,"UTF-8");
					JSONObject obj = new JSONObject(cadena);
					System.out.println(obj.get("count"));
					if(obj.getInt("count") != 0){
						final Intent intnt = new Intent(context, MyService.class);
						// Set unsynced count in intent data
						intnt.putExtra("intntdata", "Unsynced Rows Count "+obj.getInt("count"));
						// Call MyService
						context.startService(intnt);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});




	}

}

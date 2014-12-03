package com.example.nfc_writing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityMenuWrite extends Activity {


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writemenu);

		final Button writeUrl = (Button)findViewById(R.id.WriteUrl);
		final Button quitButton = (Button)findViewById(R.id.Back);
		final Button writeText = (Button)findViewById(R.id.WriteText);
		
		writeUrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityMenuWrite.this,ActivityWriteUrl.class);
				startActivity(intent);

			}	

		});
		
		writeText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityMenuWrite.this,ActivityWriteText.class);
				startActivity(intent);
			}	

		});
		
		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		}); 
	}
}
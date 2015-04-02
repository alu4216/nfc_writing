package com.example.nfc_writing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityMenuWrite extends ActionBarActivity {


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writemenu);

		final Button writeUrl = (Button)findViewById(R.id.WriteUrl);
		final Button quitButton = (Button)findViewById(R.id.Back);
		final Button writeText = (Button)findViewById(R.id.WriteText);
		
		writeUrl.setOnClickListener(new OnClickListener() { //Select write of URL or Plain TEXT

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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(" "+getTitle());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getMenuInflater().inflate(R.menu.menu2, menu);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_atras);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
		
		return true;
	}
	
}
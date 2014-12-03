package com.example.nfc_writing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityMenuRead extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readmenu);
		//Intent intent = new Intent(ActivityMenuRead.this,ActivityReadText.class);
		//startActivity(intent);

		final Button quitButton = (Button)findViewById(R.id.Quit);
		
		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		}); 
	}





}



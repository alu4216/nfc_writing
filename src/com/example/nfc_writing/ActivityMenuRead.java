package com.example.nfc_writing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ActivityMenuRead extends ActionBarActivity {

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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { //Select grouping mode through volume button

		Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		switch(keyCode){

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			SharedPreferences prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
			Boolean bool = prefs.getBoolean("LMactive",false); 
			String txt = prefs.getString("Lmultiple", "vacio");
			SharedPreferences.Editor editor = prefs.edit();

			if(bool == true)
			{
				editor.putBoolean("LMactive", false);
				editor.commit();
				Toast.makeText(this,"Disable group reading with:"+txt, Toast.LENGTH_SHORT).show();
				v.vibrate(500);
			}
			else
			{
				editor.putBoolean("LMactive", true);
				editor.commit();
				Toast.makeText(this,"Activated group reading with:"+txt, Toast.LENGTH_SHORT).show();
				v.vibrate(500);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(" "+getTitle());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}
	

}








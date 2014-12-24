package com.example.nfc_writing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		switch(keyCode){

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			SharedPreferences prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
			Boolean bool = prefs.getBoolean("LMactive",false); 
			String txt = prefs.getString("Lmultiple", "caca");
			SharedPreferences.Editor editor = prefs.edit();
			
			if(bool == true)
			{
				editor.putBoolean("LMactive", false);
				editor.commit();
				Toast.makeText(this,"Disible multimode reading with:"+txt, Toast.LENGTH_SHORT).show();
				v.vibrate(500);
			}
			else
			{
				editor.putBoolean("LMactive", true);
				editor.commit();
				Toast.makeText(this,"Active multimode reading with:"+txt, Toast.LENGTH_SHORT).show();
				v.vibrate(500);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}








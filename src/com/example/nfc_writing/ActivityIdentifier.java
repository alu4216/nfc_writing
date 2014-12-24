package com.example.nfc_writing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityIdentifier extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identifier);
		
		final Button save = (Button)findViewById(R.id.save);
		final EditText txt = (EditText)findViewById(R.id.TxtNombre);
		
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String txt_ = txt.getText().toString();
				SharedPreferences prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("Lmultiple", txt_);
				editor.putBoolean("LMactive", true);
				editor.commit();
				showmessage();
				Intent intent = new Intent(ActivityIdentifier.this, ActivityMenuRead.class);
				startActivity(intent);
			}
		});
		
	}
	private void showmessage()
	{
		
		Toast.makeText(this,"Active multimode reading", Toast.LENGTH_SHORT).show();
	}
	
	
	
}

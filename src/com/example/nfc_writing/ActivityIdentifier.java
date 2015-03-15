package com.example.nfc_writing;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityIdentifier extends ActionBarActivity {

	SharedPreferences prefs; 
	Database myDatabase; 
	HashMap<String, String> queryValues;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identifier);
		
		final TextView ftxt= (TextView)findViewById(R.id.FielText);
		final Button save = (Button)findViewById(R.id.save);
		final EditText txt = (EditText)findViewById(R.id.TxtNombre);
		prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		myDatabase = new Database(this, "DB", null, 1);
		queryValues = new HashMap<String, String>();
		if(prefs.getString("Lmultiple","vacio")!="vacio")
		{
			ftxt.setText("Last type of interaction:"+prefs.getString("Lmultiple","vacio")+".\nCreate" +
					" new interaction:");
		}
		
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { //Choose type of interaction and store in preferences 
				// TODO Auto-generated method stub
				String txt_ = txt.getText().toString();
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("Lmultiple", txt_);
				editor.putBoolean("LMactive", true);
				editor.putBoolean("OPactive",true);
				
				//Sería interesante crear un desplegable. cada desplegable tendría asociado su tipo de relacion: Interación marca un relación
				if(txt_.equals("Espacial")) //selecting the relationship based on the interaction
				{
					editor.putString("Relacion", "Esta en");
					queryValues.put("relacion","Esta en");
				}
				editor.commit();
				queryValues.put("interaccion",txt_);
				//Store in databases
				myDatabase.insert(queryValues, "Interaccion");
				myDatabase.insert(queryValues,"Relacion");
				showmessage();
				Intent intent = new Intent(ActivityIdentifier.this, ActivityMenuRead.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	private void showmessage() //Show messaging 
	{
		Toast.makeText(this,"Activated group reading", Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(" "+getTitle());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}
	
	
	
	
}

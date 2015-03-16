package com.example.nfc_writing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityShowDB extends ActionBarActivity {

	Database mydatabase;

	protected void onCreate(Bundle savedInstanceState) { //Show database
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showdatabase);

		final TextView txt = (TextView)findViewById(R.id.showText);
		final Button quitButton = (Button)findViewById(R.id.Quit);
		mydatabase =  new Database(this, "DB", null, 1);
		SQLiteDatabase db = mydatabase.getWritableDatabase();

		StringBuffer prueba = new StringBuffer();
		String[] campos = new String[] {"*"};
		Cursor c = db.query("Log", campos, null, null, null, null, null);
		if (c.moveToFirst()) {
			//Recorremos el cursor hasta que no haya más registros
			do {
				String relacion= c.getString(0);
				String objetoPadre = c.getString(1);
				String objeto = c.getString(2);
				String interaccion = c.getString(3);
				String tiempo = c.getString(4);
				String sincro = c.getString(5);
				prueba.append(relacion+","+objetoPadre+","+objeto+","+interaccion+","+tiempo+","+sincro+"\n");
			} while(c.moveToNext());
		}
		txt.setText("Relacion - Type - Sync\n"+prueba.toString());

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
		return true;
	}
	
}

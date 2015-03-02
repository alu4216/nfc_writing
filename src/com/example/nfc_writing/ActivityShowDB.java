package com.example.nfc_writing;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityShowDB extends Activity {

	Database mydatabase;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showdatabase);

		final TextView txt = (TextView)findViewById(R.id.showText);
		final Button quitButton = (Button)findViewById(R.id.Quit);
		mydatabase =  new Database(this, "DB", null, 1);
		SQLiteDatabase db = mydatabase.getWritableDatabase();

		StringBuffer prueba = new StringBuffer();
		String[] campos = new String[] {"*"};
		Cursor c = db.query("WorkFlow", campos, null, null, null, null, null);
		if (c.moveToFirst()) {
			//Recorremos el cursor hasta que no haya más registros
			do {
				String nombre= c.getString(0);
				String tipo = c.getString(1);
				String sincro = c.getString(2);
				prueba.append(nombre+","+tipo+","+sincro+"\n");
			} while(c.moveToNext());
		}
		txt.setText("Name - Type - Sync\n"+prueba.toString());

		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});	
	}
}

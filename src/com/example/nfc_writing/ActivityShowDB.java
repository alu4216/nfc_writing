package com.example.nfc_writing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class ActivityShowDB extends ActionBarActivity {

	Database mydatabase;
	TableLayout table_layout;
	protected void onCreate(Bundle savedInstanceState) { //Show database
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showdatabase);

		table_layout = (TableLayout) findViewById(R.id.tableLayout1);
		mydatabase =  new Database(this, "DB", null, 1);
		SQLiteDatabase db = mydatabase.getWritableDatabase();
		String[] campos = new String[] {"*"};
		Cursor c = db.query("Log", campos, null, null, null, null, null);
		int rows = c.getCount();
		int cols = c.getColumnCount();

		c.moveToFirst();

		for (int i = 0; i < rows; i++) {

			TableRow row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			for (int j = 0; j < cols; j++) {

				if(j!=3)
				{
					LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(1, 1, 1, 1);
					TextView tv = new TextView(this);
					tv.setLayoutParams(layoutParams);
					tv.setBackgroundColor(Color.WHITE);
					tv.setTextSize(14);
					tv.setGravity(Gravity.CENTER);
					tv.setText(c.getString(j));
					tv.setTextColor(Color.BLACK);
					row.addView(tv);


				}
			}
			c.moveToNext();
			table_layout.addView(row);
		}

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

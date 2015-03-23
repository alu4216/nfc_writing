package com.example.nfc_writing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityIdentifier extends ActionBarActivity {

	SharedPreferences prefs; 
	Database myDatabase; 
	HashMap<String, String> queryValues;
	String data;
	List<String> list;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identifier);

		//Objecto of activity
		final TextView ftxt= (TextView)findViewById(R.id.FielText);
		final TextView ftxt1= (TextView)findViewById(R.id.FielText1); 
		final Button save = (Button)findViewById(R.id.save);
		final Spinner desplegable = (Spinner)findViewById(R.id.spinner1);
		final Button add_delete = (Button)findViewById(R.id.add_delete);
		final EditText relation = (EditText)findViewById(R.id.TxtRelation);
		final EditText interaction = (EditText)findViewById(R.id.txtInteraction);
		final CheckBox box = (CheckBox)findViewById(R.id.checkBox1);

		//Initialize 
		prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();
		myDatabase = new Database(this, "DB", null, 1);
		queryValues = new HashMap<String, String>();
		final SQLiteDatabase db = myDatabase.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT interaccion FROM Conf_spinner", null);
		list = new ArrayList<String>();


		//Set text and check box
		box.setChecked(prefs.getBoolean("Cbox", false));
		if(prefs.getString("Lmultiple","vacio")!="vacio")
		{
			ftxt.setText("Last type of interaction:"+prefs.getString("Lmultiple","No exist"));
		}

		if(box.isChecked())
		{
			editor.putBoolean("Cbox", true);
			add_delete.setText("Del");
			ftxt1.setText("Delete Relation-Interaction");
		}
		else
		{
			editor.putBoolean("Cbox",false);

			ftxt1.setText("Create Relation-Interaction");
		}
		editor.commit();

		//Get the list of spinner
		if (c.moveToFirst()) {
			//Recorremos el cursor hasta que no haya más registros
			do {
				list.add(c.getString(0));
			} while(c.moveToNext());
		}
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		desplegable.setAdapter(adaptador);

		add_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(box.isChecked())
				{
					queryValues.clear();
					queryValues.put("interaccion",interaction.getText().toString());
					myDatabase.delete(queryValues,"Conf_spinner");

				}
				else
				{	
					queryValues.clear();
					queryValues.put("relacion",relation.getText().toString());
					queryValues.put("interaccion",interaction.getText().toString());
					myDatabase.insert(queryValues, "Conf_spinner");
					myDatabase.insert(queryValues, "Interaccion");
					myDatabase.insert(queryValues,"Relacion");
				}
				myDatabase.close();
				reiniciar();
			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { //Choose type of interaction and store in preferences 
				// TODO Auto-generated method stub
		

				editor.putString("Lmultiple", data);
				editor.putBoolean("LMactive", true);
				editor.putBoolean("OPactive",true);
				editor.commit();

				showmessage();
				Intent intent = new Intent(ActivityIdentifier.this, ActivityMenuRead.class);
				startActivity(intent);
				finish();

			}
		});
		desplegable.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							android.view.View v, int position, long id) {
						
					
						data = desplegable.getSelectedItem().toString();
						Cursor c = db.rawQuery("SELECT relacion FROM Conf_spinner WHERE interaccion='"+data+"'", null);
						if (c.moveToFirst()) {
							//Recorremos el cursor hasta que no haya más registros
							do {
								editor.putString("Relacion",c.getString(0));
							} while(c.moveToNext());
							
							editor.commit();
						}
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
		

				if(box.isChecked())
				{
					editor.putBoolean("Cbox", true);
					add_delete.setText("Del");
					ftxt1.setText("Delete Relation-Interaction");
				}
				else
				{
					editor.putBoolean("Cbox",false);
					add_delete.setText("Add");
					ftxt1.setText("Create Relation-Interaction");
				}
				editor.commit();
				add_delete.refreshDrawableState();
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myDatabase.close();
	}
	private void reiniciar() {
		Intent intent = new Intent(getIntent());
		startActivity(intent);
		finish();
	}




}


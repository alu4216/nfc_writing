package com.example.nfc_writing;


import java.nio.charset.Charset;
import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class ActivityReadText extends CommonMethods {

	private StringBuffer myText;
	private byte statusByte;
	private String payload; 
	private String tipo;
	private String relacion;
	private Boolean bool;
	private Database myDatabase; 
	private HashMap<String, String> queryValues;
	private TextView txt; 
	private TableLayout table_layout;
	private TextView relation;
	private TextView pObject;
	private TextView object;
	private TextView timestamp;
	private TextView sync;
	private SharedPreferences prefs ;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readtextplain);
		txt = (TextView)findViewById(R.id.textView);
		relation = (TextView)findViewById(R.id.Relation);
		pObject = (TextView)findViewById(R.id.P_object);
		object = (TextView)findViewById(R.id.Object);
		timestamp = (TextView)findViewById(R.id.Timestamp);
		sync = (TextView)findViewById(R.id.Sync);
		table_layout = (TableLayout) findViewById(R.id.tableLayout1);

		prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		bool = prefs.getBoolean("LMactive",false); 
		tipo ="sinTipo";
		relacion ="sinRelacion";
		payload = null;
		myText = new StringBuffer();
		myDatabase = new Database(this, "DB", null, 1);
		int orientation = getResources().getConfiguration().orientation;


		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){ // Read NFC card
		

			NdefMessage[] message = getNdefMessages(getIntent());

			for ( int i = 0; i< message.length; i++) {
				myText = myText.append("Message" +(1+i)+ " \n");
				for(int j=0; j<message[0].getRecords().length;j++) {
					NdefRecord record = message[i].getRecords()[j];
					statusByte = record.getPayload()[0];
					int languageCodeLength = statusByte & 0x3F;
					myText.append("Language Code Length:" + languageCodeLength+"\n");
					String languageCode = new String( record.getPayload(), 1, languageCodeLength, Charset.forName("UTF-8")); 
					myText.append("Language Code:" + languageCode+"\n"); 

					int isUTF8 = statusByte-languageCodeLength;

					if(isUTF8 == 0x00) {
						myText.append((j+1) + "th. Record is UTF-8\n");

						payload = new String( record.getPayload(), 1+languageCodeLength, record.getPayload().length-1-languageCodeLength, Charset.forName("UTF-8"));

					} 
					else if (isUTF8==-0x80) {
						myText.append((j+1) + "th. Record is UTF-16\n"); 
						payload = new String( record.getPayload(), 1+languageCodeLength, record.getPayload().length-1-languageCodeLength, Charset.forName("UTF-16")); 

					}
					myText.append((j+1) + "th. Record Tnf: " + record.getTnf() + "\n"); 
					myText.append((j+1) + "th. Record type: " + new String(record.getType()) + "\n"); 
					myText.append((j+1) + "th. Record id: " + new String(record.getId()) + "\n");
					myText.append((j+1) + "th. Record payload: " + payload + "\n");
				} 
			} 

			if(orientation == 1) { //Insertion in the database
				queryValues = new HashMap<String, String>();
				if(bool == true) {//If grouping is active
					tipo = prefs.getString("Lmultiple", "vacio");
					relacion = prefs.getString("Relacion","vacio");

					if(prefs.getBoolean("OPactive", false)==true) {//Inserting the parent object
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean("OPactive", false);
						editor.putString("OPadre",payload);
						editor.commit();
						queryValues.put("objeto",payload);
						myDatabase.insert(queryValues, "Objetos");
						queryValues.put("relacion",relacion);
						queryValues.put("interaccion",tipo);
						myDatabase.insert(queryValues,"RCruzadas");

						showtable(0,null, payload, null, null,null);


					}
					else { //Inserting the child object
					

						Long tsLong = System.currentTimeMillis()/1000;
						String timestamp = tsLong.toString();
						queryValues.put("objeto",payload);
						myDatabase.insert(queryValues,"Objetos");
						queryValues.put("relacion",relacion);
						queryValues.put("objetoPadre",prefs.getString("OPadre","SinPadre"));
						queryValues.put("interaccion",tipo);
						queryValues.put("tiempo",timestamp);
						queryValues.put("sincro","0");
						myDatabase.insert(queryValues,"Log");

						showtable(0, null,prefs.getString("OPadre","SinPadre"),payload, timestamp,null);


					}
				}
				else { //If grouping is not active
					queryValues.put("objeto",payload);
					myDatabase.insert(queryValues,"Objetos");
					showtable(1, null,null, payload, null,null);
				}
			}
			else { //Search the database
				
				SQLiteDatabase db = myDatabase.getWritableDatabase();

				String[] campos = new String[] {"*"};
				String[] args = new String[] {payload};
				Cursor c = db.query("Log", campos, "objetoPadre=?", args, null, null, null);
				if (c.moveToFirst()) {
					do {
						String relacion_ = c.getString(0);
						String objetoPadre = c.getString(1);
						String objeto = c.getString(2);
						String tiempo = c.getString(4);
						String sincro = c.getString(5);


						showtable(2, relacion_,objetoPadre, objeto, tiempo,sincro);
					} while(c.moveToNext());

				}
			}

		}


	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void showtable(int tipo_,String relacion_,String Ppayload,String payload,String tiempo, String sincro) {
	

		TableRow  row = new TableRow(this);
		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(1, 1, 1, 1);
		TextView [] tv = new TextView[5];
	
		for(int i=0; i< 5; i++) {
		
			tv[i] = new TextView(this);
			tv[i].setText(" ");
			tv[i].setLayoutParams(layoutParams);
			tv[i].setBackgroundColor(Color.WHITE);
			tv[i].setTextSize(14);
			tv[i].setGravity(Gravity.CENTER);
			tv[i].setTextColor(Color.BLACK);
		}


		switch (tipo_) {
		case 0:
			tv[0].setText(relacion);
			row.addView(tv[0]);
			tv[1].setText(Ppayload);
			row.addView(tv[1]);

			if(payload==null) {
				txt.setText("Inserted new Relation");
				tv[2].setText(tipo);
				row.addView(tv[2]);
				object.setText("Interaction");
				timestamp.setVisibility(TextView.INVISIBLE);
				sync.setVisibility(TextView.INVISIBLE);
			}
			else {
			
				txt.setText("Inserted new object in the relation \nRelation:"+relacion+","+prefs.getString("OPadre", "vacio")+","+tipo);
				tv[2].setText(payload);
				row.addView(tv[2]);
				tv[3].setText(tiempo);
				row.addView(tv[3]);	
				tv[4].setText("0");
				row.addView(tv[4]);
			}
			table_layout.addView(row);
			break;
		case 1:
			txt.setText("Only inserted Objects");

			relation.setText("Object");
			tv[0].setText(payload);
			row.addView(tv[0]);
			object.setVisibility(TextView.INVISIBLE);
			pObject.setVisibility(TextView.INVISIBLE);
			timestamp.setVisibility(TextView.INVISIBLE);
			sync.setVisibility(TextView.INVISIBLE);
			table_layout.addView(row);
			break;
		case 2:
			txt.setText("Search results with object:"+Ppayload);
			
			tv[0].setText(relacion_);
			row.addView(tv[0]);
			tv[1].setText(Ppayload);
			row.addView(tv[1]);
			tv[2].setText(payload);
			row.addView(tv[2]);
			tv[3].setText(tiempo);
			row.addView(tv[3]);
			tv[4].setText(sincro);
			row.addView(tv[4]);
			table_layout.addView(row);
			break;
		default:
			Log.e("MyTag","no show anything in the table");
			
			break;
		}



	}



}




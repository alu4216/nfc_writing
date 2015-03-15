package com.example.nfc_writing;

import java.nio.charset.Charset;
import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityReadText extends CommonMethods {

	StringBuffer myText;
	String aux;
	byte statusByte;
	String payload; 
	String tipo;
	String relacion;
	Boolean bool;
	Database myDatabase; 
	HashMap<String, String> queryValues;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readtext);
		final TextView txt = (TextView)findViewById(R.id.txt);
		final Button quitButton = (Button)findViewById(R.id.Back);
		SharedPreferences prefs = getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
		bool = prefs.getBoolean("LMactive",false); 
		tipo ="sinTipo";
		relacion ="sinRelacion";
		payload = null;
		myText = new StringBuffer();
		myDatabase = new Database(this, "DB", null, 1);
		int orientation = getResources().getConfiguration().orientation;

	
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) // Read NFC card
		{

			NdefMessage[] message = getNdefMessages(getIntent());

			for ( int i = 0; i< message.length; i++)
			{
				myText = myText.append("Message" +(1+i)+ " \n");
				for(int j=0; j<message[0].getRecords().length;j++)
				{
					NdefRecord record = message[i].getRecords()[j];
					statusByte = record.getPayload()[0];
					int languageCodeLength = statusByte & 0x3F;
					myText.append("Language Code Length:" + languageCodeLength+"\n");
					String languageCode = new String( record.getPayload(), 1, languageCodeLength, Charset.forName("UTF-8")); 
					myText.append("Language Code:" + languageCode+"\n"); 

					int isUTF8 = statusByte-languageCodeLength;

					if(isUTF8 == 0x00)
					{
						myText.append((j+1) + "th. Record is UTF-8\n");

						payload = new String( record.getPayload(), 1+languageCodeLength, record.getPayload().length-1-languageCodeLength, Charset.forName("UTF-8"));

					} 
					else if (isUTF8==-0x80)
					{
						myText.append((j+1) + "th. Record is UTF-16\n"); 
						payload = new String( record.getPayload(), 1+languageCodeLength, record.getPayload().length-1-languageCodeLength, Charset.forName("UTF-16")); 

					}
					myText.append((j+1) + "th. Record Tnf: " + record.getTnf() + "\n"); 
					myText.append((j+1) + "th. Record type: " + new String(record.getType()) + "\n"); 
					myText.append((j+1) + "th. Record id: " + new String(record.getId()) + "\n");
					myText.append((j+1) + "th. Record payload: " + payload + "\n");
				} 
			} 

			if(orientation == 1) //Insertion in the database
			{
				queryValues = new HashMap<String, String>();
				if(bool == true)//If grouping is active
				{
					tipo = prefs.getString("Lmultiple", "vacio");
					relacion = prefs.getString("Relacion","vacio");

					if(prefs.getBoolean("OPactive", false)==true)//Inserting the parent object
					{
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean("OPactive", false);
						editor.putString("OPadre",payload);
						editor.commit();
						queryValues.put("objeto",payload);
						myDatabase.insert(queryValues, "Objetos");
						queryValues.put("relacion",relacion);
						queryValues.put("interaccion",tipo);
						myDatabase.insert(queryValues,"RCruzadas");
						txt.setText("Relationship:"+ relacion+","+"Parent Object:"+prefs.getString("OPadre", "vacio")+","+"Type of Interaction:"+tipo+"\n");
					}
					else //Inserting the child object
					{

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
						txt.setText("Relationship:"+ relacion+","+"Parent Object:"+prefs.getString("OPadre", "vacio")+","+"Type of Interaction:"+tipo+"\n"
								+"Child Object:"+payload+","+"Timer:"+timestamp+"\n");

					}
				}
				else //If grouping is not active
				{
					queryValues.put("objeto",payload);
					myDatabase.insert(queryValues,"Objetos");
					txt.setText("Object inserted:"+payload);
				}
			}
			else //Search the database
			{	
				SQLiteDatabase db = myDatabase.getWritableDatabase();
				StringBuffer cadena = new StringBuffer();
				String[] campos = new String[] {"*"};
				String[] args = new String[] {payload};
				Cursor c = db.query("Log", campos, "objetoPadre=?", args, null, null, null);
				if (c.moveToFirst()) {
					do {
						String relacion= c.getString(0);
						String objetoPadre = c.getString(1);
						String objeto = c.getString(2);
						String interaccion = c.getString(3);
						String tiempo = c.getString(4);
						String sincro = c.getString(5);
						cadena.append(relacion+","+objetoPadre+","+objeto+","+interaccion+","+tiempo+","+sincro+"\n");
					} while(c.moveToNext());
				}
				txt.setText("Read TAG :"+payload+"\n"+"List:\n"+cadena);
			}

		}

		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		}); 
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}




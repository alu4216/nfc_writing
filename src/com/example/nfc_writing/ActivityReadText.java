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
		tipo = prefs.getString("Lmultiple", "vacio");
		payload = null;
		myText = new StringBuffer();
		myDatabase = new Database(this, "DB", null, 1);
		
		int orientation = getResources().getConfiguration().orientation;

		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
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

			queryValues = new HashMap<String, String>();

			if(orientation == 1)
			{
				if(bool == false)
				{ 
					queryValues.put("nombre",payload);
					queryValues.put("tipo","vacio");
					queryValues.put("sincro","0");
					myDatabase.insert(queryValues);
					txt.setText(myText+","+"vacio");
				}
				else
				{
					queryValues.put("nombre",payload);
					queryValues.put("tipo",tipo);
					queryValues.put("sincro","0");
					myDatabase.insert(queryValues);
					txt.setText(myText+","+tipo);
				}
				
			}
			else
			{	
				SQLiteDatabase db = myDatabase.getWritableDatabase();
				StringBuffer prueba = new StringBuffer();
				String[] campos = new String[] {"*"};
				String[] args = new String[] {payload};
				Cursor c = db.query("WorkFlow", campos, "nombre=?", args, null, null, null);
				if (c.moveToFirst()) {
					//Recorremos el cursor hasta que no haya más registros
					do {
						String nombre= c.getString(0);
						String tipo = c.getString(1);
						String sincro = c.getString(2);
						prueba.append(nombre+","+tipo+","+sincro+"\n");
					} while(c.moveToNext());
				}
				txt.setText("Read TAG :"+payload+"\n"+"List:\n"+prueba);
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




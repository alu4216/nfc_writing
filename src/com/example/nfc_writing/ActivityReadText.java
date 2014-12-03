package com.example.nfc_writing;

import java.nio.charset.Charset;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityReadText extends Activity {

	StringBuffer myText = new StringBuffer();
	String aux;
	byte statusByte;
	String payload = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readtext);
		final TextView txt = (TextView)findViewById(R.id.txt);
		final Button quitButton = (Button)findViewById(R.id.Back);

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

		}

		txt.setText(myText);
		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		}); 
	}

	private NdefMessage[] getNdefMessages(Intent intent)
	{
		NdefMessage[] message = null;
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
		{
			Parcelable[] rawmessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if(rawmessage != null)
			{
				message = new NdefMessage[rawmessage.length];
				for( int i = 0; i < rawmessage.length; i++)
				{
					message[i]= (NdefMessage) rawmessage[i];
				}
			}
			else
			{
				byte[] empty = new byte [] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
				message = new NdefMessage[]{msg};
			}
		}
		else
		{
			Log.d("","Unknow intent.");
			finish();
		}
		return message;

	}
}




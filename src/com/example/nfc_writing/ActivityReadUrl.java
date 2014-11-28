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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityReadUrl extends Activity {

	byte payloadHeader;
	String payload;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readurl);

		final TextView txt = (TextView)findViewById(R.id.FielText);
		final Button urlButton = (Button)findViewById(R.id.Open);
		final Button quitButton = (Button)findViewById(R.id.Back);
		
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
		{

			NdefMessage[] message = getNdefMessages(getIntent());
			for ( int i = 0; i< message.length; i++)
			{
				for ( int j = 0; j< message[0].getRecords().length; j++)
				{
					NdefRecord record = message[i].getRecords()[j];
					payload = new String (record.getPayload(),1,record.getPayload().length-1,Charset.forName("UTF-8"));
					payloadHeader = record.getPayload()[0];
				}
			}
			txt.setText("URL is recived:" + payload);
			urlButton.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(payloadHeader == 0x01)
					{
						Intent data = new Intent();
						data.setAction(Intent.ACTION_VIEW);
						data.setData(Uri.parse("http://www."+ payload));
						try
						{
							startActivity(data);
						}
						catch(ActivityNotFoundException e)
						{
							return;
						}
					}

				}
			});
		}
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
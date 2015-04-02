package com.example.nfc_writing;

import java.io.IOException;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class CommonMethods extends ActionBarActivity {

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(" "+getTitle());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_atras);
		getMenuInflater().inflate(R.menu.menu2, menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}
	
	//Read to Tag
	protected NdefMessage[] getNdefMessages(Intent intent)
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
	//Write to tag
	protected boolean writeNdefMessageToTag(NdefMessage message, Tag detectedTag) 
	{
		int size = message.toByteArray().length;
		try
		{
			Ndef ndef = Ndef.get(detectedTag);
			if(ndef != null)
			{
				ndef.connect();
				if(!ndef.isWritable())
				{
					Toast.makeText(this,"Tag Read Only",Toast.LENGTH_SHORT).show();
					return false;
				}
				if(ndef.getMaxSize()< size)
				{
					Toast.makeText(this,"Data cannot writtent to tag. Tag capacity is " + ndef.getMaxSize(), Toast.LENGTH_SHORT).show();
					return false;
				}
				ndef.writeNdefMessage(message);
				ndef.close();
				Toast.makeText(this,"Message is written tag", Toast.LENGTH_SHORT).show();
				return true;
			}
			else
			{
				NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
				if(ndefFormat != null)
				{
					try 
					{
						ndefFormat.connect();
						ndefFormat.format(message);
						ndefFormat.close();
						Toast.makeText(this,"The data is written to tag", Toast.LENGTH_SHORT).show();
						return true;
					}catch (IOException e)
					{
						Toast.makeText(this,"Fail to forma tag", Toast.LENGTH_SHORT).show();
						return false;
					}
				}
				else
				{
					Toast.makeText(this,"NDEF is not supported", Toast.LENGTH_SHORT).show();
					return false;
				}
			}

		}
		catch (Exception e)
		{
			Toast.makeText(this,"Write operation is failed", Toast.LENGTH_SHORT).show();
			return false;
		}
	}



}

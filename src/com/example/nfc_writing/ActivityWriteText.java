package com.example.nfc_writing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityWriteText extends CommonMethods {

	private String data;	
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String [][]mTechLists;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writetext);

		final Button write = (Button)findViewById(R.id.Save);
		final EditText etxt = (EditText)findViewById(R.id.TxtNombre);
		final TextView txt = (TextView)findViewById(R.id.FielText2);
		final Button quitButton = (Button)findViewById(R.id.Back);

		write.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				data = etxt.getText().toString();
				txt.setText("Touch NFC tag to write Text Plain");
				setupForenground();
				//setupNdef();
			}

		});
		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//finish();
				finish();

			}
		}); 

	}
	private void setupForenground() 
	{
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED); 
		mFilters = new IntentFilter[] {ndef, }; 
		mTechLists = new String[][] { new String[] { Ndef.class.getName() }, new String[] { NdefFormatable.class.getName() }};
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if(mNfcAdapter != null)
			mNfcAdapter.disableForegroundDispatch(this);
	}
	@Override
	public void onResume()//Dar prioridad a la activity en primer plano al descubrir una etiqueta
	{
		super.onResume();
		if(mNfcAdapter != null)
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,mFilters,mTechLists);
	}
	@Override //Set up the NDEF message
	public void onNewIntent(Intent intent) 
	{ 
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); 
		Locale locale= new Locale("en","US"); 
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII")); 
		boolean encodeInUtf8=false;
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16"); 
		int utfBit = encodeInUtf8 ? 0 : (1 << 7); 
		char status = (char) (utfBit + langBytes.length);  
		byte[] textBytes = data.getBytes(utfEncoding);
		byte[] data = new byte[1 + langBytes.length + textBytes.length]; data[0] = (byte) status; 
		System.arraycopy(langBytes, 0, data, 1, langBytes.length); System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		NdefMessage newMessage= new NdefMessage(new NdefRecord[] { textRecord });
		writeNdefMessageToTag(newMessage, tag);

	}
}





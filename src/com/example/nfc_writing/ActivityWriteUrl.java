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

public class ActivityWriteUrl extends CommonMethods {

	private String urlAddress;	
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String [][]mTechLists;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writeurl);

		final Button write = (Button)findViewById(R.id.Save);
		final EditText etxt = (EditText)findViewById(R.id.TxtNombre);
		final TextView txt = (TextView)findViewById(R.id.FielText2);
		final Button quitButton = (Button)findViewById(R.id.Back);

		write.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				urlAddress = etxt.getText().toString();
				txt.setText("Touch NFC tag to write http://www."+urlAddress);
				setupForenground();
				//setupNdef();
			}

		});
		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

	private void setupNdef()
	{
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()));
		{
			Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG); 
			byte[] uriField = urlAddress.getBytes(Charset.forName("US-ASCII"));
			byte[] payload = new byte[uriField.length + 1]; 
			payload[0] = 0x01; System.arraycopy(uriField, 0, payload, 1, uriField.length); 
			NdefRecord URIRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			NdefMessage newMessage= new NdefMessage(new NdefRecord[] { URIRecord }); 

			//WRITE DATA TO TAG
			writeNdefMessageToTag(newMessage, tag);
		}

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
		byte[] uriField = urlAddress.getBytes(Charset.forName("US-ASCII"));
		byte[] payload = new byte[uriField.length + 1]; 
		payload[0] = 0x01; 
		System.arraycopy(uriField, 0, payload, 1, uriField.length); 
		NdefRecord URIRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
		NdefMessage newMessage= new NdefMessage(new NdefRecord[] { URIRecord }); 
		//WRITE TO TAG
		writeNdefMessageToTag(newMessage, tag);
	}
}

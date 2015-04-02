package com.example.nfc_writing;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityShowSetting extends ActionBarActivity {

	private ListView lstOpciones;
	private Titular[] datos;
	protected void onCreate(Bundle savedInstanceState) { //Show Settings
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		rellenar();
		lstOpciones = (ListView)findViewById(R.id.LstOpciones);
		AdaptadorTitulares adaptador =
				new AdaptadorTitulares(this, datos);
		lstOpciones.setAdapter(adaptador);
	}
	private void rellenar() //Fill options
	{
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ip_ =String.format("%d.%d.%d.%d",
				(ip & 0xff),
				(ip >> 8 & 0xff),
				(ip>> 16 & 0xff),
				(ip >> 24 & 0xff));
		datos = new Titular[3];
		datos[0] = new Titular();
		datos[1] = new Titular();
		datos[2] = new Titular();
		datos[0].setTitulo("Local IP address", ip_);
		datos[1].setTitulo("Address of server ", "192.168.0.10");
		datos[2].setTitulo("Port of server","80");
	}
	
	public String getLocalIpAddress() { //Get Ip address
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("MYTAG", ex.toString());
        }
        return "";
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle(" "+getTitle());
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getMenuInflater().inflate(R.menu.menu2, menu);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_atras);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}
	class AdaptadorTitulares extends ArrayAdapter<Titular> {

		public AdaptadorTitulares(Context context, Titular[] datos) {
			super(context, R.layout.listitem, datos);
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View item = convertView;
			ViewHolder holder;

			if(item == null)
			{
				LayoutInflater inflater = LayoutInflater.from(getContext());
				item = inflater.inflate(R.layout.listitem, null);
				holder = new ViewHolder();
				holder.titulo = (TextView)item.findViewById(R.id.LblTitulo);
				holder.subtitulo = (TextView)item.findViewById(R.id.LblSubTitulo);

				item.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)item.getTag();
			}

			holder.titulo.setText(datos[position].getTitulo());
			holder.subtitulo.setText(datos[position].getSubtitulo());

			return(item);
		}
	}

	static class ViewHolder {
		TextView titulo;
		TextView subtitulo;
	}
}

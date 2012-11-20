package com.ssl.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import java.security.cert.Certificate;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SSLClientActivity extends Activity {
	

	private EditText mText;
	private Button mSend;
	private TextView mResponse;
	private EditText mIPaddress;
	private EditText mPort;

	/**
	 * port och ip. 
	 * Lösenord till keystore och truststore.
	 */
	private String ip_address;
	private int port = 9998;
	private SSLSocket socket = null;
	private BufferedWriter out = null;
	private BufferedReader in = null;
	private final String TAG = "TAG";
	private char keystorepass[] = "password".toCharArray();
	private char keypassword[] = "password".toCharArray();
	private char trustpassword[] = "password".toCharArray();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mText = (EditText) findViewById(R.id.editext);
		mIPaddress = (EditText) findViewById(R.id.ip_address);
		mPort = (EditText) findViewById(R.id.port);
		mSend = (Button) findViewById(R.id.send_button);
		mResponse = (TextView) findViewById(R.id.server_response);

		mSend.setClickable(true);
		mSend.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (mIPaddress.getText().toString().equals(null) || mPort.getText().toString().equals(null)){
					Toast.makeText(v.getContext(), "Please enter an IP address or Port number", Toast.LENGTH_LONG).show();
				}
				else{
					String temp = mText.getText().toString();
					if (temp == null){
						temp = "No text was entered";
					}

					Log.i(TAG,"makes it to here");

					port = Integer.parseInt(mPort.getText().toString());
					ip_address = mIPaddress.getText().toString();
					/**
					 * Denna try sats är den vi vill ha. den sätter trust key
					 */
					try{
						KeyStore ts = KeyStore.getInstance("BKS");
						InputStream trustin = v.getResources().openRawResource(R.raw.servertrust);
						ts.load(trustin,trustpassword);
						
						KeyStore ks = KeyStore.getInstance("BKS");
						InputStream keyin = v.getResources().openRawResource(R.raw.clientkey);
						ks.load(keyin,keystorepass);
						
						TrustManagerFactory tmf = TrustManagerFactory
		                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
		                tmf.init(ts);
		                
		                KeyManagerFactory kmf = KeyManagerFactory
		                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
		                kmf.init(ks, keystorepass);

		                SSLContext sslCtx = SSLContext.getInstance("TLS");
		                sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		                
		                
						SSLSocketFactory socketFactory = new SSLSocketFactory(ks);
						socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
						
						socket = (SSLSocket)
						socketFactory.createSocket(new Socket(ip_address,port), ip_address, port, false);
						socket.startHandshake();
						
						
						

						printServerCertificate(socket);
						printSocketInfo(socket);

						out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						chat(temp);
					} catch (UnknownHostException e) {
						Toast.makeText(v.getContext(), "Unknown host", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"Unknown host");
						//System.exit(1);
					} catch  (IOException e) {
						Toast.makeText(v.getContext(), "No I/O", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"No I/O");
						e.printStackTrace();
						//System.exit(1);
					} catch (KeyStoreException e) {
						Toast.makeText(v.getContext(), "Keystore ks error", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"Keystore ks error");
						//System.exit(-1);
					} catch (NoSuchAlgorithmException e) {
						Toast.makeText(v.getContext(), "No such algorithm for ks.load", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"No such algorithm for ks.load");
						e.printStackTrace();
						//System.exit(-1);
					} catch (CertificateException e) {
						Toast.makeText(v.getContext(), "certificate missing", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"certificate missing");
						e.printStackTrace();
						//System.exit(-1);
					} catch (UnrecoverableKeyException e) {
						Toast.makeText(v.getContext(), "UnrecoverableKeyException", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"unrecoverableKeyException");
						e.printStackTrace();
						//System.exit(-1);
					} catch (KeyManagementException e) {
						Toast.makeText(v.getContext(), "KeyManagementException", Toast.LENGTH_SHORT).show();
						Log.i(TAG,"key management exception");
						e.printStackTrace();
						//System.exit(-1);
					}
				}

			}
		});

	}

	private void printServerCertificate(SSLSocket socket) {
		try {
			Certificate[] serverCerts =
				socket.getSession().getPeerCertificates();
			for (int i = 0; i < serverCerts.length; i++) {
				Certificate myCert = serverCerts[i];
				Log.i(TAG,"====Certificate:" + (i+1) + "====");
				Log.i(TAG,"-Public Key-\n" + myCert.getPublicKey());
				Log.i(TAG,"-Certificate Type-\n " + myCert.getType());

				System.out.println();
			}
		} catch (SSLPeerUnverifiedException e) {
			Log.i(TAG,"Could not verify peer");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	private void printSocketInfo(SSLSocket s) {
		Log.i(TAG,"Socket class: "+s.getClass());
		Log.i(TAG,"   Remote address = "
				+s.getInetAddress().toString());
		Log.i(TAG,"   Remote port = "+s.getPort());
		Log.i(TAG,"   Local socket address = "
				+s.getLocalSocketAddress().toString());
		Log.i(TAG,"   Local address = "
				+s.getLocalAddress().toString());
		Log.i(TAG,"   Local port = "+s.getLocalPort());
		Log.i(TAG,"   Need client authentication = "
				+s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		Log.i(TAG,"   Cipher suite = "+ss.getCipherSuite());
		Log.i(TAG,"   Protocol = "+ss.getProtocol());
	}

	public void chat(String temp){
		String message = temp;
		String line = "";
		// send id of the device to match with the image
		try {
			out.write(message+"\n");
			out.flush();
		} catch (IOException e2) {
			Log.i(TAG,"Read failed");
			System.exit(1);
		}
		// receive a ready command from the server
		try {
			line = in.readLine();
			mResponse.setText("SERVER SAID: "+line);
			//Log.i(TAG,line);
		} catch (IOException e1) {
			Log.i(TAG,"Read failed");
			System.exit(1);
		}
	}
}
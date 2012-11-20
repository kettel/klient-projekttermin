import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import com.ssl.client.R;

public class SSLSocketMain {

	/**
	 *Kollar att man har skickat in port och keystoren i klassen
	 * @param args
	 */
	public static void main(String[] args) {
		//if (args.length != 2){
		//	System.out.println("Wrong number of arugments");
		//	System.out.println("Usage: SSLSocketMain.java <port> <path to key>");
		//	System.exit(-1);
		//}
		/**
		 * L�ser upp keystoren
		 */
		int socket = 9998;//ni kan nog skriva in port h�r om ni vill
		//String keystore = args[1];// l�g in vart keystoren ska ligga s� b�r det funka. om ni g�t detta s� ta bort if ovan annars kommer ni in vidare
		char keystorepass[] = "password".toCharArray();
		char keypassword[] = "password".toCharArray();
		char truststorepass[] = "password".toCharArray();
		SSLServerSocket serverSocket = null;
		SSLSocket client;
		/**
		 * packar upp nyckeln och s�tter kommunikationen till TLS och skapar sedan 
		 * socketen. servern funkar nu med tls
		 */

		try {
			KeyStore ts = KeyStore.getInstance("JKS");
			ts.load(new FileInputStream("c:\\cert\\clienttrust"),truststorepass);
			
			TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
			
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("c:\\cert\\serverkey"),keystorepass);
			KeyManagerFactory kmf =
				KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, keypassword);

			SSLContext sslcontext =
				SSLContext.getInstance("TLS");

			sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			ServerSocketFactory ssf =
				sslcontext.getServerSocketFactory();

			serverSocket = (SSLServerSocket)
			ssf.createServerSocket(socket);
			System.out.println("Starting server...");
			/**
			 * om n�got g�r fel kastas n�gon av dessa fel
			 */
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not listen on port "+socket);
			System.exit(-1);
		} catch (KeyStoreException e) {
			System.out.println("Could not get key store");
			System.exit(-1);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("There is no algorithm in ks.load");
			e.printStackTrace();
			System.exit(-1);
		} catch (CertificateException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (UnrecoverableKeyException e) {
			System.out.println("kmf.init() no key");
			System.exit(-1);
		} catch (KeyManagementException e) {
			System.out.println("sslcontext.init keymanagementexception");
			System.exit(-1);
		}
		/**
		 * server l�ser in str�ngen och sedan svar med samma str�ng
		 */
			try {
				client = (SSLSocket) serverSocket.accept();
				System.out.println("client connected");
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				String message;
				message = in.readLine();
				System.out.println("Client's message: "+message);
				System.out.println("Responding same message: "+message);
				out.write(message);
				out.flush();
				out.close();
				in.close();
				client.close();
				serverSocket.close();
				/**
				 * om man �r d�lig kommer man hit
				 */
			} catch (IOException e) {
				System.out.println("Accept failed on "+socket);
				e.printStackTrace();
				System.exit(-1);
			}
	}

}

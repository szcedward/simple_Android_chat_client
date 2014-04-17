package com.test.client;

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


/**
 * @author Zhongchen Shen
 *
 */
public class TCPClient {

	private String serverMessage;
	public static String SERVERIP = "";
	public static final int SERVERPORT = 9998;
	private OnMessageReceived mMessageListener = null;
	private boolean mRun = false;
	private String remoteIp;
	private String localIp;
	private Socket socket;
	
	PrintWriter out;
	BufferedReader in;
	
	/**
	 * constructor of the class. OnMessageReceived listens for the messages 
	 * received from server
	 */
	public TCPClient(OnMessageReceived listener) {
	    mMessageListener = listener;
	}
	
	/**
	 * 
	 * @param listener
	 */
	public TCPClient(OnMessageReceived listener, String serverIpAddr) {
	    mMessageListener = listener;
	    SERVERIP = serverIpAddr;
	}
	
	/**
	 * Sends the message entered by client to the server
	 * @param message text entered by client
	 */
	public void sendMessage(String message){
	    if (out != null && !out.checkError()) {
	        out.println(message);
	        out.flush();
	    }
	}
	
	public void stopClient() {
	    mRun = false;
	}
	
	public void run() {
	    mRun = true;
	
	    try {
	        // here you must put your computer's IP address.
	        InetAddress serverAddr = InetAddress.getByName(SERVERIP);
	
	        Log.i("TCP Client", "Client: Connecting...");
	
	        //create a socket to make the connection with the server
	        socket = new Socket(serverAddr, SERVERPORT);
	        localIp = socket.getLocalAddress().getHostAddress();
	        remoteIp = parseIp(socket.getRemoteSocketAddress().toString());
	
	        try {
	            //send the message to the server
	            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	
	            Log.i("TCP Client", "Client: Sent.");
	            Log.i("TCP Client", "Client: Done.");
	
	            //receive the message which the server sends back
	            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
	            //in this while the client listens for the messages send by the server
	            while (mRun) {
	                serverMessage = in.readLine();
	                if(serverMessage.length() < 1)
	                	Log.e("null", "receive is ''");
	
	                if (serverMessage != null && mMessageListener != null && serverMessage.length() > 1) {
	                	System.out.println("hear from server ! " + serverMessage);
	                    //call the method messageReceived from MyActivity class
	                    mMessageListener.messageReceived(serverMessage);
	                    Log.i("RESPONSE FROM SERVER", "Sever: Received Message: '" + serverMessage + "'");
	                }
	                serverMessage = null;
	            }
	
	        } catch (Exception e) {
	
	            Log.e("TCP", "Sever: Error", e);
	
	        } finally {
	            socket.close();
	        }
	    } catch (Exception e) {
	        Log.e("TCP", "Client:Error", e);
	    }
	}
	
	/**
	 * 
	 *
	 * Declare the interface. The method messageReceived(String message must be
	 * implemented in the MyActivity class at on asynckTask doInBackground
	 */
	public interface OnMessageReceived {
	    public void messageReceived(String message);
	}
	
	public String getRemoteIp() {
		return remoteIp;
	}
	
	public String getLocalIp() {
	
		return localIp;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	private String parseIp(String ipAndPort)
	{
		System.out.println(ipAndPort);
		String delims = "[/:]";
		String[] tokens = ipAndPort.split(delims);
		System.out.println(tokens.length);
		return tokens[1];
	}

}

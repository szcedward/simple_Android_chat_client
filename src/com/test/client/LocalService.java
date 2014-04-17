package com.test.client;



import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Zhongchen Shen
 *
 */
public class LocalService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private TCPClient myTcpClient;
	private String ipAddr = "";

	NetworkTask networkTask;
	
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
    	return mBinder;
    }
    
    public void CreateClientSocketAndRun()
    {
    	networkTask = new NetworkTask();
		networkTask.execute();
    }
    
    public boolean stopClientSocket()
    {
    	if(myTcpClient != null)
    	{
    		try
    		{
    			myTcpClient.getSocket().close();
    			return true;
    		}
    		catch(IOException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	else
    		return false;
    	return false;

    }
    
    public void sendMessage(String message)
    {
    	
    	if (myTcpClient != null) {
            myTcpClient.sendMessage(message);
        }
    }
    
    @SuppressLint("NewApi")
	public class NetworkTask extends AsyncTask<String,String,TCPClient>
	{
		private Context context;

         public void ConnectTask(Context context) {
             this.context = context;
         }
         @Override
         protected void onPreExecute() 
         {
        	 Log.i("AsyncTask", "onPreExecute");
         }
		
         @Override
         protected TCPClient doInBackground(String... message)
         {
        	 //we create a TCPClient object and
        	 myTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
	
        		 @Override
        		 //here the messageReceived method is implemented
        		 public void messageReceived(String message) {
        			 //this method calls the onProgressUpdate
        			 publishProgress(message);
        		 }
        	 }, ipAddr);
        	 myTcpClient.run();
			return null;
		}
			
		@Override
	    protected void onProgressUpdate(String... values) {
	        super.onProgressUpdate(values);
	        Intent intent = new Intent("myLocalBroadCast");
	        intent.putExtra("key_response", values[0]);
	        LocalService.this.sendBroadcast(intent);
	        //editReceive.setText(values[0]);
	        
	    }
	}
    public TCPClient getMyTcpClient() {
		return myTcpClient;
	}
    
    public void setServerIpAddr (String serverIpAddr) {
    	this.ipAddr = serverIpAddr;
    }
	
}

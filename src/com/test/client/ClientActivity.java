package com.test.client;

import java.io.BufferedReader; 
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.test.client.R;
import com.test.client.LocalService.LocalBinder;
import com.test.database.*;


/**
 * @author Zhongchen Shen
 *
 */
public class ClientActivity extends Activity{
		String ipAddr;
		String PushedGCMMessage;
		Socket socket;
		BufferedReader br;
		PrintStream ps;
		Button sendButton;
		Button startButton;
		Button stopButton;
		Button exitButton;
		Button fileButton;
		EditText editText;
		EditText editReceive;
		TextView tvRegStatusResult;
		TextView tvBroadcastMessage;

		//MyHandler handle;
		/*NetworkTask networkTask;
		private TCPClient myTcpClient;*/
		LocalService mService;
		boolean mBound = false;
		
		// This is the project id generated from the Google console when
		// you defined a Google APIs project.
		private static final String PROJECT_ID = "1097242790689";
		private static final String SENDER_ID_ANDROID = "AIzaSyCQXjqqNy04wnUjyKDYjPrnGWugTRo8Wyk";

		// This tag is used in Log.x() calls
		private static final String TAG = "MainActivity";

		// This string will hold the lengthy registration id that comes
		// from GCMRegistrar.register()
		private String regId = "";

		// These strings are hopefully self-explanatory
		private String registrationStatus = "Not yet registered";
		private String broadcastMessage = "No broadcast message";

		// This intent filter will be set to filter on the string "GCM_RECEIVED_ACTION"
		IntentFilter gcmFilter;
		IntentFilter socketMessageFilter;
		
		DatabaseHandler db;

		
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			Log.d("onCreate", "onCreate() is called.");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_client);
			
			//Intent intent = getIntent();
			//ipAddr = new String(intent.getStringExtra("com.test.client.IPaddr"));
			//System.out.println("IP Address is " + ipAddr);

			editReceive = (EditText) this.findViewById(R.id.editReceive);
			editText = (EditText) this.findViewById(R.id.editText);
			sendButton = (Button) this.findViewById(R.id.sendButton);
			startButton = (Button) this.findViewById(R.id.startButton);
			stopButton = (Button) this.findViewById(R.id.stopButton);
			exitButton = (Button) this.findViewById(R.id.exitButton);
			fileButton = (Button) this.findViewById(R.id.fileButton);
			tvBroadcastMessage = (TextView) findViewById(R.id.tv_message);
			tvRegStatusResult = (TextView) findViewById(R.id.tv_reg_status_result);
			//networkTask = new NetworkTask();
			//networkTask.execute();
			
			Intent intent = getIntent();
			if(intent.hasExtra("com.test.client.PushedGCMMessage"))
			{
				Log.d("Push Resume", "has extra PushedGCMMessage");
				PushedGCMMessage = new String(intent.getStringExtra("com.test.client.PushedGCMMessage"));
				System.out.println(PushedGCMMessage);
				tvBroadcastMessage.setText(PushedGCMMessage);
				
			}
			
			//get the ip address of the chat server
			if(intent.hasExtra("com.test.client.IPaddr"))
			{
				Log.d("Push Resume", "has extra IPaddr");
				ipAddr = new String(intent.getStringExtra("com.test.client.IPaddr"));
				System.out.println("ip address to connect is: " + ipAddr);
				
			}
			
			socketMessageFilter = new IntentFilter("myLocalBroadCast");
			gcmFilter = new IntentFilter("GCM_RECEIVED_ACTION");
			
			registerReceiver(mMessageReceiver, socketMessageFilter);
			registerReceiver(gcmReceiver, gcmFilter);

			registerClient();
			
			db = new DatabaseHandler(ClientActivity.this);
			Log.d("Handler", "initiate DB handler.");
			
			//Thread t = new Thread(new GCMClientHandler());
			
			//non-service click handler
/*			sendButton.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {

		            String message = editText.getText().toString();

		            //sends the message to the server
		            if (myTcpClient != null) {
		                myTcpClient.sendMessage(message);
		            }
		        }
		    });*/
			
			
			//service click handler
			sendButton.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {

		            String message = editText.getText().toString();

		            //sends the message to the server
		            mService.sendMessage(message);
		        }
		    });
			
			//Start service button click listener
			startButton.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		        	Log.d("mBound", "current mBound == " + mBound);
		        	if(mBound)
		        	{
		        		if(mService.getMyTcpClient() != null
		        				&& mService.getMyTcpClient().getSocket() != null
		        				&& mService.getMyTcpClient().getSocket().isConnected()
		        				&& !mService.getMyTcpClient().getSocket().isClosed())
		        		{
		        			editReceive.setText("Server already connected.");
		        			return;
		        		}
		        		mService.setServerIpAddr(ipAddr);
		        		mService.CreateClientSocketAndRun();
	        			System.out.println("mBound == true");
		        	}
		        	else
		        		System.out.println("mBound == false");
		        }
		    });
			
			stopButton.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		        	if(!mService.stopClientSocket())
		        		editReceive.setText("Connection has not been established!");
		        	else
		        		editReceive.setText("Connection closed");
		        }
		    });
		
			fileButton.setOnClickListener(new OnClickListener() 
			{       
				public void onClick(View v)      
				{         
					System.out.println("benfile");               
				}      
			}); 
			
			exitButton.setOnClickListener(new OnClickListener() 
			{          
				public void onClick(View v) 
				{    
					System.exit(0);//kill thread
					ClientActivity.this.finish();//finish activity
				}       
	        });
			// etReceive.setOnKeyListener(new ) ;  
		}
		
		@Override
		public void onStart()
		{
			Log.d("onStart", "onStart() is called.");
			super.onStart();
			Intent intent = new Intent(this, LocalService.class);
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	        //while(!mBound);
	        //if(mBound)
	        	//System.out.println("binder is returned.");
	        //mService.CreateClientSocketAndRun();
	        
		}
		
		@Override
		protected void onResume() {
			Log.d("onResume", "onResume() is called.");
			super.onResume();
			registerReceiver(mMessageReceiver, socketMessageFilter);
			registerReceiver(gcmReceiver, gcmFilter);
		}
		
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
			super.onSaveInstanceState(savedInstanceState);
			savedInstanceState.putString("BroadcastMessage", broadcastMessage);
		}
		
		@Override
		public void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			broadcastMessage = savedInstanceState.getString("BroadcastMessage");
			tvBroadcastMessage.setText(broadcastMessage);
		}
		
		@Override
		protected void onPause() {
			Log.d("onPause", "onPause() is called.");
			//unregisterReceiver(gcmReceiver);
			super.onPause();
		}
		
		@Override
		protected void onDestroy()
		{
			Log.d("onDestroy", "onDestroy() is called.");
			super.onDestroy();
			if (mBound) {
	            unbindService(mConnection);
	            mBound = false;
	        }
			unregisterReceiver(mMessageReceiver);
			unregisterReceiver(gcmReceiver);
			GCMRegistrar.onDestroy(this);
			//db.deleteTable("messageRecords");
		}

		
		/** Defines callbacks for service binding, passed to bindService() */
	    private ServiceConnection mConnection = new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	        	System.out.println("onServiceConnected is called!");
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	            LocalBinder binder = (LocalBinder) service;
	            mService = binder.getService();
	            mBound = true;
	            System.out.println("bind successful");
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            mBound = false;
	        }
	    };

		private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String responseData = intent.getStringExtra("key_response");
		        String senderIp = mService.getMyTcpClient().getLocalIp();
		        String receiverIp = mService.getMyTcpClient().getRemoteIp();
		        String dataTime = getDateTime();
		        //System.out.println(dateFormat.format(dateTime));
		        editReceive.setText(responseData);
		        Toast.makeText(ClientActivity.this, responseData, Toast.LENGTH_LONG).show();
		        //insert into DB
		        //get sender ip
		        
		        Log.d("Insert", "Inserting...");
		        db.addMessageRecord(new MessageRecord(receiverIp, senderIp, dataTime, responseData));
		        Log.d("Insert", "Done.");
		        List<MessageRecord> records = new ArrayList<MessageRecord>();
		        List<String> colNames = new ArrayList<String>();
		        colNames = db.getColumnNames("messageRecords");
		        records = db.getAllMessageRecords();
		        //print the table
		        System.out.println("Message Records:");
		        for(int i = 0; i < colNames.size(); i++)
		        {
		        	System.out.print(colNames.get(i) + "\t");
		        }
		        System.out.println();
		        for(int i = 0; i < records.size(); i++)
		        {
		        	System.out.println(records.get(i).getId() + "\t" 
		        					+ records.get(i).getSenderIp() + "\t" 
		        					+ records.get(i).getReceiverIp() + "\t" 
		        					+ records.get(i).getTime() + "\t" 
		        					+ records.get(i).getContent() + "\t" 
		        					);
		        }
		        System.out.println();
		    }
		};
		
		private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				broadcastMessage = intent.getStringExtra("gcm");
				if (broadcastMessage != null) {
					// display our received message
					tvBroadcastMessage.setText(broadcastMessage);
					Toast.makeText(ClientActivity.this, "Broadcast: " + broadcastMessage, Toast.LENGTH_LONG).show();
				}
			}
		};
		
		public void registerClient() {
			try {
				// Check that the device supports GCM (should be in a try / catch)
				GCMRegistrar.checkDevice(this);
				// Check the manifest to be sure this app has all the required
				// permissions.
				GCMRegistrar.checkManifest(this);
				// Get the existing registration id, if it exists.
				regId = GCMRegistrar.getRegistrationId(this);
				if (regId.equals("")) {
					registrationStatus = "Registering...";
					tvRegStatusResult.setText(registrationStatus);
					// register this device for this project
					GCMRegistrar.register(this, PROJECT_ID);
					regId = GCMRegistrar.getRegistrationId(this);
					registrationStatus = "Registration Acquired";
					// This is actually a dummy function.  At this point, one
					// would send the registration id, and other identifying
					// information to your server, which should save the id
					// for use when broadcasting messages.
					sendRegistrationToServer();
				} else {
					registrationStatus = "Already registered";
				}
				Log.i("reg id", "reg id = " + regId);
			} catch (Exception e) {
				
				e.printStackTrace();
				registrationStatus = e.getMessage();
			}
			Log.d(TAG, registrationStatus);
			tvRegStatusResult.setText(registrationStatus);
			// This is part of our CHEAT.  For this demo, you'll need to
			// capture this registration id so it can be used in our demo web
			// service.
			Log.d(TAG, regId);
		}
		
		private void sendRegistrationToServer() {
			// This is an empty placeholder for an asynchronous task to post the
			// registration
			// id and any other identifying information to your server.
		}
		
		
/*		@SuppressLint("NewApi")
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
	        	 });
	        	 myTcpClient.run();
				return null;
			}
				
			@Override
		    protected void onProgressUpdate(String... values) {
		        super.onProgressUpdate(values);
		        Intent intent = new Intent("myLocalBroadCast");
		        //intent.putExtra("key_response", values);
		        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		        editReceive.setText(values[0]);
		    }
		}*/

		
		
		public String timeFormat(int time)   
		{      
			if (time < 10) 
				return "0" + time;     
			else 
				return "" + time;   
		}
		
		private String getDateTime()
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			return dateFormat.format(date);
		}

}

package com.test.client;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.test.client.R;

/**
 * @author Zhongchen Shen
 *
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String PROJECT_ID = "1097242790689";
    
	private static final String TAG = "GCMIntentService";
	
	public GCMIntentService()
	{
		super(PROJECT_ID);
		Log.d(TAG, "GCMIntentService init");
	}
	

	@Override
	protected void onError(Context ctx, String sError) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Error: " + sError);
		
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMessage(Context ctx, Intent intent) {
		
		Log.d(TAG, "Message Received");
		
		String message = intent.getStringExtra("message");
		
		sendGCMIntent(ctx, message);
		
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, ClientActivity.class), 0);
		Intent resultIntent = new Intent(this, ClientActivity.class);
		resultIntent.putExtra("com.test.client.PushedGCMMessage", message);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(ClientActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		            .setSmallIcon(R.drawable.ic_launcher)
		            .setContentTitle("New Message")
		            .setContentIntent(resultPendingIntent)
		            .setPriority(Notification.PRIORITY_HIGH)
		            .setContentText(message)
		            .setAutoCancel(true);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
		
	}

	
	private void sendGCMIntent(Context ctx, String message) {
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("GCM_RECEIVED_ACTION");
		
		broadcastIntent.putExtra("gcm", message);
		
		ctx.sendBroadcast(broadcastIntent);
		
	}
	
	
	@Override
	protected void onRegistered(Context ctx, String regId) {
		// TODO Auto-generated method stub
		// send regId to your server
		Log.d(TAG, regId);
		
	}

	@Override
	protected void onUnregistered(Context ctx, String regId) {
		// TODO Auto-generated method stub
		// send notification to your server to remove that regId
		
	}

}

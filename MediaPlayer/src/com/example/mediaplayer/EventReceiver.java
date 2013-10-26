package com.example.mediaplayer;

import com.example.mediaplayer.MediaService.MediaBinder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/*
 * The class use to handle the event when screen off
 */
public class EventReceiver extends BroadcastReceiver {
	
	private MediaBinder mpInterface;
	
	  //handle when receiving the broadcast signal (SCREEN OFF)	
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  Log.i("info","recevier"); 
		  
		  //Creating the service intent and inform the broadcast receiver has been received (to stop mediaplayer).
		  Intent service = new Intent(context,MediaService.class);
		  service.putExtra("broadcast", "true");
		  context.startService(service); 
		  context.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
		  //unbind service after bind
		  context.unbindService(mConnection);
	  }
	  
	  
	  //Do nothing when on connected
	  private ServiceConnection mConnection = new ServiceConnection() 
	    {
	            public void onServiceConnected(ComponentName className, IBinder service) {
	        		Log.i("info","onServiceConnected");
	                mpInterface = (MediaBinder) service;
	            }
	     
	            public void onServiceDisconnected(ComponentName className) {
	            	Log.i("info","onServiceDisconnected");
	                mpInterface = null;
	            }
	    };
	    
	} 
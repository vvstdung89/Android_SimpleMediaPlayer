package com.example.mediaplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MediaService extends Service implements OnCompletionListener{
	
	//Receiver handler
	private EventReceiver  mHomeBrodcast;
	//Media Player
	private MediaPlayer mp ;
	//Data of songs
	private ArrayList<HashMap<String, String>> songsListData;
	//playing status
    private int current_id = 0;
    private int playing_id = -1;
	private int reset = 0;
    private int current_state; // 1:playing  0: stop	
	
	// This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new MediaBinder();
    
    //OnCreae of Service class
    @Override
    public void onCreate() {
    	//Create instance to handle situation when broadcast receiver receive signal
    	mHomeBrodcast= new EventReceiver();
        //Create an intent for broadcast receiver and set action which the broadcast receiver will sense on
        //HERE: when we  turn the screen of smart phone off, the broadcast receiver class will execute
        IntentFilter mHomeFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //register handle class with the broadcast receiver         
        registerReceiver(mHomeBrodcast, mHomeFilter);   
    	
        //initialize mediaplayer and status information
    	mp = new MediaPlayer();
        SongsManager songManager = new SongsManager(this);
        songsListData = songManager.getPlayList();
        mp.setOnCompletionListener(this);
        current_id = 0;
    }

	//this function will execute whenever there is a bind to service interface
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("info", "onStartCommand " + current_id);
        //receive the intent information from EventReceiver for pausing the media player
        if (intent !=null) 
	        if (intent.getExtras() !=null && intent.getExtras().get("broadcast") != null )
	        	if (intent.getExtras().get("broadcast").equals("true") && mp.isPlaying())
	        		mp.pause();
        
        //no need to reset
        reset = 0;
        
        return START_STICKY;
    }
	
	//unregister broadcast receiver onDestroy
    @Override
    public void onDestroy() {
    	mp.stop();
        mp.release();
        this.unregisterReceiver(mHomeBrodcast);
    }
    
    //unregister broadcast receiver onDestroy
    @Override
    public IBinder onBind(Intent intent) {
    	Log.i("info", "onBind");
        return mBinder;
    }
    
	//nested class, extends the binder which is the interface to communicate with the MediaService 
    public class MediaBinder extends Binder {
    	
    	// API interface which play the current_id song
    	public void play()  {
    		if (!mp.isPlaying()) {
    			if ((playing_id != current_id) || (reset == 1)){
        			mp.reset();
                	try {
        				mp.setDataSource(songsListData.get(current_id).get("songPath"));
        				mp.prepare();
        			} catch (IllegalArgumentException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalStateException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
                	playing_id = current_id;
                	reset = 0;
        		}
        		mp.start();
    		}
    	}
    	
    	// API interface which play the specified song
    	public void playFile(int position)  {
    		current_id = position;
    		
			mp.reset();
        	try {
				mp.setDataSource(songsListData.get(current_id).get("songPath"));
				mp.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	playing_id = current_id;
        	mp.start();
		}
		
    	// API interface which play previous song
		public void skipBack()  {
			current_id--;
			if (current_id < 0)
				current_id = songsListData.size()-1;
			//reset media player
			reset = 1;
			//Broadcast to PlaySong Activity to update song name
			Intent intent = new Intent("updateName-event");
		    sendBroadcast(intent);
		    
			if (mp.isPlaying()) playFile(current_id);
		}
		
		// API interface which play the next song
		public void skipForward()  {
			current_id++;
			if  (current_id >= songsListData.size()){
				current_id = 0;
			}
			//reset media player
			reset =1;
			//Broadcast to PlaySong Activity to update song name
			Intent intent = new Intent("updateName-event");
		    sendBroadcast(intent);
			if (mp.isPlaying()) playFile(current_id);
		}
		
		// API interface for pausing		
		public void pause()  {
			mp.pause();
		}
		
		// API interface for stoping
		public void stop()  {
			mp.stop();
		}
		
		// API interface which gets current song ID
		public int getCurrent_id(){
			return current_id;
		}
		
		// API interface which gets current song name
		public String getCurrent_SongName(){
			return songsListData.get(current_id).get("songName").toString();
		}
		
		// API interface which gets media player status
		public boolean isPlaying()  {
			return mp.isPlaying();
		}
		
	}
    
    //Implement OnCompletionListener for media player
    //if song is end, play next song
    @Override
   	public void onCompletion(MediaPlayer mp) {
   		// TODO Auto-generated method stub
       	current_id++;
   		if  (current_id >= songsListData.size()){
   			current_id = 0;
   		}
   		
   		mp.reset();
       	try {
       		
   			mp.setDataSource(songsListData.get(current_id).get("songPath"));
   			mp.prepare();
   		} catch (IllegalArgumentException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (SecurityException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (IllegalStateException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
       	playing_id = current_id;
       	mp.start();
       	Log.i("info","onCompletetion " + current_id);
       //Broadcast to PlaySong Activity to update song name
       	Intent intent = new Intent("updateName-event");
   	    sendBroadcast(intent);
   	}
}

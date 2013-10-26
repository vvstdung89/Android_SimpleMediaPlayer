package com.example.mediaplayer;


import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.TextView;

import com.example.mediaplayer.MediaService.MediaBinder;

public class PlaySong extends Activity implements OnClickListener  {
	
	//playSong  status information
	private int current_id;
	
	private int current_state; // 1:playing  0: stop
	
	private int action;    // 1:restart 0: continue;
	
	//the binder interface for MediaService 
	private MediaBinder mpInterface;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        
        //Register onclick event for 4 buttons 
        Button return_but = (Button)findViewById(R.id.returnMain);
        Button start_stop = (Button)findViewById(R.id.start_stop);
        Button previous  = (Button)findViewById(R.id.previous);
        Button next  = (Button)findViewById(R.id.next);
        
        start_stop.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        return_but.setOnClickListener(this);
        
	}
	
	
	@Override
	public void onResume(){
	    super.onResume();
	    Log.i("info","onResume " + current_state);
	    //Get extra information
        Bundle extras = getIntent().getExtras();
	   //checking the activityState 
        if (extras.get("activityState").toString().equals("nowPlaying")){ //from button now playing resume
        	action = 0; //continue
        }
        else { // from click to song in the songlist
        	current_id = (int)extras.getLong("songPlay"); 
        	current_state = 1;
        	action =1; //play new one
        }
        
	    //start MediaService
        Intent service = new Intent(this,MediaService.class);
        this.startService(service);
        //bind the service with mConnection
        this.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        
        
        // Register mMessageReceiver to receive updatename messages from media service.
		this.registerReceiver(mMessageReceiver,  new IntentFilter("updateName-event"));
		
		//checking the status of mediaplayer and update button status (stop/start)
        checkButton();
	}
	
	
	public void onPause(){
		super.onPause();
		
		//reupdate the activityState to nowPLaying (incase resize screen -> the activity need to continue to play)
		getIntent().putExtra("activityState", "nowPlaying");
		
		//if exit the PlaySong
		//unbind the media binder
		this.unbindService(mConnection);
		//unregister the update name broadcast receiver
		this.unregisterReceiver(mMessageReceiver);
		Log.i("info","onPause PlaySong");
	}
	
	
	//whenever media service is connected
	//getting its status information
	//and update necessary info (song name)
	 private ServiceConnection mConnection = new ServiceConnection() 
	    {
	            public void onServiceConnected(ComponentName className, IBinder service) {
	        		Log.i("info","onServiceConnected");
	                mpInterface = (MediaBinder) service;
	                if (action == 1) {
	                	mpInterface.playFile(current_id);
	                	current_state = 1;
	                } else {
	                	current_state = mpInterface.isPlaying() ? 1 : 0;
	                }
	                current_id = mpInterface.getCurrent_id();
	                updateName();
	                
	            }
	     
	            public void onServiceDisconnected(ComponentName className) {
	            	Log.i("info","onServiceDisconnected");
	                mpInterface = null;
	            }
	    };
	
	
	
	
	    // handler for received Intents for the "updateName-event" event from MediaSerive
		private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Extract data included in the Intent
		    updateName();
		  }
		};
		
		//receive current song from media player and update in the TextView
		public void updateName(){
			TextView nameView = (TextView) findViewById(R.id.playSong_text_songName);
	        nameView.setText(mpInterface.getCurrent_SongName());
	        checkButton();
		}
		
		//check the state of start/stop button -> consistent with media player 
		public void checkButton(){
			int state = 1; //1: play, 0: stop
			if ((current_state == 1) || (mpInterface !=null && mpInterface.isPlaying())){
				state = 1;
				
			}
			else
				state = 0;
			TextView text = (TextView) findViewById(R.id.start_stop);
			switch (state) {
				case 1:
					text.setText("Stop");
					text.invalidate();
					break;
				case 0:
					text.setText("Start");
					text.invalidate();
					break;
			}
		}
		
	//Implement OnClickListen	
	@Override
	public void onClick(View v) {
		Log.i("info","Click");
		switch(v.getId()) {
			case R.id.returnMain:
				this.finish();
				break;
			case R.id.start_stop:
					if (current_state == 1){
						mpInterface.pause();
						current_state = 0;
						checkButton();
					} else {
						mpInterface.play();
						current_state = 1;
						checkButton();
					}
				break;
			case R.id.previous:
				mpInterface.skipBack();
				current_id = mpInterface.getCurrent_id();
				updateName();
				break;
			case R.id.next:
				mpInterface.skipForward();
				current_id = mpInterface.getCurrent_id();
				updateName();
				break;
			
		}
	}
	
	
}

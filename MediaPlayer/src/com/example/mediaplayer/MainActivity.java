package com.example.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

/*
 * Application: MediaPlayer 
 * Author: VAN QUOC DUNG
 * 
 * INFORMATION
 * Two activities: 
 *  - MainActivity.java : list of song using ListAdapter (dynamically arrange View)
 *  - PlaySong.java: register media Service, control button and use the binder interface
 * Service: 
 *  - MediaService.java: mediaplayer instance, provide binder interface, broadcast signal for update name song (PlaySong receive)
 * BroadcastReceiver: EventReceiver.java  , register of broadcast receiver in MediaService.java (when device screen is off)
 * Helper classes: 
 * 	- SongAdapter.java: template for a View in ListView
 *  - SongManager.java: retrieve songs form external and internal device
 *  
 */
public class MainActivity extends Activity implements OnItemClickListener, OnClickListener{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set onClickListenner to handle onCLick event in the Button
		Button button = (Button) findViewById(R.id.nowPlaying);
		button.setOnClickListener(this);
		  
        //Retrieving ListView by finding its ID
        ListView list = (ListView) findViewById(R.id.listView);
        
        //Create List Adapter instance which is the handle for dynamically arrange View in ListView
        ListAdapter adapter = new SongAdapter(this);
        
        //set ListView to use above list adapter
        list.setAdapter(adapter);
        
        //set onItemClickListenner to handle onCLick event in the ListView
        list.setOnItemClickListener(this);
		        
           
    }
	
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	
    	// create the Intent to open PlaySong activity
    	Intent i = new Intent(this, PlaySong.class);
   
    	//put extra information to the intent which will communicate with PlaySong activity
    	i.putExtra("songPlay", id);
    	i.putExtra("activityState", "new");
    	
    	// start PlaySong activity
    	startActivity(i);
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.nowPlaying:
				// create the Intent to open PlaySong activity
		    	Intent i = new Intent(this, PlaySong.class);
		   
		    	//put extra information to the intent which will communicate with PlaySong activity
		    	i.putExtra("activityState", "nowPlaying");
		    	
		    	// start PlaySong activity
		    	startActivity(i);
				break;
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

package com.example.mediaplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * This class is extended from BaseAdapter
 * It is used to dynamically arrange View in the ListView of MainActivity
 */
public class SongAdapter extends BaseAdapter {
	
	//current context information
	private Context myContext;
	
	//variable to store list of song
	//Each song has the struture of a hashMap which map "songPath", "songName", "songArtist" to the real value.
	private ArrayList<HashMap<String, String>> songsListData;
	
	//construct SongAdapter
	public SongAdapter(Context c){
		//Store the passing context information
		myContext = c;
		//create SongManager instance
		SongsManager plm = new SongsManager(myContext);
        //get all Song and pass it in to songsListData
        songsListData = plm.getPlayList();
	}
	
	//ALL of the below is the override method of the Base Adapter
	//Make sure the class can function properly
	
	//get the number of song -> number of View in List View
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return songsListData.size();
	}

	//get the item which specific position -> get song for show in the View
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		
		return songsListData.get(position);
	}
	
	//get the ID for specific position -> here, the song ID also is same with its position in the ListView
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	//return the View for each position (or song)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//We want to use TextView in the ListView
		//so, contruct TextView variable
		TextView text;
	
		//handle if we need to create a new view or not 
		if(convertView == null) {
			// create a new view
			text = new TextView(myContext);
		} else {
			// recycle an old view (it might have old thumbs in it!)
			text = (TextView) convertView;
		}
		
		//set content for TextView
		text.setText(((HashMap)getItem(position)).get("songName").toString() + " - by " +((HashMap)getItem(position)).get("songArtist").toString());

		//return View
		return text;
	}

}

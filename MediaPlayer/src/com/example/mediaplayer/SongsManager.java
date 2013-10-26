package com.example.mediaplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

/*
 * This class to manage list of Song
 * There one main fuction is retrieve the list of Song from the External Device Storage
 */

public class SongsManager {
	
	//variable to store list of song
	//Each song has the structure of a hashMap which map "songPath", "songName", "songArtist" to the real value.
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    
    //current context information
    private Context context;
    
    // Constructor
    public SongsManager(Context c){
    	//Store context information
    	context = c;
    }
 
    //get list of song and put into songsList with pre-defined format
    public ArrayList<HashMap<String, String>> getPlayList(){
        
    	/*
    	 * INFO: we use Content Provider to list all the song
    	 * handling the Content Provider also like handling database, particularly in Android, it is SQLLite
    	 * we use EXTERNAL_CONTENT_URI to retrieve song from external device 
    	 * Each content URI like a table, which have column is its information and row is the record of the song
    	 */
    	
    	// A "projection" defines the columns that will be returned for each row
		String[] mProjection =
		{
			MediaStore.Audio.Media.DATA,    		//path to the song
			MediaStore.Audio.Media.DISPLAY_NAME,    //name of the song
			MediaStore.Audio.Media.ARTIST   		//artist of the song
		};

		//EXTERNAL STORAGE query
		
		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getApplicationContext().getContentResolver().query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,  // The content URI of the External Content URI
		    mProjection,                       // The columns to return for each row
		    null,                   // Either null, or the word the user entered
		    null,                    // Either empty, or the string the user entered
		    null);                       // The sort order for the returned rows

		// Some providers return null if an error occurs, others throw an exception
		if (null == mCursor) {
			Log.e("MediaPlayer","Media Provider return null");
		// If the Cursor is empty, the provider found no matches
		} else if (mCursor.getCount() < 1) {
			Log.i("MediaPlayer","No sound file");
		} else {
			//if at least one, we traverse all by move mCursor to each record
			while (mCursor.moveToNext()) {
				//Instance for each record song
				HashMap<String, String> song = new HashMap<String, String>();
		        
				//get filename
		        String file_name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		        
		        // extract filename without  file extensin 
		        if (file_name.contains(".")){
			        //split strring by dot character
		        	String[] split_dot = file_name.split("\\.");
			        file_name="";
			        //handle filename which has multiple dots in a file
			        for (int i=0; i < (split_dot.length-1);i++){
			        	file_name = file_name + "." + split_dot[i];
			        }
			        file_name = file_name.substring(1,file_name.length());
		        }
		        
		        //put infprmation to the song record
	        	song.put("songPath",mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
	        	song.put("songName",file_name);
	        	song.put("songArtist",mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
		        
	        	//add song record to songsList data
	        	songsList.add(song);
		        
			}
			
			
		}
		

		//INTERNAL STORAGE query
		
		// Does a query against the table and returns a Cursor object
		mCursor = context.getApplicationContext().getContentResolver().query(
			MediaStore.Audio.Media.INTERNAL_CONTENT_URI,  // The content URI of the Internal Content URI
		    mProjection,                       // The columns to return for each row
		    null,                   // Either null, or the word the user entered
		    null,                    // Either empty, or the string the user entered
		    null);                       // The sort order for the returned rows

		// Some providers return null if an error occurs, others throw an exception
		if (null == mCursor) {
		    
			Log.e("MediaPlayer","Media Provider return null");
		// If the Cursor is empty, the provider found no matches
		} else if (mCursor.getCount() < 1) {
			Log.i("MediaPlayer","No sound file");
		} else {
			//if at least one, we traverse all by move mCursor to each record
			while (mCursor.moveToNext()) {
				//Instance for each record song
				HashMap<String, String> song = new HashMap<String, String>();
		        
				//get filename
		        String file_name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		        
		        // extract filename without  file extensin 
		        if (file_name.contains(".")){
			        //split strring by dot character
		        	String[] split_dot = file_name.split("\\.");
			        file_name="";
			        //handle filename which has multiple dots in a file
			        for (int i=0; i < (split_dot.length-1);i++){
			        	file_name = file_name + "." + split_dot[i];
			        }
			        file_name = file_name.substring(1,file_name.length());
		        }
		        
		        //put infprmation to the song record
	        	song.put("songPath",mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
	        	song.put("songName",file_name);
	        	song.put("songArtist",mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
		        
	        	//add song record to songsList data
	        	songsList.add(song);
		        
			}
		}
        
        // return songs list array
        return songsList;
    }
 
    
}
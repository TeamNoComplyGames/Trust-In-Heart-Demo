package com.torch2424.battlequest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.trustinheartdemo.R;

public class WelcomeRRPPGG extends Activity 
{
	
	//declaring music out here to be accessed everywhere
	BGMusic bgMusic;
	boolean musicBound;
	Intent playIntent;
	//for onresume boolean to check if music is paused
	boolean musicPaused;
	
	//to fix double pause requests
	boolean noPause;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcomerrppgg);
		
		//setting fonts
		//a function that binds views by findview by Id and then sets their typeface
		setFont();
		//set up the music service
		//connects the app to the background music service
		playMusic();
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	
	//connect to the service
		ServiceConnection musicConnection = new ServiceConnection()
		{
		 
		  @Override
		  public void onServiceConnected(ComponentName name, IBinder service) 
		  {
		    MusicBinder binder = (MusicBinder)service;
		    //get service
		    bgMusic = binder.getService();
		    //music already playing for us
		    //bgMusic.startSong(R.raw.starttheme);
		  }
		 
		  @Override
		  public void onServiceDisconnected(ComponentName name) 
		  {
		    
		  }
		};
		
		public void playMusic()
		{
			//make sure only affect media playback not ringer
	   		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	   		//for pausing
	   		noPause = false;
			playIntent = new Intent(getApplicationContext(), BGMusic.class);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			//service now started by start screen
			//getApplicationContext().startService(playIntent);
		}
		
		
		public void setFont()
		{	
			//get font
			Typeface tf = FontCache.get(getApplicationContext(), "font");
			
			//getviews
			TextView infoText = (TextView) findViewById(R.id.info);
			TextView title = (TextView) findViewById(R.id.title);
			Button continueButton = (Button) findViewById(R.id.continueButton);
			Button importButton = (Button) findViewById(R.id.loadButton);
			
			//set out fonts
			infoText.setTypeface(tf);
			title.setTypeface(tf);
			continueButton.setTypeface(tf);
			importButton.setTypeface(tf);
			
			
		}
		
		public void continueButton(View view)
		{
			//dont do anything to song already playing
			Intent fight = new Intent(this, com.torch2424.broquest.Creator.class);
			fight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(fight);
			
			noPause = true;
		}
		
		public void load(View view)
		{
			Intent fight = new Intent(this, SaveFileSelect.class);
			//add this flag to remove all previous activities
			fight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(fight);
			finish();
			
			
			noPause = true;
		}
	
	
	public void quit(View view)
	{
		//im sorry
		load(null);
	}
	
	//on pause and on resume to pause and play music
	@Override
	public void onPause() 
	{
	    super.onPause();
	    
	    //need to check if it is not already pausing from when we request it to pause
	    //from switching activities
	    if(noPause == false)
	    {
	    	//to stop errors
	    	if(bgMusic != null)
	    	{
	    bgMusic.pauseSong();
	    musicPaused = true;
	    	}
	    }
	}
	
	
	@Override
	public void onBackPressed() 
	{
	   quit(null);
	}
	
	@Override
	public void onResume() 
	{
	    super.onResume();  
	    if(musicPaused)
	    {
	    bgMusic.resumeSong();
	    }
	    
	}
	
	
	//need to add this to avoid service connection leaks
			@Override
			public void onDestroy()
			{
				super.onDestroy();
				unbindService(musicConnection);
				Unbind.unbindDrawables((ScrollView) findViewById(R.id.container));
				System.gc();
			}
}

package com.torch2424.broquest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.torch2424.battlequest.BGMusic;
import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.battlequest.CharEdit;
import com.torch2424.battlequest.FirstScreen;
import com.torch2424.battlequest.FontCache;
import com.torch2424.battlequest.Journal;
import com.torch2424.battlequest.Overworld;
import com.torch2424.battlequest.ShopOverworld;
import com.torch2424.battlequest.Unbind;
import com.torch2424.trustinheartdemo.R;

public class StartScreen extends Activity 
{

	//make sure to add option to backup save file, 
	//or back up whenever they press quit
	
	//bgMusic.pause song commented since it is now handled on the next activities on create
	
	
	//declaring music out here to be accessed everywhere
	BGMusic bgMusic;
	boolean musicBound;
	Intent playIntent;
	//for onresume boolean to check if music is paused
	boolean musicPaused;
	
	//to fix double pause requests
	boolean noPause;
	
	//to get bg animation working
    AnimationDrawable bganim;
    LinearLayout bg;
    
    //boolean for if the thing is blinking
    boolean blinking;
  //blinking timers
  		Timer timer;
  		//journal button
  		Button journal;
	
	
	//connect to the service
	ServiceConnection musicConnection = new ServiceConnection()
	{
	 
	  @Override
	  public void onServiceConnected(ComponentName name, IBinder service) 
	  {
	    MusicBinder binder = (MusicBinder)service;
	    //get service
	    bgMusic = binder.getService();
	    bgMusic.startSong(R.raw.starttheme);
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
		//get buttons
		Button start = (Button) findViewById(R.id.start);
		journal = (Button) findViewById(R.id.journal);
		Button shops = (Button) findViewById(R.id.shop);
		Button character = (Button) findViewById(R.id.character);
		Button options = (Button) findViewById(R.id.options);
		Button quit = (Button) findViewById(R.id.quit);
		
		//get font
		Typeface tf = FontCache.get(getApplicationContext(), "font");
		
		//set font to buttons
		start.setTypeface(tf);
		journal.setTypeface(tf);
		shops.setTypeface(tf);
		character.setTypeface(tf);
		options.setTypeface(tf);
		quit.setTypeface(tf);
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		
		//setting fonts
		//a function that binds views by findview by Id and then sets their typeface
		setFont();
		//set up the music service
		//connects the app to the background music service
		playMusic();
		
		//set up our background
		bg = (LinearLayout) findViewById(R.id.container);
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	//function to start a battle
	public void fight(View view)
	{
				bgMusic.pauseSong();
				//dont pause twice
				noPause = true;
				//since overowrld doesnt play song do it for it
				bgMusic.playSong(R.raw.overworld);
				Intent fight = new Intent(getApplicationContext(), Overworld.class);
				//add getApplicationContext() flag to remove all previous activities
				startActivity(fight);
	}
	
	public void journal(View view)
	{
			bgMusic.pauseSong();
			//dont pause twice
			noPause = true;
			Intent edit = new Intent(getApplicationContext(), Journal.class);
			//add getApplicationContext() flag to remove all previous activities
			startActivity(edit);
	}
	
	public void shop (View view)
	{
			bgMusic.pauseSong();
			//dont pause twice
			noPause = true;
			//since overowrld doesnt play song do it for it
			bgMusic.playSong(R.raw.shoptheme);
			Intent fight = new Intent(getApplicationContext(), ShopOverworld.class);
			startActivity(fight);
	}
	
	public void character (View view)
	{
			bgMusic.pauseSong();
			//dont pause twice
			noPause = true;
			Intent edit = new Intent(getApplicationContext(), CharEdit.class);
			//getApplicationContext() to understand what class we are coming from
			edit.putExtra("CLASS", 1);
			startActivity(edit);
	}
	
	public void options (View view)
	{
		//create activity for options
		Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_LONG).show();
		//bgMusic.release();
	}
	
	public void quit(View view)
	{
		bgMusic.stopSong();
		//this will open a new start screen just to finish it and everything above it
		Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();
	}
	
	//on pause and on resume to pause and play music
	@Override
	public void onPause() 
	{
	    super.onPause();
	    //need to check if it is not already pausing from when we request it to pause
	    //from switching activities
	    blinking = false;
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
	public void onResume() 
	{
	    super.onResume();
	    
	  //to get bg animation working
		  bganim = (AnimationDrawable) bg.getBackground();
		    bganim.start();
		    
		  //check if we should blink the journal button
		    //this allows the player to have more of an interest in the story
		    blinking = false;
	   		boolean blink = false;
			try {
				blink = checkBlink();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		if(blink)
	   		{
	   			blinking = true;
	   			//then blink the journal thingy
	   			blinkButton();
	   		}
		    
		    
	    if(musicPaused)
	    {
	    bgMusic.resumeSong();
	    }
	}
	
	@Override
	public void onBackPressed() 
	{
	   quit(null);
	}
	
	//need to add getApplicationContext() to avoid service connection leaks
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unbindService(musicConnection);
		Unbind.unbindDrawables((LinearLayout) findViewById(R.id.container));
		System.gc();
	}
	
	
	//function to check the save file if we need to blink stuff
	public boolean checkBlink() throws FileNotFoundException
	{
		/*
		 * FORMAT
		 * name
		 * gender
		 * class
		 * money
		 * items (all in one line)
		 * purchased equip (all in one line)
		 * head
		 * torso
		 * leg
		 * shoes
		 * weapon
		 * HP
		 * strength
		 * intelligence
		 * dexterity
		 * level
		 * exp to next level
		 * skillpoints
		 * unlockes levels(worlds)
		 * character model
		 * wins
		 * losses
		 * score
		 * blink journal (0 no, 1 yes)
		 */
		
		//our boolean 
		boolean blinkCheck = false;
		
		//paths
		SharedPreferences prefs = this.getSharedPreferences("TrustInHeartPrefs", 0);
		
		File tempPath = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".txt");
		File ogPath = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".sav");
			
		//rename the file
		ogPath.renameTo(tempPath);
		
	//set up scanner to path
		Scanner sc = new Scanner(tempPath);
		//skipping to forth line
		for(int i = 0; i < 23; i++)
		{
			sc.nextLine();
		}
		
		//check to see if we should be blinking
		String line = sc.nextLine();
		if(Integer.valueOf(line) == 1)
		{
			blinkCheck = true;
		}
		
		//close scanner
		sc.close();
		//convert back to .sav
		tempPath.renameTo(ogPath);
		
		return blinkCheck;
	}
	
	
	//function for blinking the journal button whenever something is unread
		public void blinkButton()
		{
			//blinking set to true by fucntion caller in resume
			timer = new Timer(true);
			TimerTask timerTask = new TimerTask() 
	        {
	            @Override
	            public void run() 
	            {  
	            	if(blinking)
	            	{
	            		//have to run on ui thread to update elements
	            		runOnUiThread(new Runnable() 
	            		{
	            			@Override
	                        public void run() 
	            			{
	            				if(journal.getVisibility() == View.VISIBLE)
	            				{
	            					journal.setVisibility(View.INVISIBLE);
	            				}
	            				else
	            				{
	            					journal.setVisibility(View.VISIBLE);
	            				}
	            			}
	            		});
	            	}
	            	else
	            	{
	            		timer.cancel();
	            		timer.purge();
	            	}
	            }
	        };
	        
	        timer.schedule(timerTask, 0, 500);
			
		}
	

}

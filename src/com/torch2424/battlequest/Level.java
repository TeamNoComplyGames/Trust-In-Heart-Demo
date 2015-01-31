package com.torch2424.battlequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.broquest.Battle;
import com.torch2424.trustinheartdemo.R;

public class Level extends Activity 
{
	//music
		BGMusic bgMusic;
		boolean musicBound;
		boolean musicPaused;
		boolean noPause;
		
		//get the world
		int world;
		//get unlocked levels boolean to check if they are unlocked
		int levels;
		boolean lvl2;
		boolean lvl3;
		boolean lvl4;
		boolean lvl5;
		
		//get the plaer model
		int playerModel;
		ImageView player;
		
		
		//sleeping boolena
		boolean sleeping;
		
		//character models array
		int[] characters = new int[]{R.drawable.dmm, R.drawable.dmr, R.drawable.dmt, R.drawable.dmw, 
				R.drawable.tmm, R.drawable.tmr, R.drawable.tmt, R.drawable.tmw, R.drawable.lmm, 
				R.drawable.lmr, R.drawable.lmt, R.drawable.lmw, R.drawable.dfm, R.drawable.dfr, 
				R.drawable.dft, R.drawable.dfw, R.drawable.tfm, R.drawable.tfr, R.drawable.tft, 
				R.drawable.tfw, R.drawable.lfm, R.drawable.lfr, R.drawable.lft, R.drawable.lfw};
		
		//backgrounds array
		int[] BGs = new int[]{R.drawable.world1, R.drawable.world2bg, };
	
	//to get player animation working
    AnimationDrawable playeranim;

		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_level);
			
			//dont need to set up fonts but need to set up music
			playMusic();
			
			//aquire wakelock
	   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	   		
	   		Intent intent = getIntent();
			world = intent.getIntExtra("WORLD", 1);
			
			//getting the stats for unlocked levels
			try {
				getStats();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//set the background of our layout depending on the world unlocked
			LinearLayout bg = (LinearLayout) findViewById(R.id.container);
			bg.setBackgroundResource(BGs[world - 1]);
			
			//set up button and player model
			getViews();
			
			//unlock the levels
			unlockLevels();
			
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
		    musicBound = true;
		    if(musicBound)
		    {
		    	//no need to change song
		    		//bgMusic.playSong(R.raw.overworld);
		    }
		  }
		 
		  @Override
		  public void onServiceDisconnected(ComponentName name) 
		  {
		    musicBound = false;
		  }
		};
		
		
	public void playMusic()
	{
		//make sure only affect media playback not ringer
   		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		musicPaused = false;
		noPause = false;
		Intent playIntent = new Intent(getApplicationContext(), BGMusic.class);
	    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
	    sleeping = false;
	}
	
	public void getViews()
	{
		
		//set the font of the button
				Button quit = (Button) findViewById(R.id.quit);
				//get font
				Typeface tf = FontCache.get(getApplicationContext(), "font");
				quit.setTypeface(tf);
		
		//set up the player model
		player = (ImageView) findViewById(R.id.player);
		player.setBackgroundResource(characters[playerModel]);
		 //to get animation working
	   playeranim = (AnimationDrawable) player.getBackground();
	    playeranim.start();
	}
	
	public void getStats() throws FileNotFoundException
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
		 * feet
		 * weapon
		 * HP
		 * strength
		 * intelligence
		 * dexterity
		 * level
		 * exp to next level
		 * skill points
		 * unlocked levels
		 */
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("TrustInHeartPrefs", 0);
		//set up files
		//get character info
		File ogPath = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".sav");
		File broPath = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".txt");
		ogPath.renameTo(broPath);
	//set up scanner to path
		Scanner sc = new Scanner(broPath);
		//skipping to 19th line
		for(int i = 0; i < 18; i++)
		{
		sc.nextLine();
		}
		//get unlocked levels
		String temp = sc.nextLine();
		levels = Integer.valueOf(temp);
		//get the player model
		temp = sc.nextLine();
		playerModel = Integer.valueOf(temp);
		
		//finished getting stats
		sc.close();
		//convert back to character.bro
		broPath.renameTo(ogPath);
	}
	
	public void unlockLevels()
	{
		//get the buttons except on since it has to be unlocked to access world
		ImageButton level2 = (ImageButton) findViewById(R.id.level2);
		ImageButton level3 = (ImageButton) findViewById(R.id.level3);
		ImageButton level4 = (ImageButton) findViewById(R.id.level4);
		ImageButton level5 = (ImageButton) findViewById(R.id.level5);
		
		//set up the booleans
		lvl2 = false;
		lvl3 = false;
		lvl4 = false;
		lvl5 = false;
		
		//get base value of world
		int base = (world - 1) * 5;
		
		//change backgrounds of buttons if unlocked
		
		if(base + 2 <= levels)
		{
			level2.setBackgroundResource(R.drawable.levelicons);
			lvl2 = true;
		}
		
		if(base + 3 <= levels)
		{
			level3.setBackgroundResource(R.drawable.levelicons);
			lvl3 = true;
		}
		
		if(base + 4 <= levels)
		{
			level4.setBackgroundResource(R.drawable.levelicons);
			lvl4 = true;
		}
		
		if(base + 5 <= levels)
		{
			level5.setBackgroundResource(R.drawable.bosslevelicons);
			lvl5 = true;
		}
	}
	
	public void battle(View view)
	{
		if(sleeping == false)
		{
		//checking if the music is fading here just so we dont get too many fade requests
		//if it is, pause the game for a second to go into battle
		if(bgMusic.fading)
		{
			sleeping = true;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		bgMusic.pauseSong();
		noPause = true;
		//get the button id
		int id = view.getId();
		
		if(id == R.id.level1)
		{
			Intent fight = new Intent(getApplicationContext(), Battle.class);
			fight.putExtra("WORLD", world);
			fight.putExtra("Level", 1);
			fight.putExtra("STORYTEXT", true);
			startActivity(fight);
		}
		else if(id == R.id.level2 && lvl2)
		{
			Intent fight = new Intent(getApplicationContext(), Battle.class);
			fight.putExtra("WORLD", world);
			fight.putExtra("Level", 2);
			fight.putExtra("STORYTEXT", true);
			startActivity(fight);
		}
		else if(id == R.id.level3 && lvl3)
		{
			Intent fight = new Intent(getApplicationContext(), Battle.class);
			fight.putExtra("WORLD", world);
			fight.putExtra("Level", 3);
			fight.putExtra("STORYTEXT", true);
			startActivity(fight);
		}
		else if(id == R.id.level4 && lvl4)
		{
			Intent fight = new Intent(getApplicationContext(), Battle.class);
			fight.putExtra("WORLD", world);
			fight.putExtra("Level", 4);
			fight.putExtra("STORYTEXT", true);
			startActivity(fight);
		}
		else if(id == R.id.level5 && lvl5)
		{
			//different class set to boss battle
			Intent fight = new Intent(getApplicationContext(), BossBattle.class);
			//add getApplicationContext() flag to remove all previous activities
			fight.putExtra("WORLD", world);
			fight.putExtra("Level", 5);
			fight.putExtra("STORYTEXT", true);
			startActivity(fight);
		}
		
		}
		
	}
	
	
	//on pause and on resume to pause and play music
		@Override
		public void onPause() 
		{
		    super.onPause();
		    
		    if(noPause)
		    {
		    	
		    }
		    else
		    {
		    //need to check if it is not null
		    	if(bgMusic != null)
		    	{
		    bgMusic.pauseSong();
		    musicPaused = true;
		    	}
		    }
		}
		
		//back button pressed
		public void cancel(View view)
		{
			noPause = true;
			//dont pause song if going back to overworld!
			//bgMusic.pauseSong();
		   //need to change to back button that is on screen later
			//dont create a new overworld, we already have one, just finish!
			Intent fight = new Intent(getApplicationContext(), Overworld.class);
			fight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(fight);
		}
		
		//click character brings up character screen
		public void playerClick(View view)
		{
			if(sleeping == false)
			{
			//checking if the music is fading here just so we dont get too many fade requests
			//if it is, pause the game for a second to go into battle
			if(bgMusic.fading)
			{
				sleeping = true;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//now call class
			bgMusic.pauseSong();
			//dont pause twice
			noPause = true;
			Intent edit = new Intent(getApplicationContext(), CharEdit.class);
			//getApplicationContext() to understand what class we are coming from
			//and which world we came from
			edit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			edit.putExtra("CLASS", 3);
			edit.putExtra("WORLD", world);
			startActivity(edit);
			//just start dont finish
			}
		}
		
		@Override
		public void onResume() 
		{
		    super.onResume();  
		    
		  //to get animation working
			   playeranim = (AnimationDrawable) player.getBackground();
			    playeranim.start();
			    
			    
		    if(musicPaused)
		    {
		    bgMusic.resumeSong();
		    }
		}
		
		@Override
		public void onBackPressed() 
		{
			//call the back button
			cancel(null);
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
	
}

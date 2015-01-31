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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.broquest.StartScreen;
import com.torch2424.trustinheartdemo.R;

public class ShopOverworld extends Activity 
{
	
	//music
		BGMusic bgMusic;
		boolean musicBound;
		boolean musicPaused;
		//boolean to avoid on pause if world selected
		boolean noPause;
		
		//sleepig boolean
		boolean sleeping;
		
		//get the player model
		int playerModel;
		ImageView player;
		
		//character models array
				int[] characters = new int[]{R.drawable.dmm, R.drawable.dmr, R.drawable.dmt, R.drawable.dmw, 
						R.drawable.tmm, R.drawable.tmr, R.drawable.tmt, R.drawable.tmw, R.drawable.lmm, 
						R.drawable.lmr, R.drawable.lmt, R.drawable.lmw, R.drawable.dfm, R.drawable.dfr, 
						R.drawable.dft, R.drawable.dfw, R.drawable.tfm, R.drawable.tfr, R.drawable.tft, 
						R.drawable.tfw, R.drawable.lfm, R.drawable.lfr, R.drawable.lft, R.drawable.lfw};
			
			//to get player animation working
		    AnimationDrawable playeranim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopoverworld);
		
		//dont need to set up fonts but need to set up music
		playMusic();
		noPause = false;
		
		//get your player model from stats
		try {
			getModel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//set up button and player model
		getViews();
		
		//wont need unlocked levels until we add more worlds
		
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
		    musicBound = true;
		    if(musicBound)
		    {
		    	//handled by start screen
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
		Intent playIntent = new Intent(getApplicationContext(), BGMusic.class);
	    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
	    
	    sleeping = false;
	}
	
	public void getModel() throws FileNotFoundException
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
		 * character model
			 * wins
			 * losses
			 * score
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
		//skipping to 20th line
		for(int i = 0; i < 19; i++)
		{
		sc.nextLine();
		}
		//get the player model
		String temp = sc.nextLine();
		playerModel = Integer.valueOf(temp);
		
		//finished getting stats
		sc.close();
		//convert back to character.bro
		broPath.renameTo(ogPath);
		
	}

	public void getViews()
	{
		
		//set the font of the things
				Button quit = (Button) findViewById(R.id.quit);
				TextView itemText = (TextView) findViewById(R.id.itemText);
				TextView HPText = (TextView) findViewById(R.id.HPText);
				TextView strText = (TextView) findViewById(R.id.strText);
				TextView intText = (TextView) findViewById(R.id.intText);
				TextView dexText = (TextView) findViewById(R.id.dexText);
				//get font
				Typeface tf = FontCache.get(getApplicationContext(), "font");
				quit.setTypeface(tf);
				itemText.setTypeface(tf);
				HPText.setTypeface(tf);
				strText.setTypeface(tf);
				intText.setTypeface(tf);
				dexText.setTypeface(tf);
		
		//set up the player model
		player = (ImageView) findViewById(R.id.player);
		player.setBackgroundResource(characters[playerModel]);
		 //to get animation working
	   playeranim = (AnimationDrawable) player.getBackground();
	    playeranim.start();
	}
	
	//back button
	public void cancel(View view)
	{
		//checking if the music is fading here just so we dont get too many fade requests
		//if it is, pause the game for a second to go into battle
		if(bgMusic.fading)
		{
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		bgMusic.stopSong();
		noPause = true;
		Intent intent = new Intent(getApplicationContext(), StartScreen.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void enterShop(View view)
	{
		//get the button id
		int id = view.getId();
		
		//since music bugs out here sometimes, if the music isnt playing need to make sure it play
		//the shop song for in shop
		
		if(bgMusic.isPlaying() == false)
		{
			bgMusic.playSong(R.raw.shoptheme);
		}
		
		//don't pause the song in onpause
		noPause = true;
		
		//create the intent
		Intent intent = new Intent(getApplicationContext(), InShop.class);
		
		//add extra to intent to get what shop we chose
		if(id == R.id.itemHouse || id == R.id.itemText)
		{
			intent.putExtra("SHOP", 1);
		}
		else if(id == R.id.HPHouse || id == R.id.HPText)
		{
			intent.putExtra("SHOP", 2);
		}
		else if(id == R.id.strHouse || id == R.id.strText)
		{
			intent.putExtra("SHOP", 3);
		}
		else if(id == R.id.intHouse || id == R.id.intText)
		{
			intent.putExtra("SHOP", 4);
		}
		//dexterity
		else
		{
			intent.putExtra("SHOP", 5);
		}
		//just start dont finish
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
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
		edit.putExtra("CLASS", 4);
		edit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(edit);
		//just start dont finish
		
		}
		
	}


	//on pause and on resume to pause and play music
		@Override
		public void onPause() 
		{
		    super.onPause();
		    
		    //need to check if it is not null
		    if(noPause)
		    {
		    	
		    }
		    else
		    {
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
				Unbind.unbindDrawables((RelativeLayout) findViewById(R.id.container));
				System.gc();
			}
}

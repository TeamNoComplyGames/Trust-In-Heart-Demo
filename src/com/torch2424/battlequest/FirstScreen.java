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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.trustinheartdemo.R;

public class FirstScreen extends Activity 
{
	//declaring music out here to be accessed everywhere
		BGMusic bgMusic;
		boolean musicBound;
		Intent playIntent;
		//for onresume boolean to check if music is paused
		boolean musicPaused;
		
		//to fix double pause requests
		boolean noPause;
		
		//get our textviews
		TextView tapToBegin;
		TextView gameBy;
		TextView noComply;
		TextView year;
		
		//text animation
		Animation animation1;
		Animation animation2;
		
		//exit boolean
	    boolean exit;
		
		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		//check if we called start screen just to close app before anything else
		Intent intent = getIntent();
		exit = intent.getBooleanExtra("EXIT", false);
		if(exit)
		{
			//stop the music first
			playIntent = new Intent(this, BGMusic.class);
			stopService(playIntent);
			finish();
			System.gc();
		}
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_screen);
		
		//setting fonts
		setFont();
		//set up the music service
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
		    //dont play the song if we are exting
		    if(exit == false)
		    {
		    	bgMusic.playSong(R.raw.rrppgg_theme);
		    }
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
			playIntent = new Intent(this, BGMusic.class);
			getApplicationContext().startService(playIntent);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		}
		
		public void setFont()
		{
			//get our views
			tapToBegin = (TextView) findViewById(R.id.tapToBegin);
			gameBy = (TextView) findViewById(R.id.gameBy);
			year = (TextView) findViewById(R.id.year);
			noComply = (TextView) findViewById(R.id.noComply);
			
			//get font
			Typeface tf = FontCache.get(getApplicationContext(), "font");
			
			//set fonts
			tapToBegin.setTypeface(tf);
			gameBy.setTypeface(tf);
			year.setTypeface(tf);
			noComply.setTypeface(tf);
			
		}
		
		public void startAnimating()
		{
			//gotten from stack to animate our textview modified for me
			//http://stackoverflow.com/questions/3298330/android-alpha-animation-fadein-fadeout-with-delays
			animation1 = new AlphaAnimation(0.0f, 1.0f);
			animation1.setDuration(250);
		    animation1.setStartOffset(500);

		    //animation1 AnimationListener
		    animation1.setAnimationListener(new AnimationListener(){

		        @Override
		        public void onAnimationEnd(Animation arg0) {
		            // start animation2 when animation1 ends (continue)
		            tapToBegin.startAnimation(animation2);
		        }

		        @Override
		        public void onAnimationRepeat(Animation arg0) {
		            // TODO Auto-generated method stub

		        }

		        @Override
		        public void onAnimationStart(Animation arg0) {
		            // TODO Auto-generated method stub

		        }

		    });
		    
		    animation2 = new AlphaAnimation(1.0f, 0.0f);
		    animation2.setDuration(250);
		    animation2.setStartOffset(1000);

		    //animation2 AnimationListener
		    animation2.setAnimationListener(new AnimationListener(){

		        @Override
		        public void onAnimationEnd(Animation arg0) {
		            // start animation1 when animation2 ends (repeat)
		            tapToBegin.startAnimation(animation1);
		        }

		        @Override
		        public void onAnimationRepeat(Animation arg0) {
		            // TODO Auto-generated method stub

		        }

		        @Override
		        public void onAnimationStart(Animation arg0) {
		            // TODO Auto-generated method stub

		        }

		    });

		    tapToBegin.startAnimation(animation1);
		}
		
		//function to start the game
				public void begin(View view)
				{
					//if the music is still fading in, dont let the user go to the next activity
					if(bgMusic.fading)
					{
						
					}
					else
					{
					//however we want to check if there are any save files foe the game yet to determine if
					//this is their first time playing
					String[] files = this.getFilesDir().list();
					if(files.length >= 1)
					{
						//start choose your character
						bgMusic.pauseSong();
						//dont pause twice
						noPause = true;
						//since overowrld doesnt play song do it for it
						bgMusic.playSong(R.raw.character);
						Intent fight = new Intent(this, SaveFileSelect.class);
						//add this flag to remove all previous activities
						fight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(fight);
						//dont finish because we use this activity to close everything later
						//finish();
					}
					else
					{
						bgMusic.pauseSong();
						//dont pause twice
						noPause = true;
						//since overowrld doesnt play song do it for it
						bgMusic.playSong(R.raw.character);
						Intent edit = new Intent(this, WelcomeRRPPGG.class);
						edit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(edit);
					}
					
					}
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
		    if(musicPaused)
		    {
		    bgMusic.resumeSong();
		    }
		    
		    //animate the textview
		    startAnimating();
		    
		    //animate the layout
		    getWindow().setWindowAnimations(R.anim.layout_fadein);
		    Animation anim = AnimationUtils.loadAnimation(this,R.anim.layout_fadein);
		    // 'body' is root layout id  which is for MainActivity
		    findViewById(R.id.container).startAnimation(anim);
		}
		
		@Override
		public void onBackPressed() 
		{
			//close the app completely
			bgMusic.stopSong();
			stopService(playIntent);
			finish();
		}
		
		//need to add this to avoid service connection leaks
		@Override
		public void onDestroy()
		{
			super.onDestroy();
			unbindService(musicConnection);
			Unbind.unbindDrawables((RelativeLayout) findViewById(R.id.container));
			System.gc();
		}
		
}

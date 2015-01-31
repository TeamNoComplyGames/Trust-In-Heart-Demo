package com.torch2424.battlequest;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;

import com.torch2424.trustinheartdemo.R;

public class BGMusic extends Service 
{
	//mediaplayer and binder
	MediaPlayer bgmusic;
	IBinder musicBind = new MusicBinder();
	//for the fade in and out
	//2 was delta time, but since I want consistentent fades, why have a final int
	//when I could place that?
	float volume;
	//rate is how fast or slow you want fade 0,0.1,0.2 etc...
	float rate;
	
	//to fix conflict with music fading out and then not playing next song
	public boolean fading;
	boolean request;
	int song;
	
	//boolean for if we are currently in a dont switch songs mode (battle)
	boolean noChange;
	
	//sound effects
		SoundPool sp;
		int[] sfx;
		
		//fading timers, using here to stop start song errors
		Timer timer;
	
	

	//gotten from stack but heavily modified
	public void FadeOut()
	{
		//do this every half second so we need timer
				fading = true;
				timer = new Timer(true);
				TimerTask timerTask = new TimerTask() 
		        {
		            @Override
		            public void run() 
		            {  
		            	//hopefully fixes music crashes
		            	if(bgmusic != null)
		            	{
		            	bgmusic.setVolume(volume, volume);
		            	}
		            	else
		            	{
		            		timer.cancel();
		         	    	timer.purge();
		            	}
		         	    volume = (float) (volume - rate);
		         	    if(volume < 0)
		         	    {
		         	    	volume = 0;
		         	    	bgmusic.setVolume(volume, volume);
		         	    	timer.cancel();
		         	    	timer.purge();
		         	    	bgmusic.pause();
		         	    	fading = false;
		         	    	if(request)
		         	    	{
		         	    		playSong(song);
		         	    	}
		         	    }
		            }
		        };
		        
		        timer.schedule(timerTask, 0, 50);
	}
	
	
	public void FadeIn()
	{
		fading = true;
		volume = 0;
		bgmusic.setVolume(volume, volume);
		//do this every half second so we need timer
		bgmusic.start();
		timer = new Timer(true);
		TimerTask timerTask = new TimerTask() 
        {
            @Override
            public void run() 
            {   
            	//hopefully fixes music crashes
            	if(bgmusic != null)
            	{
            	bgmusic.setVolume(volume, volume);
            	}
            	else
            	{
            		timer.cancel();
         	    	timer.purge();
            	}
         	    volume = (float) (volume + rate);
         	    if(volume > 1)
         	    {
         	    	//adding set music of max volume here in case of odd numbered rates
         	    	volume = 1;
         	    	bgmusic.setVolume(volume, volume);
         	    	timer.cancel();
         	    	timer.purge();
         	    	fading = false;
         	    	if(request)
         	    	{
         	    		playSong(song);
         	    	}
         	    }
            }
        };
        
        timer.schedule(timerTask, 0, 50);
        
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return musicBind;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		//done with fade out
	  bgmusic.stop();
	  bgmusic.reset();
	  bgmusic.release();
	  stopSelf();
	  return false;
	}
	
	@Override
	public void onCreate()
	{
		//create the service
		super.onCreate();
		//create mediaplayer
		bgmusic = new MediaPlayer();
		bgmusic.setLooping(true);
		volume = 1;
		rate = (float) 0.05;
		fading = false;
		request = false;
		noChange = false;
		song = 0;
		
		//now set up soundpool
		sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
	    sfx = new int[8];
	    sfx[0] = sp.load(this, R.raw.sfx_attack, 1);
	    sfx[1] = sp.load(this, R.raw.sfx_crit, 1);
	    sfx[2] = sp.load(this, R.raw.sfx_item, 1);
	    sfx[3] = sp.load(this, R.raw.sfx_levelup, 1);
	    sfx[4] = sp.load(this, R.raw.sfx_run, 1);
	    sfx[5] = sp.load(this, R.raw.sfx_shop, 1);
	    sfx[6] = sp.load(this, R.raw.sfx_unlock, 1);
	    sfx[7] = sp.load(this, R.raw.sfx_unlockboss, 1);
	}
	
	//function to play a song with a fade activity
	public void playSong(int id)
	{
		if(fading)
		{
			request = true;
			song = id;
		}
		else
		{
		if(bgmusic != null)
		{
			bgmusic.reset();
			bgmusic.release();
		}
		bgmusic = null;
		bgmusic = MediaPlayer.create(this, id);
		FadeIn();
		bgmusic.setLooping(true); //may not need, but I noticed sometimes music doesn't loop
		request = false;
		}
	}
	
	//no fade in play song
	public void startSong(int id)
	{
		//need to cancel and timers for fading if they are running
		if(fading)
		{
			timer.cancel();
			timer.purge();
		}
		if(bgmusic != null)
		{
			bgmusic.reset();
			bgmusic.release();
		}
		bgmusic = null;
		bgmusic = MediaPlayer.create(this, id);
		bgmusic.setVolume((float) 1.0, (float) 1.0);
		bgmusic.start();
		bgmusic.setLooping(true);
	}
	
	//play sound effect
	public void playSFX(int index)
	{
		sp.play(sfx[index], (float) 0.5, (float) 0.5, 1, 0, 1);
	}
	
	public void stopSong()
	{
		//to stop errors
		if(bgmusic != null)
		{
		bgmusic.stop();
		}
	}
	
	public void pauseSong()
	{
		//need to check if it is playing duh
		//checking will help with closing app
		//need to check if it is playing duh
				//checking will help with closing app
				if(bgmusic != null)
				{
				if(bgmusic.isPlaying())
				{
					FadeOut();
				}
				//done in fade out
				//bgmusic.pause();
				}
				
				//could have done bgmusic !=null && is playing in one if
				//but I feel more comfortable this way
	}
	
	public void resumeSong()
	{
		FadeIn();
		//done in fade in
		//bgmusic.start();
	}
	
	public boolean isPlaying()
	{
		if(bgmusic != null && bgmusic.isPlaying())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//function to change no oncreate from true to false
		//only used for battle to keep the music from restarting
	public void changeState()
	{
		if(noChange)
		{
			noChange = false;
		}
		else
		{
			noChange = true;
		}
	}
	
	public boolean getState()
	{
		if(noChange)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	//binder for the music
	public class MusicBinder extends Binder 
	{
		
		  public BGMusic getService() 
		  {
		    return BGMusic.this;
		  }
	}

}

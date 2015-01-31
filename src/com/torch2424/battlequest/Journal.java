package com.torch2424.battlequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.broquest.StartScreen;
import com.torch2424.trustinheartdemo.R;

public class Journal extends Activity 
{
	
			//music
			BGMusic bgMusic;
			boolean musicBound;
			boolean musicPaused;
			boolean noPause;
			
			//unlocked levels
			int levels;
			
			
			//views
			TextView title;
			LinearLayout entryList;
			GameTextView entryView;
			TextView entry1;
			TextView entry2;
			TextView entry3;
			Button quit;
			
			//navigation boolean
			Boolean entryBool;
			
			
			
			
			

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journal);
		
		//dont need to set up fonts but need to set up music
		playMusic();
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//getting the stats for unlocked levels
		try {
			getStats();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//assign views
		getViews();
		
		//hide/unlock the entries
		unlockEntries();
		
		//set fonts
		setFonts();
		
		//stop the blinking
				try {
					stopBlink();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			    	//song may not be shrines just a placeholder and test
			    	bgMusic.playSong(R.raw.shrines);
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
			File inPath = new File(getApplicationContext().getFilesDir(), "");
			File ogPath = new File(inPath.getAbsolutePath() + "/Trust In Heart-" +
			prefs.getString("SAVEFILE", "character") + ".sav");
			File broPath = new File(inPath.getAbsolutePath() + "/Trust In Heart-" +
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
			
			//finished getting stats
			sc.close();
			//convert back to character.bro
			broPath.renameTo(ogPath);
		}
		
		public void getViews()
		{
			/*
			 * //views
			TextView title;
			LinearLayout entryList;
			TextView entryView;
			TextView entry1;
			TextView entry2;
			Button quit;
			 */
			
			title = (TextView) findViewById(R.id.title);
			entryList = (LinearLayout) findViewById(R.id.entryList);
			entryView = (GameTextView) findViewById(R.id.entryView);
			entry1 = (TextView) findViewById(R.id.entry1);
			entry2 = (TextView) findViewById(R.id.entry2);
			entry3 = (TextView) findViewById(R.id.entry3);
			quit = (Button) findViewById(R.id.quit);
			
			
		}
		
		public void unlockEntries()
		{
			//hiding the entry view since we just opened
			entryView.setVisibility(View.GONE);
			
			//boolean to let us know if we are reading an entry, initilization
			entryBool = false;
			
			//hide stuff we havent unlocked
			
			if(levels < 6)
			{
				entry2.setVisibility(View.GONE);
			}
			if(levels < 11)
			{
				entry3.setVisibility(View.GONE);
			}
			
		}
		
		public void setFonts()
		{
			//get font
			Typeface tf = FontCache.get(getApplicationContext(), "font");
			
			title.setTypeface(tf);
			entryView.setTypeface(tf);
			entry1.setTypeface(tf);
			entry2.setTypeface(tf);
			entry3.setTypeface(tf);
			quit.setTypeface(tf);
			
		}
		
		public void readEntry(View view)
		{
			//items animated in xml
			
			//hide the entry list
			entryList.setVisibility(View.GONE);
			
			//reveal the entryview
			entryView.setVisibility(View.VISIBLE);
			
			//set the text of the button
			quit.setText("Back");
			
			//navigation bool
			entryBool = true;
			
			//get the button id
			int id = view.getId();
			
			if(id == R.id.entry1)
			{
				entryView.animateText(getApplicationContext().getString(R.string.entry1Text));
			}
			else if (id == R.id.entry2)
			{
				entryView.animateText(getApplicationContext().getString(R.string.entry2Text));
			}
			else if (id == R.id.entry3)
			{
				entryView.animateText(getApplicationContext().getString(R.string.entry3Text));
			}	
			
		}
		
		public void cancel(View view)
		{
			if(entryBool)
			{
				//reset out visibilities
				entryView.setVisibility(View.GONE);
				entryList.setVisibility(View.VISIBLE);
				
				//set the text of the button
				quit.setText("Quit");
				
				//navigation bool
				entryBool = false;
			}
			else
			{
				bgMusic.stopSong();
				noPause = true;
				Intent intent = new Intent(getApplicationContext(), StartScreen.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
		
		//on pause and on resume to pause and play music
		@Override
		public void onPause() 
		{
		    super.onPause();
		    
		    if(noPause == false)
		    {
		    //need to check if it is not null
		    	
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
		}
		
		@Override
		public void onBackPressed() 
		{
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
		
		public void stopBlink() throws FileNotFoundException
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
			
			//our other file
			File tempFile = new File(tempPath.getParentFile().getAbsolutePath() + "/temp.txt");
			PrintStream fileStream = new PrintStream(tempFile);
			
			//skip to the last line
			for(int i = 0; i < 23; i++)
			{
				String temp = sc.nextLine();
				fileStream.println(temp);
			}
			
			//set blink to zero for not to blink
			fileStream.println(0);
			
			//finish up
			sc.close();
			fileStream.close();
			tempPath.renameTo(ogPath);
			tempFile.renameTo(ogPath);
			
			
		}
	
}

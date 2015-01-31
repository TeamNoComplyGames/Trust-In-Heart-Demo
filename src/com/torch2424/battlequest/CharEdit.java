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
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.broquest.StartScreen;
import com.torch2424.trustinheartdemo.R;

public class CharEdit extends Activity 
{
	//global textviews
	ImageView player;
	TextView nameView;
	TextView genderView;
	TextView charClassView;
	TextView winsView;
	TextView lossView;
	TextView totalBattlesView;
	TextView scoreView;
	TextView guapsView;
	TextView levelView;
	TextView expView;
	TextView headView;
	TextView torsoView;
	TextView legView;
	TextView feetView;
	TextView weaponView;
	TextView availableView;
	TextView editHPView;
	TextView editStrengthView;
	TextView editIntView;
	TextView editDexView;
	TextView totalHPView;
	TextView totalStrengthView;
	TextView totalIntView;
	TextView totalDexView;
	//global stats
	String name;
	String gender;
	String charClass;
	int playerModel;
	int wins;
	int losses;
	int score;
	int guaps;
	int level;
	int exp;
	String head;
	String torso;
	String legs;
	String feet;
	String weapon;
	int available;
	int HP;
	int baseHP;
	int strength;
	int baseStrength;
	int Int;
	int baseInt;
	int dex;
	int baseDex;
	int totalHP;
	int totalStrength;
	int totalInt;
	int totalDex;
	int worlds;
	
	//int to stop reversing stats
	int statCount;
	
	//scanner
	Scanner sc;
	
	//paths to files
	File tempPath;
	File ogPath;
	
	//music player
	BGMusic bgMusic;
	boolean musicBound;
	boolean musicPaused;
	
	//to fix double pause requests
	boolean noPause;
	
	//character models array
		int[] characters = new int[]{R.drawable.dmm, R.drawable.dmr, R.drawable.dmt, R.drawable.dmw, 
				R.drawable.tmm, R.drawable.tmr, R.drawable.tmt, R.drawable.tmw, R.drawable.lmm, 
				R.drawable.lmr, R.drawable.lmt, R.drawable.lmw, R.drawable.dfm, R.drawable.dfr, 
				R.drawable.dft, R.drawable.dfw, R.drawable.tfm, R.drawable.tfr, R.drawable.tft, 
				R.drawable.tfw, R.drawable.lfm, R.drawable.lfr, R.drawable.lft, R.drawable.lfw};
	
	//to get player animation working
    AnimationDrawable playeranim;
    
    //int to know what class to go back to
    //and if level which world
    int previousClass;
    int world;
    
    //Our toast
    Toast toasty;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chareditlayout);
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("TrustInHeartPrefs", 0);
		//get stats
		tempPath = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".txt");
		ogPath = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".sav");
		try {
			getStats();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//assign stats to views
		getViews();
		
		//set fonts
		getFont();
		
		//get the total Stats
		getTotals();
		
		 //setting up music
		 playMusic();
		
		//set statcount
		statCount = 0;
		
		//get what class we came from
		Intent intent = getIntent();
		previousClass = intent.getIntExtra("CLASS", 1);
		world = intent.getIntExtra("WORLD", 1);
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
   		
   	//set up toast
   		toasty = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		
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
		 * available stat points
		 * unlocked levels(worlds)
		 * character model
		 * wins
		 * losses
		 * score
		 */
		
		//rename the file
		ogPath.renameTo(tempPath);
		
	//set up scanner to path
		sc = new Scanner(tempPath);
		//get name
		name = sc.nextLine();
		//get gender
		gender = sc.nextLine();
		String temp = sc.nextLine();
		charClass = temp.toString();
		//getting guaps
		temp = sc.nextLine();
		guaps = Integer.valueOf(temp);
		//skipping items (can be handled by items button) and purchased equip
		sc.nextLine();
		sc.nextLine();
		//getting equipment, head, torso, legs, feet, weapon
		temp = sc.nextLine();
		head = temp;
		temp = sc.nextLine();
		torso = temp.toString();
		temp = sc.nextLine();
		legs = temp.toString();
		temp = sc.nextLine();
		feet = temp.toString();
		temp = sc.nextLine();
		weapon = temp.toString();
		//getting stats HP, Strength, Intelligence, Dexterity
		temp = sc.nextLine();
		HP = Integer.valueOf(temp);
		temp = sc.nextLine();
		strength = Integer.valueOf(temp);
		temp = sc.nextLine();
		Int = Integer.valueOf(temp);
		temp = sc.nextLine();
		dex = Integer.valueOf(temp);
		//get level
		temp = sc.nextLine();
		level = Integer.valueOf(temp);
		//get exp to next level
		temp = sc.nextLine();
		exp = Integer.valueOf(temp);
		//get available stat points
		temp = sc.nextLine();
		available = Integer.valueOf(temp);
		//get unlocked world levels
		temp = sc.nextLine();
		worlds = Integer.valueOf(temp);
		//character model
		temp = sc.nextLine();
		playerModel = Integer.valueOf(temp);
		//wins
			temp = sc.nextLine();
			wins = Integer.valueOf(temp);
			//losses
			temp = sc.nextLine();
			losses = Integer.valueOf(temp);
			//score
			temp = sc.nextLine();
			score = Integer.valueOf(temp);
		
		//finished getting stats
		sc.close();
		//convert back to character.bro
		tempPath.renameTo(ogPath);
		
		//setting up base stats
		baseStrength = strength;
		baseHP = HP;
		baseInt = Int;
		baseDex = dex;
	}
	
	public void getTotals()
	{
		//set up the total stats according to equipment
		totalHP = HP;
		totalStrength = strength;
		totalInt = Int;
		totalDex = dex;
		
		if(head.contains("Baby"))
		{
			totalHP = totalHP + 1;
		}
		else if(head.contains("Red"))
		{
			totalDex = totalDex + 3;
		}
		else if(head.contains("Blue"))
		{
			totalDex = totalDex + 6;
		}
		//torso
		if(torso.contains("Baby"))
		{
			totalHP = totalHP + 1;
		}
		else if(torso.contains("Plastic"))
		{
			totalHP = totalHP + 3;
		}
		else if(torso.contains("Wooden"))
		{
			totalHP = totalHP + 6;
		}
		//legs
		if(legs.contains("Baby"))
		{
			totalHP = totalHP + 1;
		}
		//feet
		if(feet.contains("Baby"))
		{
			totalHP = totalHP + 1;
		}
		//weapon
		if(weapon.contains("Baby"))
		{
			totalStrength = totalStrength + 1;
		}
		else if(weapon.contains("Durable") || weapon.contains("Great"))
		{
			totalStrength = totalStrength + 3;
		}
		else if(weapon.contains("Strong"))
		{
			totalStrength = totalStrength + 6;
		}
		
		//set the values
		totalHPView.setText("Total HP: " + Integer.toString(totalHP));
		totalStrengthView.setText("Total Strength: " + Integer.toString(totalStrength));
		totalIntView.setText("Total Intelligence: " + Integer.toString(totalInt));
		totalDexView.setText("Total Dexterity: " + Integer.toString(totalDex));
	}
	
	public void getViews()
	{
		
		//set up character model
		player = (ImageView) findViewById(R.id.player);
		player.setBackgroundResource(characters[playerModel]);
		 //to get animation working
	   playeranim = (AnimationDrawable) player.getBackground();
	    playeranim.start();
		
		
		//set up views
		nameView = (TextView) findViewById(R.id.name);
		genderView = (TextView) findViewById(R.id.gender);
		charClassView = (TextView) findViewById(R.id.charclass);
		winsView = (TextView) findViewById(R.id.wins);
		lossView = (TextView) findViewById(R.id.losses);
		totalBattlesView = (TextView) findViewById(R.id.totalBattles);
		scoreView = (TextView) findViewById(R.id.score);
		guapsView = (TextView) findViewById(R.id.guaps);
		levelView = (TextView) findViewById(R.id.level);
		expView = (TextView) findViewById(R.id.exp);
		headView = (TextView) findViewById(R.id.head);
		torsoView = (TextView) findViewById(R.id.torso);
		legView = (TextView) findViewById(R.id.leg);
		feetView = (TextView) findViewById(R.id.feet);
		weaponView = (TextView) findViewById(R.id.weapon);
		availableView = (TextView) findViewById(R.id.available);
		editHPView = (TextView) findViewById(R.id.editHP);
		editStrengthView = (TextView) findViewById(R.id.editStrength);
		editIntView = (TextView) findViewById(R.id.editInt);
		editDexView = (TextView) findViewById(R.id.editDex);
		totalHPView = (TextView) findViewById(R.id.totalHP);
		totalStrengthView = (TextView) findViewById(R.id.totalStrength);
		totalIntView = (TextView) findViewById(R.id.totalInt);
		totalDexView = (TextView) findViewById(R.id.totalDex);
		
		//now assign all of the values
		nameView.setText(name);
		genderView.setText("Gender: " + gender);
		charClassView.setText("Job: " + charClass);
		winsView.setText("Wins: " + Integer.toString(wins));
		lossView.setText("Losses: " + Integer.toString(losses));
		totalBattlesView.setText("Total Battles: " + Integer.toString(wins + losses));
		//show current score if score is zero, else show the final story score
		if(score == 0)
		{
			//to avoid dividing by zero
			if(wins == 0 && losses == 0)
			{
				wins++;
				losses++;
			}
			//get score wins - losses/ total battles. multiplied by 100 + your money and level
			score = ((guaps * 7) + (level * 10) + ((wins + losses) * 2) - (losses * 5) + (wins * 10)); 
			scoreView.setText("Current Score: " + Integer.toString(score));
		}
		else
		{
			scoreView.setText("Final Score: " + Integer.toString(score));
		}
		guapsView.setText("Guaps: " + Integer.toString(guaps));
		levelView.setText("Level: " + Integer.toString(level));
		expView.setText("EXP to next level: " + Integer.toString(exp));
		headView.setText("Head: " + head);
		torsoView.setText("Torso: " + torso);
		legView.setText("Legs: " + legs);
		feetView.setText("Feet: " + feet);
		weaponView.setText("Weapon: " + weapon);
		availableView.setText("Available stat points: " + Integer.toString(available));
		editHPView.setText("HP: " + Integer.toString(HP));
		editStrengthView.setText("Strength: " + Integer.toString(strength));
		editIntView.setText("Intelligence: " + Integer.toString(Int));
		editDexView.setText("Dexterity: " + Integer.toString(dex));
		
		//totals done in get totals (next function)
		
	}
	
	public void getFont()
	{
		//get non-globally declared views
		Button minusHP = (Button) findViewById(R.id.minusHP);
		Button plusHP = (Button) findViewById(R.id.plusHP);
		Button minusStrength = (Button) findViewById(R.id.minusStrength);
		Button plusStrength = (Button) findViewById(R.id.plusStrength);
		Button minusInt = (Button) findViewById(R.id.minusInt);
		Button plusInt = (Button) findViewById(R.id.plusInt);
		Button minusDex = (Button) findViewById(R.id.minusDex);
		Button plusDex = (Button) findViewById(R.id.plusDex);
		Button changeEquip = (Button) findViewById(R.id.changeEquip);
		Button cancel = (Button) findViewById(R.id.cancel);
		Button saveChar = (Button) findViewById(R.id.saveChar);
		TextView equipHead = (TextView) findViewById(R.id.equip);
		TextView statsHead = (TextView) findViewById(R.id.skills);
		TextView totalstatsHead = (TextView) findViewById(R.id.stats);
		
		
		//get font
		Typeface tf = FontCache.get(getApplicationContext(), "font");
		
		//assign the font to EVERYTHING
		nameView.setTypeface(tf);
		genderView.setTypeface(tf);
		charClassView.setTypeface(tf);
		guapsView.setTypeface(tf);
		winsView.setTypeface(tf);
		lossView.setTypeface(tf);
		totalBattlesView.setTypeface(tf);
		scoreView.setTypeface(tf);
		levelView.setTypeface(tf);
		expView.setTypeface(tf);
		headView.setTypeface(tf);
		torsoView.setTypeface(tf);
		legView.setTypeface(tf);
		feetView.setTypeface(tf);
		weaponView.setTypeface(tf);
		availableView.setTypeface(tf);
		editHPView.setTypeface(tf);
		editStrengthView.setTypeface(tf);
		editIntView.setTypeface(tf);
		editDexView.setTypeface(tf);
		totalHPView.setTypeface(tf);
		totalStrengthView.setTypeface(tf);
		totalIntView.setTypeface(tf);
		totalDexView.setTypeface(tf);
		minusHP.setTypeface(tf);
		plusHP.setTypeface(tf);
		minusStrength.setTypeface(tf);
		plusStrength.setTypeface(tf);
		minusInt.setTypeface(tf);
		plusInt.setTypeface(tf);
		minusDex.setTypeface(tf);
		plusDex.setTypeface(tf);
		changeEquip.setTypeface(tf);
		cancel.setTypeface(tf);
		saveChar.setTypeface(tf);
		equipHead.setTypeface(tf);
		statsHead.setTypeface(tf);
		totalstatsHead.setTypeface(tf);
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
	    	bgMusic.playSong(R.raw.character);
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
	

//to enter the change equipment class
public void changeEquip(View view)
{
	bgMusic.pauseSong();
	//dont pause twice
	noPause = true;
	//since overowrld doesnt play song do it for it
	bgMusic.playSong(R.raw.shoptheme);
	Intent fight = new Intent(getApplicationContext(), ChangeEquip.class);
	//add getApplicationContext() flag to remove all previous activities
	fight.putExtra("CLASS", previousClass);
	startActivity(fight);
	//just start dont finish
}
	public void minus(View view)
	{
		//get the button id
		int id = view.getId();
		
		//To stop users from making their stats below five, or undoing where they
		//distribute their stats
		if(statCount <= 0)
		{
			//toasty.cancel();
			toasty.setText("Cannot undo saved stats without the special shop!");
			toasty.show();
		}
		else
		{
		//change depending on button id
		if(id == R.id.minusStrength)
		{
			if(strength == 1)
			{
				//maybe add toast saying cannot be less than zero
			}
			else if(baseStrength > strength - 1)
			{
				//toasty.cancel();
				toasty.setText("Cannot undo saved stats without the special shop!");
				toasty.show();
			}
			else
			{
			// increase availalbe stats
			available++;
			strength--;
			editStrengthView.setText("Strength: " + Integer.toString(strength));
			}
		}
		else if(id == R.id.minusHP)
		{
			if(HP == 1)
			{
				
			}
			else if(baseHP > HP - 1)
			{
				//toasty.cancel();
				toasty.setText("Cannot undo saved stats without the special shop!");
				toasty.show();
			}
			else
			{
				// increase availalbe stats
				available++;
			HP--;
			editHPView.setText("HP: " + Integer.toString(HP));
			}
		}
		else if(id == R.id.minusInt)
		{
			if(Int == 1)
			{
				
			}
			else if(baseInt > Int - 1)
			{
				//toasty.cancel();
				toasty.setText("Cannot undo saved stats without the special shop!");
				toasty.show();
			}
			else
			{
				// increase availalbe stats
				available++;
			Int--;
			editIntView.setText("Intelligence: " + Integer.toString(Int));
			}
		}
		else
		{
			if(dex == 1)
			{
				
			}
			else if(baseDex > dex - 1)
			{
				//toasty.cancel();
				toasty.setText("Cannot undo saved stats without the special shop!");
				toasty.show();
			}
			else
			{
				// increase availalbe stats
				available++;
			dex--;
			editDexView.setText("Dexterity: " + Integer.toString(dex));
			}
		}
		//set text for available stats points
		availableView.setText("Available stat points: " + Integer.toString(available));
		
		//get the total Stats
		getTotals();
		}
	}
	
	public void plus (View view)
	{
		//get the button id
		int id = view.getId();
				
		//change depending on button id
		if(id == R.id.plusHP)
		{
			if(available == 0)
			{
				
			}
			else
			{
				available--;
				HP++;
				editHPView.setText("HP: " + Integer.toString(HP));
				statCount++;
			}
		}
		else if(id == R.id.plusStrength)
		{
			if(available == 0)
			{
				
			}
			else
			{
				available--;
				strength++;
				editStrengthView.setText("Strength: " + Integer.toString(strength));
				statCount++;
			}
		}
		else if(id == R.id.plusInt)
		{
			if(available == 0)
			{
				
			}
			else
			{
				available--;
				Int++;
				editIntView.setText("Intelligence: " + Integer.toString(Int));
				statCount++;
			}
		}
		else
		{
			if(available == 0)
			{
				
			}
			else
			{
				available--;
				dex++;
				editDexView.setText("Dexterity: " + Integer.toString(dex));
				statCount++;
			}
		}
		//set text for available points
		availableView.setText("Available stat points: " + Integer.toString(available));
		
		//get the total Stats
		getTotals();
	}
	
	public void cancel(View view)
	{
		noPause = true;
		//restarting to previous class
		Intent intent;
		if(previousClass == 1)
		{
			//start screen
			bgMusic.stopSong();
			intent = new Intent(getApplicationContext(), StartScreen.class);
		}
		else if(previousClass == 2)
		{
			//overworld
			//since overowrld doesnt play song do it for it
			bgMusic.pauseSong();
			bgMusic.playSong(R.raw.overworld);
			intent = new Intent(getApplicationContext(), Overworld.class);
		}
		else if(previousClass == 3)
		{
			//level
			//since overowrld doesnt play song do it for it
			bgMusic.pauseSong();
			bgMusic.playSong(R.raw.overworld);
			intent = new Intent(getApplicationContext(), Level.class);
			intent.putExtra("WORLD", world);
		}
		else
		{
			//shop
			//since overowrld doesnt play song do it for it
			bgMusic.pauseSong();
			bgMusic.playSong(R.raw.shoptheme);
			intent = new Intent(getApplicationContext(), ShopOverworld.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void saveChar(View view)
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
		 * unlockes levels(worlds)
		 * character model
		 * wins
		 * losses
		 * score
		 */
		
		//only really need to save equipment stats and skill points
		
		//look at battle for comments on whats going on
		
		ogPath.renameTo(tempPath);
		//use the printstream to output lines
		//and use scanner to get old lines and simply replace them
		File tempFile = new File(tempPath.getParentFile().getAbsolutePath() + "/temp.txt");
		try 
		{
			PrintStream fileStream = new PrintStream(tempFile);
			sc = new Scanner(tempPath);
			
			//skip to 7th line
			for(int i = 0; i < 6; i++)
			{
				String temp = sc.nextLine();
				fileStream.println(temp);
			}
			
			//equipment
			sc.nextLine();
			fileStream.println(head);
			sc.nextLine();
			fileStream.println(torso);
			sc.nextLine();
			fileStream.println(legs);
			sc.nextLine();
			fileStream.println(feet);
			sc.nextLine();
			fileStream.println(weapon);
			
			//stats
			sc.nextLine();
			fileStream.println(Integer.toString(HP));
			sc.nextLine();
			fileStream.println(Integer.toString(strength));
			sc.nextLine();
			fileStream.println(Integer.toString(Int));
			sc.nextLine();
			fileStream.println(Integer.toString(dex));
			
			//skip to skill points
			for(int i = 0; i < 2; i++)
			{
				String temp = sc.nextLine();
				fileStream.println(temp);
			}
			sc.nextLine();
			fileStream.println(Integer.toString(available));
			
			// worlds
			sc.nextLine();
			fileStream.println(Integer.toString(worlds));
			
			//finish up everything else that isnt edited
			//model, wins, losses, score, blink
			for(int i = 0; i < 5; i++)
			{
				String temp = sc.nextLine();
				fileStream.println(temp);
			}
			
			
			//finish up
			sc.close();
			fileStream.close();
			tempPath.renameTo(ogPath);
			tempFile.renameTo(ogPath);
			
			//call cancel to get back to start
			cancel(editHPView);
		}
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	//on pause and on resume to pause and play music
		@Override
		public void onPause() 
		{
		    super.onPause();
		    
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
		   cancel(null);
		}
		
		//need to add getApplicationContext() to avoid service connection leaks
		@Override
		public void onDestroy()
		{
			super.onDestroy();
			unbindService(musicConnection);
			Unbind.unbindDrawables((ScrollView) findViewById(R.id.container));
			System.gc();
		}

}

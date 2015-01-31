package com.torch2424.battlequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.trustinheartdemo.R;

public class BossBattle extends Activity 
{
	
	//simply a modified battle activity
	//Changes:
	//only one song
	//monsters generated with more HP, a level higher than they would usually be, and less attack
	//more experience (x2)
	//different unlock dialogue
	//different layout since we want enemies to be bigger in boss mode
	
	
	//music
	BGMusic bgMusic;
	//boolean for service, on pause, and on resume
	boolean musicBound;
	boolean musicPaused;
	boolean battleOver;
	//character stats
	int guaps = 0;
	String charClass;
	String head = "" ;
	String torso = "";
	String legs = "";
	String feet = "";
	String weapon = "";
	int HP;
	int HPCap;
	int strength;
	int intelligence;
	int dexterity;
	int level;
	int exp;
	//scanner to read character file and path
	Scanner sc;
	File ogPath;
	File broPath;
	//Random to generate random numbers
	Random ran;
	//enemy Stats
	int eHP;
	int estrength;
	int eintelligence;
	int edexterity;
	int elevel;
	//turn counter
	int turn;
	//text boxes
	GameTextView console;
	ListView itemView;
	//arrays for enemy sprites
		int[] enemySprite = new int[]{R.drawable.baby_ghost, R.drawable.speyeder, R.drawable.deathflower, 
				R.drawable.composite, R.drawable.boss1,R.drawable.skull,
				R.drawable.legstheclown, R.drawable.goblin_scavenger,  R.drawable.lizaroni, R.drawable.boss2, };
		//array for enemy names
		String[] enemyName = new String[]{"Neighborly Ghost", "Speyeder", "Death Flower", "Composite",
				"Gligan The Sharp", "Cracked Skull",  "Legs The Clown", "Goblin Scavenger",  "Lizaroni", "Dark Wizard", };
		//array for enemy levels per level
		int[] enemyLevels = new int[]{1, 1, 2, 2, 4, 4, 5, 6, 7, 9};
		//array for backgrounds
		int[] backgrounds = new int[]{R.drawable.moval_bg, R.drawable.desert_bg, };
		//array for battle music
		int[] battleMusic = new int[]{R.raw.dirtybattle, R.raw.judgementday, };
	
	//array for damage sayings
	String[] damageSaying = new String[]{"BANG", "BINK", "BOP", "SLASH", 
			"BOOM", "KAPOW", "BLAM", "CLANG", "SMASH", "WHAM", "CRASH", 
			"CRACK", "BODIED", "OOOF", "SMACK", "CLINK", "SLAP", "THUD",
			"THUMP", "THWACK", "ZING", "ZAP", "SQUISH", "CLANK"};
	//enemy sprite imageview
	ImageView monster;
	AnimationDrawable monsteranim;
	//framelayout of battle to set bg
	RelativeLayout bg;
	//damage ints
	int damage;
	int edamage;
	//battle buttons
	Button fight;
	Button secondary;
	Button items;
	Button run;
	Button reset;
	Button quit;
	//viewing items boolean
	boolean itemSelect;
	//item used boolean
		boolean itemUsed;
		//item HP gain int
		int itemGain;
	//items string
	String itemString;
	//critical hits
	boolean crit;
	boolean ecrit;
	//run boolean
	boolean runFail;
	//dodges
	boolean dodge;
	boolean edodge;
	//battle animation
	Animation animation;
	//sound effects
	//handled by service now
	
	//world and level and number that determines generation
	int world;
	int worldLevel;
	int generate;
	
	//secondary attack booleans
	boolean counter;
	boolean counterFail;
	boolean block;
	boolean blockUsed;
	boolean magic;
	String magicString;
	byte charge;
	
	//pause wait boolean and counter
	boolean waiting;
	byte count;
	
	//boolean for story text
	boolean storyText;
	
	//array for monster story text
	int[] monsterText = new int[]{R.string.monster1, R.string.monster2, R.string.monster3, 
			R.string.monster4, R.string.monster5, R.string.monster6, R.string.monster7, R.string.monster8, 
			R.string.monster9, R.string.monster10, };
	
	//getting the intent
	public void getIntentStuff()
	{
		Intent intent = getIntent();
		world = intent.getIntExtra("WORLD", 1);
		worldLevel = intent.getIntExtra("Level", 1);
		storyText = intent.getBooleanExtra("STORYTEXT", false);
		generate = ((world - 1) * 5) + worldLevel;
		
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
		    	//check to see if no change allows us to switch songs or not
		    	if(bgMusic.getState())
		    	{
		    		
		    	}
		    	else
		    	{
		    		//only play the boss music
		    		bgMusic.playSong(R.raw.scaryboss);
		    	}
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
		battleOver = false;
		Intent playIntent = new Intent(getApplicationContext(), BGMusic.class);
	    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
	}
	
	
	public void setFonts()
	{
		//get font
		Typeface tf = FontCache.get(getApplicationContext(), "font");
		
		//get text
		Button fight = (Button) findViewById(R.id.fight);
		Button defend = (Button) findViewById(R.id.secondary);
		Button items = (Button) findViewById(R.id.items);
		Button run = (Button) findViewById(R.id.run);
		Button reset = (Button) findViewById(R.id.reset);
		Button quit = (Button) findViewById(R.id.quit);
		GameTextView console = (GameTextView) findViewById(R.id.console);
		
		//set text to font
		fight.setTypeface(tf);
		defend.setTypeface(tf);
		items.setTypeface(tf);
		run.setTypeface(tf);
		reset.setTypeface(tf);
		quit.setTypeface(tf);
		console.setTypeface(tf);
	}
	
	public void getButtons()
	{
		fight = (Button) findViewById(R.id.fight);
		secondary = (Button) findViewById(R.id.secondary);
		items = (Button) findViewById(R.id.items);
		run = (Button) findViewById(R.id.run);
		reset = (Button) findViewById(R.id.reset);
		quit = (Button) findViewById(R.id.quit);
		
		reset.setVisibility(View.GONE);
		quit.setVisibility(View.GONE);
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
		 *  skillpoints
		 * unlockes levels(worlds)
		 * character model
		 * wins
		 * losses
		 * score
		 */
		
	//set up scanner to path
		sc = new Scanner(broPath);
		//skipping to 3rd line
		for(int i = 0; i < 2; i++)
		{
		sc.nextLine();
		}
		String temp = sc.nextLine();
		charClass = temp.toString();
		//getting guaps
		temp = sc.nextLine();
		guaps = Integer.valueOf(temp);
		//skipping purchased equip
		temp = sc.nextLine();
		itemString = temp.toString();
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
		HPCap = HP;
		temp = sc.nextLine();
		strength = Integer.valueOf(temp);
		temp = sc.nextLine();
		intelligence = Integer.valueOf(temp);
		temp = sc.nextLine();
		dexterity = Integer.valueOf(temp);
		//get level
		temp = sc.nextLine();
		level = Integer.valueOf(temp);
		//get exp to next level
		temp = sc.nextLine();
		exp = Integer.valueOf(temp);
		//finished getting stats
		sc.close();
		//convert back to character.bro
		broPath.renameTo(ogPath);
	}
	
	public void getSecondary()
	{
		Button defend = (Button) findViewById(R.id.secondary);
		if(charClass.contentEquals("Tank"))
		{
			secondary.setText("Defend");
			defend.setBackgroundResource(R.drawable.blockbutton);
		}
		else if(charClass.contentEquals("Warrior"))
		{
			secondary.setText("Charge");
			defend.setBackgroundResource(R.drawable.chargebutton);
		}
		else if(charClass.contentEquals("Mage"))
		{
			secondary.setText("Magic");
			defend.setBackgroundResource(R.drawable.magicbutton);
		}
		else if(charClass.contentEquals("Rogue"))
		{
			secondary.setText("Counter");
			defend.setBackgroundResource(R.drawable.counterbutton);
		}
		
		//set the booleans to false
		counter = false;
		counterFail = false;
		block = false;
		blockUsed = false;
		magic = false;
	}
	
	
	public void getEquip()
	{
		if(head.contains("Baby"))
		{
			HP = HP + 1;
		}
		else if(head.contains("Red"))
		{
			dexterity = dexterity + 3;
		}
		else if(head.contains("Blue"))
		{
			dexterity = dexterity + 6;
		}
		//torso
		if(torso.contains("Baby"))
		{
			HP = HP + 1;
		}
		else if(torso.contains("Plastic"))
		{
			HP = HP + 3;
		}
		else if(torso.contains("Wooden"))
		{
			HP = HP + 6;
		}
		//legs
		if(legs.contains("Baby"))
		{
			HP = HP + 1;
		}
		//feet
		if(feet.contains("Baby"))
		{
			HP = HP + 1;
		}
		//weapon
		if(weapon.contains("Baby"))
		{
			strength = strength + 1;
		}
		else if(weapon.contains("Durable") || weapon.contains("Great"))
		{
			strength = strength + 3;
		}
		else if(weapon.contains("Strong"))
		{
			strength = strength + 6;
		}
	}
	
	public void getEnemy()
	{
		//how to generate an enemy, since each character starts with 5,5,5,10, 
		//we should get the level, then multiply that by 5, and randomly distribute until stats
		//until we get a full enemy 
		//equation to get random stats
		//get level using werg thing to round up
		elevel = enemyLevels[generate - 1];
		
		//boss battles only, boss is one level higher
		//elevel++; now done in elevel array
		
		//assignning stats
		//Since HP is the only thing user can see from stats, want to keep it not random
		//so they understand levels get harder, and they aren't random
		//bosses have more HP and Less attack so hp * 4 attack *2 instead of 3 and 3
		eHP = 5 + (elevel * 4) + generate;
		estrength = 5 + (elevel * 2) + ran.nextInt(3);
		//we arent going to change intelligence since enemies are mostly physical
		//but I may want some intelligence based attack so...
		eintelligence = 5 + (elevel * 3) + ran.nextInt(3);
		//want to make dexterity same as HP, since we want enemies to be consistently fast
		edexterity = generate + (generate/2) + (elevel/2);
		
		//just going to assign stats withut all of getApplicationContext() randomization see above
		/*
		//getting class
		int eclass = ran.nextInt(3);
		//no mage since we want enemies to be tough not weak
		//and since people tend to allocate more to their class do getApplicationContext()
		if(eclass == 0)
		{
			eHP = eHP + (elevel * 3);
		}
		else if (eclass == 1)
		{
			estrength = estrength + (elevel * 3);
		}
		else if(eclass == 2)
		{
			edexterity = edexterity + (elevel * 3);
		}
		//assigning skill points
		int skillPoints = (elevel * 2) - 0;
		while(skillPoints > 0)
		{
			int points = ran.nextInt(skillPoints) + 1;
			// to make sure everything is fair, and points aren't over allocated or
			// too dumped in one stat
			if(points > 5)
			{
				points = 5;
			}
			int which = ran.nextInt(2);
			if(which == 0)
			{
				eHP = eHP + points;
			}
			else if (which == 1)
			{
				estrength = estrength + points;
			}
			else if(which == 2)
			{
				edexterity = edexterity + points;
			}
			skillPoints = skillPoints - points;
		}
		//assigning equipment
		int equipment = elevel * 10;
		while (equipment > 0)
		{
			int points = ran.nextInt(equipment) + 1;
			// to make sure everything is fair, and points aren't over allocated or
						// too dumped in one stat
						if(points > 5)
						{
							points = 5;
						}
			int which = ran.nextInt(3);
			if(which == 0)
			{
				eHP = eHP + points;
			}
			else if (which == 1)
			{
				estrength = estrength + points;
			}
			else if(which == 2)
			{
				edexterity = edexterity + points;
			}
			equipment = equipment - points;
		}
		
		*/
		
		//assigning sprite here and in on resume for animation isues
		monster.setBackgroundResource(enemySprite[generate - 1]);
	}
	
	//function to pause game for a short while
		public void pauseWait()
		{
			//set waiting to true
			waiting = true;
			//create counter for how many times run has been called
			count = 0;
			//millisecond delay
			long delay = 350;
			//use a timer on another thread to stop fight from being executed until
			//timer ends
			final Timer timer = new Timer(true);
			TimerTask timerTask = new TimerTask() 
	        {
	            @Override
	            public void run() 
	            {  
	         	    count++;
	         	    if(count >= 4)
	         	    {
	         	    	count = 0;
	         	    	waiting = false;
	         	    	timer.cancel();
	         	    	timer.purge();
	         	    }
	            }
	        };
	        
	        timer.schedule(timerTask, 0, delay);
		}
	
	public void turn()
	{

		if(storyText == false)
		{
		//increment turn
		turn++;
		}
		
		//reset console no matter what
				console.animateText("");
				
				//set up temp text for getting the old text to build the console
				String tempText = "";
				
				
		//initialize exp and guap gain
		int expGain = 0;
		int guapGain = 0;
		int guapLoss = 0;
		
		//which sound is played
		if(turn == 1)
		{
					//nothing
		}
		else if(crit || ecrit)
		{
			//crit
			bgMusic.playSFX(1);
		}
		else
		{
			//for some reason calls atack when boss battle is first called
			if(storyText == false)
			{
			//attack
			bgMusic.playSFX(0);
			}
		}
				
		//for crits and ecrits
		if(crit)
		{
		crit = false;
		tempText = (String) console.getString();
		console.animateText(tempText + getResources().getString(R.string.crit));
					
		}
				
		if(ecrit)
		{
		ecrit = false;
		tempText = (String) console.getString();
		console.animateText(tempText + getResources().getString(R.string.ecrit));
		}
				
				
		//for dodges && edodges
		if(dodge)
		{
		dodge = false;
		tempText = (String) console.getString();
		console.animateText(tempText + getResources().getString(R.string.dodge));
		}
		if(edodge)
		{
		edodge = false;
		tempText = (String) console.getString();
		console.animateText(tempText + getResources().getString(R.string.edodge));
		}
		
		//for items
				if(itemUsed)
				{
					//item
					bgMusic.playSFX(2);
					itemUsed = false;
					tempText = (String) console.getString();
					console.animateText(tempText + "Your item gave you " + itemGain + " HP!\n");
					itemGain = 0;
				}
		
		//bgmusic get stands for no change and stuff, so only show the story text
		//if the bg music is freshm and not restarting. story text initialized with music
		if(storyText)
		{
					console.animateText(getApplicationContext().getString(monsterText[generate - 1]));
					storyText = false;
		}
		else if(turn == 1)
		{
			console.animateText("Battle Start!\n" + enemyName[generate - 1] + " appears!\nYour HP: " 
					+ HP + "\nEnemy HP: " + eHP);
		}
		else if(HP <= 0 && eHP <= 0)
		{
			if(dexterity >= edexterity)
			{
				//getting exp
				//times 2 for boss
				expGain = generate * 2;
				exp = exp - expGain;
				
				//getting money
				guapGain = generate * 3;
				guaps = guaps + guapGain;
				
				
				if(exp > 0)
				{
					tempText = (String) console.getString();
					console.animateText(tempText + "Tie!\nBut you strike first!\nYou won!\nYou gained " + expGain + " experience!" +
							"\nEnemy dropped " + guapGain + " guaps!\nEXP to next level: " +
							exp + "\nCurrent guaps: " + guaps);
				//saving
					
				try 
				{
					save();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
							
				}
				else
				{
					int expNextLevel = ((level + 1) * 20) - ((generate + elevel) - exp);
					tempText = (String) console.getString();
					console.animateText(tempText + "Tie!\nBut you strike first!\nYou won!\nYou gained " + expGain + " experience!"
							+ "\nEnemy dropped " + guapGain + " guaps!\nLEVEL UP!!!\nYou gained 5 skill points!\nCurrent level: "
							+ (level + 1) + "\nEXP to next level: " +
							 expNextLevel + "\nCurrent guaps: " + guaps);
					//level Up!
					try {
						levelUp();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//setting buttons and monster gone
				monster.setVisibility(View.GONE);
				fight.setVisibility(View.GONE);
				secondary.setVisibility(View.GONE);
				items.setVisibility(View.GONE);
				run.setVisibility(View.GONE);
				reset.setVisibility(View.VISIBLE);
				quit.setVisibility(View.VISIBLE);
			}
			else
			{
				
				//now losing money from losing, lost however much HP the enemy had left, or when ehp is
				//zero, a ran between 1 and 3
				guapLoss = 0;
				//we already know enemy has no hp no check
				//base guap loss is 3
				guapLoss = (3 * world);
				
				if(guapLoss > guaps)
				{
					guapLoss = guaps;
				}
				
				guaps = guaps - guapLoss;
				
				tempText = (String) console.getString();
				console.animateText(tempText + "Tie!\nBut the enemy strikes first!\nYou lost..." +
						"\nYou dropped " + guapLoss + " guaps..." + "\nCurrent guaps: " + guaps
						+ "\n\nTry to level up \nin past areas,\nor get new equipment \nfrom the shops!");
				
				//counter results in losses
				if(counter)
				{
					tempText = (String) console.getString();
					console.animateText(tempText + "\n\nCounter succesful!\nYou used the enemy attack against them!");
					counter = false;
				}
				else if(counterFail)
				{
					tempText = (String) console.getString();
					console.animateText(tempText + "\n\nCounter failed...\nHigher dexterity increases your chance!");
					counterFail = false;
				}
				
				//save the loss
				try {
					saveLoss();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//setting buttons
				fight.setVisibility(View.GONE);
				secondary.setVisibility(View.GONE);
				items.setVisibility(View.GONE);
				run.setVisibility(View.GONE);
				reset.setVisibility(View.VISIBLE);
				quit.setVisibility(View.VISIBLE);
			}
		}
		else if(eHP <= 0)
		{
			//getting exp * 4
			expGain = generate * 2;
			exp = exp - expGain;
			
			//getting money
			guapGain = generate * 3;
			guaps = guaps + guapGain;
			
			
			//saving win
			if(exp > 0)
			{
			try
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "You won!\nYou gained " + expGain + " experience!" +
						"\nEnemy dropped " + guapGain + " guaps!\nEXP to next level: " +
						exp + "\nCurrent guaps: " + guaps);
				//saving
				save();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
			else
			{
				int expNextLevel = ((level + 1) * 20) - ((generate + elevel) - exp);
				tempText = (String) console.getString();
				console.animateText(tempText + "You won!\nYou gained " + expGain + " experience!" +
						"\nEnemy dropped " + guapGain + " guaps!\nLEVEL UP!!!\nYou gained 5 skill points!\nCurrent level: " 
						 + (level + 1) + "\nEXP to next level: " +
						expNextLevel + "\nCurrent guaps: " + guaps);
				//level up!!!
				try {
					levelUp();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			
			
			//setting buttons and moster dead
			monster.setVisibility(View.GONE);
			fight.setVisibility(View.GONE);
			secondary.setVisibility(View.GONE);
			items.setVisibility(View.GONE);
			run.setVisibility(View.GONE);
			reset.setVisibility(View.VISIBLE);
			quit.setVisibility(View.VISIBLE);
		}
		else if(HP <= 0)
		{
			
			//now losing money from losing, lost however much HP the enemy had left, or when ehp is
			//zero, a ran between 1 and 5
			guapLoss = 0;
			guapLoss = eHP;
			
			//apply base guaploss
			if(guapLoss < 3 * world)
			{
				guapLoss = (3 * world);
			}
			
			if(guapLoss > guaps)
			{
				guapLoss = guaps;
			}
			
			guaps = guaps - guapLoss;
			
			
			tempText = (String) console.getString();
			console.animateText(tempText + "You lost..." +
						"\nYou dropped " + guapLoss + " guaps..." + "\nCurrent guaps: " + guaps
						+ "\n\nTry to level up \nin past areas,\nor get new equipment \nfrom the shops!");
			
			//in case you lose while trying to run (wont apply to tie since you do no damage
			if(runFail)
			{
				runFail = false;
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nYou failed to run away...");
			}
			//adding counter results to losses
			else if(counter)
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nCounter succesful!\nYou used the enemy attack against them!");
				counter = false;
			}
			else if(counterFail)
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nCounter failed...\nHigher dexterity increases your chance!");
				counterFail = false;
			}
			
			//saving the loss
			try {
				saveLoss();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			//setting buttons
			fight.setVisibility(View.GONE);
			secondary.setVisibility(View.GONE);
			items.setVisibility(View.GONE);
			run.setVisibility(View.GONE);
			reset.setVisibility(View.VISIBLE);
			quit.setVisibility(View.VISIBLE);
			

			
		}
		else
		{
			//show damage
			tempText = (String) console.getString();
			console.animateText(tempText + damageSaying[ran.nextInt(damageSaying.length)] + "!\nYou dealt " + damage + 
					" to the enemy\nEnemy deals " + edamage + 
					" to you\nYour HP: " + HP + "\nEnemy HP: "+ eHP);
			
			//for warriors multi part special
			//and reset secondaries
			if(charge >= 1 && charge < 2)
			{
				charge++;
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nCharging the attack...");
			}
			else if(charge == 2)
			{
				charge++;
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nCharge Succesful!\nUnleash the attack!");
			}
			else if(counter)
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nCounter succesful!\nYou used the enemy attack against them!");
				counter = false;
			}
			else if(counterFail)
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nCounter failed...\nHigher dexterity increases your chance!");
				counterFail = false;
			}
			else if(block && blockUsed == false)
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nBlock Succesful!\nNo damage was taken!");
				block = false;
				blockUsed = true;
			}
			else if(blockUsed && block)
			{
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nBlock failed...\nBlock can only be used once per battle!");
				block = false;
			}
			else if(runFail)
			{
				runFail = false;
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nYou failed to run away...");
			}
			else if(magic)
			{
				magic = false;
				tempText = (String) console.getString();
				console.animateText(tempText + "\n\nYou cast " + magicString + "!");
			}
			
		}
	}
	
	//function for when secodnary attack chosen
	public void secondary(View view)
	{
		//check to see if were waiting so user reads console
		if(waiting)
		{
			
		}
		else if(turn == 0)
		{
			//go straight to turn since story was displayed
			turn();
		}
		else
		{
		if(charClass.contentEquals("Rogue"))
		{
			int chance = ran.nextInt(100) + 1;
			if(chance >= (55 - (dexterity/9 * 5)))
			{
				counter = true;
			}
			else
			{
				counterFail = true;
			}
		}
		else if(charClass.contentEquals("Tank"))
		{
			block = true;
		}
		else if(charClass.contentEquals("Warrior"))
		{
			if(charge == 0)
			{
			charge = 1;
			}
		}
		else if(charClass.contentEquals("Mage"))
		{
			magic = true;
		}
		
		//call fight since we now have our booleans set up
		fight(null);
		}
	}
	
	public void fight(View view)
	{
		//use timer on another thread to set a boolean to true, no inputs are detected unitl timer ends
		//we can do getApplicationContext() to pause the game and make sure the user reads the console
		//waiting is boolean that decieds getApplicationContext()
		if(waiting)
		{
			
		}
		else if(turn == 0)
		{
			//go straight to turn since story was displayed
			turn();
		}
		else
		{
		//dealing damage to enemy
		//did you get a crit? or did enemy dodge
		int chance = ran.nextInt(100) + 1;
		int dodgeChance = ran.nextInt(100) + 1;
		//crit
		if(chance >= (100 - (dexterity/9 * 5)))
		{
			crit = true;
		}
		
		//edodge
		if(dodgeChance >= (100 - (edexterity/9 * 5)))
		{
			edodge = true;
		}
		
		//if crit is true
		if(crit == true)
		{
			damage = (strength + 1)/2;
		}
		else
		{
		int basedamage = strength/8;
		damage = basedamage + ran.nextInt(basedamage/5 + 3);
		}
		
		//say getApplicationContext() before dodge since we want damage to be zero
		if (damage < 1)
		{
			damage = 1;
		}
		
		//secondary abilities
		//counter done in enemy section
		if(charge > 0)
		{
			if(charge < 3)
			{
				damage = 0;
				crit = false;
			}
			else
			{
				charge = 0;
				damage = (strength + 1)/2;
				crit = true;
			}
		}
		else if(magic)
		{
			getMagic();
		}
		
		//did enemy dodge? did you try to run? did you use an item
				if(edodge == true || runFail || itemUsed || counterFail)
				{
					damage = 0;
					crit = false;
				}
				
		//need to stop crit and edodge from appearing if they ran or use item
				if(runFail || itemUsed)
				{
					crit = false;
					edodge = false;
				}
		

		if(counter == false)
		{
			eHP = eHP - damage;
		}
		
		
		//dealing damage to you
		//did they get a crit? or did you dodge
		//chances
		chance = ran.nextInt(100) + 1;
		dodgeChance = ran.nextInt(100) + 1;
		
		//ecrit
		if(chance >= (100 - (edexterity/9 * 5)))
		{
			ecrit = true;
		}
		
		//dodge
		if(dodgeChance >= (100 - (dexterity/9 * 5)))
		{
			dodge = true;
		}
		
		//is ecrit true?
		if(ecrit == true)
		{
			edamage = (estrength + 1)/2;
		}
		else
		{
			int baseedamage = estrength/8;
			edamage = baseedamage + ran.nextInt(baseedamage/5 + 3);
		}
		
		//declare before dodge
		if (edamage < 1)
		{
			edamage = 1;
		}
		
		
		//secondary abilities
		//counter did below
				if(block && blockUsed == false)
				{
					edamage = 0;
					
				}
				
				//did you dodge?
				if(dodge == true)
				{
					edamage = 0;
					ecrit = false;
				}
		
		if(counter)
		{
			//incase the enemy dodges do nothing
			if(edodge)
			{
				damage = 0;
			}
			//no user crits only enemy ones
			if(crit)
			{
				crit = false;
			}
			damage = edamage;
			eHP = eHP - damage;
		}
			HP = HP - edamage; 
		
		//doing animation on monster
		animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enemy_damage);  
        monster.startAnimation(animation);
        
        //reset our secondaries
        //done at the end of a turn where battle doesnt end
        //since we dont need to reset it if the battle ends
        
        //call pause wait so user reads console and stuff
        pauseWait();
		
		//calling turn
		turn();
		}
	}
	
	public void getMagic()
	{
		//find out which magic to use
		if(weapon.contains("Baby"))
		{
			//small fireball does base 1 damage
			magicString = "Small Fireball";
			//1 is base damage
			damage = (1 + ran.nextInt(intelligence/7 + 2));
			if(crit)
			{
				damage = (intelligence + 1)/2;
			}
		}
		else if(weapon.contains("Water"))
		{
			//small fireball does base 1 damage
			magicString = "Water Slash";
			//2 is base damage
			damage = (2 + ran.nextInt(intelligence/7 + 2));
			if(crit)
			{
				damage = (intelligence + 1)/2;
			}
		}
		else if(weapon.contains("Good"))
		{
			//small fireball does base 1 damage
			magicString = "Minor Healing, you gain 2 HP";
			//2 is base damage
			damage = 0;
			//if you get critical hit you gain that much HP
			if(crit)
			{
				HP = HP + (intelligence + 1)/2;
				if(HP >= HPCap)
				{
					magicString = "Minor Healing. Critical Hit! Your HP maxed out";
				}
				else
				{
					magicString = "Minor Healing. Critical Hit! You gain " + Integer.toString(intelligence);
				}
			}
		}
	}
	
	public void items(View view)
	{
		if(itemSelect)
		{
			itemsMenuReset();
		}
		else if(turn == 0)
		{
			//go straight to turn since story was displayed
			turn();
		}
		else
		{
		//set the item selecting boolean
		itemSelect = true;
		
		//hide the console
		console.setVisibility(View.GONE);
		
		//show the listview
		itemView.setVisibility(View.VISIBLE);
		
		//hide the buttons
		fight.setVisibility(View.GONE);
		secondary.setVisibility(View.GONE);
		run.setVisibility(View.GONE);
		
		//set the items button text to back
		items.setText("Back");
		
		//set up the items array
		String[] array = itemString.split("/");
		final List<String> objects = new ArrayList<String>();
		for(int i = 0; i < array.length; i++)
		{
			//we dont want unncessary space in array
			if(array[i].contentEquals("") == false)
			{
				objects.add(array[i]);
			}
		}
		//set up listview
		itemView.setAdapter(new CustomListview(getApplicationContext(), objects, false));
		
		// listener for when someone clicks a file
				OnItemClickListener listclick = new OnItemClickListener() 
				{
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {
				    	String selectedItem = objects.get(position);
				    	
				    	//water heals 4 hp
				    	if(selectedItem.contains("Water"))
				    	{
				    		itemGain = 4;
				    		HP = HP + itemGain;
				    	}
				    	//tea heals 10
				    	else if(selectedItem.contains("Tea"))
				    	{
				    		itemGain = 10;
				    		HP = HP + itemGain;
				    	}
				    	
				    	//recreate itemString and remove used item
				    	objects.remove(position);
				    	String tempString = "";
				    	for(int i = 0; i < objects.size(); i++)
				    	{
				    		if(i + 1 == objects.size())
				    		{
				    			tempString = tempString + objects.get(i);
				    		}
				    		else
				    		{
				    		tempString = tempString + objects.get(i) + "/";
				    		}
				    	}
				    	itemString = tempString;
				    	
				    	//now reset the menu
				    	itemsMenuReset();
				    	
				    	//finish and call fight
				    	itemUsed = true;
				    	fight(null);
				    }
				};

				itemView.setOnItemClickListener(listclick); 
		
		}
	}
	
	public void itemsMenuReset()
	{
		//reset everything back
		itemSelect = false;
		
		//reset console
		console.setVisibility(View.VISIBLE);
		
		//hide the listview
		itemView.setVisibility(View.GONE);
		
		//reset buttons
		fight.setVisibility(View.VISIBLE);
		secondary.setVisibility(View.VISIBLE);
		run.setVisibility(View.VISIBLE);
		
		//set the items button text to items
		items.setText("Items");
	}
	
	public void run(View view)
	{
		//pause wait
		if(waiting)
		{
			
		}
		else if(turn == 0)
		{
			//go straight to turn since story was displayed
			turn();
		}
		else
		{
			runFail = false;
			//run succesful/slightly harder to run from boss
			if(ran.nextInt(100) + 1 >= (70 - dexterity) )
			{
				//dont stop music, messes up flow of game
				
				
				//play run away sound
				bgMusic.playSFX(4);
				
				//setting console text
				console.animateText("Look Bro, I aint trying to\ndie tonight!\n\n\nYou ran away...");
				//resetting buttons
				fight.setVisibility(View.GONE);
				secondary.setVisibility(View.GONE);
				items.setVisibility(View.GONE);
				run.setVisibility(View.GONE);
				reset.setVisibility(View.VISIBLE);
				quit.setVisibility(View.VISIBLE);
				
				}
			
			else
			{
				//run failed
			runFail = true;
			//call turn
			fight(null);
			}
			
			
		}
		
		
		
	}
	
	public void reset(View view)
	{
		//pause wait boolean
		if(waiting)
		{
			
		}
		else
		{
		battleOver = true;
		//make sure no change is set to true for when activity is called again
		if(bgMusic.getState())
		{
			
		}
		else
		{
			bgMusic.changeState();
		}
		//finish activity and call it again
			Intent intent = new Intent(getApplicationContext(), BossBattle.class);
			intent.putExtra("WORLD", world);
			intent.putExtra("Level", worldLevel);
			intent.putExtra("STORYTEXT", false);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
		}
			
	}
	
	public void quit(View view)
	{
		if(waiting || bgMusic.fading)
		{
			
		}
		else
		{
		//to fade out the song manually
		battleOver = true;
		bgMusic.pauseSong();
		
		//make sure no change is set to false for when activity is called again
				if(bgMusic.getState())
				{
					bgMusic.changeState();
				}
				else
				{
					
				}
		//play the level song since level activity doesnt play songs
		bgMusic.playSong(R.raw.overworld);
		
		//dont pause music here since on stop will do it for us
		//just finish, we already have a world behind us with the right world
		Intent intent = new Intent(getApplicationContext(), Level.class);
		intent.putExtra("WORLD", world);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		}
	}
	
	//method to save if you lose
	public void saveLoss() throws IOException
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
		
		//renaming ogpath to bro path

				ogPath.renameTo(broPath);
				//use getApplicationContext() to replace lines of txt file
				//use the printstream to output lines
				//and use scanner to get old lines and simply replace them
				//use data directory tempoararily to make our new text file, will be deleted anyways
				File tempFile = new File(broPath.getParentFile().getAbsolutePath() + "/temp.txt");
				PrintStream fileStream = new PrintStream(tempFile);
				sc = new Scanner(broPath);
				//skipping to forth line
				for(int i = 0; i < 3; i++)
				{
					String temp = sc.nextLine();
					fileStream.println(temp);
				}
				//class is being overwritten
				sc.nextLine();
				fileStream.println(guaps);
				//print items
				sc.nextLine();
				fileStream.println(itemString);
				
				//skipping to losses
				for(int i = 0; i < 16; i++)
				{
					String temp = sc.nextLine();
					fileStream.println(temp);
				}
				
				//losses + 1
				String lastTemp = sc.nextLine();
				int tempInt = Integer.valueOf(lastTemp);
				tempInt++;
				fileStream.println(Integer.toString(tempInt));
				//score stays the same, wont change till you beat last boss
				String temp = sc.nextLine();
				fileStream.println(temp);
				
				//blink stays the same
				temp = sc.nextLine();
				fileStream.println(temp);
				
				//finish
				sc.close();
				fileStream.close();
				
				//fininshing up and renaming to ogpath
				broPath.renameTo(ogPath);
				tempFile.renameTo(ogPath);	
		
	}
	
	//method to save if you win
	public void save() throws IOException
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
		
		//open up the demoover activity
		if(generate == 10)
		{
		Intent intent = new Intent(getApplicationContext(), DemoOver.class);
		startActivity(intent);
		}
		
		//renaming ogpath to bro path

		ogPath.renameTo(broPath);
		//use getApplicationContext() to replace lines of txt file
		//use the printstream to output lines
		//and use scanner to get old lines and simply replace them
		//use data directory tempoararily to make our new text file, will be deleted anyways
		File tempFile = new File(broPath.getParentFile().getAbsolutePath() + "/temp.txt");
		PrintStream fileStream = new PrintStream(tempFile);
		sc = new Scanner(broPath);
		//skipping to forth line
		for(int i = 0; i < 3; i++)
		{
			String temp = sc.nextLine();
			fileStream.println(temp);
		}
		//class is being overwritten
		sc.nextLine();
		fileStream.println(guaps);
		//print items
		sc.nextLine();
		fileStream.println(itemString);
		//skipping to exp to next level
		for(int i = 0; i < 11; i++)
		{
			String temp = sc.nextLine();
			fileStream.println(temp);
		}
		//print exp
		fileStream.println(exp);
		sc.nextLine();
		//finish skill points
		String lastTemp = sc.nextLine();
		fileStream.println(lastTemp);
		//finish unlocked worlds
		lastTemp = sc.nextLine();
		//store this for later
		String blinkCheck = lastTemp;
		//check to see if you've never beaten getApplicationContext() world before
		if(Integer.valueOf(lastTemp) == generate)
		{
			//play boss unlock sound
			bgMusic.playSFX(7);
			
			String temptext = (String) console.getString();
			//added unlock diaogue to change level to world and mention journal entry
			console.animateText(temptext + "\n\nThe next world has been opened!\nNew journal entry unlocked!");	
			fileStream.println(Integer.toString(generate + 1));
		}
		else
		{
			fileStream.println(lastTemp);
		}
		//character model
		lastTemp = sc.nextLine();
		fileStream.println(lastTemp);
		//wins/losses/total battles
		//wins + 1
		lastTemp = sc.nextLine();
		int tempInt = Integer.valueOf(lastTemp);
		tempInt++;
		fileStream.println(Integer.toString(tempInt));
		//losses stay the same
		lastTemp = sc.nextLine();
		fileStream.println(lastTemp);
		//score stays the same, wont change till you beat last boss
		String temp = sc.nextLine();
		fileStream.println(temp);
		
		//need to check if you just unlock journal
		//use blinkcheck from earlier when we found unlocked worlds
		if(Integer.valueOf(blinkCheck) == generate)
		{
			//blink no matter what
			temp = sc.nextLine();
			fileStream.println(1);
		}
		else
		{
			//blink stays the same
			temp = sc.nextLine();
			fileStream.println(temp);
		}
		
		
		sc.close();
		fileStream.close();
		
		//fininshing up and renaming to ogpath
		broPath.renameTo(ogPath);
		tempFile.renameTo(ogPath);
	}
	
	//method for saving when you level up
	public void levelUp() throws FileNotFoundException
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
		 */
		
		//play the level up sound first
		bgMusic.playSFX(3);
		
		//open up the demoover activity
				if(generate == 10)
				{
				Intent intent = new Intent(getApplicationContext(), DemoOver.class);
				startActivity(intent);
				}
		
		//renaming ogpath to bro path

		ogPath.renameTo(broPath);
		//use getApplicationContext() to replace lines of txt file
		//use the printstream to output lines
		//and use scanner to get old lines and simply replace them
		//use data directory tempoararily to make our new text file, will be deleted anyways
		File tempFile = new File(broPath.getParentFile().getAbsolutePath() + "/temp.txt");
		PrintStream fileStream = new PrintStream(tempFile);
		sc = new Scanner(broPath);
		//skipping to forth line
		for(int i = 0; i < 3; i++)
		{
			String temp = sc.nextLine();
			fileStream.println(temp);
		}
		//class is being overwritten
		sc.nextLine();
		fileStream.println(guaps);
		//print items
		sc.nextLine();
		fileStream.println(itemString);
		//skipping to level
		for(int i = 0; i < 10; i++)
		{
			String temp = sc.nextLine();
			fileStream.println(temp);
		}
		fileStream.println(level + 1);
		sc.nextLine();
		//print exp
		//was times 15, but leveling too fast
		//so now times 20
		fileStream.println( ((level + 1) * 20) - ((generate * 4) - exp) );
		sc.nextLine();
		//finish skill points
		String temp = sc.nextLine();
		//becomes 6 so add 4 to make it 5
		fileStream.println(Integer.valueOf(temp) + 5);
		
		//finish unlocked worlds
				String lastTemp = sc.nextLine();
				String blinkCheck = lastTemp;
				//check to see if you've never beaten getApplicationContext() world before
				if(Integer.valueOf(lastTemp) == generate)
				{
					String temptext = (String) console.getString();
					//added unlock diaogue to change level to world and mention journal entry
					console.animateText(temptext + "\n\nThe next world has been opened!\nNew journal entry unlocked!");
					fileStream.println(Integer.toString(generate + 1));
				}
				else
				{
					fileStream.println(lastTemp);
				}
				
				//character model
				lastTemp = sc.nextLine();
				fileStream.println(lastTemp);
				//wins/losses/total battles
				//wins + 1
				lastTemp = sc.nextLine();
				int tempInt = Integer.valueOf(lastTemp);
				tempInt++;
				fileStream.println(Integer.toString(tempInt));
				//losses stay the same
				lastTemp = sc.nextLine();
				fileStream.println(lastTemp);
				//score stays the same, wont change till you beat last boss
				lastTemp = sc.nextLine();
				fileStream.println(lastTemp);
				
				
				//need to check if you just unlock journal
				//use blink check from earlier when we found unlocked worlds
				if(Integer.valueOf(blinkCheck) == generate)
				{
					//blink no matter what
					temp = sc.nextLine();
					fileStream.println(1);
				}
				else
				{
					//blink stays the same
					temp = sc.nextLine();
					fileStream.println(temp);
				}
				
				
		sc.close();
		fileStream.close();
		
		//fininshing up and renaming to ogpath
		broPath.renameTo(ogPath);
		tempFile.renameTo(ogPath);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//contentview now boss battle
		setContentView(R.layout.activity_bossbattle);
		//setting fonts
		setFonts();
		//get character info
		File inPath = new File(getApplicationContext().getFilesDir(), "");
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("TrustInHeartPrefs", 0);
		ogPath = new File(inPath.getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".sav");
		broPath = new File(inPath.getAbsolutePath() + "/Trust In Heart-" +
						prefs.getString("SAVEFILE", "character") + ".txt");
		ogPath.renameTo(broPath);
		//initilize buttons
		getButtons();
		//set up items listview
		itemView = (ListView) findViewById(R.id.ListView1);
		itemView.setVisibility(View.GONE);
		itemSelect = false;
		itemUsed = false;
		itemGain = 0;
		//initializing pause wait boolean
		waiting = false;
		//intializing crit chance
		crit = false;
		ecrit = false;
		//initialize running
		runFail = false;
		//initializing dodge chance
		dodge = false;
		edodge = false;
		//getting character stats
		try {
			getStats();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get secondary button depending on class
		getSecondary();
		//add equipment to stats
		getEquip();
		//generate enemy
		//myImgView.setImageResource(R.drawable.monkey);
		monster = (ImageView) findViewById(R.id.monster);
		//getting intent stuff before I start assigning stuff
		getIntentStuff();
		//setting background
		bg = (RelativeLayout) findViewById(R.id.battleScreen);
		bg.setBackgroundResource(backgrounds[world - 1]);
		//starting music
		playMusic();
		ran = new Random();
		getEnemy();
		
		//initilize warrior secondary
		charge = 0;
		//start first turn
		console = (GameTextView) findViewById(R.id.console);
		turn = 0;
		turn();
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
   		
   		
		//override back pressed to return to start screen, and finish its activity and reset music player
	}
	
	//on pause and on resume to pause and play music
		@Override
		public void onPause() 
		{
			 super.onPause();
			    
			    //need to check if it is not null
			    if(bgMusic != null)
			    {
			    	if(battleOver)
			    	{
			    		
			    	}
			    	else
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
		    //get animation working on device
		    monsteranim = (AnimationDrawable) monster.getBackground();
		    monsteranim.start();
		}
		
		@Override
		public void onBackPressed() 
		{
		   if(quit.isShown())
		   {
			   quit(null);
		   }
		   else if(items.getText() == "Back")
		   {
			   items(null);
		   }
		   else
		   {
			   run(null);
		   }
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

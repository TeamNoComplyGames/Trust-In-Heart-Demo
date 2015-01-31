package com.torch2424.battlequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.trustinheartdemo.R;

public class ChangeEquip extends Activity 
{
	//layout of battle to set shopbg
			RelativeLayout shopBG;
			AnimationDrawable BGanim;
			
			
		//music
				BGMusic bgMusic;
				boolean musicBound;
				boolean musicPaused;
				//boolean to avoid on pause if world selected
				boolean noPause;
				
				//stats stuff
				Scanner sc;
				File tempPath;
				File ogPath;
				
				//guaps, score, wins losses etc...
				String itemString;
				String equipString;
				String head;
				String torso;
				String legs;
				String feet;
				String weapon;
				int HP;
				int str;
				int Int;
				int dex;
				int level;
				int exp;
				int available;
				int wins;
				int losses;
				
				//views
				GameTextView shopText;
				Button equip;
				Button quit;
				
				//Shopkeeper Sayings
				int[] sayings = new int[]{R.string.store1, R.string.store2, R.string.store3, R.string.store4, 
						R.string.store5, R.string.store6, R.string.store7, R.string.store8, R.string.store9, 
						R.string.store10, R.string.store11, R.string.store12, R.string.store13, R.string.store14, 
						R.string.store15, R.string.store16, R.string.store17, R.string.store18, R.string.store19, 
						R.string.store20, R.string.store21, R.string.store22, R.string.store23, R.string.store24, 
						R.string.store25, R.string.store26, R.string.store27, R.string.store28, R.string.store29, 
						R.string.store30, R.string.store31, R.string.store32, R.string.store33, R.string.store34, 
						R.string.store35, R.string.store36, };
				Random ran;
				
				//our listview stuff
				String[] array;
				List<String> objects;
				ListView menu;
				String selectedItem;
				String oldItem;
				
				//boolean firstOpened
				boolean firstOpened;
				//int whichClass, need to send this back to char edit when we reopen it
				int whichClass;
				//confirm boolean to make sure they want to equip
				boolean confirm;
				boolean itemSelected;
				
				//SFX sound
				//handled by service now
				
				
				@Override
				protected void onCreate(Bundle savedInstanceState) 
				{
					super.onCreate(savedInstanceState);
					setContentView(R.layout.activity_changeequip);
					
					//dont need to set up fonts but need to set up music
					playMusic();
					noPause = false;
					
					//aquire wakelock
			   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			   		
			   		//getting started on setting up the actual shop
			   		//get intents
			   		Intent intent = getIntent();
			   		//need to send back the activity we came from
			   		whichClass = intent.getIntExtra("CLASS", 1);
			   		
			   		//get the shop background
			   		shopBG = (RelativeLayout) findViewById(R.id.shopScreen);
			   		BGanim = (AnimationDrawable) shopBG.getBackground();
			   		
			   		//get your money, stats, and score
			   		try {
						getStats();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			   		
			   		//set the fonts of everything and get their views for below
			   		setFonts();
			   		
			   		
			   		
			   		//setup shop keeper saying 
			   		ran = new Random();
			   		
			   		//this is done in on resume using first open boolean
			   		firstOpened = true;
			   		//shopText.animateText(getResources().getString(sayings[ran.nextInt(sayings.length)]));
			   		
			   		//animate bg while text is animating
			   		//animateBG(); //the whole move mouth while talking thing doesnt work...
			   		
			   		//slower Text for shops change it in on puase and on resume
			   		shopText.setTextSpeed(2);
			   		
			   		//need this for sell
			   		//String[] array = itemString.split("/");
			   		//now to set up listview
			   		//since we start on the equip screen
			   		objects = new ArrayList<String>();
			   		getListview();
			   		
			   		//we are not confirming anything yet
			   		confirm = false;
			   		
					
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
				    	//handled by charedit
				    		//bgMusic.playSong(R.raw.shoptheme);
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
				Intent playIntent = new Intent(this, BGMusic.class);
			    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			    
			}
			
			public void setFonts()
			{
				//get the views
		   		shopText = (GameTextView) findViewById(R.id.shopText);
		   		equip = (Button) findViewById(R.id.equip);
		   		quit = (Button) findViewById(R.id.quit);
		   		menu = (ListView) findViewById(R.id.ListView1);
		   		
		   		//get font
		   		Typeface tf = FontCache.get(getApplicationContext(), "font");
				
				//set the fonts
				shopText.setTypeface(tf);
				equip.setTypeface(tf);
				quit.setTypeface(tf);
			}
			
			//gets the array we need to create a listview
			public void getArray()
			{
				array = null;
				objects.clear();
					//we need to check if our strings have items in them
					//first add your items to arraylist
					if(itemString.length() > 2)
					{
					String[] temp = itemString.split("/");
					for(int i = 0; i < temp.length; i++)
					{
						//we dont want unncessary space in array
						if(temp[i].contentEquals("") == false)
						{
							objects.add(temp[i]);
						}
					}
					}
					
					//add your objects to arraylist
					if(equipString.length() > 2)
					{
					 String[] temp = equipString.split("/");
					for(int i = 0; i < temp.length; i++)
					{
						if(temp[i].contentEquals("") == false)
						{
							objects.add(temp[i]);
						}
					}
					}
					
					//now add equipement to object string
					
					objects.add(head + " " + getResources().getString(R.string.equipped));
					objects.add(torso + " " + getResources().getString(R.string.equipped));
					objects.add(legs + " " + getResources().getString(R.string.equipped));
					objects.add(feet + " " + getResources().getString(R.string.equipped));
					objects.add(weapon + " " + getResources().getString(R.string.equipped));
					
					//sort our items and equipment
					Collections.sort(objects);
			}
			
			public void getListview()
			{
				//set up the items array
		   		getArray();
		   		menu.setAdapter(new CustomListview(this, objects, false));
		   		
		   		//set up the listview
		   	// listener for when someone clicks a file
				OnItemClickListener listclick = new OnItemClickListener() 
				{
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {
				    	selectedItem = objects.get(position);
				    	itemSelected = true;
				    	
				    	//need only the first line of a description
				    	String temp = getResources().getString(whichD());
				    	String [] tempArray = temp.split("\n");
				    	temp = tempArray[0];
				    	
				    	shopText.animateText(temp);
				    	talk();
				    }
				};
					//set the on click listener
					menu.setOnItemClickListener(listclick); 
			}
			
			public void talk()
			{
				//easy way to make character talk
				if(BGanim.isRunning())
				{
					BGanim.stop();
				}
				
				BGanim.start();
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
					 * unlockes levels(worlds)
					 * character model
					 * wins
					 * losses
					 * score
					 */
				
				SharedPreferences prefs = this.getSharedPreferences("TrustInHeartPrefs", 0);
				
				//paths
				tempPath = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
						prefs.getString("SAVEFILE", "character") + ".txt");
				ogPath = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
						prefs.getString("SAVEFILE", "character") + ".sav");
					
				//rename the file
				ogPath.renameTo(tempPath);
				
			//set up scanner to path
				sc = new Scanner(tempPath);
				//string
				String temp;
				//skipping to forth line
				for(int i = 0; i < 4; i++)
				{
					temp = sc.nextLine();
				}
				//no guaps guaps
				//getting items and equip
				temp = sc.nextLine();
				itemString = temp;
				temp = sc.nextLine();
				equipString = temp;
				//getting equipment
				temp = sc.nextLine();
				head = temp;
				temp = sc.nextLine();
				torso = temp;
				temp = sc.nextLine();
				legs = temp;
				temp = sc.nextLine();
				feet = temp;
				temp = sc.nextLine();
				weapon = temp;
				//getting stats HP, Strength, Intelligence, Dexterity
				temp = sc.nextLine();
				HP = Integer.valueOf(temp);
				temp = sc.nextLine();
				str = Integer.valueOf(temp);
				temp = sc.nextLine();
				Int = Integer.valueOf(temp);
				temp = sc.nextLine();
				dex = Integer.valueOf(temp);
				//get level
				temp = sc.nextLine();
				level = Integer.valueOf(temp);
				temp = sc.nextLine();
				exp = Integer.valueOf(temp);
				temp = sc.nextLine();
				available = Integer.valueOf(temp);
				//skipping 3 lines
				for(int i = 0; i < 2; i++)
				{
					temp = sc.nextLine();
				}
				//wins
					temp = sc.nextLine();
					wins = Integer.valueOf(temp);
					//losses
					temp = sc.nextLine();
					losses = Integer.valueOf(temp);
					//score
					sc.nextLine();
				
				//finished getting stats
				sc.close();
				//convert back to character.bro
				tempPath.renameTo(ogPath);
				
			}
			
			public void savePurchase() throws FileNotFoundException
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
				PrintStream fileStream = new PrintStream(tempFile);
				sc = new Scanner(tempPath);
					
					//skip to 5th line
					for(int i = 0; i < 4; i++)
					{
						String temp = sc.nextLine();
						fileStream.println(temp);
					}
					//no money
					//itemstring and equipString
					sc.nextLine();
					fileStream.println(itemString);
					sc.nextLine();
					fileStream.println(equipString);
					
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
					fileStream.println(Integer.toString(str));
					sc.nextLine();
					fileStream.println(Integer.toString(Int));
					sc.nextLine();
					fileStream.println(Integer.toString(dex));
					sc.nextLine();
					//level exp to next level skill pints
					fileStream.println(Integer.toString(level));
					sc.nextLine();
					fileStream.println(Integer.toString(exp));
					sc.nextLine();
					fileStream.println(Integer.toString(available));
					//finish up everything else that isnt edited
					//world, model, wins, losses, score, blink
					for(int i = 0; i < 6; i++)
					{
						String temp = sc.nextLine();
						fileStream.println(temp);
					}
					
					
					//finish up
					sc.close();
					fileStream.close();
					tempPath.renameTo(ogPath);
					tempFile.renameTo(ogPath);
					
			}
			
	//gets an item description
	public int whichD()
	{
		//the baby tree
		if(selectedItem.contains("Baby"))
		{
			if(selectedItem.contains("Hat"))
			{
				return R.string.babyHD;
			}
			else if(selectedItem.contains("Shirt"))
			{
				return R.string.babyTD;
			}
			else if(selectedItem.contains("Pants"))
			{
				return R.string.babyLD;
			}
			else if(selectedItem.contains("Shoes"))
			{
				return R.string.babyFD;
			}
			else
			{
				return R.string.babyWD;
			}
		}
		//HP Equipment
		else if(selectedItem.contains("Plastic") || selectedItem.contains("Wooden"))
		{
			if(selectedItem.contains("Plastic"))
			{
				return R.string.HP1d;
			}
			else
			{
				return R.string.HP2d;
			}
		}
		//StrEquipment
		else if(selectedItem.contains("Durable") || selectedItem.contains("Strong"))
		{
			if(selectedItem.contains("Durable"))
			{
				return R.string.str1d;
			}
			else
			{
				return R.string.str2d;
			}
		}
		//intEquipment
		else if(selectedItem.contains("Book") || selectedItem.contains("Good"))
		{
			if(selectedItem.contains("Book"))
			{
				return R.string.int1d;
			}
			else
			{
				return R.string.int2d;
			}
		}
		//dexEquipment
		else if(selectedItem.contains("Red") || selectedItem.contains("Blue"))
		{
			if(selectedItem.contains("Red"))
			{
				return R.string.dex1d;
			}
			else
			{
				return R.string.dex2d;
			}
		}
		//items
		else
		{
			if(selectedItem.contentEquals("Water"))
			{
				return R.string.item1d;
			}
			else
			{
				return R.string.item2d;
			}
		}
		
	}
			
			//resets all of the buttons and things
			public void resetShop()
			{
				equip.setText(R.string.equip);
				quit.setText(R.string.quit);
				confirm = false;
			}
		

			//function to check if the selected item is an item
	public boolean isItem()
	{
		if(selectedItem.contains("Hat") || selectedItem.contains("Helm") || 
				selectedItem.contains("Bandanna") ||
				selectedItem.contains("Shirt") || selectedItem.contains("Chest") || 
				selectedItem.contains("Legs") || selectedItem.contains("Pants") || 
				selectedItem.contains("Shoes") || selectedItem.contains("Boots") )
		{
			return false;
		}
		else
		{
			if(selectedItem.contains("Durable") || selectedItem.contains("Strong") || 
					selectedItem.contains("Book") || selectedItem.contains("Wand"))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
	}
	
	//function to assign selected item to variable, then add that old variable ot equipString
	public void equipThing()
	{
		if(selectedItem.contains("Hat") || selectedItem.contains("Helm") || 
				selectedItem.contains("Bandanna") )
		{
			//it is a head
			equipString = equipString + "/" + head;
			head = selectedItem;
		}
		else if(selectedItem.contains("Shirt") || selectedItem.contains("Chest"))
		{
			//it is a  torso
			equipString = equipString + "/" + torso;
			torso = selectedItem;
		}
		else if(selectedItem.contains("Legs") || selectedItem.contains("Pants"))
		{
			//it is a legs
			equipString = equipString + "/" +  legs;
			legs = selectedItem;
		}
		else if(selectedItem.contains("Shoes") || selectedItem.contains("Boots"))
		{
			//it is a feet
			equipString = equipString + "/" +  feet;
			feet = selectedItem;
		}
		else
		{
			//it is a weapon
			equipString = equipString + "/" +  weapon;
			weapon = selectedItem;
		}
		
		//remove the selected item from the equipString
		equipString = equipString.replaceFirst("/" + selectedItem, "");
			
	}
			
	//equip button
	public void equip(View view)
	{
		if(itemSelected && confirm == false)
		{
			if(isItem())
			{
				//if it is an item
				shopText.animateText(getResources().getString(R.string.itemEquip));
				talk();
			}
			else if(selectedItem.contains(getResources().getString(R.string.equipped)))
			{
				//if it is already equipped
				shopText.animateText(getResources().getString(R.string.cantEquip));
				talk();
			}
			else
			{
				//you can equip it
				shopText.animateText(getResources().getString(R.string.shopConfirm));
				talk();
				
				//set up the buttons
				equip.setText(getResources().getString(R.string.yes));
				quit.setText(getResources().getString(R.string.yes));
				
				confirm = true;
				
			}
		}
		else if(itemSelected && confirm)
		{
			//this is when it equals yes
			//equip the thingy
			//this function adds it to variable and then removes old one from equipString
			equipThing();
			
			try {
				savePurchase();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//inform user of success, and then reset the shops
			shopText.animateText(getResources().getString(R.string.equipConfirm));
			talk();
			
			//play equip sound
			bgMusic.playSFX(2);
			
			resetShop();
			
		}
	}
	
	
	
	public void cancel(View view)
	{
		//incase we are currently confirming something, just call no/sell
		//since this is hidden, and back button is linked to here, this is best place to put it
		if(confirm)
		{
			resetShop();
		}
		else
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
			
			
			//finish the acitivity
		noPause = true;
		//dont pause song if going back to overworld!
		Intent edit = new Intent(getApplicationContext(), CharEdit.class);
		//getApplicationContext() to understand what class we are coming from
		//and which world we came from
		edit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		edit.putExtra("CLASS", whichClass);
		startActivity(edit);
		//just start dont finish
		}
	}
	
	
	//on pause and on resume to pause and play music
			@Override
			public void onPause() 
			{
			    super.onPause();
			    
			    shopText.setTextSpeed(3);
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
			    
			    shopText.setTextSpeed(2);
			    
			    if(firstOpened)
			    {
			    shopText.animateText(getResources().getString(sayings[ran.nextInt(sayings.length)]));
		   		
		   		//animate bg while text is animating
		   		talk();
			    }
				    
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
			
			//need to add this to avoid service connection leaks
				@Override
				public void onDestroy()
				{
					super.onDestroy();
					unbindService(musicConnection);
					Unbind.unbindDrawables((LinearLayout) findViewById(R.id.container));
					System.gc();
				}
}

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
import android.widget.TextView;
import android.widget.Toast;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.trustinheartdemo.R;

public class InShop extends Activity 
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
			
	//booleans for things
			boolean itemSelected;
			boolean confirm;
			boolean equipConfirm;
			boolean sellConfirm;
			boolean buySelected;
			boolean sellSelected;
			boolean specialSelected;
			boolean firstOpened;
			
			//backgounds
			int[] BGs = new int[]{R.drawable.iteminshop, R.drawable.hpinshop,
					R.drawable.strinshop, R.drawable.intinshop, R.drawable.dexinshop};
			
			//stats stuff
			Scanner sc;
			File tempPath;
			File ogPath;
			
			//which shop are we going to
			int whichShop;
			
			//guaps, score, wins losses etc...
			int guaps;
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
			int score;
			
			//views
			TextView scoreView;
			TextView guapView;
			GameTextView shopText;
			Button buy;
			Button sell;
			Button quit;
			Button special;
			
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
			
			//Item Shop Items
			int[] ISI = new int[]{R.string.item1, R.string.item2};
			
			int[] ISD = new int[]{R.string.item1d, R.string.item2d};
			
			//HP Shop Items
			int[] HPSI = new int[]{R.string.HP1, R.string.HP2};
			
			int[] HPSD = new int[]{R.string.HP1d, R.string.HP2d};
			
			
			//STR Shop Items
			int[] strSI = new int[]{R.string.str1, R.string.str2};
			
			int[] strSD = new int[]{R.string.str1d, R.string.str2d};
			
			//INT Shop Items
			int[] intSI = new int[]{R.string.int1, R.string.int2};
			
			int[] intSD = new int[]{R.string.int1d, R.string.int2d};
			
			//Dex Shop Items
			int[] dexSI = new int[]{R.string.dex1, R.string.dex2};
			
			int[] dexSD = new int[]{R.string.dex1d, R.string.dex2d};
			
			//requirements
			int[] req = new int[]{15, 25};
			
			//itemPrices
			int[] itemPrices = new int[]{50, 100};
			
			//equipPrices
			int[] equipPrices = new int[]{175, 275};
			
			//reasons you cannot buy stuff
			int[] reasons = new int[]{R.string.HPR, R.string.strR, R.string.intR, R.string.dexR, 
					R.string.scoreR, R.string.guapsR};
			
			//our listview stuff
			String[] array;
			List<String> objects;
			ListView menu;
			String selectedItem;
			int whichItem;
			String oldItem;
			int salePrice;
			
			//cash register sound
			//handled by service now
			
			//score boolean
			boolean scoreBool;
			
			//bought Item to avoid a possible bug
			String boughtItem;
			
			//for intelligence barter
			int barterPrice;
			int finalPrice;
			String barterString;
			
			
			@Override
			protected void onCreate(Bundle savedInstanceState) 
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_inshop);
				
				//dont need to set up fonts but need to set up music
				playMusic();
				noPause = false;
				
				//aquire wakelock
		   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		   		
		   		//getting started on setting up the actual shop
		   		//get intents
		   		Intent intent = getIntent();
		   		whichShop = intent.getIntExtra("SHOP", 1);
		   		
		   		//get the shop background
		   		shopBG = (RelativeLayout) findViewById(R.id.shopScreen);
		   		shopBG.setBackgroundResource(BGs[whichShop - 1]);
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
		   		
		   		//get the score and money
		   		scoreBool = false;
		   		getScore();
		   		
		   		
		   		//setup shop keeper saying 
		   		ran = new Random();
		   		
		   		//getApplicationContext() is done in on resume using first open boolean
		   		firstOpened = true;
		   		//shopText.animateText(getResources().getString(sayings[ran.nextInt(sayings.length)]));
		   		
		   		//animate bg while text is animating
		   		//animateBG(); //the whole move mouth while talking thing doesnt work...
		   		
		   		//slower Text for shops change it in on puase and on resume
		   		shopText.setTextSpeed(2);
		   		
		   		//initialize our boolean
		   		buySelected = false;
		   		sellSelected = false;
		   		specialSelected = false;
		   		itemSelected = false;
		   		
		   		//need getApplicationContext() for sell
		   		//String[] array = itemString.split("/");
		   		//now to set up listview
		   		//since we start on the buy screen
		   		buySelected = true;
		   		objects = new ArrayList<String>();
		   		getListview();
		   		
				
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
			Intent playIntent = new Intent(getApplicationContext(), BGMusic.class);
		    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		    //set up buy sound effect
		    //handled by service now
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
			
			SharedPreferences prefs = getApplicationContext().getSharedPreferences("TrustInHeartPrefs", 0);
			
			//paths
			tempPath = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
					prefs.getString("SAVEFILE", "character") + ".txt");
			ogPath = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
					prefs.getString("SAVEFILE", "character") + ".sav");
				
			//rename the file
			ogPath.renameTo(tempPath);
			
		//set up scanner to path
			sc = new Scanner(tempPath);
			//string
			String temp;
			//skipping to forth line
			for(int i = 0; i < 3; i++)
			{
				temp = sc.nextLine();
			}
			//getting guaps
			temp = sc.nextLine();
			guaps = Integer.valueOf(temp);
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
				temp = sc.nextLine();
				score = Integer.valueOf(temp);
			
			//finished getting stats
			sc.close();
			//convert back to character.bro
			tempPath.renameTo(ogPath);
			
		}
		
		public void getScore()
		{
			//function to get score and guap everytime you buy or sell something
			if(score == 0 || scoreBool)
			{
				scoreBool = true;
				//to avoid dividing by zero
				if(wins == 0 && losses == 0)
				{
					wins++;
					losses++;
				}
				//get score wins - losses/ total battles. multiplied by 100 + your money and level
				score = ((guaps * 7) + (level * 10) + ((wins + losses) * 2) - (losses * 5) + (wins * 10)); 
				//set our texts
				scoreView.setText("Current Score: " + Integer.toString(score));
			}
			else
			{
				scoreView.setText("Final Score: " + Integer.toString(score));
			}
			//set guaps
			guapView.setText("Current Guaps: " + Integer.toString(guaps));
		}
		
		
		public void setFonts()
		{
			//get the views
			scoreView = (TextView) findViewById(R.id.scoreView); 
	   		guapView = (TextView) findViewById(R.id.guapView); 
	   		shopText = (GameTextView) findViewById(R.id.shopText);
	   		buy = (Button) findViewById(R.id.buy);
	   		sell = (Button) findViewById(R.id.sell);
	   		quit = (Button) findViewById(R.id.quit);
	   		special = (Button) findViewById(R.id.special);
	   		menu = (ListView) findViewById(R.id.ListView1);
	   		
	   		//get font
	   		Typeface tf = FontCache.get(getApplicationContext(), "font");
			
			//set the fonts
			scoreView.setTypeface(tf);
			guapView.setTypeface(tf);
			shopText.setTypeface(tf);
			buy.setTypeface(tf);
			sell.setTypeface(tf);
			quit.setTypeface(tf);
			special.setTypeface(tf);
		}
		
		//gets the array we need to create a listview
		public void getArray()
		{
			array = null;
			objects.clear();
			//need to put in order of which is more likely
			if(buySelected)
			{
				//figure out which shop we in then set those itesm to the array
			if(whichShop == 1)
			{
				array = new String[ISI.length];
				for(int i = 0; i < ISI.length; i++)
				{
					array[i] = getResources().getString(ISI[i]);
				}
			}
			else if(whichShop == 2)
			{
				array = new String[HPSI.length];
				for(int i = 0; i < HPSI.length; i++)
				{
					array[i] = getResources().getString(HPSI[i]);
				}
			}
			else if(whichShop == 3)
			{
				array = new String[strSI.length];
				for(int i = 0; i < strSI.length; i++)
				{
					array[i] = getResources().getString(strSI[i]);
				}
			}
			else if(whichShop == 4)
			{
				array = new String[intSI.length];
				for(int i = 0; i < intSI.length; i++)
				{
					array[i] = getResources().getString(intSI[i]);
				}
			}
			else
			{
				array = new String[dexSI.length];
				for(int i = 0; i < dexSI.length; i++)
				{
					array[i] = getResources().getString(dexSI[i]);
				}
			}
			
			//add array stuff to object
			for(int i = 0; i < array.length; i++)
			{
				objects.add(array[i]);
			}
			
			}
			else if(sellSelected)
			{
				//we need to check if our strings have items in them
				//first add your items to arraylist
				if(itemString.length() > 2)
				{
				String[] temp = itemString.split("/");
				for(int i = 0; i < temp.length; i++)
				{
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
			//special selected
			else
			{
				
			}
			
		}
		
		public void barter(int position, String input)
		{
			if(whichShop == 1)
			{
				barterPrice = itemPrices[position] - (10 * ((int)Int/10));
			}
			else
			{
				barterPrice = equipPrices[position] - (10 * ((int)Int/10));
			}
			
			barterString = input.replaceFirst("Cost: Guaps", "Cost: " + Integer.toString(barterPrice)
					+ " Guaps");
		}
		
		public void getListview()
		{
			//set up the items array
	   		getArray();
	   		menu.setAdapter(new CustomListview(getApplicationContext(), objects, false));
	   		
	   		//set up the listview
	   	// listener for when someone clicks a file
			OnItemClickListener listclick = new OnItemClickListener() 
			{
			    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			    {
			    	whichItem = position;
			    	selectedItem = objects.get(position);
			    	
			    	//put stuff here
			    	itemSelected = true;
			    	if(buySelected)
			    	{
			    		//first barter for our item and get out String
			    		barter(position, getResources().getString(whichShopD()[position]));
			    		shopText.animateText(barterString);
			    	}
			    	else if(sellSelected)
			    	{
			    		oldItem = selectedItem;
			    		//get sale price will return the sale price of any item
						salePrice = getPrice();
						shopText.animateText(getResources().getString(R.string.shopSell) + " " + oldItem + " " +
								getResources().getString(R.string.shopFor) + " " + Integer.toString(salePrice) + " guaps?");
			    	}
			    	talk();
			    }
			};
				//set the on click listener
				menu.setOnItemClickListener(listclick); 
		}
		//returns which shop description array we need
		public int[] whichShopD()
		{
			
			if(whichShop == 1)
			{
				return ISD;
			}
			else if(whichShop == 2)
			{
				return HPSD;
			}
			else if(whichShop == 3)
			{
				return strSD;
			}
			else if(whichShop == 4)
			{
				return intSD;
			}
			else
			{
				return dexSD;
			}
		}
	
	//method to return if you have required stats to buy something
	public boolean canBuy()
	{
		
		if(whichShop == 1)
		{
			return true;
		}
		else if(whichShop == 2)
		{
			if(HP >= req[whichItem])
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(whichShop == 3)
		{
			if(str >= req[whichItem])
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(whichShop == 4)
		{
			if(Int >= req[whichItem])
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(dex >= req[whichItem])
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
	}
	
	//method to return if you have the required money to buy something
	public boolean gotGuaps()
	{
		if(whichShop == 1)
		{
			if(guaps >= barterPrice)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(guaps >= barterPrice)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
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
			
			//skip to 4th line
			for(int i = 0; i < 3; i++)
			{
				String temp = sc.nextLine();
				fileStream.println(temp);
			}
			//save money
			sc.nextLine();
			fileStream.println(Integer.toString(guaps));
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
	
	//resets all of the buttons and things
	public void resetShop()
	{
		buy.setVisibility(View.VISIBLE);
		sell.setVisibility(View.VISIBLE);
		special.setVisibility(View.VISIBLE);
		quit.setVisibility(View.VISIBLE);
		buy.setText(R.string.buy);
		sell.setText(R.string.sell);
		special.setText(R.string.special);
		quit.setText(R.string.quit);
		confirm = false;
		itemSelected = false;
		equipConfirm = false;
		sellConfirm = false;
	}
	
	public int getPrice()
	{
		int tempPrice = 0;
		if(oldItem.contentEquals("Water"))
		{
			tempPrice = 5;
		}
		else if(oldItem.contentEquals("Tea"))
		{
			tempPrice = 5;
		}
		else if(oldItem.contains("Baby"))
		{
			tempPrice = 20;
		}
		else if(oldItem.contains("Plastic") || oldItem.contains("Durable") || oldItem.contains("Book") ||
				oldItem.contains("Red"))
		{
			tempPrice = 30;
		}
		else if(oldItem.contains("Wooden") || oldItem.contains("Strong") || oldItem.contains("Good") ||
				oldItem.contains("Blue"))
		{
			tempPrice = 50;
		}
		else
		{
			//incase of errors
			tempPrice = 0;
		}
		
		if(tempPrice == 0)
		{
			//in case of errors do nothing
			return 0;
		}
		else
		{
			tempPrice = tempPrice + (5 * ((int)Int/10));
			return tempPrice;
		}
	}
		
	public void buy(View view)
	{
		if(itemSelected && buySelected && confirm == false && equipConfirm == false && sellConfirm == false)
		{
			if(canBuy() && gotGuaps())
			{
			//confirm they want to make purchase
			shopText.animateText(getResources().getString(R.string.shopConfirm) + " "  + selectedItem + "?");
			talk();
			buy.setText(R.string.yes);
			sell.setText(R.string.no);
			//now hide other buttons
			special.setVisibility(View.GONE);
			quit.setVisibility(View.GONE);
			confirm = true;
			
			//hopefully to avoid price mishaps, set barterPrice to a final one here
			finalPrice = barterPrice;
			}
			else
			{
				//then cant buy
				if(gotGuaps())
				{
				shopText.animateText(getResources().getString(reasons[whichShop - 2]));
				talk();
				}
				else
				{
					shopText.animateText(getResources().getString(reasons[reasons.length - 1]));
					talk();
				}
			}
		}
		else if(confirm && buySelected)
		{
			//they said yes, I want to buy getApplicationContext() item
			if(whichShop == 1)
			{
				//everything ends here if item
				guaps = guaps - finalPrice;
				itemString = itemString + "/" + selectedItem;
				boughtItem = selectedItem;
				
				//save
				try {
					savePurchase();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				shopText.animateText(getResources().getString(R.string.shopBuy));
				talk();
				getScore();
				resetShop();
			}
			else
			{
				guaps = guaps - finalPrice;
				equipString = equipString + "/" + selectedItem;
				boughtItem = selectedItem;
				shopText.animateText(getResources().getString(R.string.shopEquip));
				talk();
				equipConfirm = true;
				getScore();
			}
			
			//save
			try {
				savePurchase();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//play money sound
			bgMusic.playSFX(5);
			
			talk();
			confirm = false;
		}
		else if(confirm && sellSelected)
		{
			//play money sound
			bgMusic.playSFX(5);
			//they said yes, I want to sell getApplicationContext() item
			//remove the old item, but check it it is an item
			if(selectedItem.contentEquals("Water") || selectedItem.contentEquals("Tea"))
			{
				itemString = itemString.replaceFirst("/" + selectedItem, "");
			}
			else
			{
			  equipString = equipString.replaceFirst("/" + selectedItem, "");
			}
			
			//add the guaps
			guaps = guaps + salePrice;
			
			//save
			try {
				savePurchase();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//thank them for shopping with them
			shopText.animateText(getResources().getString(R.string.shopBuy));
			talk();
			resetShop();
			getScore();
			//only in sell do we need to re get out listview
			getListview();
		}
		else if(equipConfirm)
		{
			if(selectedItem.contains("Hat") || selectedItem.contains("Helm")|| selectedItem.contains("Bandanna"))
			{
				oldItem = head;
				equipString = equipString + "/" + oldItem;
				head = boughtItem;
			}
			else if(selectedItem.contains("Chest") || selectedItem.contains("Shirt"))
			{
				oldItem = torso;
				equipString = equipString + "/" + oldItem;
				torso = boughtItem;
			}
			else if(selectedItem.contains("Legs") || selectedItem.contains("Pants"))
			{
				oldItem = legs;
				equipString = equipString + "/" + oldItem;
				legs = boughtItem;
			}
			else if(selectedItem.contains("Shoes") || selectedItem.contains("Boots"))
			{
				oldItem = feet;
				equipString = equipString + "/" + oldItem;
				feet = boughtItem;
			}
			else
			{
				oldItem = weapon;
				equipString = equipString + "/" + oldItem;
				weapon = selectedItem;
			}
			
			//forgot to remove the bought item from equipstring here, that's what was causing
			//bug
			equipString = equipString.replaceFirst("/" + boughtItem, "");
			
			//play equip sound
			bgMusic.playSFX(2);
			
			//save
			try {
				savePurchase();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			equipConfirm = false;
			sellConfirm = true;
			//get sale price will return the sale price of any item
			salePrice = getPrice();
			shopText.animateText(getResources().getString(R.string.shopSell) + " " + oldItem + " " +
					getResources().getString(R.string.shopFor) + " " + Integer.toString(salePrice) + " guaps?");
			talk();
		}
		else if(sellConfirm)
		{
			//play money sound
			bgMusic.playSFX(5);
			//remove the old item from equip String
			equipString = equipString.replaceFirst("/" + oldItem, "");
			
			//add the guapsn\
			guaps = guaps + salePrice;
			
			//save
			try {
				savePurchase();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//need to get their new score after selling
			getScore();
			//thank them for shopping with them
			shopText.animateText(getResources().getString(R.string.shopBuy));
			talk();
			resetShop();
			
		}
		//incase we're in another menu
		else
		{
			//reset the listview and go back to buy menu
			//switch from the buy menu to the sell menu
			sellSelected = false;
			buySelected = true;
			getListview();
		}
	}
	
	public void sell(View view)
	{
		//these are just a bunch of if statements for our buttons depending on the booleans
		if(confirm || equipConfirm || sellConfirm)
		{
			//they say no, on second though i do not want to buy getApplicationContext() thing\//thank them for shopping with them
			//getApplicationContext() also works for no they would not like to sell or equip
			shopText.animateText(getResources().getString(R.string.shopNo));
			talk();
			resetShop();
			confirm = false;
			equipConfirm = false;
			sellConfirm = false;
		}
		else if(buySelected)
		{
			//switch from the buy menu to the sell menu
			sellSelected = true;
			buySelected = false;
			getListview();
		}
		else if(itemSelected && sellSelected)
		{
			//if it isn't equipped
			if(selectedItem.contains(getResources().getString(R.string.equipped)) == false)
			{
			//confirm they would like to sell the item
			//confirm they want to make purchase
			shopText.animateText(getResources().getString(R.string.shopSure));
			talk();
			buy.setText(R.string.yes);
			sell.setText(R.string.no);
			//now hide other buttons
			special.setVisibility(View.GONE);
			quit.setVisibility(View.GONE);
			confirm = true;
			}
			else
			{
				shopText.animateText(getResources().getString(R.string.shopCantSell));
				talk();
			}
			
		}
	}
	
	public void special(View view)
	{
		Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_LONG).show();
	}
	
	
	public void cancel(View view)
	{
		//incase we are currently confirming something, just call no/sell
		//since getApplicationContext() is hidden, and back button is linked to here, getApplicationContext() is best place to put it
		if(confirm || equipConfirm || sellConfirm)
		{
			sell(null);
		}
		else
		{
			//finish the acitivity
		noPause = true;
		//dont pause song if going back to overworld!
		//bgMusic.pauseSong();
		//dont recreate just finish
		finish();
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

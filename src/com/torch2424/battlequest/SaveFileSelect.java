package com.torch2424.battlequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.broquest.StartScreen;
import com.torch2424.trustinheartdemo.R;

public class SaveFileSelect extends Activity 
{
	
	//music
	BGMusic bgMusic;
	Intent playIntent;
	boolean musicBound;
	boolean musicPaused;
	boolean noPause;
	
	//our listview stuff
	List<String> objects;
	ListView menu;
	//array for files in a directory
	String [] directory;
	
	//our views
	Button newChar;
	Button importChar;
	Button startChar;
	Button editChar;
	Button deleteChar;
	TextView confirmView;
	EditText editView;
	TextView fileType;
	GameTextView filePath;
	TextView title;
	
	//int for our position
	int itemNumber;
	
	//booleans for editing and deleting
	boolean editing;
	boolean deleting;
	
	//boolean for importing mode
	boolean importing;
	boolean exit;
	File currentDir;
	CustomListview adapter;
	String selectedFile;
	
	//our toast
	Toast toasty;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savefileselect);
		
		//dont need to set up fonts but need to set up music
		playMusic();
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
   		
   	//set our fonts
   		setFont();
   		
   		//get our listview
   		menu = (ListView) findViewById(R.id.ListView1);
   		
   		//need to get out lisview stuff here
   		objects = new ArrayList<String>();
   		getListview();
   		
   		//set up out booleans
		editing = false;
		deleting = false;
		importing = false;
		exit = true;
		
		//set up toast
   		toasty = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
   		
		
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
			    	//play the character song done for it
			    	//bgMusic.playSong(R.raw.character);
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
			playIntent = new Intent(this, BGMusic.class);
		    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		}
		
		
		//gets the array we need to create a listview
		public void getArray()
		{
			//get all the files in the directory from our app directory and then
			//add them to our arraylist
			directory = this.getFilesDir().list();
			
			for(int i = 0; i < directory.length; i++)
			{
				//specifically for save file select array list
				//getting rid of file extension and begging part in file name
				
				//only want .sav files
				if(directory[i].contains(".sav"))
				{
				String tempString = directory[i];
				tempString = tempString.replace("Trust In Heart-", "");
				tempString = tempString.replace(".sav", "");
				objects.add(tempString);
				}
			}
				
				//sort our items and equipment
				Collections.sort(objects);
		}
		
		public void getListview()
		{
			//set up the items array
			objects.clear();
	   		getArray();
	   		//setting adapter to null to help GC
	   		adapter = null;
	   		adapter = new CustomListview(this, objects, true);
	   		menu.setAdapter(adapter);
	   		
	   		//set up the listview
	   	// listener for when someone clicks a file
			OnItemClickListener listclick = new OnItemClickListener() 
			{
				@Override
			    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			    {
					adapter.setSelectedIndex(position);
			    	//set up our number
					itemNumber = position;
					
					//reset our booleans
					editing = false;
					deleting = false;
					
					//and show the right buttons/reset
					reset();
			    }
			};
				//set the on click listener
				menu.setOnItemClickListener(listclick); 
		}
	
	
		public void setFont()
		{
			//get our thingys
			newChar = (Button) findViewById(R.id.newChar);
			importChar = (Button) findViewById(R.id.importChar);
			startChar = (Button) findViewById(R.id.startChar);
			editChar = (Button) findViewById(R.id.editChar);
			deleteChar = (Button) findViewById(R.id.deleteChar);
			confirmView = (TextView) findViewById(R.id.confirmView);
			editView = (EditText) findViewById(R.id.editName);
			fileType = (TextView) findViewById(R.id.fileType);
			filePath = (GameTextView) findViewById(R.id.directoryView);
			
			title = (TextView) findViewById(R.id.title);
			
			//get font
			Typeface tf = FontCache.get(getApplicationContext(), "font");
			
			newChar.setTypeface(tf);
			importChar.setTypeface(tf);
			startChar.setTypeface(tf);
			editChar.setTypeface(tf);
			deleteChar.setTypeface(tf);
			confirmView.setTypeface(tf);
			editView.setTypeface(tf);
			title.setTypeface(tf);
			fileType.setTypeface(tf);
			filePath.setTypeface(tf);
			
			//now hide some of our views and stuff
			confirmView.setVisibility(View.GONE);
			editView.setVisibility(View.GONE);
			startChar.setVisibility(View.GONE);
			editChar.setVisibility(View.GONE);
			deleteChar.setVisibility(View.GONE);
			fileType.setVisibility(View.GONE);
			filePath.setVisibility(View.GONE);
		}
		
		public void reset()
		{
			//function to reset the things after pressing buttton
			//show the right buttons
			newChar.setVisibility(View.GONE);
			importChar.setVisibility(View.GONE);
			confirmView.setVisibility(View.GONE);
			editView.setVisibility(View.GONE);
			startChar.setVisibility(View.VISIBLE);
			editChar.setVisibility(View.VISIBLE);
			deleteChar.setVisibility(View.VISIBLE);
			fileType.setVisibility(View.GONE);
			filePath.setVisibility(View.GONE);
			
			//make sure they have the right text
			//now set the text of save button and edit button
			startChar.setText(getResources().getString(R.string.start));
			editChar.setText(getResources().getString(R.string.editChar));
			deleteChar.setText(getResources().getString(R.string.deleteChar));
			title.setText(getResources().getString(R.string.selectChar));
			importChar.setText(getResources().getString(R.string.importChar));
			
			editing = false;
			deleting = false;
		}
		
		public void newChar(View view)
		{
			//open a new character creation screen
			//dont pause twice
			noPause = true;
			Intent edit = new Intent(this, WelcomeRRPPGG.class);
			edit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(edit);
			//finish(); dont finish, incase the user presses back
		}
		
		public void importChar(View view)
		{
			if(importing)
			{
				//if on the base directory
				if(exit)
				{
				importing = false;
				//reset everything
				importViews();
				}
				else
				{
					remDir();  
   	   		    	menu.setAdapter(adapter);
   	   		    	menu.invalidateViews();
				}
			}
			else if(Environment.getExternalStorageDirectory().exists() == false)
			{
				//toasty.cancel();
				toasty.setText("No External Storage Found!");
				toasty.show();
			}
			else
			{
			//use our current listview to browse through the thingy, and use buttons to import files
			importing = true;
			exit = false;
			
			//set import views
			importViews();
			
			//set up the items array
			objects.clear();
	   		getImportArray();
	   		//setting adapter to null to help GC
	   		adapter = null;
	   		adapter = new CustomListview(this, objects, true);
	   		menu.setAdapter(adapter);
	   		
	   		//gotta animate the text to the current directory
	   		filePath.animateText(currentDir.getAbsolutePath());
	   		
	   	//set up the listview
		   	// listener for when someone clicks a file
				OnItemClickListener listclick = new OnItemClickListener() 
				{
					@Override
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {
						//set our directories and exit boolean
						selectedFile = objects.get(position);
	       		    	currentDir = new File(currentDir.getAbsolutePath() + "/" + selectedFile);
	       		    	
	       		    	//check if we can exit
	       		   //now do an exit variable check
						if(currentDir.getParentFile() == null)
						{
							exit = true;
						}
	       		    	
	       		    	if(selectedFile.contains("Trust In Heart-") && selectedFile.contains(".sav"))
	       		    	{
	       		    		// import to app file directory and reset everything
	       		    		if(new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" +
	       		    		selectedFile).exists())
	       		    		{
	       		    		//no overwrite
		       		    		//toasty.cancel();
		       					toasty.setText("There is already a character with this name!");
		       					toasty.show();
	       		    		}
	       		    		else
	       		    		{
	       		    			//no overwrite add this file to our character files
	       		    			File importFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" +
	       		    		selectedFile);
	       		    			//we already have the current directory with the file
	       		    			//now call function to copy files
	       		    			
	       		    			try {
									copyTxtFile(currentDir, importFile);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	       		    			
	       		    			//now reset everything
	       		    		//function to reset the things after pressing buttton
	       		 			//show the right buttons
	       		 			newChar.setVisibility(View.VISIBLE);
	       		 			importChar.setVisibility(View.VISIBLE);
	       		 			fileType.setVisibility(View.GONE);
	       		 			filePath.setVisibility(View.GONE);
	       		 			
	       		 			//make sure they have the right text
	       		 			//now set the text of save button and edit button
	       		 			startChar.setText(getResources().getString(R.string.start));
	       		 			editChar.setText(getResources().getString(R.string.editChar));
	       		 			deleteChar.setText(getResources().getString(R.string.deleteChar));
	       		 			title.setText(getResources().getString(R.string.selectChar));
	       		 			importChar.setText(getResources().getString(R.string.importChar));
	       		 			
	       		 			editing = false;
	       		 			deleting = false;
	       		 			importing = false;
	       		 			
	       		 			//now get our listview
	       		 			getListview();
	       		    			
	       		    		}
	       		    	}
	       		    	else if (currentDir.isDirectory())
	       		    	{
	       	   		    	newDir();  
	       	   		    	menu.setAdapter(adapter);
	       	   		    	menu.invalidateViews();
	       		    	}
	       		    	else
	       		    	{
	       		    		//not the right file
	       		    		//toasty.cancel();
	       					toasty.setText("This is not a Trust In Heart save file!");
	       					toasty.show();
	       		    	}
				    }
				};
					//set the on click listener
					menu.setOnItemClickListener(listclick); 
					
			}
	   		
			
		}
		
		//gets the array we need to create a listview
				public void getImportArray()
				{
					//path to sd folder
		        	String dPath = (String) Environment.getExternalStorageDirectory().getAbsolutePath();
		       		File startDir = new File(dPath);
		       		//setting current dir to our start path
		       		currentDir = new File(startDir.getAbsolutePath());
		       		//putting the list of all the files into an array
		       		directory = startDir.list();
		       		//sorting directory, do this every time it is set
		       		Arrays.sort(directory);
					
					for(int i = 0; i < directory.length; i++)
					{
						String tempString = directory[i];
						objects.add(tempString);
					}
						
						//sort our items and equipment
						Collections.sort(objects);
				}
				
				public void importViews()
				{
					if(importing)
					{
					//set our views
					newChar.setVisibility(View.GONE);
					importChar.setVisibility(View.VISIBLE);
					confirmView.setVisibility(View.GONE);
					editView.setVisibility(View.GONE);
					startChar.setVisibility(View.GONE);
					editChar.setVisibility(View.GONE);
					deleteChar.setVisibility(View.GONE);
					fileType.setVisibility(View.VISIBLE);
					filePath.setVisibility(View.VISIBLE);
					
					//set title text
					title.setText(getResources().getString(R.string.importCharTitle));
					//set button text
					importChar.setText(getResources().getString(R.string.back));
					}
					else
					{
						//set title text
						title.setText(getResources().getString(R.string.selectChar));
						//set button text
						importChar.setText(getResources().getString(R.string.importChar));
						
						//set views
						newChar.setVisibility(View.VISIBLE);
						importChar.setVisibility(View.VISIBLE);
						confirmView.setVisibility(View.GONE);
						editView.setVisibility(View.GONE);
						startChar.setVisibility(View.GONE);
						editChar.setVisibility(View.GONE);
						deleteChar.setVisibility(View.GONE);
						fileType.setVisibility(View.GONE);
						filePath.setVisibility(View.GONE);
						
						getListview();
					}
				}
				//going forward in file search
				public void newDir()
				{
					filePath.animateText(currentDir.getAbsolutePath());
					directory = currentDir.list();
					
					objects.clear();
					//was giving a weird null poiinter exception, check ot make sure it is not null first
					if(directory != null)
					{
					for(int i = 0; i < directory.length; i++)
					{
						String tempString = directory[i];
						objects.add(tempString);
					}
					}
						
						//sort our items and equipment
						Collections.sort(objects);
					adapter = new CustomListview(this, objects, true);
				}
				
				//going back in file search
				public void remDir()
				{
					currentDir = currentDir.getParentFile();
					filePath.animateText(currentDir.getAbsolutePath());
					directory = currentDir.list();
					
					//set up our arraylist
					objects.clear();
					for(int i = 0; i < directory.length; i++)
					{
						String tempString = directory[i];
						objects.add(tempString);
					}
						
						//sort our items and equipment
						Collections.sort(objects);
					Arrays.sort(directory);
					adapter = new CustomListview(this, objects, true);
					
					//exit check 
					if(currentDir.getParentFile() == null)
					{
						exit = true;
					}
				}
				
		public void copyTxtFile(File in, File out) throws FileNotFoundException
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
			
			PrintStream fileStream = new PrintStream(out);
			Scanner sc = new Scanner(in);
			
			while(sc.hasNextLine())
			{
				String temp = sc.nextLine();
				fileStream.println(temp);
			}
			
			//finish up
			sc.close();
			fileStream.close();
		}
	
		
		public void startChar(View view)
		{
			if(editing)
			{
				//yes they want to save the new name
				//so get the input in the edittext, and rename the old file to the new file
				String newName = editView.getText().toString();
				//need to check for overwrite, we aint playing that overwrite stuff in di game
				//nu uh, we aint doing that I dont know what you think this is, I'm a grown man now
				//I got stuff to do, I ain't got no time to be doing all dat, be happy I gave you
				//the ability to even edit and store files, I wish a nigga would...
				if(new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" + newName + ".sav").exists())
				{
					//no overwrite
   		    		//toasty.cancel();
   					toasty.setText("A character with that name already exists!");
   					toasty.show();
				}
				else
				{
				File tempFile = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" + objects.get(itemNumber)
						+ ".sav");
				File newFile = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" + newName
						+ ".sav");
				tempFile.renameTo(newFile);
				
				//reset everything
				reset();
				getListview();
				}
				
			}
			else if(deleting)
			{
				//yes they want to delete this file
				File oldFile = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" + objects.get(itemNumber)
						+ ".sav");
				oldFile.delete();
				
				//reset everything
				reset();
				getListview();
				
				//however, after a delete, set new import back to main screen, 
				//just in case you delete everything you are not stuck
				newChar.setVisibility(View.VISIBLE);
				importChar.setVisibility(View.VISIBLE);
				startChar.setVisibility(View.GONE);
				editChar.setVisibility(View.GONE);
				deleteChar.setVisibility(View.GONE);
				
				
			}
			else
			{
				//starting the game
			//set the save file in the preferences and then
	    	//call start screen
	    	//dont want file extensions since we need to convert from thing to thing
	    	SharedPreferences prefs = this.getSharedPreferences("TrustInHeartPrefs", 0);
			Editor editor = prefs.edit();
			editor.putString("SAVEFILE", objects.get(itemNumber));
			editor.commit();
			
			//now start the startscreen
			
			bgMusic.stopSong();
			noPause = true;
		   //need to change to back button that is on screen later
			Intent fight = new Intent(this, StartScreen.class);
			//add this flag to remove all previous activities
			fight.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(fight);
			//finishing is a bad call, only call when you are never coming back in activity life time
			finish();
			}
		}
		
		public void editChar(View view)
		{
			if(editing)
			{
				//no they don't want to edit
				reset();
			}
			else
			{
			//start editing file name
			//unhide our views
			confirmView.setVisibility(View.VISIBLE);
			confirmView.setText(getResources().getString(R.string.editFile));
			editView.setVisibility(View.VISIBLE);
			
			//hide the delete button
			deleteChar.setVisibility(View.GONE);
			
			//now set the text of save button and edit button
			startChar.setText(getResources().getString(R.string.save));
			editChar.setText(getResources().getString(R.string.back));
			
			//set editing to true
			editing = true;
			}
			
		}
		
		public void deleteChar(View view)
		{
			if(deleting)
			{
				//no we do not want to delete
				reset();
			}
			else
			{
			//start editing file name
			//unhide our views
			confirmView.setVisibility(View.VISIBLE);
			confirmView.setText(getResources().getString(R.string.deleteConfirm));
			
			//hide the edit button
			editChar.setVisibility(View.GONE);
			
			//now set the text of save button and edit button
			startChar.setText(getResources().getString(R.string.yes));
			deleteChar.setText(getResources().getString(R.string.no));
			
			//set editing to true
			deleting = true;
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
				//check if they are doing something backbutton might be useful for
				if(editing)
				{
					editChar(null);
				}
				else if(deleting)
				{
					deleteChar(null);				
				}
				else if(importing)
				{
					//if on the base directory
					if(exit)
					{
					importing = false;
					//reset everything
					importViews();
					}
					else
					{
						remDir();  
	   	   		    	menu.setAdapter(adapter);
	   	   		    	menu.invalidateViews();
					}
				}
				else
				{
				//close the app completely
					bgMusic.stopSong();
					//this will open a new start screen just to finish it and everything above it
					Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("EXIT", true);
					startActivity(intent);
					finish();
				}
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

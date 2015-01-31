package com.torch2424.broquest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.torch2424.battlequest.BGMusic;
import com.torch2424.battlequest.BGMusic.MusicBinder;
import com.torch2424.battlequest.FontCache;
import com.torch2424.battlequest.SaveFileSelect;
import com.torch2424.battlequest.Unbind;
import com.torch2424.trustinheartdemo.R;

public class Creator extends Activity 
{
	EditText nameText;
	RadioButton dude;
	RadioButton lady;
	RadioButton dark;
	RadioButton tan;
	RadioButton light;
	RadioButton tank;
	RadioButton warrior;
	RadioButton mage;
	RadioButton rouge;
	ImageView player;
	Button saveButton;
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
	
	//ints to add together to get desired model
	int gender;
	int playerClass;
	int skin;
	
	//to get player animation working
    AnimationDrawable monsteranim;
    
    //to close app
    Intent playIntent;
    
    //our toast
    Toast toasty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creator);
		
		 nameText = (EditText) findViewById(R.id.nameText);
		 dude = (RadioButton) findViewById(R.id.dude);
		 lady = (RadioButton) findViewById(R.id.lady);
		 dark = (RadioButton) findViewById(R.id.dark);
		 tan = (RadioButton) findViewById(R.id.tan);
		 light = (RadioButton) findViewById(R.id.light);
		 tank = (RadioButton) findViewById(R.id.tank);
		 warrior = (RadioButton) findViewById(R.id.warrior);
		 mage = (RadioButton) findViewById(R.id.mage);
		 rouge = (RadioButton) findViewById(R.id.rouge);
		 saveButton = (Button) findViewById(R.id.saveButton);
		 player = (ImageView) findViewById(R.id.player);
		 
		 //initializing model ints
		 gender = 0;
		 playerClass = 0;
		 skin = 0;
		 
		 //setting up music
		 playMusic();
		 noPause = false;
		setFont();
		
		//aquire wakelock
   		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
   		
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
			    	//already playing
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
			playIntent = new Intent(this, BGMusic.class);
		    bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		}
	
	public void setFont()
	{
		//get font
		Typeface tf = FontCache.get(getApplicationContext(), "font");
		
		//get text views
		TextView nameQuestion = (TextView) findViewById(R.id.nameQuestion);
		TextView genderQuestion = (TextView) findViewById(R.id.genderQuestion);
		TextView skinQuestion = (TextView) findViewById(R.id.skinQuestion);
		TextView statQuestion = (TextView) findViewById(R.id.statQuestion);
		TextView finish = (TextView) findViewById(R.id.finish);
		
		//set the text
		
		nameQuestion.setTypeface(tf);
		genderQuestion.setTypeface(tf);
		skinQuestion.setTypeface(tf);
		statQuestion.setTypeface(tf);
		finish.setTypeface(tf);
		nameText.setTypeface(tf);
		dude.setTypeface(tf);
		lady.setTypeface(tf);
		dark.setTypeface(tf);
		tan.setTypeface(tf);
		light.setTypeface(tf);
		tank.setTypeface(tf);
		warrior.setTypeface(tf);
		rouge.setTypeface(tf);
		mage.setTypeface(tf);
		saveButton.setTypeface(tf);
	}
	
	//when save button is clicked, write things into methods
	public void save (View view) throws IOException
	{
		//checking to see if they filled out the whole form, and putting entered text into string
		String name = nameText.getText().toString();
		//maybe add return or enter check here
		if (name.contentEquals(""))
		{
			//toasty.cancel();
			toasty.setText("You have to finish everything!");
			toasty.show();
		}
		else if(dude.isChecked() == false && lady.isChecked() == false)
		{
			//toasty.cancel();
			toasty.setText("You have to finish everything!");
			toasty.show();
		}
		else if (tank.isChecked() == false && warrior.isChecked() == false && mage.isChecked() == false
				&& rouge.isChecked() == false)
		{
			//toasty.cancel();
			toasty.setText("You have to finish everything!");
			toasty.show();
		}
		else
		{
			//getting internal sd
		File savePath = this.getFilesDir();
		
		//set up our preferences
		//set up our preferences
    	SharedPreferences prefs = this.getSharedPreferences("TrustInHeartPrefs", 0);
		Editor editor = prefs.edit();
		editor.putString("SAVEFILE", name);
		editor.commit();
		//start wrting text into there, print stream superior to file writer, adds new line after each entered text
		//write name
		PrintStream fileStream = new PrintStream(new File(savePath.getAbsolutePath() + "/Trust In Heart-" + name + ".txt"));
		fileStream.println(name);
		
		//writing gender
		if(dude.isChecked())
		{
			fileStream.println("Boy");
		}
		else if (lady.isChecked())
		{
			fileStream.println("Girl");
		}
		
		//writing class
		if (tank.isChecked())
		{
			fileStream.println("Tank");
		}
		else if(warrior.isChecked())
		{
			fileStream.println("Warrior");
		}
		else if (mage.isChecked())
		{
			fileStream.println("Mage");
		}
		else if (rouge.isChecked())
		{
			fileStream.println("Rogue");
		}
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
		 */
		//giving money (guaps)
		fileStream.println("20");
		//giving potion
		fileStream.println("Water");
		//purchased equip giving nothing
		fileStream.println("");
		//giving noob equipment
		fileStream.println("Baby Hat");
		fileStream.println("Baby Shirt");
		fileStream.println("Baby Pants");
		fileStream.println("Baby Shoes");
		fileStream.println("Baby Stick");
		//giving stats depending on class
		//hp
		if(tank.isChecked())
		{
			fileStream.println("10");
		}
		else
		{
			fileStream.println("5");
		}
		//strength
		if(warrior.isChecked())
		{
			fileStream.println("10");
		}
		else
		{
			fileStream.println("5");
		}
		//intelligence
		if (mage.isChecked())
		{
			fileStream.println("10");
		}
		else
		{
			fileStream.println("5");
		}
		//dexterity
		if(rouge.isChecked())
		{
			fileStream.println("10");
		}
		else
		{
			fileStream.println("5");
		}
		//print level
		fileStream.println("1");
		//print exp to next level
		fileStream.println("10");
		//print skillpoints
		fileStream.println("0");
		//print the unlocked levels
		fileStream.println("1");
		//print the character model
		fileStream.println(Integer.toString(gender + playerClass + skin));
		//print the wins, losses, and final score (zero until you beat game)
		fileStream.println("0");
		fileStream.println("0");
		fileStream.println("0");
		//print yes to blink the jounrnal
				fileStream.println("1");
    	fileStream.close();
    	
    	//renaming file to .sav, so no one easily hacks their save file
    	File broFile = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".txt");
    	File finalFile = new File(this.getFilesDir().getAbsolutePath() + "/Trust In Heart-" +
				prefs.getString("SAVEFILE", "character") + ".sav");
    	broFile.renameTo(finalFile);
    	
    	//finish confirmation
    	//toasty.cancel();
		toasty.setText("Welcome to Trust In Heart!");
		toasty.show();
		
    	//stopping music
    	bgMusic.stopSong();
    	noPause = true;
    	//restarting start screen
    	Intent intent = new Intent(this, StartScreen.class);
    	//clear top since we dont want any history
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    	//finish here because you aren't coming back to here
    	//hopfully not finishing helps with closing entire app on new character create
		//finish();
		
		}
		
	}
	
	//function call that easily starts the playermodel animation
	public void animationStart()
	{
		 //to get animation working
	    monsteranim = (AnimationDrawable) player.getBackground();
	    monsteranim.start();
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

		    if(musicPaused)
		    {
		    bgMusic.resumeSong();
		    }
		    
		    animationStart();
		}
		
		//don't ovveride on back pressed, just go to last activity
		
	
	//for radio button clicks
	public void genderClick(View view)
	{
		//get the button id
		int id = view.getId();
		
		//depending on the button clicked, change the values to appropriate sections
		if(id == R.id.dude)
		{
			gender = 0;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		else
		{
			gender = 12;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		
		animationStart();
	}
	
	public void skinClick(View view)
	{
		//get the button id
				int id = view.getId();
				
				
				//depending on the button clicked, change the values to appropriate sections
		if(id == R.id.dark)
		{
			skin = 0;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		else if(id == R.id.tan)
		{
			skin = 4;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		else
		{
			skin = 8;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		
		animationStart();
	}
	
	public void classClick(View view)
	{
		//get the button id
		int id = view.getId();
		
		//depending on the button clicked, change the values to appropriate sections
		if(id == R.id.mage)
		{
			playerClass = 0;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		else if(id == R.id.rouge)
		{
			playerClass = 1;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		else if(id == R.id.tank)
		{
			playerClass = 2;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		else
		{
			playerClass = 3;
			player.setBackgroundResource(characters[gender + playerClass + skin]);
		}
		
		animationStart();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent fight = new Intent(this, SaveFileSelect.class);
		//add this flag to remove all previous activities
		fight.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(fight);
		finish();
		
		
		noPause = true;
	}
	
	//need to add this to avoid service connection leaks
			@Override
			public void onDestroy()
			{
				super.onDestroy();
				unbindService(musicConnection);
				Unbind.unbindDrawables((ScrollView) findViewById(R.id.container));
				System.gc();
			}
	

}

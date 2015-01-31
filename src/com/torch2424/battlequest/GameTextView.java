package com.torch2424.battlequest;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

//guided this class by stack overflow
//http://stackoverflow.com/questions/6700374/android-character-by-character-display-text-animation
public class GameTextView extends TextView 
{
	

	private CharSequence mText;
    private int mIndex;
    private long mDelay = 1; //Default 500ms delay //Maybe add preference to change value
    boolean animating = false; //bolean to tell if the text is displaying
    private int textSpeed = 3; // our textspeed
	
    
    //the public class of this view
    public GameTextView(Context context, AttributeSet attrs) 
    {
    	super(context.getApplicationContext(), attrs);
	}
	
	private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() 
    {
        @Override
        public void run() 
        {
        	mIndex = mIndex + textSpeed;
        	if(mIndex >= mText.length())
        	{
        		mIndex =  mText.length();
        		 mHandler.removeCallbacks(characterAdder);
        		animating = false;
        	}
            setText(mText.subSequence(0, mIndex));
            if(mIndex <= mText.length()) 
            {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    public void animateText(CharSequence text) 
    {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        //start animating
        animating = true;
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) 
    {
        mDelay = millis;
    }
    
    public void setTextSpeed (int input)
    {
    	textSpeed = input;
    }
    
    
    //custom way to get text from our textview the accurate way
    //created by me
    public CharSequence getString()
    {
    	return mText;
    }
    
    //return the animating boolean
    public boolean isAnimating()
    {
    	if(animating)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

}

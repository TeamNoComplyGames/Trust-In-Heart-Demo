package com.torch2424.battlequest;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class Unbind 
{
	
	//class to unbind drawables on destroy
	
	//a function to fix image issues in all other activities
		//got from blog http://androidbyhp.blogspot.com/2011/07/android-how-to-get-rid-of-out-of.html
		 public static void unbindDrawables(View view) 
	     {
	        if (view.getBackground() != null) 
	        {
	           view.getBackground().setCallback(null);
	        }
	        if (view instanceof ViewGroup  && !(view instanceof AdapterView)) 
	        {
	           for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) 
	           {
	              unbindDrawables(((ViewGroup) view).getChildAt(i));
	           }
	           ((ViewGroup) view).removeAllViews();
	        }
	     }

}

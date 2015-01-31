package com.torch2424.battlequest;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.torch2424.trustinheartdemo.R.color;

//got this from stack overflow
//http://stackoverflow.com/questions/4576441/custom-font-in-android-listview
public class CustomListview extends BaseAdapter 
{
	private boolean centerText;
	private List<String>   objects; // obviously don't use object, use whatever you really want
	private Context   context;
	int selectedIndex = -1;
	
	public CustomListview(Context context, List<String> objects, boolean bool) 
	{
	     this.context = context.getApplicationContext();
	    this.objects = objects;
	    centerText = bool;
	}

	@Override
	public int getCount() 
	{
		return objects.size();
	}

	@Override
	public String getItem(int position) 
	{
		return objects.get(position);
	}
	
	public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		 Object obj = objects.get(position);

		    TextView tv = new TextView(context);
		    //since I'm Using application COntext set text to black
		    tv.setTextColor(Color.BLACK);
		    tv.setTextSize(20);
		    tv.setPadding(5, 0, 0, 0);
		    tv.setText(obj.toString());
		  //get font
		    Typeface tf = FontCache.get(context, "font");
			tv.setTypeface(tf);
			
			//center text
			if(centerText)
			{
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextSize(32);
				
				if(selectedIndex!= -1 && position == selectedIndex)
		        {
		            tv.setBackgroundResource(color.semired);
		        }
				
			}
		    return tv;
	}

}

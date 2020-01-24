/*
 *
 * Copyright (c) 2010-2014 EVE GROUP PTE. LTD.
 *
 */


package org.sapplayer.sample.data;

import java.io.File;
import java.util.Comparator;

import org.sapplayer.sample.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FileItem extends GridData
{
    public FileItem (String name, String url, String user, String password, long id, int image, String image_file) 
    {
    	super(name, url, user, password, id, image, image_file);
    }
    
    public boolean DrawItem(final Context context, final View view, float thumbWidth, float thumbHeight, float shadowRadius, PointF shadowDirection)
    {
    	if (super.DrawItem(context, view, thumbWidth, thumbHeight, shadowRadius, shadowDirection))
    		return true;
    	
        ImageView imbThumb = (ImageView) view.findViewById(R.id.document_item);
        imbThumb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        
        if (!isDirectory && draw == null)
        	return false;
        
    	if(isDirectory)
    	{
    		imbThumb.setImageResource(R.drawable.ic_menu_archive);
   		    imbThumb.setScaleType(ScaleType.CENTER_INSIDE);
    	}
    	else
    	{
			imbThumb.setImageDrawable(draw);
        	imbThumb.setScaleType(ScaleType.FIT_CENTER);
			isUpdate = false;
    	}
    	
    	return true;
    }
}

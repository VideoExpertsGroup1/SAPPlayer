/*
 *
 * Copyright (c) 2010-2014 EVE GROUP PTE. LTD.
 *
 */


package org.sapplayer.sample.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import veg.mediaplayer.sdk.MediaPlayer;
import veg.mediaplayer.sdk.MediaPlayer.MediaPlayerCallback;
import veg.mediaplayer.sdk.MediaPlayer.PlayerState;
import veg.mediaplayer.sdk.MediaPlayerConfig;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class SAPUpdater 
{
	public abstract interface SAPUpdaterCallbacks 
	{
		public void sapPreExecute();
		public void sapPostExecute(final Boolean success);
		public void sapDataReady(String data);
	}

	public static synchronized SAPUpdater getInstance()
	{
		return _inst;
	} 

	public static synchronized SAPUpdater getInstance(Context context, SAPUpdaterCallbacks subscriber)
	{
		if (_inst == null)
		{
			_inst = new SAPUpdater(context, subscriber);
		}

		return _inst;
	} 

	private UpdateSAPTask updateSAPTask = null;
    
    private boolean stop = false;

    private Context context = null;
	private SAPUpdaterCallbacks subscriber = null;
	private static volatile SAPUpdater _inst = null; 
	
    private SAPUpdater(Context context, SAPUpdaterCallbacks subscriber)
    {
		Log.i(TAG, "SAPUpdater init");
		
    	this.context = context;
    	this.subscriber = subscriber;
    }
    
    public boolean IsWorking()  
	{
		return (updateSAPTask != null); 
	}
	
	public boolean Start() 
	{
		if (updateSAPTask != null)
			return true;
		
		stop = false;
		updateSAPTask = new UpdateSAPTask();
		executeAsyncTask(updateSAPTask, (Void) null);
		return true;
	}
	
	public void Stop() 
	{
		Log.i(TAG, "SAPUpdater: Stop");
		if (updateSAPTask != null)
		{
			stop = true;
			Log.i(TAG, "SAPUpdater: Stoping " + stop);
			updateSAPTask.cancel(true);
			updateSAPTask = null;
		}
	}
	
    class UpdateSAPTask extends AsyncTask<Void, Void, Boolean> implements MediaPlayerCallback 
	{
    	//private final WeakReference<LaunchActivity> loginActivityWeakRef;
    	public UpdateSAPTask() 
    	{
//    		super();
//    		this.loginActivityWeakRef= new WeakReference<LaunchActivity >(loginActivity);
    	}
    	  
    	@Override
		protected void onPreExecute() 
		{
			if (subscriber != null)
				subscriber.sapPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			Log.i(TAG, "SAPUpdater: start thread");
	    	MediaPlayer player = new MediaPlayer(context, false);
	    	MediaPlayerConfig config = new MediaPlayerConfig();
	    	
			config.setConnectionUrl("sap://");
			config.setConnectionNetworkProtocol(1);
			config.setEnableAudio(0);
			config.setConnectionDetectionTime(500);
			config.setDataReceiveTimeout(2000000000);
			config.setConnectionBufferingTime(0);
			player.Open(config, this);
			while (!stop)
			{
//				if (player.getState() == PlayerState.Closed )
//				{
//					Log.i(TAG, "SAPUpdater: player closed");
//					break;
//				}
				
				if (player.getState() == PlayerState.Closing || 
						player.getState() == PlayerState.Opening)
				{
					Log.i(TAG, "SAPUpdater: wait player openning...");
					try { Thread.sleep(1000); } 
					catch (InterruptedException e) { break; }
					continue;
				}

//				if (subscriber != null && player.getState() == PlayerState.Started )
//					subscriber.sapDataReady("sdp://test");
				
				try { Thread.sleep(500); } 
				catch (InterruptedException e) { break; }
			}
			
			Log.i(TAG, "SAPUpdater: start player closing");
			player.Close();
			stop = true;
			Log.i(TAG, "SAPUpdater: stop thread");
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) 
		{
			updateSAPTask = null;
			if (subscriber != null)
			{
				//loginActivityWeakRef.get().finish();
				subscriber.sapPostExecute(success);
			}
		}

		@Override
		public int Status(int arg) 
		{
			Log.i(TAG, "SAPUpdater: player status: " + arg);
			return 0;
		}

		@Override
		public int OnReceiveData(ByteBuffer buffer, int size, long pts) 
		{
		    ByteBuffer clone = ByteBuffer.allocate(buffer.capacity());
		    buffer.rewind();//copy from the beginning
		    clone.put(buffer);
		    buffer.rewind();
		    clone.flip();
			String strData = StandardCharsets.US_ASCII.decode(clone).toString();
			//Log.i(TAG, "SAPUpdater: player OnReceiveData: " + StandardCharsets.US_ASCII.decode(buffer).toString() + ", " + size);
			if (subscriber != null)
				subscriber.sapDataReady(strData);
			return 0;
		}
		
	}
    
    static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) 
    {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
    	{
    		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    	}
    	else 
    	{
    		task.execute(params);
    	}
    }  
    
	private static final String TAG	= "SAPUpdater";
}


package com.ryanm.deathworm;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

/**
 * an {@link AsyncTask} for loading a location feed
 * 
 * @author ryanm
 */
public class LocationFeedLoader extends AsyncTask<String, Integer, Location[]>
{

	@Override
	protected Location[] doInBackground( String... params )
	{
		try
		{
			URL url = new URL( params[ 0 ] );
		}
		catch( MalformedURLException e )
		{
			Log.e( DeathwormActivity.LOGTAG, "doh!", e );
		}

		return new Location[ 0 ];
	}
}

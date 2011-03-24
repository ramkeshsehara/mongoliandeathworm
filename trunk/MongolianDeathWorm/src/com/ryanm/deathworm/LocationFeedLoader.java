
package com.ryanm.deathworm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An {@link AsyncTask} for loading a location feed
 * 
 * @author ryanm
 */
class LocationFeedLoader extends AsyncTask<String, Void, ContentLocation[]>
{
	private final DeathwormActivity activity;

	LocationFeedLoader( DeathwormActivity dwa )
	{
		activity = dwa;
	}

	@Override
	protected ContentLocation[] doInBackground( String... params )
	{
		try
		{
			URL url = new URL( params[ 0 ] );
			URLConnection conn = url.openConnection();
			BufferedReader in =
					new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
			StringBuilder jsonString = new StringBuilder();

			String line;
			while( ( line = in.readLine() ) != null )
			{
				jsonString.append( line );
			}

			in.close();

			JSONObject json = new JSONObject( jsonString.toString() );
			JSONArray locArray = json.getJSONArray( "locations" );

			ContentLocation[] locations = new ContentLocation[ locArray.length() ];
			for( int i = 0; i < locArray.length(); i++ )
			{
				locations[ i ] = new ContentLocation( ( JSONObject ) locArray.get( i ) );
			}

			return locations;
		}
		catch( Exception e )
		{
			Log.e( DeathwormActivity.LOGTAG, "doh!", e );
		}

		return new ContentLocation[ 0 ];
	}

	@Override
	protected void onPostExecute( ContentLocation[] result )
	{
		activity.setLocations( result );
	}
}

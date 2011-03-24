
package com.ryanm.deathworm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LocationFeed
{
	public final String name;

	public final String summary;

	public final ContentLocation[] locations;

	public final static LocationFeed empty = new LocationFeed();

	public LocationFeed( String feedURL ) throws MalformedURLException, IOException,
			JSONException
	{
		URL url = new URL( feedURL );
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

		name = json.optString( "name" );
		summary = json.optString( "summary" );

		JSONArray locArray = json.getJSONArray( "locations" );

		locations = new ContentLocation[ locArray.length() ];
		for( int i = 0; i < locArray.length(); i++ )
		{
			locations[ i ] = new ContentLocation( ( JSONObject ) locArray.get( i ) );
		}
	}

	private LocationFeed()
	{
		name = "Empty";
		summary = "";
		locations = new ContentLocation[ 0 ];
	}

	static class Loader extends AsyncTask<String, Void, LocationFeed>
	{
		private final DeathwormActivity activity;

		Loader( DeathwormActivity dwa )
		{
			activity = dwa;
		}

		@Override
		protected LocationFeed doInBackground( String... params )
		{
			try
			{
				return new LocationFeed( params[ 0 ] );
			}
			catch( Exception e )
			{
				Toast.makeText( activity, "Error parsing location feed!", Toast.LENGTH_LONG )
						.show();
				Log.e( DeathwormActivity.LOGTAG, "doh!", e );
			}

			return LocationFeed.empty;
		}

		@Override
		protected void onPostExecute( LocationFeed result )
		{
			activity.setLocations( result );
		}
	}
}

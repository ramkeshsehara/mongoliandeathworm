
package com.ryanm.deathworm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author ryanm
 */
public class LocationFeed
{
	/***/
	public final String name;

	/***/
	public final String summary;

	/***/
	public final ContentLocation[] locations;

	/**
	 * The default, empty, feed
	 */
	public final static LocationFeed empty = new LocationFeed();

	/**
	 * @param jsonString
	 *           encoded feed data
	 * @throws JSONException
	 *            If something goes wrong when parsing the feed data
	 */
	public LocationFeed( String jsonString ) throws JSONException
	{
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

	/**
	 * @return encoded feed data
	 */
	public String encode()
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put( "name", name );
			json.put( "summary", summary );

			JSONArray locs = new JSONArray();
			for( ContentLocation cl : locations )
			{
				locs.put( cl.encode() );
			}

			json.put( "locations", locs );

			return json.toString();
		}
		catch( JSONException e )
		{
			Log.e( DeathwormActivity.LOGTAG, "encoding error", e );
		}

		return "";
	}

	/**
	 * Loads a feed from the interwebs
	 */
	public static class Loader extends AsyncTask<String, Void, LocationFeed>
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

				return new LocationFeed( jsonString.toString() );
			}
			catch( Exception e )
			{
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

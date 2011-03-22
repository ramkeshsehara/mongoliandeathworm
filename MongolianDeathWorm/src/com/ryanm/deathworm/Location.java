
package com.ryanm.deathworm;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

/**
 * Encapsulates a location's details
 * 
 * @author ryanm
 */
public class Location
{
	private final static String LAT = "lat", LON = "lon", SIZE = "size", NAME = "name",
			HIDDEN = "hidden", URL = "url";

	/**
	 * The coordinates of the location
	 */
	public final GeoPoint location;

	/**
	 * How close the user must be to activate the location, in meters
	 */
	public final int size;

	/**
	 * The location's name
	 */
	public final String name;

	/**
	 * <code>true</code> if the location is not shown on the map
	 */
	public final boolean hidden;

	/**
	 * The content to show at the location
	 */
	public final URL content;

	/**
	 * @param json
	 * @throws JSONException
	 * @throws MalformedURLException
	 */
	public Location( JSONObject json ) throws JSONException, MalformedURLException
	{
		int lat = json.getInt( LAT );
		int lon = json.getInt( LON );
		location = new GeoPoint( lat, lon );
		size = json.getInt( SIZE );
		name = json.getString( NAME );
		hidden = json.getBoolean( HIDDEN );
		content = new URL( json.getString( URL ) );
	}

	/**
	 * @return the encoded json form of the point
	 * @throws JSONException
	 */
	public JSONObject encode() throws JSONException
	{
		JSONObject json = new JSONObject();

		json.put( LAT, location.getLatitudeE6() );
		json.put( LON, location.getLatitudeE6() );
		json.put( SIZE, size );
		json.put( NAME, name );
		json.put( HIDDEN, hidden );
		json.put( URL, content.toString() );

		return json;
	}

	/**
	 * Computes the distance from this {@link Location} to another
	 * point
	 * 
	 * @param point
	 * @return the distance, in meters
	 */
	public float distance( GeoPoint point )
	{
		return 0;
	}
}

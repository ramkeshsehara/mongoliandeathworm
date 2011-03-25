
package com.ryanm.deathworm;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Encapsulates a location's details
 * 
 * @author ryanm
 */
public class ContentLocation extends OverlayItem
{
	private final static String LAT = "lat", LON = "lon", SIZE = "size", NAME = "name",
			HIDDEN = "hidden", URL = "url";

	/**
	 * How close the user must be to activate the location, in meters
	 */
	public final int size;

	/**
	 * <code>true</code> if the location is not shown on the map
	 */
	public final boolean hidden;

	/**
	 * The content to show at the location
	 */
	public final Uri content;

	/**
	 * Is set to <code>true</code> when the user is close enough
	 */
	public boolean active = false;

	/**
	 * @param json
	 * @throws JSONException
	 */
	public ContentLocation( JSONObject json ) throws JSONException
	{
		super( new GeoPoint( json.getInt( LAT ), json.getInt( LON ) ), json
				.getString( NAME ), null );

		size = json.optInt( SIZE );
		hidden = json.optBoolean( HIDDEN, false );
		content = Uri.parse( json.getString( URL ) );
	}

	/**
	 * @return the encoded json form of the point
	 * @throws JSONException
	 */
	public JSONObject encode() throws JSONException
	{
		JSONObject json = new JSONObject();

		json.put( LAT, getPoint().getLatitudeE6() );
		json.put( LON, getPoint().getLongitudeE6() );
		json.put( SIZE, size );
		json.put( NAME, getTitle() );
		json.put( HIDDEN, hidden );
		json.put( URL, content.toString() );

		return json;
	}

	private static float[] results = new float[ 1 ];

	/**
	 * Activates the location if the user is close enough
	 * 
	 * @param user
	 * @param userSize
	 */
	public void checkActivation( GeoPoint user, float userSize )
	{
		float distance = distance( user );
		active = distance <= size + userSize;
	}

	/**
	 * Computes the distance from this {@link ContentLocation} to
	 * another point.
	 * 
	 * @param point
	 * @return the distance, in meters
	 */
	public float distance( GeoPoint point )
	{
		double lat1 = getPoint().getLongitudeE6() / 1E6;
		double lon1 = getPoint().getLatitudeE6() / 1E6;
		double lat2 = point.getLongitudeE6() / 1E6;
		double lon2 = point.getLatitudeE6() / 1E6;
		Location.distanceBetween( lat1, lon1, lat2, lon2, results );
		return results[ 0 ];
	}

	@Override
	public Drawable getMarker( int stateBitset )
	{
		final Drawable d;

		if( active )
		{
			d = ContentOverlay.active;
		}
		else if( hidden )
		{
			d = ContentOverlay.hidden;
		}
		else
		{
			d = ContentOverlay.inactive;
		}

		setState( d, stateBitset );
		return d;
	}

	@Override
	public String toString()
	{
		return getTitle() + "@" + getPoint() + " : " + content;
	}
}

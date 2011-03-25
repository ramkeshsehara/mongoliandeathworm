
package com.ryanm.deathworm;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.ryanm.preflect.Persist;
import com.ryanm.preflect.Preflect;
import com.ryanm.preflect.annote.Summary;
import com.ryanm.preflect.annote.Variable;

/**
 * @author ryanm
 */
public class DeathwormActivity extends MapActivity
{
	/**
	 * Key for saved location feed data in bundle
	 */
	private static final String LOC_FEED_KEY = "locFeed";

	/***/
	public static final String LOGTAG = "deathworm";

	private MapView map;

	private MyLocationOverlay locationOverlay;

	private ContentOverlay contentOverlay;

	/***/
	@Variable( "Location Feed" )
	@Summary( "Where to find a location list" )
	public String locationFeedURL =
			"http://mongoliandeathworm.googlecode.com/svn/trunk/MongolianDeathWorm/locations.json";

	/***/
	@Variable( "User size" )
	@Summary( "Added to location size to determine a hit" )
	public int userSize = 10;

	/***/
	@Variable( "Check Delay" )
	@Summary( "The delay between checking locations for activation" )
	public float refreshDelay = 3;

	private LocationFeed locations = LocationFeed.empty;

	/**
	 * Use this to schedule location checks
	 */
	private Handler handler = new Handler();

	private Runnable locCheckTask = new Runnable() {
		@Override
		public void run()
		{
			GeoPoint userLoc = locationOverlay.getMyLocation();

			if( userLoc != null )
			{
				map.getController().animateTo( userLoc );

				for( ContentLocation cl : locations.locations )
				{
					cl.checkActivation( userLoc, userSize );
				}
			}

			handler.postAtTime( locCheckTask, SystemClock.uptimeMillis()
					+ ( long ) ( refreshDelay * 1000 ) );
		}
	};

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		ExceptionHandler.register( this, "therealryan+deathworm@gmail.com" );

		map = new MapView( this, getResources().getString( R.string.debug_maps_api_key ) );
		map.setClickable( true );

		locationOverlay = new MyLocationOverlay( this, map );
		map.getOverlays().add( locationOverlay );
		map.getController().setZoom( 18 );

		contentOverlay = new ContentOverlay( this );
		map.getOverlays().add( contentOverlay );

		setContentView( map );

		Persist.load( this, "default", this );

		if( savedInstanceState != null )
		{
			String savedFeed = savedInstanceState.getString( LOC_FEED_KEY );
			if( savedFeed != null )
			{
				try
				{
					Log.i( LOGTAG, "loading feed" );
					setLocations( new LocationFeed( savedFeed ) );
				}
				catch( JSONException e )
				{
					Log.e( LOGTAG, "Error restoring feed", e );
				}
			}
		}

		if( locations == LocationFeed.empty )
		{
			refreshLocationFeed();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		locationOverlay.enableMyLocation();

		handler.post( locCheckTask );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		Persist.onActivityResult( requestCode, resultCode, data, this, "default", this );
	}

	@Override
	public boolean onKeyUp( int keyCode, KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_MENU )
		{
			Preflect.configure( this, false, false, this );
		}

		return super.onKeyUp( keyCode, event );
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		locationOverlay.disableMyLocation();
		handler.removeCallbacks( locCheckTask );
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		Log.i( LOGTAG, "saving feed" );

		outState.putString( LOC_FEED_KEY, locations.encode() );
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	/***/
	@Variable( "Refresh" )
	@Summary( "Re-downloads the location feed" )
	public void refreshLocationFeed()
	{
		Log.i( LOGTAG, "downloading feed" );

		LocationFeed.Loader loader = new LocationFeed.Loader( this );
		loader.execute( locationFeedURL );
	}

	void setLocations( LocationFeed feed )
	{
		locations = feed;

		contentOverlay.setLocations( feed.locations );

		Log.i( LOGTAG, "Loaded " + feed.locations.length );
		for( ContentLocation cl : feed.locations )
		{
			Log.i( LOGTAG, cl.toString() );
		}
	}
}
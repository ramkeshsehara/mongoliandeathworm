
package com.ryanm.deathworm;

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
	@Variable( "Refresh Delay" )
	@Summary( "The delay between checking locations" )
	public float refreshDelay = 3;

	private ContentLocation[] locations = new ContentLocation[ 0 ];

	/**
	 * Use this to schedule location checks
	 */
	private Handler handler = new Handler();

	private Runnable locCheckTask = new Runnable() {
		@Override
		public void run()
		{
			GeoPoint userLoc = locationOverlay.getMyLocation();

			Log.i( LOGTAG, "Checking locations! " + userLoc );

			if( userLoc != null )
			{
				for( ContentLocation cl : locations )
				{
					float d = cl.distance( userLoc );

					if( d < cl.size + userSize )
					{
						Log.i( LOGTAG, "Hit " + cl );
					}
				}
			}

			handler.postAtTime( locCheckTask, SystemClock.uptimeMillis()
					+ ( long ) ( refreshDelay * 1000 ) );
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		map = new MapView( this, getResources().getString( R.string.debug_maps_api_key ) );
		map.setClickable( true );

		locationOverlay = new MyLocationOverlay( this, map );
		map.getOverlays().add( locationOverlay );

		contentOverlay = new ContentOverlay( this );
		map.getOverlays().add( contentOverlay );

		setContentView( map );

		Persist.load( this, "default", this );

		loadLocations();
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
		Preflect.onActivityResult( requestCode, resultCode, data, this );
	}

	@Override
	public boolean onKeyUp( int keyCode, KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_MENU )
		{
			Preflect.configure( this, this );
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
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	private void loadLocations()
	{
		LocationFeedLoader loader = new LocationFeedLoader( this );
		loader.execute( locationFeedURL );
	}

	void setLocations( ContentLocation[] locations )
	{
		this.locations = locations;
		Log.i( LOGTAG, "Loaded " + locations.length + " locations" );

		contentOverlay.setLocations( locations );
	}
}
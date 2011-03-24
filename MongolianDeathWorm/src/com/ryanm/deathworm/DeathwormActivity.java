
package com.ryanm.deathworm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

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

			Log.i( LOGTAG, "Checking locations! " + userLoc );

			if( userLoc != null )
			{
				map.getController().animateTo( userLoc );

				for( ContentLocation cl : locations.locations )
				{
					float d = cl.distance( userLoc );

					if( d < cl.size + userSize )
					{
						Toast.makeText( DeathwormActivity.this, "Hit " + cl.name,
								Toast.LENGTH_SHORT ).show();

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

		refreshLocationFeed();
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

	/***/
	@Variable( "Refresh" )
	@Summary( "Re-downloads the location feed" )
	public void refreshLocationFeed()
	{
		LocationFeed.Loader loader = new LocationFeed.Loader( this );
		loader.execute( locationFeedURL );
	}

	void setLocations( LocationFeed feed )
	{
		locations = feed;

		Toast.makeText( this,
				"Loaded " + feed.locations.length + " from " + feed.name + " feed",
				Toast.LENGTH_LONG ).show();

		contentOverlay.setLocations( feed.locations );
	}
}
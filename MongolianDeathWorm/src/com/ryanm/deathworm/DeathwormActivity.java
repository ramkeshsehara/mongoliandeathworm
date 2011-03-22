
package com.ryanm.deathworm;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

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

	/***/
	@Variable( "Location Feed" )
	@Summary( "Where to find a location list" )
	public String locationFeedURL = null;

	/***/
	@Variable( "User size" )
	@Summary( "Added to location size to determine a hit" )
	public int userSize = 10;

	private Location[] locations = new Location[ 0 ];

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		map = new MapView( this, getResources().getString( R.string.debug_maps_api_key ) );
		map.setClickable( true );

		locationOverlay = new MyLocationOverlay( this, map );
		map.getOverlays().add( locationOverlay );

		setContentView( map );

		Persist.load( this, "default", this );

		loadLocations();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		locationOverlay.enableMyLocation();
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
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	private void loadLocations()
	{
	}
}
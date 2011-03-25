
package com.ryanm.deathworm;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

class ContentOverlay extends ItemizedOverlay
{
	private ContentLocation[] locations = new ContentLocation[ 0 ];

	public static Drawable hidden, inactive, active;

	private final Context context;

	ContentOverlay( Context context )
	{
		super( inactive =
				boundCenter( context.getResources().getDrawable( R.drawable.star_off ) ) );

		this.context = context;

		active = boundCenter( context.getResources().getDrawable( R.drawable.star_on ) );
		hidden = new ShapeDrawable();
		hidden.setAlpha( 0 );
	}

	void setLocations( ContentLocation[] locations )
	{
		this.locations = locations;

		populate();
	}

	@Override
	protected OverlayItem createItem( int i )
	{
		return locations[ i ];
	}

	@Override
	public int size()
	{
		return locations.length;
	}

	@Override
	protected boolean onTap( int index )
	{
		ContentLocation cl = locations[ index ];

		if( cl.active )
		{
			context.startActivity( new Intent( Intent.ACTION_VIEW, cl.content ) );

			return true;
		}

		return false;
	}
}


package com.ryanm.deathworm;

import java.util.ArrayList;

import android.R;
import android.content.Context;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

class ContentOverlay extends ItemizedOverlay
{
	private ArrayList<ContentLocation> visibles = new ArrayList<ContentLocation>();

	ContentOverlay( Context context )
	{
		super( boundCenter( context.getResources().getDrawable( R.drawable.star_on ) ) );
	}

	void setLocations( ContentLocation[] locations )
	{
		visibles.clear();
		for( int i = 0; i < locations.length; i++ )
		{
			if( !locations[ i ].hidden )
			{
				visibles.add( locations[ i ] );
			}
		}

		populate();
	}

	@Override
	protected OverlayItem createItem( int i )
	{
		return visibles.get( i ).overlayItem;
	}

	@Override
	public int size()
	{
		return visibles.size();
	}
}

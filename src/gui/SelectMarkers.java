package gui;

import java.util.*;

import data.*;

class SelectMarkers
{
	static void doSelection(LinkageGroup lGroup)
	{
		for (CMarker cm: lGroup.getMarkers())
		{
			// If we're only matching against markers that are not yet
			// selected, and this one is, then go no further
			if (Prefs.gui_select_match_selected && cm.checked)
				continue;
			
			if (cm.marker.isBestRatioKnown() == false)
				continue;
			
			// Start by setting the marker to selected
			cm.checked = true;
			
			// Now analyse the marker itself
			Marker marker = cm.marker;			

			// Select on TYPE
			if (Prefs.gui_select_on_type)
				if (marker.getType() != Prefs.gui_select_type)
					cm.checked = false;
			
			// Select on PARENT
			if (Prefs.gui_select_on_parent)
			{
				// Is it present in parent 1?
				if (Prefs.gui_select_parent == 1)
				{
					if (marker.isPresentInParent(2))
						cm.checked = false;
				}
				// Is it present in parent 2?
				else if (Prefs.gui_select_parent == 2)
				{
					if (marker.isPresentInParent(1))
						cm.checked = false;
				}
				// Is it present in parent 1 and parent 2?
				else if (Prefs.gui_select_parent == 3)
				{
					if (marker.isPresentInParent(1) == false ||
						marker.isPresentInParent(2) == false)
						cm.checked = false;
				}
			}
			
			// Select on RATIO
			if (Prefs.gui_select_on_ratio)
				if (marker.getRatioCode() != Prefs.gui_select_ratio ||
					marker.getRatioSignificance() < Prefs.gui_select_ratio_sig)
				{
					cm.checked = false;
				}

			// Select on DOUBLE REDUCTION
			if (Prefs.gui_select_on_dr)
			{
				// Looking for YES on DR
				if (Prefs.gui_select_dr &&
					marker.getDRSignificance() > Prefs.gui_select_dr_sig)
					cm.checked = false;
				// Looking for NO on DR
				else if (Prefs.gui_select_dr == false &&
					marker.getDRSignificance() <= Prefs.gui_select_dr_sig)
					cm.checked = false;
			}
		}
	}
	
	static void selectAll(LinkageGroup lGroup)
	{
		for (CMarker cm: lGroup.getMarkers())
			if (cm.marker.isBestRatioKnown())
				cm.checked = true;
	}
	
	static void selectNone(LinkageGroup lGroup)
	{
		for (CMarker cm: lGroup.getMarkers())
			if (cm.marker.isBestRatioKnown())
				cm.checked = false;
	}
	
	static void selectInvt(LinkageGroup lGroup)
	{
		for (CMarker cm: lGroup.getMarkers())
			if (cm.marker.isBestRatioKnown())
				cm.checked = !cm.checked;
	}
}
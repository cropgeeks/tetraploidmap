package gui.nav;

import data.*;

class MarkersNode
{
	LinkageGroup lGroup;
//		MarkerDetails markerDetails;
	
	MarkersNode(LinkageGroup lGroup)
	{
		this.lGroup = lGroup;
//			markerDetails = new MarkerDetails(lGroup);
	}
	
	public String toString() { return lGroup.getMarkerCountString(); }
}
	
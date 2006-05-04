package gui.nav;

import javax.swing.*;

import data.*;

class GroupsNode
{
	LinkageGroup lGroup;
	String name;
	int markerCount;
	
	GroupsNode(LinkageGroup lGroup)
	{
		this.lGroup = lGroup;
//		this.name = name;
//		this.markerCount = markerCount;
	}
	
	public String toString()
	{ 
		return lGroup.getName() + " (" + lGroup.getMarkerCount() + ")";
	}
	
	public JLabel getLabel()
	{
		markerCount = lGroup.getMarkerCount();
		
		return new JLabel(lGroup.getName() + " (" + markerCount
			+ " marker" + (markerCount == 1 ? "" : "s") + ")",
			JLabel.CENTER);
	}
}
	
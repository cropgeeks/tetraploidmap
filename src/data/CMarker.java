package data;

import java.io.*;

import gui.*;

// Wrapper class for Marker that provides the ability for the same Marker within
// different LinkageGroups (possible from different analyses results) to have a
// different checked state
public class CMarker implements Serializable
{
	static final long serialVersionUID = -5022081160204683415L;
	
	public boolean checked = true;
	public Marker marker = null;
	
	// It's "safe" name, which is passed to the fortran programs (eg "MKR123")
	public String safeName;
		
	public CMarker(Marker marker, int safeNameSuffix)
	{
		this.marker = marker;
		
		checked  = marker.canEnable();
		setSafeName(safeNameSuffix);
	}
	
	public void setSafeName(int safeNameSuffix)
		{ safeName = "mkr" + Prefs.i3.format(safeNameSuffix); }
	
	public String toString()
	{
		return marker.getName();
	}
	
	public int getIndex()
	{
		return Integer.parseInt(safeName.substring(3));
	}
	
	public CMarker getClone()
	{
		CMarker clone = new CMarker(marker, getIndex());
//		clone.prefix = prefix;
		
		return clone;
	}
}
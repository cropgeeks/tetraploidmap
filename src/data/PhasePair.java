package data;

import java.io.*;

public class PhasePair implements Serializable
{
	static final long serialVersionUID = -9043725362181996778L;
	
	public CMarker cm1, cm2;
	public String phase1, phase2;
	
	// Recombination frequency and LOD score
	public float rfq, lod;
	
	PhasePair(CMarker m1, CMarker m2, String p1, String p2)
	{
		cm1 = m1;
		cm2 = m2;
		phase1  = p1;
		phase2  = p2;
	}
	
	public int compare(CMarker m1, CMarker m2)
	{
		// 1st case
		if (cm1.marker == m1.marker && cm2.marker == m2.marker)
			return 1;
		
		// Alternate case
		if (cm1.marker == m2.marker && cm2.marker == m1.marker)
			return 2;
		
		return 0;
	}
	
	// Given the marker 'cm', returns the other marker in this pairing
	public CMarker getPartner(CMarker cm)
	{
		if (cm1.marker == cm.marker)
			return cm2;
		else if (cm2.marker == cm.marker)
			return cm1;
		else
			return null;
	}
	
	// Returns true if this PhasePair contains the two markers
	public boolean hasMarkers(CMarker m1, CMarker m2)
	{
		if (cm1.marker == m1.marker && cm2.marker == m2.marker ||
			cm1.marker == m2.marker && cm2.marker == m1.marker)
		{
			return true;
		}
		
		return false;
	}
}
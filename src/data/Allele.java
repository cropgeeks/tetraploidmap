package data;

import java.io.*;
import java.util.*;

public class Allele implements Serializable
{
	static final long serialVersionUID = -5925065724311527021L;
	
	// The Marker that this allele belongs to
	private Marker marker;
	
	private Vector<AlleleState> states = new Vector<AlleleState>();
	
	public Allele(Marker marker)
	{
		this.marker = marker;
	}
	
	public void addAlleleState(AlleleState state)
	{
		states.add(state);
	}
	
	public Vector<AlleleState> getStates() { return states; }
	
	public int getStateCount() { return states.size(); }
	
/*	public Object[] getTabularData(int markerIndex, int alleleIndex)
	{
		Object[] data = new Object[3];
		
		data[0] = "" + markerIndex;
		
		if (alleleIndex == 0)
			data[1] = marker.getName();
		else
			data[1] = "    " + marker.getName();
		
		data[2] = "" + (alleleIndex+1);
		
		return data;
	}
*/
}
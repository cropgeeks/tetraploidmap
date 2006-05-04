package data;

import java.io.*;
import java.util.*;

public class AnovaTraitResult implements Serializable
{
	private String traitName;
	private Vector<Marker> markers = new Vector<Marker>();
	private Vector<String> data = new Vector<String>();
	
	public AnovaTraitResult(String name)
	{
		traitName = name;
	}
	
	public void addMarker(Marker m, String d)
	{
		markers.add(m);
		data.add(d);
	}
	
	public String toString()
		{ return "(" + markers.size() + ") " + traitName; }
	
	public String getTraitName()
		{ return traitName; }
	
	public int getResultCount()
		{ return markers.size(); }
	
	public Marker getMarker(int index)
		{ return markers.get(index); }
	
	public String getData(int index)
		{ return data.get(index); }
}
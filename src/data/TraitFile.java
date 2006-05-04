package data;

import java.io.*;
import java.util.*;

public class TraitFile implements Serializable
{
	static final long serialVersionUID = 2675383049059248499L;
	
	private Vector<String> names = new Vector<String>();
	private Vector<float[]> rows = new Vector<float[]>();
	private Vector<Boolean> enabled;
	
	public TraitFile()
	{
	}
	
	public void addName(String name)
		{ names.add(name); }
	
	public void addRow(float[] row)
		{ rows.add(row); }
	
	public Vector<String> getNames()
		{ return names; }
	
	public Vector<float[]> getRows()
		{ return rows; }
	
	public Vector<Boolean> getEnabled()
	{ 
		if (enabled == null)
		{
			enabled = new Vector<Boolean>();
			for (int i = 0; i < names.size(); i++)
				enabled.add(true);
		}
	
		return enabled;
	}
	
	public int getSelectedCount()
	{
		int count = 0;
		for (Boolean b: getEnabled())
			if (b) count++;
		
		System.out.println("Count is " + count);
		return count;
	}
}

package data;

import java.io.*;
import java.util.*;

public class AnovaResult implements Serializable
{
	private String name;
	private Vector<AnovaTraitResult> results = new Vector<AnovaTraitResult>();
	
	public void addResult(AnovaTraitResult r)
	{
		results.add(r);
	}
	
	public Vector<AnovaTraitResult> getResults()
		{ return results; }
		
	public void setName(String name)
		{ this.name = name; }
	
	public String toString()
		{ return name; }
}
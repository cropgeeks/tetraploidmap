package data;

import java.io.*;
import java.util.*;

// Class the represents a cluster of LinkageGroups - results from running the
// CLUSTER module
public class Cluster implements Serializable
{
	static final long serialVersionUID = -2652444294541417903L;
	
	private Summary summary;
	private String name;
	
	private LinkedList<LinkageGroup> groups = new LinkedList<LinkageGroup>();
	
	private Dendrogram avLnkDendrogram = null;
	private Dendrogram snLnkDendrogram = null;
		
	public Cluster()
	{
	}
	
	public void setSummary(Summary summary)
		{ this.summary = summary; }
	
	public Summary getSummary()
		{ return summary; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public String getName()
		{ return name; }
	
	public String toString()
		{ return name; }
	
	public LinkedList<LinkageGroup> getGroups()
		{ return groups; }
	
	public void addLinkageGroup(LinkageGroup lGroup)
		{ groups.add(lGroup); }
	
	public void setAvLnkDendrogram(Dendrogram dendrogram)
	{
		avLnkDendrogram = dendrogram;
	}
	
	public void setSnLnkDendrogram(Dendrogram dendrogram)
	{
		snLnkDendrogram = dendrogram;
	}
	
	public Dendrogram getAvLnkDendrogram()
		{ return avLnkDendrogram; }
	
	public Dendrogram getSnLnkDendrogram()
		{ return snLnkDendrogram; }
}
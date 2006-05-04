package data;

import java.io.*;
import java.util.*;

public class LinkageGroup implements Serializable
{
	static final long serialVersionUID = -7093173103799457188L;
	
	// Stores the actual markers
	private Vector<CMarker> markers = new Vector<CMarker>();
	
	// Stores a set of results that have been run on this linkage group
	private LinkedList<Object> results = new LinkedList<Object>();
	
	// Stores TraitFile data for this group (only applies to the top-level group)
	private TraitFile tFile;
	
	private String name;
	private int clusterCount;
	private int orderedCount;
	private int anovaCount;
	
	public LinkageGroup(String name)
		{ this.name = name; }
	
	public void setTraitFile(TraitFile tFile)
		{ this.tFile = tFile; }
	
	public TraitFile getTraitFile()
		{ return tFile; }
	
	public String getName()
		{ return name; }
	
	public int getMarkerCount()
		{ return markers.size(); }
	
	// Adds a new marker to this linkage group
	public CMarker addMarker(Marker marker)
	{
		// Note that this CMarker's SAFE NAME will be indexed using the current
		// size of the marker list. This means we can always find a marker by
		// its safe name simply by going to that index
		CMarker cm = new CMarker(marker, markers.size());		
		markers.add(cm);
		
		return cm;
	}
	
	// Adds a Marker to the group, ensuring its "safe name" is the same as the
	// the safe name the CMarker passed in has
	public void addMarker(CMarker cm)
	{
		markers.add(cm.getClone());
	}
	
	// Returns the Marker with the given name. If it doesn't exist, a new
	// Marker is created to represent it
	public Marker getOrAddMarker(String name, int alleleCount)
		throws CreationException
	{
		// 1) Does it already exist? If so, no need to do anything else
		for (CMarker cm : markers)
			if (cm.marker.getName().equals(name))
				throw new CreationException(CreationException.NOT_UNIQUE, name);
		
		// 2) Otherwise, add it
		return addMarker(new Marker(name, alleleCount)).marker;
	}
	
	// Returns the Marker with the given (safe) name.
	public CMarker getMarkerBySafeName(String name)
	{
		int index = Integer.parseInt(name.substring(3));
		return markers.get(index);
		
//		for (CMarker cm : markers)
//			if (cm.safeName.equals(name))
//				return cm;
		
//		return null;
	}
	
	// Removes a marker from this group (also reindexes the group)
	public void removeMarker(CMarker toRemove)
	{
		markers.remove(toRemove);
		
		int i = 0;
		for (CMarker cm: markers)
			cm.setSafeName(i++);
	}
		
	// Returns the actual marker name for the marker with the given safe name
	public String getMarkerName(String safeName)
	{
		if (safeName.equals(""))
			return "";
			
		return getMarkerBySafeName(safeName).marker.getName();
	}
	
	// Returns the marker at the given index
	public CMarker getMarker(int index) { return markers.get(index); }
		
	public Vector<CMarker> getMarkers() { return markers;}
	
	public LinkedList<Object> getResults() { return results; }
	
	public void addAnovaResults(AnovaResult newAnova)
	{
		newAnova.setName("Anova " + (++anovaCount));
		results.add(newAnova);
	}
	
	public void addCluster(Cluster newCluster)
	{
		newCluster.setName("Cluster Analysis " + (++clusterCount));
		results.add(newCluster);
	}
	
	public void addOrderedResult(OrderedResult order)
	{
		order.setName("Ordered Analysis " + (++orderedCount));
		results.add(order);
	}
	
	// Returns a count of the number of alleles in this dataset (which will be
	// more than the number of markers if SSR data is involved)
	public int getAlleleCount()
	{
		int count = 0;		
		for (CMarker cm : markers)
			count += cm.marker.getAlleleCount();
		
		return count;
	}
	
	// Uses the first Marker in the set to determine how many individuals there
	// are
	public int getIndividualCount()
	{
		if (markers.size() > 0)
			return markers.get(0).marker.getAllele(0).getStateCount();
		return 0;
	}
	
	// Return a friendly-form String saying how many markers are in this group
	public String getMarkerCountString()
	{
		return markers.size() + " marker" + (markers.size() == 1 ? "" : "s");
	}
	
	// Returns the number of markers that are currently selected
	public int getSelectedMarkerCount()
	{
		int count = 0;
		for (CMarker cm: markers)
			if (cm.checked)
				count++;
		
		return count;
	}
	
	// Returns a new LinkageGroup that contains the same markers as this group
	// (with only the selected markers if needbe)
	public LinkageGroup getClonedLinkageGroup(boolean selectedOnly, boolean maintainNames)
	{
		LinkageGroup lGroup = new LinkageGroup(name);
		
		for (CMarker cm: markers)
			if (selectedOnly && cm.checked || !selectedOnly)
			{
				if (maintainNames)
					lGroup.addMarker(cm);
				else
					lGroup.addMarker(cm.marker);
			}
		
		return lGroup;
	}
	
	public void verify()
		throws CreationException
	{
		long s = System.currentTimeMillis();
		
		if (markers.size() > 800)
			throw new CreationException(CreationException.TOO_MANY_LOCI);
		
		if (getIndividualCount() > 300)
			throw new CreationException(CreationException.TOO_MANY_INDV);
		
		for (CMarker cm: markers)
			cm.marker.verifyAlleles();
		
		long e = System.currentTimeMillis();
		System.out.println("Dataset verified in " + (e-s) + "ms");
	}
}
package gui.map;

import java.util.*;
import java.text.*;

import analyses.order.*;
import data.*;

public class MapCreator
{
	private OrderedResult order;
//	private LinkageAnalysis analysis;
	private LinkageGroup[] lGroups;
	
	float totalDistance;

	GMarker[] chromoAll = null;
	Vector<GMarker[]> chromosomes = null;
	
	public MapCreator(OrderedResult order, LinkageGroup[] lGroups)
	{
		this.order = order;
		this.lGroups = lGroups;
		
		calculateDistances();
		populateChromosomes();
		
		for (int i = 0; i < chromosomes.size(); i++)
		{
			GMarker[] markers = chromosomes.get(i);
//			markers = orderChromosome(markers, i);
			setupInternalReferences(markers);
		}
	}
		
	private void calculateDistances()
	{
		totalDistance = order.getDistanceTotal();
		System.out.println("totalDistance = " + totalDistance);
		
		LinkageGroup lGroup = order.getLinkageGroup();		
		chromoAll = new GMarker[lGroup.getMarkerCount()];
		
		Vector<CMarker> markers = lGroup.getMarkers();
		Vector<Float> distances = order.getDistances();
		
		float cmValue = 0, aValue = 0;
		for (int i = 0; i < chromoAll.length; i++)
		{
			CMarker mkr = markers.get(i);
			chromoAll[i] = new GMarker(mkr, "" + mkr);
			
			if (i > 0)
			{
				float dist = distances.get(i-1);
				float aPos = dist / totalDistance;
				
				aValue  += aPos;
				cmValue += dist;
			}
			
			chromoAll[i].aPos = aValue;
			chromoAll[i].cm = cmValue;		
			
			System.out.println(chromoAll[i].name + " at " + chromoAll[i].cm + " (" + chromoAll[i].aPos + "%)");
		}
		
// all markers to top/bottom test		
//		float cm = chromoAll[chromoAll.length-1].aPos;
//		float cm = chromoAll[0].aPos;
//		for (int i = chromoAll.length-5; i < chromoAll.length-1; i++)
//		{
//			chromoAll[i].aPos = cm;
//		}
	}
	
	private void populateChromosomes()
	{		
		// Take the markers from each of the chromosomes produced by the analysis
		// and put them into the seperate GMarker arrays (one for each chromosome)
		chromosomes = new Vector<GMarker[]>(lGroups.length+1);
		chromosomes.add(chromoAll);
		
		// For each chromosome
		for (int i = 0; i < lGroups.length; i++)
		{
			// Create it...
			GMarker[] chromo = new GMarker[lGroups[i].getMarkerCount()];
			chromosomes.add(chromo);
						
			// Populate it...
			Vector<CMarker> markers = lGroups[i].getMarkers();
			for (int j = 0; j < markers.size(); j++)
			{
				CMarker cm = markers.get(j);
				
				// By finding the matching GMarker for this CMarker
				for (int k = 0; k < chromoAll.length; k++)
					if (cm.marker.getName().equals(chromoAll[k].name))
					{
						chromo[j] = chromoAll[k].getClone();
						break;
					}
			}
		}
	}
	
/*	private void populateChromosomes()
	{		
		// Take the markers from each of the chromosomes produced by the analysis
		// and put them into the seperate GMarker arrays (one for each chromosome)
		LinkageGroup[] lGroups = analysis.getGroups();
		chromosomes = new Vector<GMarker[]>(lGroups.length+1);
		chromosomes.add(chromoAll);
		
		// For each chromosome
		for (int i = 0; i < lGroups.length; i++)
		{
			// Create it...
			GMarker[] chromo = new GMarker[lGroups[i].getMarkerCount()];
			chromosomes.add(chromo);
			
			System.out.println();
			
			// Populate it...
			Vector<CMarker> markers = lGroups[i].getMarkers();
			for (int j = 0; j < markers.size(); j++)
			{
				CMarker cm = markers.get(j);
				System.out.println("pop: " + cm);
				
				// By finding the matching GMarker for this CMarker
				for (int k = 0; k < chromoAll.length; k++)
					if (cm.marker.getName().equals(chromoAll[k].name))
					{
						chromo[j] = chromoAll[k].getClone();
						break;
					}
			}
		}
	}
*/
	
	// Orders the markers in this chromosome. We have to sort on original marker
	// order to ensure markers with the same cm score retain the correct position
	private GMarker[] orderChromosome(GMarker[] unsorted, int position)
	{
		Vector<CMarker> oMarkers = order.getLinkageGroup().getMarkers();
		GMarker[] sorted = new GMarker[unsorted.length];
		
		int i = 0;
		for (CMarker cm: oMarkers)
		{
			for (int j = 0; j < unsorted.length; j++)
			{
				if (unsorted[j].name.equals(cm.marker.getName()))
				{
					sorted[i++] = unsorted[j];
					break;
				}
			}
		}
		
		chromosomes.setElementAt(sorted, position);
		return sorted;
	}
	
	private void setupInternalReferences(GMarker[] markers)
	{
		System.out.println();
		GMarker prev = null;
		for (int i = 0; i < markers.length; i++)
		{
			System.out.println(gui.Prefs.d4.format(markers[i].aPos)
				+ ": " + markers[i] + " linked to " + prev);
			
			markers[i].prev = prev;
			prev = markers[i];
		}
	}
}
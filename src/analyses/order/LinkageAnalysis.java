package analyses.order;

import java.util.*;

import data.*;

public class LinkageAnalysis
{
	private OrderedResult order;
	private Vector<String[]> rows;
	private int parent;
	
	private LinkageGroup[] groups;
	
	public LinkageAnalysis(OrderedResult order)
	{
		this.order = order;
	}
	
	public LinkageAnalysis(OrderedResult order, int p)
	{
		this.order = order;
		rows = order.rows;
		parent = p;
	}
	
	public void run()
	{
		// Contains the set of ordered markers
		LinkageGroup lGroup =
			order.getLinkageGroup().getClonedLinkageGroup(false, true);
		
		// The four groups we want to split it up into
		groups = new LinkageGroup[4];
		for (int i = 0; i < groups.length; i++)
			groups[i] = new LinkageGroup("Chromosome " + (i+1));
		
		for (int i = 0; i < rows.size(); i++)
		{
			String[] str = rows.get(i);
			String data = str[parent-1];			
			
			CMarker cm = lGroup.getMarkers().get(i);
			
			for (int c = 0; c < 4; c++)
				if (data.charAt(c) != '0')
					groups[c].addMarker(cm.marker);
		}
	}
	
	public LinkageGroup[] getGroups() { return groups; }
	
/*	public void run()
	{
		// Contains the set of ordered markers
		LinkageGroup lGroup =
			order.getLinkageGroup().getClonedLinkageGroup(false);
		
		// The four groups we want to split it up into
		groups = new LinkageGroup[4];
		for (int i = 0; i < groups.length; i++)
			groups[i] = new LinkageGroup("Chromosome " + (i+1));
		
		
		// Now...for each group, see what markers should be added to it
		for (int i = 0; i < groups.length; i++)
		{
			if (lGroup.getMarkerCount() > 0)
			{
				CMarker cmMiddle = getMiddleMarker(lGroup, groups[i]);
				processChromosome(cmMiddle, lGroup, groups[i]);
			}
		}

		print(lGroup);
	}
	
	private CMarker getMiddleMarker(LinkageGroup lGroup, LinkageGroup chromo)
	{
		System.out.println("Processing " + chromo.getName());		
		int middleMarker = (int) (lGroup.getMarkerCount() / 2);
		
		// Pull the middle marker out of the original group
		CMarker cmMiddle = lGroup.getMarkers().remove(middleMarker);
		// And add this marker to the chromosome group
		chromo.addMarker(cmMiddle.marker);
		
		System.out.println("  middle is " + cmMiddle);
		return cmMiddle;
	}
	
	private void processChromosome(CMarker cmMiddle, LinkageGroup lGroup, LinkageGroup chromo)
	{		
		// Check this marker against all the others by comparing their phases
		for (PhasePair pp: order.getPhasePairs())
		{
			CMarker cmOther = pp.getPartner(cmMiddle);
			if (cmOther == null)
				continue;
			
			System.out.println("  comparing with " + cmOther);
			
			if (compare(pp))
//			if (true)
			{
				removeMarker(cmOther, lGroup);
				chromo.addMarker(cmOther.marker);
				
				System.out.println("    added " + cmOther);
			}
		}
	}
	
	private boolean compare(PhasePair pp)
	{
		if (pp.rfq < 0.4 && pp.lod > 8)
			return true;
		else
			return false;
	}
	
	// Removes the given marker from the given linkage group
	private void removeMarker(CMarker cmRemove, LinkageGroup lGroup)
	{
		Vector<CMarker> markers = lGroup.getMarkers();
		
		for (int i = 0; i < markers.size(); i++)
			if (cmRemove.marker == markers.get(i).marker)
			{
				markers.remove(i);
				return;
			}
	}

	private void print(LinkageGroup lGroup)
	{
		// Print out each marker in their original order
		System.out.println();
		for (CMarker cm: order.getLinkageGroup().getMarkers())
		{
			int group = getGroup(cm);
			if (group == -1) continue;
			
			// spacing...
			int i = 0;
			for (; i < group; i++)
			{
				for (int space = 0; space < 17; space++)
					if (space == 0)
						System.out.print(".");
					else
						System.out.print(" ");
			}
			
			System.out.print(cm.marker.getName(17));
			
			for (i++; i < groups.length; i++)
			{
				for (int space = 0; space < 17; space++)
					if (space == 0)
						System.out.print(".");
					else
						System.out.print(" ");
			}
			System.out.println();
		}
		
		if (lGroup.getMarkerCount() == 0)
			System.out.println("\nNo left over markers");
		else
		{
			System.out.println("\nMarkers left:");
			for (CMarker cm: lGroup.getMarkers())
				System.out.println(" " + cm);
		}
	}
	
	private int getGroup(CMarker cm)
	{
		for (int i = 0; i < groups.length; i++)
			if (contains(cm, groups[i]))
				return i;
		
		return -1;
	}
	
	private boolean contains(CMarker cmarker, LinkageGroup lGroup)
	{
		for (CMarker cm: lGroup.getMarkers())
			if (cm.marker == cmarker.marker)
				return true;
		
		return false;
	}
*/
}
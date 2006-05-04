package data;

import java.io.*;
import java.util.*;

import gui.*;

import doe.*;

public class OrderedResult implements Serializable
{
	static final long serialVersionUID = 481001104925097199L;
	
	private Summary summary;
	private String name;
	private boolean simOK;
	
	// A linkage group that stores the final ordered list of markers
	private LinkageGroup lGroup = new LinkageGroup(null);
	// and the intermarker distances
	private Vector<Float> distances = new Vector<Float>();
	
	// Stores a list of all the phase pairs found during TwoPoint's run
//	private LinkedList<PhasePair> phasePairs = new LinkedList<PhasePair>();
	private PhasePair[][] ppData;
	
	public Vector<String[]> rows;
	
	private Vector<QTLResult> qtlResults;
	private int qtlCount = 0;
	private Vector<LinkageMapGraph> maps;
	private int mapCount = 0;

	public OrderedResult(int mkrCount)
	{
		ppData = new PhasePair[mkrCount][mkrCount];
	}
	
	// Resets the ordering - used when simanneal is run *after* a custom order
	// has been created. The current order is passed to simanneal as its initial
	// order, but it must then be blanked, as simanneal will create a new result
	public void reset()
	{
		lGroup = new LinkageGroup(null);
		distances = new Vector<Float>();
	}

	public void setSummary(Summary summary)
		{ this.summary = summary; }
	
	public Summary getSummary()
		{ return summary; }
	
	public void setSimOK()
		{ simOK = true; }
	
	public boolean isSimOK()
		{ return simOK; }
	
	public Vector<QTLResult> getQTLResults()
		{ return qtlResults; }
	
	public Vector<LinkageMapGraph> getLinkageMaps()
		{ return maps; }
	
	public void addQTLResult(QTLResult newResult)
	{
		if (qtlResults == null)
			qtlResults = new Vector<QTLResult>();
		
		newResult.setName("QTL Analysis " + (++qtlCount));
		qtlResults.add(newResult);
	}
	
	public void addLinkageMapGraph(LinkageMapGraph newMap)
	{
		if (maps == null)
			maps = new Vector<LinkageMapGraph>();
		
		newMap.setName("Linkage Map " + (++mapCount));
		maps.add(newMap);
	}
	
	public LinkageGroup getLinkageGroup()
		{ return lGroup; }
	
//	public LinkedList<PhasePair> getPhasePairs()
//		{ return phasePairs; }
	
	public PhasePair[][] getPhasePairArray()
		{ return ppData; }
	
	// Adds the given marker to the list of ordered markers
	public void addMarker(CMarker cm)
		{ lGroup.addMarker(cm);	}
	
	public void addDistance(float distance)
	{
		// TODO: Can we do this?
		// Ensure no distance is negative
		if (distance < 0)
			distance *= -1;
		
		// Multiply by 100 to get the distance into centiMorgans
		distances.add(distance * 100);
		System.out.println("  " + distance);
	}
	
	// Returns the sum of all the inter-marker distances
	public float getDistanceTotal()
	{
		float total = 0;
		for (float d: distances)
			total += d;
		
		return total;
	}
	
	public boolean doesRowsContainZeros()
	{
		for (String[] s: rows)
			if (s[0].equals("0000") && s[1].equals("0000"))
				return true;
		
		return false;
	}
	
	public Vector<Float> getDistances() { return distances; }
	
	// Adds a new PhasePair
	public void addPhasePair(CMarker cm1, CMarker cm2, String p1, String p2)
	{
		PhasePair pp = new PhasePair(cm1, cm2, p1, p2);		
//		phasePairs.add(pp);
		
		int i = cm1.getIndex();
		int j = cm2.getIndex();
				
		ppData[i][j] = ppData[j][i] = pp;
	}
	
	// Sets a PhasePair's recombination frequency and LOD score
	public void setPhasePairValues(CMarker cm1, CMarker cm2, float rfq, float lod)
	{
		int i = cm1.getIndex();
		int j = cm2.getIndex();
		
		ppData[i][j].rfq = rfq;
		ppData[i][j].lod = lod;
		
		
		// Find this PhasePair...
/*		for (PhasePair pp: phasePairs)
		{
			if (pp.compare(cm1, cm2) > 0)
			{
				// ...and set their values
				pp.rfq = rfq;
				pp.lod = lod;
				break;
			}
		}
*/
	}
	
	public void setName(String name)
		{ this.name = name; }
	
	public String toString()
		{ return name; }

/*	
	// Collates the ordered list of markers with the list of phases
	public void orderPhases()
	{
		long s = System.currentTimeMillis();
		
		phases.append("First pass\n\n");
		
		ListIterator<CMarker> itor = lGroup.getMarkers().listIterator(0);
		while (itor.hasNext())
		{
			CMarker cm1 = itor.next();
			// Quit if we've reached the 2nd last marker in the list
			if (itor.hasNext() == false) break;
			CMarker cm2 = itor.next();
			
			// Find the phase pair for these two markers
			PhasePair pp = null;
			boolean reverse = false;
			for (PhasePair phasePair: phasePairs)
			{
				int comparison = phasePair.compare(cm1, cm2);
				if (comparison == 1)
					pp = phasePair;
				else if (comparison == 2)
				{
					pp = phasePair;
					reverse = true;
				}
			}
			
			if (reverse == false)
			{
				phases.append(pp.cm1.safeName + ": " + pp.cm1.marker.getName(20) + " " + pp.phase1 + "\n");
				phases.append(pp.cm2.safeName + ": " + pp.cm2.marker.getName(20) + " " + pp.phase2 + "\n");
			}
			else
			{
				phases.append(pp.cm2.safeName + ": " + pp.cm2.marker.getName(20) + " " + pp.phase2 + "\n");
				phases.append(pp.cm1.safeName + ": " + pp.cm1.marker.getName(20) + " " + pp.phase1 + "\n");				
			}
			phases.append("\n");
			
			// Remember to step back one element before proceeding
			itor.previous();
		}
		
		System.out.println();
		System.out.println("Time: " + (System.currentTimeMillis() - s));
		
		phases.append("\nAll pairs:\n\n");
		for (PhasePair pp: phasePairs)
		{
			phases.append(pp.cm1.safeName + ": " + pp.cm1.marker.getName(20) + " " + pp.phase1 + "\n");
			phases.append(pp.cm2.safeName + ": " + pp.cm2.marker.getName(20) + " " + pp.phase2 + "\t\t");
			phases.append(pp.rfq + "\t" + pp.lod);
			phases.append("\n\n");
		}
	}
*/
	
	////////////////////////////////////////////////////////////////////////////
	public StringBuffer tp1 = new StringBuffer();
	public StringBuffer tp2 = new StringBuffer();

	public StringBuffer sm1 = new StringBuffer();
	public StringBuffer sm2 = new StringBuffer();
	
	public StringBuffer phases = new StringBuffer();
	
	public void setTwoPointResults(File f1, File f2)
		throws Exception
	{
		readFile(tp1, f1);
		readFile(tp2, f2);
	}
	
	public void setSimAnnealResults(File f1, File f2)
		throws Exception
	{
////	readFile(sm1, f1);
		readFile(sm2, f2);
	}
	
	private void readFile(StringBuffer str, File file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String line = in.readLine();
		while (line != null)
		{
			str.append(line + "\n");
			line = in.readLine();
		}
			
		in.close();
	}
}

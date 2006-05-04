package analyses.order;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

class ProcessResults extends Thread
{
	// Is the thread still running?
	boolean isRunning = true;
	// Have any errors occurred?
	boolean error = false;
	
	private LinkageGroup lGroup;
	private OrderedResult order;
	private File simOut, twoOut, twoPwd;
	
	ProcessResults(LinkageGroup lGroup, OrderedResult order, File simOut,
		File twoOut, File twoPwd)
	{
		this.lGroup = lGroup;
		this.order = order;
		this.simOut = simOut;
		this.twoOut = twoOut;
		this.twoPwd = twoPwd;
		
		start();
	}
	
	public void run()
	{
		BufferedReader in = null;
		
		try
		{
			// Process the results from sim.out
			try
			{
				in = new BufferedReader(new FileReader(simOut));			
				processSimOut(in);
				in.close();
				
				order.setSimOK();
			}
			catch (Exception e)
			{
//				for (CMarker cm: lGroup.getMarkers())
//					order.addMarker(cm);
//				error = true;
			}
			
			// Process the results from twopoint.out
			in = new BufferedReader(new FileReader(twoOut));
			System.out.print("Processing .out ");
			processTwoOut(in);
			System.out.println("done");
			in.close();
			
			// Process the results from twopoint.pwd
			in = new BufferedReader(new FileReader(twoPwd));
			System.out.print("Processing .pwd ");
			processTwoPwd(in);
			System.out.println("done");
			in.close();
			
//			order.orderPhases();
		}
		catch (Exception e)
		{
			error = true;
			e.printStackTrace(System.out);
			MsgBox.msg("Unable to complete ordering due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	private void processSimOut(BufferedReader in)
		throws Exception
	{
		String str = in.readLine();
		
		// Read down the file until the final ordering is found
		while (str.startsWith("  ****   RESULTS AFTER SA   ****") == false)
			str = in.readLine();
		str = in.readLine();
		
		while (str.length() > 0)
		{
			str = in.readLine();
			
			// Add this marker to the OrderedResult's list
			if (str.length() > 0)
				order.addMarker(lGroup.getMarkerBySafeName(str.trim()));
		}
		
		// Now find the distances
		while (str.startsWith("  DISTANCE") == false)
			str = in.readLine();
		str = in.readLine();
		
		while (str.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(str);
			float distance = Float.parseFloat(st.nextToken());
			
			order.addDistance(distance);
			
			str = in.readLine();
		}
	}
	
	private void processTwoOut(BufferedReader in)
		throws Exception
	{
		String str = in.readLine();
		while (str != null)
		{
			if (str.startsWith(" The most likely phase"))
				readPhase(in);
				
			str = in.readLine();
		}
	}
	
	private void readPhase(BufferedReader in)
		throws Exception
	{
		StringTokenizer st1 = new StringTokenizer(in.readLine());
		StringTokenizer st2 = new StringTokenizer(in.readLine());
		
		// Read (and find) the two marker names
		CMarker marker1 = lGroup.getMarkerBySafeName(st1.nextToken());
		CMarker marker2 = lGroup.getMarkerBySafeName(st2.nextToken());
		
		// Read the phase strings
		String phase1 = st1.nextToken() + " " + st1.nextToken();
		String phase2 = st2.nextToken() + " " + st2.nextToken();
		
		order.addPhasePair(marker1, marker2, phase1, phase2);
	}
	
	private void processTwoPwd(BufferedReader in)
		throws Exception
	{
		in.readLine();		
		String str = null;

		while ((str = in.readLine()) != null && str.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(str);
			
			// Read (and find) the two marker names
			CMarker marker1 = lGroup.getMarkerBySafeName(st.nextToken());
			CMarker marker2 = lGroup.getMarkerBySafeName(st.nextToken());
		
			// Read the numerical values
			float rfq = Float.parseFloat(st.nextToken());
			float lod = Float.parseFloat(st.nextToken());
			
			order.setPhasePairValues(marker1, marker2, rfq, lod);
		}
	}
	
	boolean verifyPhaseData(PhasePair[][] ppData, File file)
	{
		String failed = new String();
		
		for (int i = 0; i < lGroup.getMarkerCount(); i++)
			for (int j = i+1; j < lGroup.getMarkerCount(); j++)
				if (i != j && ppData[i][j] == null)
				{
					failed +=
						lGroup.getMarkerBySafeName("mkr" + Prefs.i3.format(i))
						+ " and " +
						lGroup.getMarkerBySafeName("mkr" + Prefs.i3.format(j))
						+ Prefs.sepL;
				}
		
		if (failed.length() > 0)
		{
			try
			{				
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(failed);
				out.close();
			}
			catch (Exception e) {}
			
			MsgBox.msg("The two-point analysis failed because one or more "
				+ "pairs of markers could not be processed. A list of these\n"
				+ "markers has been written to " + file + "\nYou may wish to "
				+ "remove them from future analyses.", MsgBox.ERR);
			
			return false;
		}
		else
			return true;
	}
	
	// Performs a pre-TwoPoint check on the linkage group to ensure that only
	// markers present in the same parent (or both parents) have been selected
	static boolean verifyForTwoPoint(LinkageGroup lGroup)
	{
		int parent = 0;
		
		System.out.println("Marker count: " + lGroup.getMarkerCount());
		
		for (CMarker cm: lGroup.getMarkers())
		{
			if (cm.marker.isPresentInParent(1) && cm.marker.isPresentInParent(2))
				continue;
			
			int isPresentIn = (cm.marker.isPresentInParent(1) ? 1 : 2);
			if (parent == 0)
				parent = isPresentIn;
				
			if (isPresentIn != parent)
			{
				MsgBox.msg("This analysis can only be run on markers whose "
					+ "alleles are present in the same parent. Please select "
					+ "markers\nthat are either present in both parents, or are "
					+ "only present in parent 1 OR parent 2.", MsgBox.ERR);
				return false;
			}
		}
		
		return true;
	}
}
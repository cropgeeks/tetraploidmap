package analyses.order.ripple;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

class ProcessRippleResults extends Thread
{
	// Is the thread still running?
	boolean isRunning = true;
	// Have any errors occurred?
	boolean error = false;
	
	private LinkageGroup lGroup;
	private OrderedResult order;
	private File rippler;
	
	ProcessRippleResults(LinkageGroup lGroup, OrderedResult order, File rippler)
	{
		this.lGroup = lGroup;
		this.order = order;
		this.rippler = rippler;
		
		start();
	}
	
	public void run()
	{
		BufferedReader in = null;
		
		// Process the results from rippler.txt
		try
		{
			in = new BufferedReader(new FileReader(rippler));			
			process(in);
			in.close();
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
	
	private void process(BufferedReader in)
		throws Exception
	{
		String str = in.readLine();
		
		// Read down the file until the final ordering is found
		while (str.startsWith(" MARKER") == false)
			str = in.readLine();
		
		str = in.readLine();
		
		// Now for each remaining line		
		for (int count = 0; str != null; count++, str = in.readLine())
		{
			StringTokenizer st = new StringTokenizer(str);
			
			String name = st.nextToken();
			order.addMarker(lGroup.getMarkerBySafeName(name));
			System.out.println("Reading results for " + name);
			
			String cDist = st.nextToken();
			
			// Distances are intermarker, so we don't need the first one
			if (count > 0)
			{
				float distance = Float.parseFloat(st.nextToken());
				order.addDistance(distance);
			}
		}
	}
}
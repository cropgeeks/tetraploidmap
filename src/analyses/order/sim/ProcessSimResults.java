package analyses.order.sim;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

class ProcessSimResults extends Thread
{
	// Is the thread still running?
	boolean isRunning = true;
	// Have any errors occurred?
	boolean error = false;
	
	private LinkageGroup lGroup;
	private OrderedResult order;
	private File simOut;
	
	ProcessSimResults(LinkageGroup lGroup, OrderedResult order, File simOut)
	{
		this.lGroup = lGroup;
		this.order = order;
		this.simOut = simOut;
		
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
}
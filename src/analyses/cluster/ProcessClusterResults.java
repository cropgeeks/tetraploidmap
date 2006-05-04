package analyses.cluster;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

class ProcessClusterResults extends Thread
{
	// Is the thread still running?
	boolean isRunning = true;
	// Have any errors occurred?
	boolean error = false;
	// Clustering method in use
	int method;
	// Processing phase (1 or 2)
	int phase = 1;
	
	// The linkage group that was processed
	private LinkageGroup lGroup;
	// The cluster group that will be created from the analysis
	private Cluster cluster;

	// Stores results from the average linkage clustering
	LinkedList<String> avList = new LinkedList<String>();
	// Stores results from the single linkage clustering
	LinkedList<String> snList = new LinkedList<String>();
	
	private File file;
	
	ProcessClusterResults(LinkageGroup lGroup, Cluster cluster, File file, int method/*, int phase*/)
	{
		this.lGroup = lGroup;
		this.cluster = cluster;
		this.file = file;
		this.method = method;
//		this.phase = phase;
		
		start();
	}
		
	// This method must run in two phases (over two methods)
	//  Phase 1, method 1 must read the snDendrogram
	//  Phase 1, method 2 must read both dendrograms
	//  Phase 2 for both methods is the same - read the linkage groups
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			
//			if (phase == 1)
			{			
				processDendrogram(in, snList);
				if (method == 2)
				{
					processDendrogram(in, avList);
				}

				Dendrogram snDendrogram = null; 
				Dendrogram avDendrogram = null;
				
				// Single linkage always done...
				snDendrogram = new Dendrogram(lGroup, snList);
				cluster.setSnLnkDendrogram(snDendrogram);
				
				// Average linkage only done if 2nd clustering method in use...
				if (method == 2)
				{
					avDendrogram = new Dendrogram(lGroup, avList);
					cluster.setAvLnkDendrogram(avDendrogram);
				}
			}
//			else if (phase == 2)
//			{
				processLinkageGroups(in);
//			}
			
			in.close();			
						
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("The clustering process has failed due to the following "
				+ "error:\n" + e, MsgBox.ERR);
		}
				
		isRunning = false;
		System.out.println("proc2: size is " + cluster.getGroups().size());
	}
	
	private void processDendrogram(BufferedReader in, LinkedList<String> list)
		throws IOException
	{
		// Determine the terminating string to match the dendrogram against
		String term = null;
		if (list == snList)	term = " Single linkage clustering";
		else term = " Average linkage clustering";
			
		String line = in.readLine();
		while (line != null && !line.equals(term))
			line = in.readLine();

		line = in.readLine();
		line = in.readLine();
		while (line != null && line.length() > 0)
		{
			list.add(line);
			line = in.readLine();
		}
	}

	private void processLinkageGroups(BufferedReader in)
		throws IOException
	{
		int groupCount = 1;
		
		String line = in.readLine();
		while (line != null)
		{
			if (line.startsWith(" Linkage group"))
			{
				LinkageGroup newGroup = processLinkageGroup(in, groupCount++);
				if (newGroup.getMarkerCount() > 0)
					cluster.addLinkageGroup(newGroup);
			}
			
			line = in.readLine();
		}
	}
	
	private LinkageGroup processLinkageGroup(BufferedReader in, int groupCount)
		throws IOException
	{
		LinkageGroup group = new LinkageGroup("Group " + groupCount);
		
		String line = in.readLine();
		while (line.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(line);
			
			String markerName = st.nextToken();
			int alleleCount = Integer.parseInt(st.nextToken());
				
			CMarker cm = lGroup.getMarkerBySafeName(markerName);
			CMarker added = group.addMarker(cm.marker);
			
			for (int a = 0; a < alleleCount; a++)
				line = in.readLine();
			
			if ((line = in.readLine()) == null)
				break;
		}
		
		return group;
	}
}
package analyses.simmatch;

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
	private File nameFile, parentFile;
	
	ProcessResults(LinkageGroup lGroup, File nameFile, File parentFile)
	{
		this.lGroup = lGroup;
		this.nameFile = nameFile;
		this.parentFile = parentFile;
		
		start();
	}
	
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(nameFile));
			
			processNames(in);
			in.close();
			
			in = new BufferedReader(new FileReader(parentFile));
			processParentData(in);
			in.close();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("SimMatch was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	private void processNames(BufferedReader in)
		throws Exception
	{
		String str = in.readLine();
		while (str != null && str.length() > 0)
		{
			String name = str.trim();
			
			String markerName = name.substring(name.indexOf("mkr"));
			String prefix = name.substring(0, name.lastIndexOf("_"));
			
			Marker marker = lGroup.getMarkerBySafeName(markerName).marker;
			marker.setPrefix(prefix);
			
			str = in.readLine();
		}
	}
	
	private void processParentData(BufferedReader in)
		throws Exception
	{
		int dataCount = 0;
				
		String str = in.readLine();
		while (str != null)
		{
			if (str.equals(" Summary of significant linkages"))
				processLinkages(++dataCount, in);
			
			str = in.readLine();
		}
	}
	
	private void processLinkages(int dataCount, BufferedReader in)
		throws Exception
	{
		System.out.println("Reading dataset: " + dataCount);
		
		String str = in.readLine();
		while (str != null && str.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(str);
			
			Marker m1 = getMarker(st.nextToken());
			Marker m2 = getMarker(st.nextToken());
			float chi = Float.parseFloat(st.nextToken());
			float sig = Float.parseFloat(st.nextToken());
			String phase = null;
			if (st.hasMoreTokens())
				phase = st.nextToken();
			
			// Add the data for marker 1
			SigLinkage s1 = new SigLinkage(m2, chi, sig, phase);
			m1.getSimMatchData().add(getSigLinkage(m1, dataCount), s1);
			
			// Add the data for marker 2
			SigLinkage s2 = new SigLinkage(m1, chi, sig, phase);
			m2.getSimMatchData().add(getSigLinkage(m2, dataCount), s2);
						
			
			str = in.readLine();
		}
	}
	
	// Finds a marker based on its safename or prefix_safeName
	private Marker getMarker(String name)
	{
		if (name.startsWith("mkr"))
			return lGroup.getMarkerBySafeName(name).marker;
		else
		{
			name = name.substring(name.indexOf("mkr"));
			return lGroup.getMarkerBySafeName(name).marker;
		}
	}
	
	// Returns the appropriate vector of data for the given marker and the given
	// group - groups are 1-6
	private Vector<SigLinkage> getSigLinkage(Marker marker, int dataCount)
	{
		switch (dataCount)
		{
			case 1: return marker.getSimMatchData().p1A;
			case 2: return marker.getSimMatchData().p2A;
			
			case 3: return marker.getSimMatchData().p1B;			
			case 4: return marker.getSimMatchData().p2B;
			
			case 5: return marker.getSimMatchData().p1C;
			case 6: return marker.getSimMatchData().p2C;
		}
		
		return null;
	}
}
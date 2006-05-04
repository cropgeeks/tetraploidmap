package analyses.anova;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

class ProcessAnovaResults extends Thread
{
	// Have any errors occurred?
	boolean error = false;
	
	private LinkageGroup lGroup;
	private AnovaResult results;
	private File file;
	
	ProcessAnovaResults(File f, AnovaResult r, LinkageGroup l)
	{
		file = f;
		results = r;
		lGroup = l;
		
		process();
	}
	
	public void process()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			processTraits(in);
			in.close();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("Anova was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
			
			e.printStackTrace(System.out);
		}
		
		System.out.println("processed");
	}
	
	private void processTraits(BufferedReader in)
		throws Exception
	{
		String line = in.readLine();
		while (line != null)
		{
			if (line.startsWith(" trait ="))
				line = processTrait(in, line);
			else				
				line = in.readLine();
		}
	}
	
	private String processTrait(BufferedReader in, String line)
		throws Exception
	{
		String traitName = line.substring(9);
		AnovaTraitResult r = new AnovaTraitResult(traitName);
		results.addResult(r);
				
		line = in.readLine();
		line = in.readLine();
		while (line != null && line.length() > 0)
		{
			if (line.trim().length() == 0)
				break;
			
			String markerName = line.substring(0, 6);
			Marker m = lGroup.getMarkerBySafeName(markerName).marker;
			
			String data = line.substring(22);			
			r.addMarker(m, data);
			
			line = in.readLine();
		}
		
		System.out.println(r.getTraitName() + " has " + r.getResultCount());
		
		return line;
	}
}

package data;

import java.io.*;
import java.text.*;

// Class that stores summary information on an analysis run
public class Summary implements Serializable
{
	static final long serialVersionUID = 5663087561843697420L;
	
	// How long did the analysis take to run
	private long time;
	
	// What was the original LinkageGroup that it run on
	private LinkageGroup oGroup;
	
	// Stores a list of markers that were selected at the time of the run
	private LinkageGroup sGroup;
		
	public Summary(LinkageGroup oGroup, long time)
	{
		this.oGroup = oGroup;
		this.time = time;
		
		sGroup = oGroup.getClonedLinkageGroup(true, false);
	}
	
	public LinkageGroup getOriginalGroup() { return oGroup; }
	
	public LinkageGroup getSelectedGroup() { return sGroup; }
	
	public String getTimeSummary()
	{
		if (time < 1000)
			return "Analysis ran in " + time + " milliseconds";
			
		else if (time < 60000)
		{
			long secs = (long) (time / 1000f);
			return "Analysis ran in " + secs + " second"
				+ (secs == 1 ? "" : "s");
		}
		
		else
		{
			float mins = ((float)time / 1000f / 60f);
			return "Analysis ran in " + new DecimalFormat("0.0").format(mins)
				+ " minute" + (mins == 1 ? "" : "s");
		}
		
	}
	
	public String getMarkersSummary()
	{
		return sGroup.getMarkerCount() + " of " + oGroup.getMarkerCount()
			+ " markers were selected at the time of the analysis";
	}
}
package data;

import java.io.*;
import java.util.*;
import java.text.*;

import gui.*;

public class AnalysisLog implements Serializable
{
	static final long serialVersionUID = 3154353755671453381L;
	
	private static DateFormat df1 = DateFormat.getDateInstance(DateFormat.MEDIUM);
	private static DateFormat df2 = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	
	private LinkedList<LogEntry> log = new LinkedList<LogEntry>();
	
	public void add(String entry)
	{
		long now = System.currentTimeMillis();
		String time = df1.format(now) + " " + df2.format(now);
		
		log.add(new LogEntry(time, entry));
		
		AppFrameMenuBar.aFileSave.setEnabled(true);
	}
	
	// Converts the log into a single String that can returned
	public String getLog()
	{
		StringBuffer buffer = new StringBuffer(1000);
		
		buffer.append("<font size=\"3\" face=\"Monospaced\">");
		for (LogEntry l: log)
			buffer.append("<p><b>" + l.time + "</b><br>" + l.entry + "</p>");
		buffer.append("</font>");
		
		return buffer.toString();
	}
	
	private class LogEntry implements Serializable
	{
		String time;
		String entry;
		
		LogEntry(String time, String entry)
		{
			this.time = time;
			this.entry = entry;
		}
		
		public String toString()
		{
			return time + " - " + entry;
		}
	}
}
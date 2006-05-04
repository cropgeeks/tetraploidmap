package analyses.order.twopoint;

import java.io.*;
import java.util.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunTwoPoint extends FortranRunner
{
	// How many lines have been processed?
	int count = 0;
	// What are the names of the two markers being processed
	String marker1 = "", marker2 = "";
	
	public void run()
	{				
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_twopoint_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new TwoPointCatcher(proc.getInputStream());
			new TwoPointCatcher(proc.getErrorStream());
			
			writer.println("twopoint");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("TwoPoint was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class TwoPointCatcher extends StreamCatcher
	{
		TwoPointCatcher(InputStream in) { super(in); }
		
		public void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);						
			}
			
			else if (line.startsWith(" L1"))
			{
				marker1 = line.substring(4).trim();
				count++;
			}
			else if (line.startsWith(" L2"))
			{
				marker2 = line.substring(4).trim();
				count++;
			}
			else if (line.length() > 5)
				System.out.println(line);
		}
	}
}
package analyses.simmatch;

import java.io.*;
import java.util.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunSimMatch extends FortranRunner
{
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_simmatch_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new SimMatchCatcher(proc.getInputStream());
			new SimMatchCatcher(proc.getErrorStream());
			
			writer.println("simmatch");
			writer.println("n");
			writer.println("n");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("SimMatch was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class SimMatchCatcher extends StreamCatcher
	{
		BufferedReader reader = null;
	
		SimMatchCatcher(InputStream in) { super(in); }
		
		protected void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);						
			}
			
//			System.out.println(line);
		}
	}
}
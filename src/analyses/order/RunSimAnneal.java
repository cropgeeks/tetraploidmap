package analyses.order;

import java.io.*;
import java.util.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunSimAnneal extends FortranRunner
{
	// How much has been processed?
	int count = 0;
	// Current temperature?
	float temp = -1;
		
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_simanneal_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new SimAnnealCatcher(proc.getInputStream());
			new SimAnnealCatcher(proc.getErrorStream());
			
			writer.println("sim");
			writer.println("n");
			writer.println("" + Prefs.sim_rt_value);
			writer.println("" + Prefs.sim_t_value);
			writer.println("" + Prefs.sim_nt_value);
			writer.println("" + Prefs.sim_eps_value);
			writer.println("1");
			writer.println("y");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("SimAnneal was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class SimAnnealCatcher extends StreamCatcher
	{
		SimAnnealCatcher(InputStream in) { super(in); }
		
		protected void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);
			}
			
			else if (line.startsWith("  CURRENT TEMPERATURE:"))
			{
				temp = Float.parseFloat(line.substring(22).trim());
				count++;
			}
			
			System.out.println(line);
		}
	}
}
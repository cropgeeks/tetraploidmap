package analyses.order.ripple;

import java.io.*;
import java.util.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunRipple extends FortranRunner
{
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_ripple_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new RippleCatcher(proc.getInputStream());
			new RippleCatcher(proc.getErrorStream());
			
			writer.println("ripple");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("Ripple was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class RippleCatcher extends StreamCatcher
	{
		RippleCatcher(InputStream in) { super(in); }
		
		protected void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);
			}
			
			System.out.println(line);
		}
	}
}
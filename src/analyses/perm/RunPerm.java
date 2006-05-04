package analyses.perm;

import java.io.*;
import java.util.*;
import javax.swing.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunPerm extends FortranRunner
{
	boolean fullModel;
	int position;
	
	public void run()
	{
		try
		{	
			if (fullModel)
				proc = Runtime.getRuntime().exec(Prefs.tools_perm_path, null,
					new File(Prefs.tools_scratch));
			else
				proc = Runtime.getRuntime().exec(Prefs.tools_permsimp_path, null,
					new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new PermCatcher(proc.getInputStream());
			new PermCatcher(proc.getErrorStream());
			
			writer.println("perm");
			if (fullModel)
				writer.println("0");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("Perm was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class PermCatcher extends StreamCatcher
	{
		BufferedReader reader = null;
	
		PermCatcher(InputStream in) { super(in); }
		
		protected void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);						
			}
			
			else if (line.startsWith(" CL"))
			{
				StringTokenizer st = new StringTokenizer(line);
				st.nextToken();
				
				position = (int) Float.parseFloat(st.nextToken());
			}
					
//			System.out.println(line);
		}
	}
}
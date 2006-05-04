package analyses.anova;

import java.io.*;
import java.util.*;
import javax.swing.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunAnova extends FortranRunner
{
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_anova_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new AnovaCatcher(proc.getInputStream());
			new AnovaCatcher(proc.getErrorStream());
			
			writer.println("anova");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("Anova was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class AnovaCatcher extends StreamCatcher
	{
		BufferedReader reader = null;
	
		AnovaCatcher(InputStream in) { super(in); }
		
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
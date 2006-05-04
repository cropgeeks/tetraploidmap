package analyses.qtl;

import java.io.*;
import java.util.*;
import javax.swing.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunQTL extends FortranRunner
{
	// How many lines have been processed?
	int indCount = 0;
	int stage = 1;
	int parent;
	
	RunQTL(int parent)
		{ this.parent = parent; }
	
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_qtl_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new QTLCatcher(proc.getInputStream());
			new QTLCatcher(proc.getErrorStream());
			
			writer.println("qtl");
			writer.println(""+parent);
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("QTL was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class QTLCatcher extends StreamCatcher
	{
		BufferedReader reader = null;
	
		QTLCatcher(InputStream in) { super(in); }
		
		protected void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);						
			}
			
			else if (line.startsWith(" Individual"))
			{
				StringTokenizer st = new StringTokenizer(line);
				st.nextToken();
				
				stage = 1;
				indCount = Integer.parseInt(st.nextToken());
			}
			
			else if (line.startsWith(" CL"))
			{
				StringTokenizer st = new StringTokenizer(line);
				st.nextToken();
				
				stage = 2;
				indCount = (int) Float.parseFloat(st.nextToken());
			}
			
//			System.out.println(line);
		}
	}
}
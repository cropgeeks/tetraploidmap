package analyses.findgeno;

import java.io.*;
import java.util.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunFindGeno extends FortranRunner
{
	// How many lines have been processed?
	int locusCount = 0;
	// What locus is being read
	String marker = "";
	
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_findgeno_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new FindGenoCatcher(proc.getInputStream());
			new FindGenoCatcher(proc.getErrorStream());
			
			writer.println("findgeno");
			writer.println("y");
			writer.println("y");
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("FindGeno was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class FindGenoCatcher extends StreamCatcher
	{
		BufferedReader reader = null;
	
		FindGenoCatcher(InputStream in) { super(in); }
		
		protected void processLine(String line)
		{
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);						
			}
			
			else if (line.startsWith("Locus"))
			{
				StringTokenizer st = new StringTokenizer(line);
				st.nextToken();
				
				locusCount = Integer.parseInt(st.nextToken());
				marker = st.nextToken();
			}
			
//			System.out.println(line);
		}
	}
}
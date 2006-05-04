package analyses.cluster;

import java.io.*;
import java.util.*;

import analyses.*;
import data.*;
import gui.*;

import doe.*;

class RunCluster extends FortranRunner
{
	// How many lines have been processed?
	int locusCount = 0;
	// What are the names of the two markers being processed
	String marker1 = "", marker2 = "";
	// How many linkage groups should Cluster try to create
	private float numToSend;
	// Clustering method in use (1 or 2)
	private int method;
	
	RunCluster(float numToSend, int method)
	{
		this.numToSend = numToSend;
		this.method = method;
	}
	
	public void run()
	{
		try
		{	
			proc = Runtime.getRuntime().exec(Prefs.tools_cluster_path, null,
				new File(Prefs.tools_scratch));
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				proc.getOutputStream()));
			
			new ClusterCatcher(proc.getInputStream());
			new ClusterCatcher(proc.getErrorStream());
			
			writer.println("cluster");
			writer.println("" + method);
			writer.println("" + numToSend);
			writer.close();
			
			proc.waitFor();
		}
		catch (Exception e)
		{
			error = true;
			MsgBox.msg("The clustering process has failed due to the following "
				+ "error:\n" + e, MsgBox.ERR);
		}
		
		isRunning = false;
	}
	
	class ClusterCatcher extends StreamCatcher
	{
		ClusterCatcher(InputStream in) { super(in); }

		protected void processLine(String line)
		{	
			if (line.startsWith("forrtl: severe"))
			{
				error = true;
				MsgBox.msg("Critical error:\n" + line, MsgBox.ERR);						
			}
			
			else if (line.startsWith("Pair of loci"))
				locusCount = Integer.parseInt(line.substring(12, 17).trim());
			
			else if (line.startsWith(" L1"))
				marker1 = line.substring(4).trim();
			else if (line.startsWith(" L2"))
				marker2 = line.substring(4).trim();
			
//			System.out.println(line);
		}
	}
}
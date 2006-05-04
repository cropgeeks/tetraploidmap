package gui.exporter;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

// Writes a file in the format expected as input to SIMANNEAL.EXE
public class FileWriterPWD
{
	// The file that will be written to
	private File file;
	
	public FileWriterPWD(File file)
	{
		this.file = file;
		
		// Ensure that the directory to write the file to exists
		new File(file.getParent()).mkdir();
	}
	
	public boolean writeData(OrderedResult result, LinkageGroup lGroup)
	{		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			
			// Header line
			int mCount = lGroup.getMarkerCount();
			out.write(mCount + " " + (int)(mCount * (mCount-1) / 2));
			out.newLine();
			
			// The results from TwoPoint
			StringTokenizer st1 = new StringTokenizer(result.tp1.toString(), "\n");
			st1.nextToken();
			while (st1.hasMoreTokens())
			{
				StringTokenizer st2 = new StringTokenizer(st1.nextToken());
				
				out.write(st2.nextToken());
				out.write("\t");
				out.write(st2.nextToken());
				out.write("\t");
				
				float rfq = Float.parseFloat(st2.nextToken());
				if (rfq >= 0.5f)
					out.write("0.4999");
				else
					out.write(Prefs.d4.format(rfq));
				
				out.write("\t");
				out.write(st2.nextToken());
				
//				out.write((String)st.nextToken());
				out.newLine();
			}
			
			// The (random) initial marker order
			for (CMarker cm : lGroup.getMarkers())
			{
				Marker m = cm.marker;
				
				out.write(" " + cm.safeName);
				out.newLine();
			}
			
			out.close();
		}
		catch (IOException e)
		{
			MsgBox.msg("Unable to write " + file + " due to the following "
				+ "error:\n" + e, MsgBox.ERR);
			return false;
		}
		
		return true;
	}
	
/*	public boolean writeData(OrderedResult result, LinkageGroup lGroup)
	{		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			
			LinkedList<PhasePair> pp = result.getPhasePairs();
			
			for (int i = 0; i < 9; i++)
			{
				out.write("" + i + "\t");
				System.out.println(i);
				
				for (int j = 0; j <= i; j++)
				{
					PhasePair pair = getPair(i, j, pp);
					out.write(pair.rfq + "\t");
				}
				
				out.newLine();
			}
			
			System.out.println("DONE");
			out.close();
		}
		catch (IOException e)
		{
			MsgBox.msg("Unable to write " + file + " due to the following "
				+ "error:\n" + e, MsgBox.ERR);
			return false;
		}
		
		return true;
	}
	
	private PhasePair getPair(int i, int j, LinkedList<PhasePair> ppList)
	{
		java.text.DecimalFormat d =  new java.text.DecimalFormat("000");
		String iS = "MKR" + d.format(i);
		String jS = "MKR" + d.format(j);
		
		for (PhasePair pp: ppList)
		{
			if (pp.cm1.safeName.equals(iS) && pp.cm2.safeName.equals(jS) ||
				pp.cm2.safeName.equals(iS) && pp.cm1.safeName.equals(jS))
			{
				return pp;
			}
		}
		
		return null;
	}
	*/
}
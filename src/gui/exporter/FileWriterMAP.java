package gui.exporter;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

public class FileWriterMAP
{
	// The file that will be written to
	private File file;
	
	public FileWriterMAP(File file)
	{
		this.file = file;
		
		// Ensure that the directory to write the file to exists
		new File(file.getParent()).mkdir();
	}
	
	public boolean writeData(OrderedResult order, Vector<String[]> rows)
	{		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("" + order.getLinkageGroup().getMarkerCount());
			out.newLine();
			
			Vector<Float> distances = order.getDistances();
			float totalDistance = order.getDistanceTotal();
			
			float cmValue = 0;
			int i = 0;
			
			for (CMarker cm: order.getLinkageGroup().getMarkers())
			{
				// Calculations...
				if (i > 0)
					cmValue += distances.get(i-1);
				
				// Output...
				
				// Name
				out.write(getName20(cm.safeName));
				
				// Format the distance to a 6digit string
				// TODO: API way of doing this?
				String s1 = Prefs.d1.format(cmValue);
				String s2 = s1;
				for (int j = s1.length(); j < 6; j++)
					s2 = " " + s2;
				
				out.write(s2 + "     ");
								
				// Parent geno 2
				String[] data = rows.get(i++);
				out.write(data[0] + "  ");
				out.write(data[1]);
					
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
	
	private String getName20(String name)
	{
		String str = name;
		if (str.length() == 20)
			return str;
		else if (str.length() > 20)
			return str.substring(0, 20);
		else
		{
			for (int i = str.length(); i < 20; i++)
				str += " ";
			return str;
		}
	}
}

/*
	DecimalFormat d = new DecimalFormat("###0.0");
		
	System.out.println();
	System.out.println(chromoAll.length);
	for (GMarker marker: chromoAll)
	{
		System.out.print(marker.marker.safeName + " " + d.format(marker.cm));
		System.out.print("     ");
		System.out.print("0000 ");
		
		System.out.println();
	}
*/
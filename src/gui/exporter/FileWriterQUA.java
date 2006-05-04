package gui.exporter;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

public class FileWriterQUA
{
	// The file that will be written to
	private File file;
	
	public FileWriterQUA(File file)
	{
		this.file = file;
		
		// Ensure that the directory to write the file to exists
		new File(file.getParent()).mkdir();
	}
	
	// Writes trait data to a file - if tIndex > -1 then only that trait is
	// written
	public boolean writeData(TraitFile tFile, String tName)
	{		
		if (tName != null)
			tName = tName.trim();
	
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			
			// Number of markers
			if (tName == null)
				out.write("" + tFile.getSelectedCount());
			else
				out.write("1");
			out.newLine();
			
			// Marker names
			for (int i = 0; i < tFile.getNames().size(); i++)
				if (tFile.getEnabled().get(i))
					if (tName == null || tName.equals(tFile.getNames().get(i)))
						out.write("  " + tFile.getNames().get(i));
			out.newLine();
			
			// Data
			for (float[] data: tFile.getRows())
			{
				out.write("  " + ((int)data[0]));
				
				for (int i = 0; i < tFile.getNames().size(); i++)
					if (tFile.getEnabled().get(i))
						if (tName == null || tName.equals(tFile.getNames().get(i)))
							out.write("  " + Prefs.d3.format(data[i+1]));
				
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
}
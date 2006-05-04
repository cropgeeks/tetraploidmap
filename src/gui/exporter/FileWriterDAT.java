package gui.exporter;

import java.io.*;
import java.util.*;

import data.*;

import doe.*;

public class FileWriterDAT
{
	// The file that will be written to
	private File file;
	
	public FileWriterDAT(File file)
	{
		this.file = file;
		
		// Ensure that the directory to write the file to exists
		new File(file.getParent()).mkdir();
	}
	
	public boolean writeData(LinkageGroup lGroup, boolean checkedOnly)
	{		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			
			// Header line
			out.write("  " + (lGroup.getIndividualCount()-2) + "  ");
			if (checkedOnly)
				out.write("" + lGroup.getSelectedMarkerCount());
			else
				out.write("" + lGroup.getMarkerCount());
			out.newLine();
			
			for (CMarker cm : lGroup.getMarkers())
			{
				if (checkedOnly && cm.checked == false)
					continue;
				
				Marker m = cm.marker;
				
				out.write(cm.safeName + "  " + m.getAlleleCount());
				out.newLine();
				
				for (int a = 0; a < m.getAlleleCount(); a++)
				{
					writeStates(out, m.getAllele(a));
					out.newLine();
				}
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
	
	private void writeStates(BufferedWriter out, Allele allele)
		throws IOException
	{
		int count = 0;
		for (AlleleState state: allele.getStates())
		{
			out.write(" " + state.getState());
			if (++count == 40)
			{
				out.newLine();
				count = 0;
			}
		}
	}
}
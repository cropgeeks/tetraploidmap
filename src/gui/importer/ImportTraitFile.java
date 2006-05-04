package gui.importer;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

public class ImportTraitFile
{
	private File file;
	private TraitFile tFile;
	
	public ImportTraitFile(File file)
		{ this.file = file; }
	
	public boolean doImport()
	{
		tFile = new TraitFile();
		
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			// First line containing number of traits
			in.readLine();
			
			// Next line containing trait names
			StringTokenizer st = new StringTokenizer(in.readLine());
			while (st.hasMoreTokens())
				tFile.addName(st.nextToken());
			
			// Rows of data
			String str = in.readLine();
			while (str != null && str.length() > 0)
			{
				float[] data = new float[tFile.getNames().size() + 1];
				
				st = new StringTokenizer(str);
				if (st.countTokens() != 0)
				{				
					for (int i = 0; i < data.length; i++)
						data[i] = Float.parseFloat(st.nextToken());
					tFile.addRow(data);
				}
				
				str = in.readLine();
			}
			
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			MsgBox.msg("TetraploidMap could not import any trait data from the "
				+ "selected file. Check to ensure the file format is correct.\n"
				+ e, MsgBox.ERR);
			
			return false;
		}
		
		return true;
	}
	
	public TraitFile getTraitFile()
		{ return tFile; }
}
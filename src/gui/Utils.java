package gui;

import java.io.*;

// Contains misc methods that don't really fit in anywhere else
public class Utils
{
	// Deletes all the files in the scratch directory
	public static void emptyScratch()
	{
		File file = new File(Prefs.tools_scratch);
		
		if (file.exists() && file.isDirectory())
		{
			File[] files = file.listFiles();
		
			for (int i = 0; i < files.length; i++)
				files[i].delete();
		}
	}
}

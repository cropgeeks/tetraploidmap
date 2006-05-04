package analyses.perm;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

class ProcessPermResults extends Thread
{
	private PermResult permResult;
	private File file;
	
	ProcessPermResults(PermResult permResult, File file)
	{
		this.permResult = permResult;
		this.file = file;
	}
	
	public boolean process()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			String str = in.readLine();
			// Skip first line
			str = in.readLine();
			
			// Read the LOD scores
			int i = 0;
			while (str != null && str.startsWith(" ****") == false)
			{
				permResult.lodScores[i++] = Float.parseFloat(str);
				str = in.readLine();
			}
			
			// Read the 90% score
			StringTokenizer st = new StringTokenizer(in.readLine());
			st.nextToken();
			float sig90 = Float.parseFloat(st.nextToken());
			
			// Read the 95% score
			st = new StringTokenizer(in.readLine());
			st.nextToken();
			float sig95 = Float.parseFloat(st.nextToken());
			
			permResult.setSigScores(sig90, sig95);
						
			in.close();
			
			return true;
		}
		catch (Exception e)
		{
			MsgBox.msg("Perm was unable to run due to the following error:"
				+ "\n" + e, MsgBox.ERR);
			
			e.printStackTrace(System.out);
			
			return false;
		}
	}
}

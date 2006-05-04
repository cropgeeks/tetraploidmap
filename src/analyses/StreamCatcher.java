package analyses;

import java.io.*;
import java.util.*;

public abstract class StreamCatcher extends Thread
{
	private BufferedReader reader = null;
	
	public StreamCatcher(InputStream in)
	{
		reader = new BufferedReader(new InputStreamReader(in));
		start();
	}
	
	public void run()
	{
		try
		{
			String line = reader.readLine();
			StringTokenizer st = null;
									
			while (line != null)
			{
				processLine(line);
				line = reader.readLine();					
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		try { reader.close(); }
		catch (IOException e) {}
	}
	
	protected abstract void processLine(String line);
}
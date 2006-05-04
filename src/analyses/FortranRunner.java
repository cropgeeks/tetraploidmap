package analyses;

import java.io.*;

public abstract class FortranRunner extends Thread
{
	protected Process proc = null;
	
	// Is the executable still running?
	public boolean isRunning = true;
	// Have any errors occurred?
	public boolean error = false;
	
	public void exit()
	{
		System.out.println("Calling proc.destroy()");
		
		if (proc != null)
			proc.destroy();
	}
}
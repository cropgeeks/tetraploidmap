package analyses.simmatch;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class SimMatchDialog extends JDialog
{
	private LinkageGroup lGroup;
	
	private JLabel markerLabel;
	private boolean isOK = true;
	
	private RunSimMatch simMatch;
	
	public SimMatchDialog(AppFrame appFrame, LinkageGroup lGroup)
	{
		super(appFrame, "Performing Simplex Analysis", true);
		this.lGroup = lGroup;
		
		markerLabel = new JLabel("Performing simplex analysis. Please be patient...");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(markerLabel);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runSimMatch();
			}
			
			public void windowClosing(WindowEvent e)
			{
				exit();
			}
		});
		
		pack();
		setLocationRelativeTo(appFrame);
		setResizable(false);
		setVisible(true);
	}
	
	private void exit()
	{
		if (simMatch != null)
			simMatch.exit();
		
		isOK = false;
		setVisible(false);
	}

	private void runSimMatch()
	{
		// Write out the file that SimMatch needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "simmatch.loc");		
		FileWriterDAT writer = new FileWriterDAT(file);
		if (writer.writeData(lGroup, false) == false)
		{
			exit();
			return;
		}
		
		// Create FindGeno
		simMatch = new RunSimMatch();
		// Start the thread that will monitor its progress
		new SimMatchMonitor().start();
		// Start the thread that will run it
		simMatch.start();		
	}

	private class SimMatchMonitor extends Thread
	{
		public void run()
		{
			while (simMatch.isRunning)
			{
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			if (simMatch.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File nFile = new File(Prefs.tools_scratch, "simmatch.nam");
		File pFile = new File(Prefs.tools_scratch, "simmatch.p1r");
		ProcessResults p = new ProcessResults(lGroup, nFile, pFile);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;
	}
	
	public boolean isOK() { return isOK; }
}
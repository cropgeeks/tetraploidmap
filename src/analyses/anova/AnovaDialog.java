package analyses.anova;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class AnovaDialog extends JDialog
{
	private LinkageGroup lGroup;
	private TraitFile tFile;
	
	private boolean isOK = true;
	
	private RunAnova runAnova;
	private AnovaResult results = new AnovaResult();
	
	
	public AnovaDialog(AppFrame appFrame, LinkageGroup lGroup, TraitFile tFile)
	{
		super(appFrame, "Running ANOVA", true);
		this.lGroup = lGroup;
		this.tFile = tFile;
				
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(new JLabel("Running ANOVA module...please be patient."));		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runAnova();
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
	
	public AnovaResult getAnovaResults()
		{ return results; }
		
	private void exit()
	{
		if (runAnova != null)
			runAnova.exit();
		
		isOK = false;
		setVisible(false);
	}

	private void runAnova()
	{
		// Write out the files that QTL needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "anova.loc");
		FileWriterDAT writerDAT = new FileWriterDAT(file);
		if (writerDAT.writeData(lGroup, true) == false)
		{
			exit();
			return;
		}
		
		file = new File(Prefs.tools_scratch, "anova.qua");
		FileWriterQUA writerQUA = new FileWriterQUA(file);
		if (writerQUA.writeData(tFile, null) == false)
		{
			exit();
			return;
		}
		
	
		// Create runAnova
		runAnova = new RunAnova();
		// Start the thread that will monitor its progress
		new AnovaMonitor().start();
		// Start the thread that will run it
		runAnova.start();		
	}

	private class AnovaMonitor extends Thread
	{
		public void run()
		{
			while (runAnova.isRunning)
			{
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
									
			if (runAnova.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File file = new File(Prefs.tools_scratch, "anova.aov");
		ProcessAnovaResults p = new ProcessAnovaResults(file, results, lGroup);
		
		isOK = !p.error;
	}
	
	public boolean isOK() { return isOK; }
}
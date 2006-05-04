package analyses.findgeno;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class FindGenoDialog extends JDialog
{
	private LinkageGroup lGroup;
	
	private JProgressBar pBar;
	private JLabel markerLabel;
	private boolean isOK = true;
	
	private RunFindGeno findGeno;
	
	public FindGenoDialog(AppFrame appFrame, LinkageGroup lGroup)
	{
		super(appFrame, "Importing and Processing Dataset", true);
		this.lGroup = lGroup;
		
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		markerLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(new JLabel("Running genotype inference module (FINDGENO)..."), BorderLayout.NORTH);
		p1.add(pBar, BorderLayout.CENTER);
		p1.add(markerLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runFindGeno();
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
		if (findGeno != null)
			findGeno.exit();
		
		isOK = false;
		setVisible(false);
	}

	private void runFindGeno()
	{
		// Write out the file that FindGeno needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "findgeno.dat");		
		FileWriterDAT writer = new FileWriterDAT(file);
		if (writer.writeData(lGroup, false) == false)
		{
			exit();
			return;
		}
		
		pBar.setMaximum(lGroup.getMarkerCount());
	
		// Create FindGeno
		findGeno = new RunFindGeno();
		// Start the thread that will monitor its progress
		new FindGenoMonitor().start();
		// Start the thread that will run it
		findGeno.start();		
	}

	private class FindGenoMonitor extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(findGeno.locusCount);
					markerLabel.setText(
						"Processing " + lGroup.getMarkerName(findGeno.marker));
				}
			};
			
			while (findGeno.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
			if (findGeno.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File file = new File(Prefs.tools_scratch, "findgeno.seg");
		ProcessResults p = new ProcessResults(lGroup, file);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;
	}
	
	public boolean isOK() { return isOK; }
}
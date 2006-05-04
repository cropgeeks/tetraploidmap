package analyses.altqtl;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class AltQtlDialog extends JDialog
{
	private TraitFile tFile;
	private int model, cmLength;
	private String tName;
	
	private JProgressBar pBar;
	private JLabel markerLabel;
	private boolean isOK = true;
	
	private RunAltQtl runAltQtl;
	private Trait trait;
	
	public AltQtlDialog(JFrame parent, TraitFile tFile, Trait trait, int model, int cmLength)
	{
		super(parent, "Running Alternative QTL Model", true);
		this.tFile = tFile;
		this.trait = trait;
		this.model = model;
		this.tName = trait.getName();
		this.cmLength = cmLength;
		
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		markerLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(new JLabel("Running Alternative QTL Model (ALTQTLMODEL)..."), BorderLayout.NORTH);
		p1.add(pBar, BorderLayout.CENTER);
		p1.add(markerLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runPerm();
			}
			
			public void windowClosing(WindowEvent e)
			{
				exit();
			}
		});
		
		pack();
		setLocationRelativeTo(parent);
		setResizable(false);
		setVisible(true);
	}
	
	private void exit()
	{
		if (runAltQtl != null)
			runAltQtl.exit();
		
		isOK = false;
		setVisible(false);
	}

	private void runPerm()
	{
		// Write out the file that FindGeno needs
//		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "altqtl.qua");
		FileWriterQUA writerQUA = new FileWriterQUA(file);
		if (writerQUA.writeData(tFile, tName) == false)
		{
			exit();
			return;
		}
		
		pBar.setMaximum(cmLength);
	
		// Create FindGeno
		runAltQtl = new RunAltQtl();
		runAltQtl.model = model;
		// Start the thread that will monitor its progress
		new RunAtlQtlMonitor().start();
		// Start the thread that will run it
		runAltQtl.start();		
	}

	private class RunAtlQtlMonitor extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(runAltQtl.position);
					markerLabel.setText(
						"Chromosome position: " + (runAltQtl.position));
				}
			};
			
			while (runAltQtl.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
			if (runAltQtl.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File file = new File(Prefs.tools_scratch, "altqtlaltQTL.out");
		ProcessAltQtlResults p = new ProcessAltQtlResults(trait, file);
		
		isOK = p.process();
	}
	
	public boolean isOK() { return isOK; }
}
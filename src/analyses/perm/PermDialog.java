package analyses.perm;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class PermDialog extends JDialog
{
	private TraitFile tFile;
	private int cmLength;
	private String tName;
	private boolean fullModel;
	
	private JProgressBar pBar;
	private JLabel markerLabel;
	private boolean isOK = true;
	
	private RunPerm runPerm;
	private PermResult permResult;
	
	public PermDialog(JFrame parent, TraitFile tFile, boolean fullModel, String tName, int cmLength)
	{
		super(parent, "Running Permutation Test", true);
		this.tFile = tFile;
		this.fullModel = fullModel;
		this.tName = tName;
		this.cmLength = cmLength;
		
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		markerLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(new JLabel("Running permutation test module (PERM)..."), BorderLayout.NORTH);
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
		if (runPerm != null)
			runPerm.exit();
		
		isOK = false;
		setVisible(false);
	}

	private void runPerm()
	{
		// Write out the file that FindGeno needs
//		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "perm.qua");
		FileWriterQUA writerQUA = new FileWriterQUA(file);
		if (writerQUA.writeData(tFile, tName) == false)
		{
			exit();
			return;
		}
		
		pBar.setMaximum(cmLength);
	
		// Create FindGeno
		runPerm = new RunPerm();
		runPerm.fullModel = fullModel;
		// Start the thread that will monitor its progress
		new RunPermMonitor().start();
		// Start the thread that will run it
		runPerm.start();		
	}

	private class RunPermMonitor extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(runPerm.position);
					markerLabel.setText(
						"Chromosome position: " + (runPerm.position));
				}
			};
			
			while (runPerm.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
			if (runPerm.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		permResult = new PermResult();
		
		File file = new File(Prefs.tools_scratch, "permQTL.out");
		ProcessPermResults p = new ProcessPermResults(permResult, file);
		
		isOK = p.process();
	}
	
	public boolean isOK() { return isOK; }
	
	public PermResult getResult()
		{ return permResult; }
}
package analyses.cluster;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class ClusterDialog extends JDialog
{
	// LinkageGroup that is being processed
	private LinkageGroup lGroup;
	// Cluster collection that will result from it
	private Cluster cluster;
	// Clustering method to be run
	private int method;
	
	// Number sent to CLUSTER (either number of groups or threshold recom freq)
	private float numToSend;	
	private JProgressBar pBar;
	private JLabel markerLabel;
	private boolean isOK = true;
	
	private RunCluster runCluster;
	
	public ClusterDialog(AppFrame appFrame, LinkageGroup lGroup, Cluster cluster, int method)
	{
		super(appFrame, "Processing Dataset", true);
		this.lGroup = lGroup;
		this.cluster = cluster;
		this.method = method;
		
		// Before doing anything, get the number of groups from the user
		if (method == 2)
		{
			numToSend = getNumGroups();
			if (numToSend < 1)
			{
				isOK = false;
				return;
			}
		}
				
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		markerLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(new JLabel("Running marker grouping module (CLUSTER)..."), BorderLayout.NORTH);
		p1.add(pBar, BorderLayout.CENTER);
		p1.add(markerLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runCluster();
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
		if (runCluster != null)
			runCluster.exit();
		
		isOK = false;
		setVisible(false);
	}
		
	private void runCluster()
	{
		// Write out the file that FindGeno needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "cluster.dat");		
		FileWriterDAT writer = new FileWriterDAT(file);
		if (writer.writeData(lGroup, true) == false)
		{
			exit();
			return;
		}
		
		pBar.setMaximum(lGroup.getSelectedMarkerCount());
	
		// Create Cluster
		runCluster = new RunCluster(numToSend, method);
		// Start the thread that will monitor its progress
		new ClusterMonitor().start();
		// Start the thread that will run it
		runCluster.start();		
	}
	
	private class ClusterMonitor extends Thread
	{		
		public void run()
		{
			long start = System.currentTimeMillis();
			
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(runCluster.locusCount);
					markerLabel.setText(
						"Processing "
						+ lGroup.getMarkerName(runCluster.marker1)
						+ " and "
						+ lGroup.getMarkerName(runCluster.marker2));
				}
			};
			
			while (runCluster.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
						
			// Final stage...
			r = new Runnable() { public void run() {
				markerLabel.setText("Analysing dendrograms");
			}};
			
			try { SwingUtilities.invokeAndWait(r); }
			catch (Exception e) {}
			
			if (runCluster.error == false)
				processResults();
			else
				isOK = false;
			
			cluster.setSummary(
				new Summary(lGroup, (System.currentTimeMillis()-start)));
			
			System.out.println("setvisible(false)");
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File file = new File(Prefs.tools_scratch, "cluster.chi");
		ProcessClusterResults p =
			new ProcessClusterResults(lGroup, cluster, file, method);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;
	}
	
	public boolean isOK() { return isOK; }
	
	private int getNumGroups()
	{
		String num = (String) JOptionPane.showInputDialog(this, "Enter the "
			+ "expected number of linkage groups:", "Enter Group Count",
			JOptionPane.PLAIN_MESSAGE, null, null, "" + 12);
		
		if (num != null)
		{
			try
			{
				int value = Integer.parseInt(num);
				if (value < 1)
					throw new NumberFormatException();
				else
					return value;
			}
			catch (NumberFormatException e)
			{
				MsgBox.msg("Please ensure a positive integer value is entered.",
					MsgBox.ERR);
			}
		}
		
		return 0;
	}
}
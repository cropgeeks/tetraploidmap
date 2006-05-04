package analyses.order.twopoint;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class TwoPointDialog extends JDialog
{
	private LinkageGroup lGroup;
	private OrderedResult order;
	private int num = 0;
	
	private JProgressBar pBar;
	private JLabel descriptionLabel, statusLabel;
	private boolean isOK = true;
	
	private RunTwoPoint twoPoint;
	
	public TwoPointDialog(AppFrame appFrame, LinkageGroup grp)
	{
		super(appFrame, "Processing Dataset", true);
		
		lGroup = grp;
		num = lGroup.getMarkerCount();
		order = new OrderedResult(num);
		
		if (ProcessTwoPointResults.verifyForTwoPoint(lGroup) == false)
		{
			isOK = false;
			return;
		}
	
		
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		statusLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		descriptionLabel = new JLabel(
			"Running two-point linkage analysis (TWOPOINT)...");
		p1.add(descriptionLabel, BorderLayout.NORTH);
		p1.add(pBar, BorderLayout.CENTER);
		p1.add(statusLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runTwoPoint();
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
		isOK = false;
		
		if (twoPoint != null)
			twoPoint.exit();
		
		setVisible(false);
	}	

	private void runTwoPoint()
	{
		// Write out the file that TwoPoint needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "twopoint.loc");		
		FileWriterDAT writer = new FileWriterDAT(file);
		if (writer.writeData(lGroup, true) == false)
		{
			exit();
			return;
		}
		
		// For 5 markers, TP runs the following iterations:
		//  4 + 3 + 2 + 1    (n-1) * (n/2)
		int markerCount = lGroup.getSelectedMarkerCount();
		int runs = (int)((markerCount - 1) * ((float)markerCount / 2.0));
		// We do *2 because TP prints out L1 and L2 markers at each iteration
		pBar.setMaximum(2 * runs);
	
		// Create TwoPoint
		twoPoint = new RunTwoPoint();
		// Start the thread that will monitor its progress
		new TwoPointMonitor().start();
		// Start the thread that will run it
		twoPoint.start();
	}
	
	private class TwoPointMonitor extends Thread
	{		
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(twoPoint.count);
					statusLabel.setText(
						"Processing "
						+ lGroup.getMarkerName(twoPoint.marker1)
						+ " and "
						+ lGroup.getMarkerName(twoPoint.marker2));
				}
			};
			
			while (twoPoint.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
			if (twoPoint.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File twoPwd = new File(Prefs.tools_scratch, "twopoint.pwd");
		File twoOut = new File(Prefs.tools_scratch, "twopoint.out");
		
		// GUI results only - don't matter
		try { order.setTwoPointResults(twoPwd, twoOut); }
		catch (Exception e) {}	
		
		ProcessTwoPointResults p = new ProcessTwoPointResults(
			lGroup, order, twoOut, twoPwd);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;
		
		// If the files were read ok, perform a final check on *what* was read
		// from them		
		if (isOK)
		{
			File file = new File(Project.filename + "_tpFailed.txt");
			isOK = p.verifyPhaseData(order.getPhasePairArray(), file);
		}
	}
	
	public boolean isOK() { return isOK; }
	
	public OrderedResult getOrderedResult() { return order; }
}
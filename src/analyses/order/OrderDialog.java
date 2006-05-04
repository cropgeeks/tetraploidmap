package analyses.order;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;
/*
public class OrderDialog extends JDialog
{
	private LinkageGroup oGroup, lGroup; // original group, and cloned group
	private OrderedResult order;
	private int num = 0;
	
	private JProgressBar pBar;
	private boolean isProcessing;
	private JLabel descriptionLabel, statusLabel;
	private boolean isOK = true;
	private boolean isMultipleRun = false;
	private boolean runSim = false;
	
	private RunTwoPoint twoPoint;
	private RunSimAnneal simAnneal;
	private LinkageAnalysis2 getOrder;
	
	public OrderDialog(AppFrame appFrame, OrderedResult ord, LinkageGroup grp, boolean isMultiple)
	{
		super(appFrame, "Processing Dataset", true);
		order = ord;
		isMultipleRun = isMultiple;
		
		lGroup = grp.getClonedLinkageGroup(true, false);
		oGroup = grp;
		num = lGroup.getMarkerCount();
		
		if (ProcessResults.verifyForTwoPoint(this.lGroup) == false)
		{
			isOK = false;
			return;
		}
		
		if (order == null)
			order = new OrderedResult(num);
		
		if (isMultipleRun == false)
		{
			// If a 2nd simanneal run is selected, then the 1st run must be via
			// the custom ordering method (hence leaving runSim as false). When
			// this dialog is run for the 2nd time, isMultipleRun will be true
			// so simanneal will then be run
			
			if (MsgBox.yesno("Run Simulated Annealing instead of custom ordering?", 1) == JOptionPane.YES_OPTION)
			{
				new SimSettingsDialog(appFrame);
				isMultipleRun = true;
			}
		}
		else
			runSim = true;
		
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
				runAnalyses();
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
		if (simAnneal != null)
			simAnneal.exit();
		if (getOrder != null)
			getOrder.exit();
		
		isProcessing = false;
		setVisible(false);
	}
	
	public boolean isMultipleRun()
		{ return isMultipleRun; }
	
	private void runAnalyses()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				long start = System.currentTimeMillis();
				
				// Run TwoPoint...
				if (runSim == false)
				{
					runTwoPoint();
				
					// ...and wait for it to finish
					while (isProcessing)
						try { Thread.sleep(50); }
						catch (InterruptedException e) {}
				}
				
				// Then run SimAnneal
				if (isOK() && runSim)
				{
					runSimAnneal();
				}
				// Or the custom ordering module
				else if (isOK() && !runSim)
				{
					processResults();
					if (isOK)
					{
						runGetOrder();
					}
				}
				
				// ...and wait for it to finish
				while (isProcessing)
					try { Thread.sleep(50); }
					catch (InterruptedException e) {}
				
				if (isOK && runSim)
					processResults();
				
				order.setSummary(
					new Summary(oGroup, (System.currentTimeMillis()-start)));
				
				// Then exit
				setVisible(false);
			}
		};
		
		new Thread(r).start();
	}
	
	// Helper method to update the GUI ready for SimAnneal starting (done in a
	// SwingUtilities thread to ensure it DOES update before the processing
	// thread begins)
	private void setGUIForSimAnneal()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				descriptionLabel.setText(
					"Running marker ordering module (SIMANNEAL)...");
				statusLabel.setText("Current temperature:");
			}
		};
		
		try { SwingUtilities.invokeAndWait(r); }
		catch (Exception e) {}
	}
	
	private void setGUIForGetOrder()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				pBar.setValue(0);
				pBar.setMaximum(lGroup.getMarkerCount());
				
				descriptionLabel.setText(
					"Running marker ordering module (Custom)...");
				statusLabel.setText(" ");
			}
		};
		
		try { SwingUtilities.invokeAndWait(r); }
		catch (Exception e) {}
	}

	private void runTwoPoint()
	{
		isProcessing = true;
		
		// Write out the file that FindGeno needs
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
				processTwoPointResults();
			else
				isOK = false;
			
			isProcessing = false;
		}
	}
	
	private void runSimAnneal()
	{		
		isProcessing = true;
		
		// Write out the file that SimAnneal needs
		File file = new File(Prefs.tools_scratch, "sim.pwd");
		FileWriterPWD writer = new FileWriterPWD(file);
		
		// Changes made when Christine wanted simanneal run after custom ordering
//		if (writer.writeData(order, lGroup) == false)
		if (writer.writeData(order, order.getLinkageGroup()) == false)
		{
			exit();
			return;
		}
		
		// Clear the results from the custom run
		order.reset();
		
		setGUIForSimAnneal();
			
		// Create SimAnneal
		simAnneal = new RunSimAnneal();
		// Start the thread that will monitor its progress
		new SimAnnealMonitor().start();
		// Start the thread that will run it
		simAnneal.start();		
	}

	private class SimAnnealMonitor extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
//					pBar.setValue(simAnneal.count);
					if (simAnneal.temp != -1)
						statusLabel.setText("Current temperature: "
							+ Prefs.d5.format(simAnneal.temp));
				}
			};
			
			while (simAnneal.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
			if (simAnneal.error == false)
				processSimAnnealResults();
//			else
//				isOK = false;
			
			isProcessing = false;
		}
	}
	
	private void runGetOrder()
	{		
		isProcessing = true;
		
		setGUIForGetOrder();
			
		// Create the thread
		getOrder = new LinkageAnalysis2(order, lGroup);
		// Start the thread that will monitor its progress
		new GetOrderMonitor().start();
		// Start the thread that will run it
		getOrder.start();
	}

	private class GetOrderMonitor extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run() {
					pBar.setValue(getOrder.mkrCount);
					statusLabel.setText("Processing order "
						+ (getOrder.mkrCount+1) + " of " + num);
				}
			};
			
			while (getOrder.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
//			if (simAnneal.error == false)
//				processSimAnnealResults();
//			else
//				isOK = false;
			
			isProcessing = false;
		}
	}
	
	private void processTwoPointResults()
	{
		File file1 = new File(Prefs.tools_scratch, "twopoint.pwd");
		File file2 = new File(Prefs.tools_scratch, "twopoint.out");
		
		try
		{
			order.setTwoPointResults(file1, file2);
		}
		catch (Exception e)
		{
			System.out.println(e);
//			isOK = false;
		}
	}
	
	private void processSimAnnealResults()
	{
		File file1 = new File(Prefs.tools_scratch, "sim.txt");
		File file2 = new File(Prefs.tools_scratch, "sim.out");
		
		try
		{
			order.setSimAnnealResults(file1, file2);
		}
		catch (Exception e)
		{
			System.out.println(e);
			isOK = false;
		}
	}
	
	private void processResults()
	{
		File simOut = new File(Prefs.tools_scratch, "sim.out");
		File twoOut = new File(Prefs.tools_scratch, "twopoint.out");
		File twoPwd = new File(Prefs.tools_scratch, "twopoint.pwd");
		
		ProcessResults p = new ProcessResults(
			lGroup, order, simOut, twoOut, twoPwd);
		
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
*/
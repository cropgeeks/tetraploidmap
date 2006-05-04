package analyses.order.sim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class SimAnnealDialog extends JDialog
{
	private LinkageGroup lGroup;
	private OrderedResult order;
	
	private JLabel descriptionLabel, statusLabel;
	private boolean isOK = true;
	
	private RunSimAnneal simAnneal;
	
	public SimAnnealDialog(AppFrame appFrame, OrderedResult ord, LinkageGroup grp)
	{
		super(appFrame, "Processing Dataset", true);
		
		lGroup = grp;
		order = ord;
		
		statusLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		descriptionLabel = new JLabel(
			"Running simulated annealing marker ordering (SIMANNEAL)...");
		p1.add(descriptionLabel, BorderLayout.NORTH);
		p1.add(statusLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runSimAnneal();
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
		
		if (simAnneal != null)
			simAnneal.exit();
		
		setVisible(false);
	}	

	private void runSimAnneal()
	{
		// Write out the file that SimAnneal needs
		Utils.emptyScratch();
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
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File simTxt = new File(Prefs.tools_scratch, "sim.txt");
		File simOut = new File(Prefs.tools_scratch, "sim.out");
		
		// GUI results only - don't matter
		try { order.setSimAnnealResults(simTxt, simOut); }
		catch (Exception e) {}
		
		ProcessSimResults p = new ProcessSimResults(lGroup, order, simOut);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;
	}
	
	public boolean isOK() { return isOK; }
}
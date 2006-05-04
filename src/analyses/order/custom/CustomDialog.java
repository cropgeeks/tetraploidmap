package analyses.order.custom;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class CustomDialog extends JDialog
{
	private LinkageGroup lGroup;
	private OrderedResult order;
	private int num = 0;
	
	private JProgressBar pBar;
	private JLabel descriptionLabel, statusLabel;
	private boolean isOK = true;
	
	private CustomAnalysis getOrder;
		
	public CustomDialog(AppFrame appFrame, OrderedResult ord, LinkageGroup grp)
	{
		super(appFrame, "Processing Dataset", true);
		
		order = ord;
		lGroup = grp;
		num = lGroup.getMarkerCount();
				
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		pBar.setMaximum(num);
		statusLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		descriptionLabel = new JLabel(
			"Running marker ordering module (CUSTOM)...");
		p1.add(descriptionLabel, BorderLayout.NORTH);
		p1.add(pBar, BorderLayout.CENTER);
		p1.add(statusLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runCustom();
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
		
		if (getOrder != null)
			getOrder.exit();
		
		setVisible(false);
	}	

	private void runCustom()
	{
		// Create the thread
		getOrder = new CustomAnalysis(order, lGroup);
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
			
			setVisible(false);
		}
	}
	
	public boolean isOK() { return isOK; }
}
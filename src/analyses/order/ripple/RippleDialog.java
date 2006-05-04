package analyses.order.ripple;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class RippleDialog extends JDialog
{
	private LinkageGroup lGroup;
	private OrderedResult order;
	
	private JLabel descriptionLabel, statusLabel;
	private boolean isOK = true;
	
	private RunRipple ripple;
	
	public RippleDialog(AppFrame appFrame, OrderedResult ord, LinkageGroup grp)
	{
		super(appFrame, "Processing Dataset", true);
		
		lGroup = grp;
		order = ord;
		
		statusLabel = new JLabel("");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		descriptionLabel = new JLabel(
			"Running ripple marker ordering (RIPPLE)...");
		p1.add(descriptionLabel, BorderLayout.NORTH);
		p1.add(statusLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runRipple();
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
		
		if (ripple != null)
			ripple.exit();
		
		setVisible(false);
	}	

	private void runRipple()
	{
		// Write out the file that Ripple needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "ripple.pwd");

		FileWriterPWD writer = new FileWriterPWD(file);
		if (writer.writeData(order, order.getLinkageGroup()) == false)
		{
			exit();
			return;
		}
		
		// Clear the results from the custom run
		order.reset();
					
		// Create Ripple
		ripple = new RunRipple();
		// Start the thread that will monitor its progress
		new RippleMonitor().start();
		// Start the thread that will run it
		ripple.start();		
	}
	
	private class RippleMonitor extends Thread
	{
		public void run()
		{
/*			Runnable r = new Runnable() {
				public void run() {
					if (ripple.temp != -1)
						statusLabel.setText("Current temperature: "
							+ Prefs.d5.format(ripple.temp));
				}
			};
*/			
			while (ripple.isRunning)
			{
//				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
//			SwingUtilities.invokeLater(r);
			
			if (ripple.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File rippler = new File(Prefs.tools_scratch, "rippler.txt");		
		ProcessRippleResults p =
			new ProcessRippleResults(lGroup, order, rippler);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;
	}
	
	public boolean isOK() { return isOK; }
}
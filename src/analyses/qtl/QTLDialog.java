package analyses.qtl;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;
import gui.exporter.*;

import doe.*;

public class QTLDialog extends JDialog
{
	private OrderedResult order;
	private TraitFile tFile;
	
	private JProgressBar pBar;
	private JLabel markerLabel;
	private boolean isOK = true;
	
	private RunQTL runQTL;
	private int currentStage = 1;
	
	private QTLResult qtlResult = new QTLResult();
	private int maximum;
	
	public QTLDialog(AppFrame appFrame, OrderedResult order, TraitFile tFile, final int parent)
	{
		super(appFrame, "Running QTL Analysis", true);
		this.order = order;
		this.tFile = tFile;
				
		pBar = new JProgressBar();
		pBar.setPreferredSize(new Dimension(300, 20));
		markerLabel = new JLabel("Processing");
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(new JLabel("Running QTL analysis module (REALONEPARRECON)..."), BorderLayout.NORTH);
		p1.add(pBar, BorderLayout.CENTER);
		p1.add(markerLabel, BorderLayout.SOUTH);
		
		add(p1);
				
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				runQTL(parent);
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
	
	public QTLResult getQTLResult()
		{ return qtlResult; }
	
	private void exit()
	{
		if (runQTL != null)
			runQTL.exit();
		
		isOK = false;
		setVisible(false);
	}

	private void runQTL(int parent)
	{
		// Write out the files that QTL needs
		Utils.emptyScratch();
		File file = new File(Prefs.tools_scratch, "qtl.loc");
		FileWriterDAT writerDAT = new FileWriterDAT(file);
		if (writerDAT.writeData(order.getLinkageGroup(), true) == false)
		{
			exit();
			return;
		}
		
		file = new File(Prefs.tools_scratch, "qtl.map");
		FileWriterMAP writerMAP = new FileWriterMAP(file);
		if (writerMAP.writeData(order, order.rows) == false)
		{
			exit();
			return;
		}
		
		file = new File(Prefs.tools_scratch, "qtl.qua");
		FileWriterQUA writerQUA = new FileWriterQUA(file);
		if (writerQUA.writeData(tFile, null) == false)
		{
			exit();
			return;
		}
		
		maximum = order.getLinkageGroup().getIndividualCount();		
		pBar.setMaximum(maximum);

	
		// Create runQTL
		runQTL = new RunQTL(parent);
		// Start the thread that will monitor its progress
		new QTLMonitor().start();
		// Start the thread that will run it
		runQTL.start();		
	}

	private class QTLMonitor extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run()
				{
					// Assume changeover has happened
					if (runQTL.stage != currentStage)
						pBar.setMaximum((int)order.getDistanceTotal());
					
					pBar.setValue(runQTL.indCount);
					currentStage = runQTL.stage;
					
					if (currentStage == 1)
						markerLabel.setText(
							"(Phase 1) Individuals processed: " + (runQTL.indCount));
					else if (currentStage == 2)
						markerLabel.setText(
							"(Phase 2) Chromosome position: " + (runQTL.indCount));
				}
			};
			
			while (runQTL.isRunning)
			{
				SwingUtilities.invokeLater(r);
				
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}
			}
			
			SwingUtilities.invokeLater(r);
			
			if (runQTL.error == false)
				processResults();
			else
				isOK = false;
			
			setVisible(false);
		}
	}
	
	private void processResults()
	{
		File file = new File(Prefs.tools_scratch, "qtlQTL.out");
		File bkDir = new File(Prefs.tools_scratch, "tempqtl");
		File bkFile = new File(bkDir, "test.txt");
		ProcessQTLResults p = new ProcessQTLResults(qtlResult, file, bkFile);
		
		while (p.isRunning)
		{
			try { Thread.sleep(50); }
			catch (InterruptedException e) {}
		}
		
		isOK = !p.error;

	}
	
	public boolean isOK() { return isOK; }
}
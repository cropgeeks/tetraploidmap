package analyses.cluster;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.*;
import gui.*;

class ThresholdDialog extends JDialog
{
	private Cluster cluster;
	
	ThresholdDialog(AppFrame frame, Cluster c)
	{
		super(frame, "Select Threshold Recombination Frequency", true);
		
		cluster = c;
		
		createControls();
		pack();
		
//		setResizable(false);
		setSize(400, 300);
		setLocationRelativeTo(frame);
		setVisible(true);
	}
	
	private void createControls()
	{
		JScrollPane sp = new JScrollPane();
		JLabel label = new JLabel();
		
		Dendrogram dg = cluster.getSnLnkDendrogram();
		
		DendrogramCanvas canvas = new DendrogramCanvas(sp, dg, label);
		sp.getViewport().setView(canvas);
		
		setLayout(new BorderLayout());
		add(sp);
		add(label, BorderLayout.SOUTH);
	}
}
package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.text.*;
import javax.swing.*;

import data.*;

import doe.*;

public class DendrogramsPanel extends JPanel
{
	private Dendrogram snDen = null;
	private Dendrogram avDen = null;
	
	private DendrogramCanvas snCanvas;
	private DendrogramChart snChart;
	private DendrogramCanvas avCanvas;
	private DendrogramChart avChart;
			
	public DendrogramsPanel(Cluster cluster)
	{
		snDen = cluster.getSnLnkDendrogram();
		avDen = cluster.getAvLnkDendrogram();
				
		JPanel p1 = getResultPanel(snDen, "Single Linkage Clustering", true);
		JPanel p2 = getResultPanel(avDen, "Average Linkage Clustering", false);
		
		JSplitPane splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splits.setOneTouchExpandable(true);
		splits.setTopComponent(p1);
		splits.setBottomComponent(p2);
		splits.setResizeWeight(0.5);
				
		setLayout(new BorderLayout());
		add(splits);
	}
	
	private JPanel getResultPanel(Dendrogram d, String title, boolean sn)
	{
		JScrollPane sp = new JScrollPane();
		JLabel label = new JLabel();
		sp.getVerticalScrollBar().setUnitIncrement(15);
		DendrogramCanvas canvas = new DendrogramCanvas(sp, d, label);
		sp.getViewport().setView(canvas);
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(sp);
		p1.add(label, BorderLayout.SOUTH);
		
		DendrogramChart chart = new DendrogramChart(d, false);

		JSplitPane splits = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splits.setLeftComponent(p1);
		splits.setRightComponent(chart);
		splits.setResizeWeight(0.5);
		
		DendrogramToolBar toolbar = new DendrogramToolBar(this, canvas, chart);

		JPanel p2 = new JPanel(new BorderLayout(0, 0));
		p2.add(toolbar, BorderLayout.EAST);
		p2.add(splits);
		
		JPanel p3 = new JPanel(new BorderLayout(0, 0));
		p3.add(new GradientPanel(title), BorderLayout.NORTH);		
		p3.add(p2);
		
		if (sn)
		{
			snCanvas = canvas;
			snChart = chart;
		}
		else
		{
			avCanvas = canvas;
			avChart = chart;
		}
		
		return p3;
	}
	
	public void print()
	{
		Printable[] toPrint = { snCanvas, snChart, avCanvas, avChart };		
		new PrinterDialog(toPrint);
	}
	
	void setChartScaling(DendrogramChart chart)
	{
		Object[] values = {
			"Autorange",
			"Scaled to between 0 and 1",
			"Scaled to the minimum value from both graphs"
		};
		
		Object selectedValue = JOptionPane.showInputDialog(MsgBox.frm,
			"Select the type of graph scaling to be applied.\n(Note that this "
			+ "only affects the graphs when showing non-log values)",
			"Set Domain-Range Scaling", JOptionPane.QUESTION_MESSAGE, null,
			values, values[Prefs.gui_graph_scale_type]);
	
		if (selectedValue == null)
			return;
		
		
		if (selectedValue == values[0])
		{
			chart.setScaling(true, 0, 1);
			Prefs.gui_graph_scale_type = 0;
		}
		
		else if (selectedValue == values[1])
		{
			chart.setScaling(false, 0, 1);
			Prefs.gui_graph_scale_type = 1;
		}
		
		else if (selectedValue == values[2])
		{
			double minimum = getMinimumSimilarity();
			snChart.setScaling(false, minimum, 1);
			avChart.setScaling(false, minimum, 1);
			
			Prefs.gui_graph_scale_type = 2;
		}
	}
	
	private double getMinimumSimilarity()
	{
		double num1 = avDen.getRootSimilarity();
		double num2 = snDen.getRootSimilarity();
		
		// Return the smallest value
		return num1 <= num2 ? num1 : num2;
	}
}
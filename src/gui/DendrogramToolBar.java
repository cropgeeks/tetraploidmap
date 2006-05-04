package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class DendrogramToolBar extends JToolBar
{
	private DendrogramsPanel panel;
	private DendrogramCanvas canvas;
	private DendrogramChart chart;
	
	private JToggleButton bScale, bLog;
	private AbstractAction aScale, aColor, aLog, aRange;
	
	DendrogramToolBar(DendrogramsPanel panel, DendrogramCanvas canvas, DendrogramChart chart)
	{
		this.panel = panel;
		this.canvas = canvas;
		this.chart = chart;
		
		createActions();
		
		setFloatable(false);
		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		setOrientation(JToolBar.VERTICAL);
		
		bScale = (JToggleButton)
			AppFrameToolBar.getButton(true, null,
				"Scale Dendrogram to Fit Window", Icons.SCALE, aScale);
		bLog = (JToggleButton)
			AppFrameToolBar.getButton(true, null,
				"View Log Scales on Graph", Icons.LOG, aLog);
		
		add(bScale);
		add(AppFrameToolBar.getButton(false, null,
			"Colour Dendrogram via Similarity Score", Icons.COLOR_DENDROGRAM, aColor));
		addSeparator();
		add(bLog);
		add(AppFrameToolBar.getButton(false, null,
			"Set Domain-Range Scaling", Icons.GRAPH_SCALE, aRange));
	}
	
	private void createActions()
	{
		aScale = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				canvas.setSizeToFit(bScale.isSelected()); } };
		
		aColor = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				canvas.updateColouriser(true, 0); } };
		
		aLog = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				chart.setVisibleChart(bLog.isSelected()); } };
		
		aRange = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.setChartScaling(chart); } };
	}
}

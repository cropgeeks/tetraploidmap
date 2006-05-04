package gui.map;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gui.*;

import doe.*;

class MapToolBar extends JToolBar
{
	private LinkageMapPanel panel;
	private MapPanel canvas;
	
	private JToggleButton bTopDown, bAntiAlias, bOverall;
	private AbstractAction aTopDown, aAntiAlias, aOverall, aSave;
	private AbstractAction aZoomIn, aZoomOut, aSaveText;
	
	MapToolBar(LinkageMapPanel panel, MapPanel canvas)
	{
		this.panel = panel;
		this.canvas = canvas;
		
		createActions();
		
		setFloatable(false);
		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		setOrientation(JToolBar.VERTICAL);
		
		bTopDown = (JToggleButton)
			AppFrameToolBar.getButton(true, null,
				"Draw Chromosomes from Bottom-Up", Icons.FLIP, aTopDown);
		
		bAntiAlias = (JToggleButton)
			AppFrameToolBar.getButton(true, null,
				"Anti-alias Display", Icons.AA, aAntiAlias);
		
		bOverall = (JToggleButton)
			AppFrameToolBar.getButton(true, null,
				"Don't Draw Overall Chromosome", Icons.HIDE_OVERALL, aOverall);
				
		add(bTopDown);
		add(bAntiAlias);
		add(bOverall);
		addSeparator();
		add(AppFrameToolBar.getButton(false, null,
			"Zoom Display In", Icons.ZOOM_IN, aZoomIn));
		add(AppFrameToolBar.getButton(false, null,
			"Zoom Display Out", Icons.ZOOM_OUT, aZoomOut));
		addSeparator();
		add(AppFrameToolBar.getButton(false, null,
			"Save LinkageMap as PNG Image", Icons.SAVE_IMAGE, aSave));
		add(AppFrameToolBar.getButton(false, null,
			"Save LinkageMap as Text", Icons.SAVE_TEXT, aSaveText));
	}
	
	private void createActions()
	{
		aTopDown = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				canvas.flip(bTopDown); } };
		
		aAntiAlias = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				canvas.toggleAntiAliasing(); } };
		
		aOverall = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				canvas.toggleOverallShown(); } };
		
		aSave = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.save(true); } };
		
		aZoomIn = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				zoom(true); } };
		
		aZoomOut = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				zoom(false); } };
		aZoomOut.setEnabled(false);
		
		aSaveText = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.save(false); } };
	}
	
	private void zoom(boolean zoomin)
	{
		if (zoomin)
			canvas.zoom(1);
		else
			canvas.zoom(-1);
		
		if (canvas.zoom == 1)
			aZoomOut.setEnabled(false);
		else
			aZoomOut.setEnabled(true);
	}
}

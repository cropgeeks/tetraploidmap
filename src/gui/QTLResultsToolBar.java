package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.*;
import gui.*;

import doe.*;

class QTLResultsToolBar extends JToolBar
{
	private QTLResultsPanel panel;
	
	AbstractAction aSavePNG, aSaveTXT, aRescan, aPerm;
	
	QTLResultsToolBar(QTLResultsPanel panel)
	{
		this.panel = panel;
		
		createActions();
		
		setFloatable(false);
		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		setOrientation(JToolBar.VERTICAL);
		
		
		add(AppFrameToolBar.getButton(false, null,
			"Save Graph as PNG Image", Icons.SAVE_IMAGE, aSavePNG));
		add(AppFrameToolBar.getButton(false, null,
			"Save Graph as CSV Text File", Icons.SAVE_TEXT, aSaveTXT));
		addSeparator();
		add(AppFrameToolBar.getButton(false, null,
			"Rescan With Simple Model", Icons.RESCAN, aRescan));
		add(AppFrameToolBar.getButton(false, null,
			"Run Permutation Test", Icons.PERM, aPerm));
		
		enableButtons(false, null);
	}
	
	private void createActions()
	{
		aSavePNG = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.savePNG(); } };
		
		aSaveTXT = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.saveTXT(); } };
		
		aRescan = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.runRescan(); } };
		
		aPerm = new AbstractAction("") {
			public void actionPerformed(ActionEvent e) {
				panel.runPerm(); } };
	}
	
	void enableButtons(boolean state, TraitFile traitFile)
	{
		aSavePNG.setEnabled(state);
		aSaveTXT.setEnabled(state);
		
		if (traitFile == null) state = false;
		
		aRescan.setEnabled(state);
		aPerm.setEnabled(state);		
	}
}
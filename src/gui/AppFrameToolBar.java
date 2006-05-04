package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppFrameToolBar extends JToolBar
{
	AppFrameToolBar()
	{
		setFloatable(false);
//		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		
		add(getButton(false, "New Project", "Create a New Project",
			Icons.NEW_PROJECT, AppFrameMenuBar.aFileNewProject));
		add(getButton(false, "Open Project", "Open an Existing Project",
			Icons.OPEN, AppFrameMenuBar.aFileOpenProject));
		addSeparator();
		add(getButton(false, null, "Save Current Project",
			Icons.SAVE, AppFrameMenuBar.aFileSave));
		add(getButton(false, null, "Print",
			Icons.PRINT, AppFrameMenuBar.aFilePrint));
		addSeparator();
		add(getButton(false, null, "Remove Analysis Results",
			Icons.DELETE, AppFrameMenuBar.aAnalysisRemove));
		add(getButton(false, "Import", "import dataset into Project",
			Icons.IMPORT, AppFrameMenuBar.aFileImport));
		addSeparator();
//		add(getButton("", "", Icons.DELETE, null));
//		addSeparator();
		add(getButton(false, "Select Markers", "Select Markers for Analysis",
			Icons.SELECT_MARKERS, AppFrameMenuBar.aSelect));
		addSeparator();
		add(getButton(false, "Cluster", "Cluster Markers into Linkage Groups",
			Icons.RUN_CLUSTER, AppFrameMenuBar.aAnalysisCluster));
		add(getButton(false, "Order", "Compute Phase/Dosage and Order Markers",
			Icons.RUN_SIMANNEAL, AppFrameMenuBar.aAnalysisSim));
		add(getButton(false, "Map", "Generate Linkage Map",
			Icons.RUN_MAP, AppFrameMenuBar.aAnalysisMap));
		add(getButton(false, "ANOVA", "Run Analysis of Variance",
			Icons.RUN_ANOVA, AppFrameMenuBar.aAnalysisAnova));
		add(getButton(false, "QTL", "Perform QTL Analysis",
			Icons.RUN_SIMANNEAL, AppFrameMenuBar.aAnalysisQTL));
	}
	
	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	public static AbstractButton getButton(
		boolean toggle, String title, String tt, ImageIcon icon, Action a)
	{
		AbstractButton button = null;
		
		if (toggle)
			button = new JToggleButton(a);
		else
			button = new JButton(a);

		button.setBorderPainted(false);
		button.setMargin(new Insets(1, 1, 1, 1));
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		
		button.setText(title);
		
		return button;
	}
}

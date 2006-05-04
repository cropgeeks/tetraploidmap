package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppFrameMenuBar extends JMenuBar
{
	private AppFrame appFrame;
	
	JMenu mFile, mFileRecent;
	JMenuItem mFileNewProject, mFileOpenProject, mFileExit, mFileImport;
	JMenuItem mFileSave, mFileSaveAs, mFilePrint, mFilePrintSetup;
	
	JMenu mMarkers, mMarkersSelect;
	JMenuItem mTrait, mTraitView, mSelect, mSelectInvt, mSelectNone, mSelectAll;
	JMenuItem mMove;
	
	JMenu mAnalysis;
	JMenuItem mAnalysisCluster, mAnalysisTwoPoint, mAnalysisSim, mAnalysisLog;
	JMenuItem mAnalysisQTL, mAnalysisRemove, mAnalysisMap, mAnalysisAnova;
	
	public static AbstractAction aFileNewProject, aFileOpenProject, aFileExit, aFilePrint;
	public static AbstractAction aFileImport, aFileSave, aFileSaveAs, aFilePrintSetup;
	public static AbstractAction aTrait, aTraitView, aSelect, aSelectInvt, aSelectNone, aSelectAll;
	public static AbstractAction aMove;
	public static AbstractAction aAnalysisCluster, aAnalysisSim, aAnalysisLog, aAnalysisQTL;
	public static AbstractAction aAnalysisRemove, aAnalysisMap, aAnalysisAnova;
	
	public AppFrameMenuBar(AppFrame appFrame)
	{
		this.appFrame = appFrame;
		
		createActions();
		createFileMenu();
		createMarkersMenu();
		createAnalysisMenu();
		createHelpMenu();
		
		setBorderPainted(false);
		setInitialState();
	}
	
	private void createActions()
	{
		aFileNewProject = new AbstractAction("New Project...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.newProject(); } };
		
		aFileOpenProject = new AbstractAction("Open Project...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.openProject(null); } };
				
		aFileImport = new AbstractAction("import dataset") {
			public void actionPerformed(ActionEvent e) {
				appFrame.importDataSet(); } };
		
		aFileSave = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				appFrame.saveProject(false); } };
		
		aFileSaveAs = new AbstractAction("Save As...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.saveProject(true); } };
		
		aFilePrint = new AbstractAction("Print") {
			public void actionPerformed(ActionEvent e) {
				appFrame.print(); } };
		
		aFilePrintSetup = new AbstractAction("Page Setup...") {
			public void actionPerformed(ActionEvent e) {
				PrinterDialog.showPageSetupDialog(appFrame); } };

		aFileExit = new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); } };

		
		aTrait = new AbstractAction("Associate Trait Data...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.associateTraitData(); } };
		
		aTraitView = new AbstractAction("View Trait Data...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.viewTraitData(); } };
		
		aSelect = new AbstractAction("Select Markers...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.selectMarkers(1); } };
		
		aSelectAll = new AbstractAction("Select All") {
			public void actionPerformed(ActionEvent e) {
				appFrame.selectMarkers(2); } };
		
		aSelectNone = new AbstractAction("Select None") {
			public void actionPerformed(ActionEvent e) {
				appFrame.selectMarkers(3); } };
		
		aSelectInvt = new AbstractAction("Invert Selection") {
			public void actionPerformed(ActionEvent e) {
				appFrame.selectMarkers(4); } };
		
		aMove = new AbstractAction("Move Marker to Group...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.moveMarkers(); } };

		
		aAnalysisCluster = new AbstractAction("Run Clustering...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.runCluster(); } };
		
		aAnalysisSim = new AbstractAction("Run Marker Ordering...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.runOrdering(); } };
		
		aAnalysisAnova = new AbstractAction("Run Analysis of Variance...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.runANOVA(); } };
		
		aAnalysisQTL = new AbstractAction("Run QTL Analysis...") {
			public void actionPerformed(ActionEvent e) {
				appFrame.runQTL(); } };
		
		aAnalysisMap = new AbstractAction("Generate Linkage Map") {
			public void actionPerformed(ActionEvent e) {
				appFrame.makeLinkageMap(); } };
		
		aAnalysisLog = new AbstractAction("View Project Log...") {
			public void actionPerformed(ActionEvent e) {
				new LogViewerDialog(appFrame, appFrame.getProject()); } };
		
		aAnalysisRemove = new AbstractAction("Remove Results") {
			public void actionPerformed(ActionEvent e) {
				appFrame.removeAnalysis(); } };
	}
	
	private void createFileMenu()
	{
		mFile = new JMenu("File");
		mFile.setMnemonic(KeyEvent.VK_F);
		
		mFileRecent = new JMenu("Recent Projects");
		mFileRecent.setMnemonic(KeyEvent.VK_R);
		setRecentMenu("");

		mFileNewProject = getItem(aFileNewProject,
			KeyEvent.VK_N, KeyEvent.VK_N, KeyEvent.CTRL_MASK, false);
		mFileOpenProject = getItem(aFileOpenProject,
			KeyEvent.VK_O, KeyEvent.VK_O, KeyEvent.CTRL_MASK, false);
		mFileImport = getItem(aFileImport,
			KeyEvent.VK_I, 0, 0, false);
		mFileSave = getItem(aFileSave,
			KeyEvent.VK_S, KeyEvent.VK_S, KeyEvent.CTRL_MASK, false);
		mFileSaveAs = getItem(aFileSaveAs,
			0, 0, 0, false);
		mFileSaveAs.setDisplayedMnemonicIndex(5);
		mFilePrint = getItem(aFilePrint,
			KeyEvent.VK_P, KeyEvent.VK_P, KeyEvent.CTRL_MASK, false);
		mFilePrintSetup = getItem(aFilePrintSetup,
			KeyEvent.VK_U, 0, 0, false);
		mFileExit = getItem(aFileExit,
			KeyEvent.VK_X, 0, 0, false);
		
		mFile.add(mFileNewProject);
		mFile.add(mFileOpenProject);
		mFile.addSeparator();
		mFile.add(mFileSave);
		mFile.add(mFileSaveAs);
		mFile.addSeparator();
		mFile.add(mFileImport);
		mFile.addSeparator();
		mFile.add(mFilePrint);
		mFile.add(mFilePrintSetup);
		mFile.addSeparator();
		mFile.add(mFileRecent);
		mFile.addSeparator();
		mFile.add(mFileExit);
		
		add(mFile);
	}
	
	private void createMarkersMenu()
	{
		mMarkers = new JMenu("Markers");
		mMarkers.setMnemonic(KeyEvent.VK_M);
		
		mMarkersSelect = new JMenu("Select");
		mMarkersSelect.setMnemonic(KeyEvent.VK_S);
		
		mTrait = getItem(aTrait,
			KeyEvent.VK_A, 0, 0, false);
		mTraitView = getItem(aTraitView,
			KeyEvent.VK_V, 0, 0, false);
		mSelect = getItem(aSelect,
			KeyEvent.VK_S, KeyEvent.VK_F6, 0, false);
		mSelectAll = getItem(aSelectAll,
			KeyEvent.VK_A, 0, 0, false);
		mSelectNone = getItem(aSelectNone,
			KeyEvent.VK_N, 0, 0, false);
		mSelectInvt = getItem(aSelectInvt,
			KeyEvent.VK_I, 0, 0, false);
		
		mMove = getItem(aMove,
			KeyEvent.VK_M, KeyEvent.VK_M, KeyEvent.CTRL_MASK, false);
		
//		mMarkersSelect.add(mSelect);
//		mMarkersSelect.addSeparator();
//		mMarkersSelect.add(mSelectAll);
//		mMarkersSelect.add(mSelectNone);
//		mMarkersSelect.add(mSelectInvt);
		
		mMarkers.add(mTrait);
		mMarkers.add(mTraitView);
//		mMarkers.add(mMarkersSelect);
		mMarkers.addSeparator();
		mMarkers.add(mSelect);
		mMarkers.add(mSelectAll);
		mMarkers.add(mSelectNone);
		mMarkers.add(mSelectInvt);
		mMarkers.addSeparator();
		mMarkers.add(mMove);
		
		add(mMarkers);
	}
	
	private void createAnalysisMenu()
	{
		mAnalysis = new JMenu("Analysis");
		mAnalysis.setMnemonic(KeyEvent.VK_A);

		mAnalysisCluster = getItem(aAnalysisCluster,
			KeyEvent.VK_C, KeyEvent.VK_1, KeyEvent.ALT_MASK, false);
		mAnalysisSim = getItem(aAnalysisSim,
			KeyEvent.VK_O, KeyEvent.VK_2, KeyEvent.ALT_MASK, false);
		mAnalysisAnova = getItem(aAnalysisAnova,
			KeyEvent.VK_A, KeyEvent.VK_4, KeyEvent.ALT_MASK, false);
		mAnalysisQTL = getItem(aAnalysisQTL,
			KeyEvent.VK_Q, KeyEvent.VK_5, KeyEvent.ALT_MASK, false);
		mAnalysisMap = getItem(aAnalysisMap,
			KeyEvent.VK_G, KeyEvent.VK_3, KeyEvent.ALT_MASK, false);
		
		mAnalysisLog = getItem(aAnalysisLog,
			KeyEvent.VK_V, 0, 0, false);
		mAnalysisRemove = getItem(aAnalysisRemove,
			KeyEvent.VK_R, 0, 0, false);
		
		mAnalysis.add(mAnalysisCluster);
		mAnalysis.add(mAnalysisSim);
		mAnalysis.add(mAnalysisMap);
		mAnalysis.add(mAnalysisAnova);
		mAnalysis.add(mAnalysisQTL);		
		mAnalysis.addSeparator();
		mAnalysis.add(mAnalysisLog);
		mAnalysis.addSeparator();
		mAnalysis.add(mAnalysisRemove);
		
		add(mAnalysis);
	}
	
	private void createHelpMenu()
	{
		JMenu mHelp = new JMenu("Help");
		mHelp.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem mHelpAbout = new JMenuItem("About TetraploidMap...");
		mHelpAbout.setMnemonic(KeyEvent.VK_A);
		mHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String msg = "<html><b>TetraploidMap for Windows</b><br><br>"
					+ "Copyright &copy 2005-2006<br><br>"
					+ "Christine Hackett and Iain Milne<br>"
					+ "Biomathematics & Statistics Scotland<br><br>"
					+ "This software is licensed. Please see accompanying "
					+ "license file for details."
					+ "</html>";
				
				doe.MsgBox.msg(msg, doe.MsgBox.INF);
			}
		});
		
		mHelp.add(mHelpAbout);
		
		add(mHelp);
	}
	
	private JMenuItem getItem(Action act, int m, int k, int mask, boolean icon)
	{
		JMenuItem item = new JMenuItem(act);
		item.setMnemonic(m);
		if (k != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(k, mask));
//		if (icon)
//			item.setIcon(Icons.MENU);
				
		return item;
	}
	
	private void setInitialState()
	{
		aFileImport.setEnabled(false);
		aFileSave.setEnabled(false);
		aFileSaveAs.setEnabled(false);
		aFilePrint.setEnabled(false);
		
		aTrait.setEnabled(false);
		aTraitView.setEnabled(false);
		aSelect.setEnabled(false);
		aSelectAll.setEnabled(false);
		aSelectNone.setEnabled(false);
		aSelectInvt.setEnabled(false);
		aMove.setEnabled(false);
		
		aAnalysisCluster.setEnabled(false);
		aAnalysisSim.setEnabled(false);
		aAnalysisLog.setEnabled(false);
		aAnalysisAnova.setEnabled(false);
		aAnalysisQTL.setEnabled(false);
		aAnalysisMap.setEnabled(false);
		aAnalysisRemove.setEnabled(false);
	}
	
	void setProjectOpenedState(Project project)
	{
		aFileImport.setEnabled(true);
		aFileSave.setEnabled(false);
		aFileSaveAs.setEnabled(true);
		
		aAnalysisLog.setEnabled(true);
		
		updateRecentFileList(project);
	}
	
	public static void setMenusForLinkageGroupSelected(boolean state)
	{
		aSelect.setEnabled(state);
		aSelectAll.setEnabled(state);
		aSelectNone.setEnabled(state);
		aSelectInvt.setEnabled(state);
		
		aAnalysisCluster.setEnabled(state);
		aAnalysisSim.setEnabled(state);
		aAnalysisAnova.setEnabled(state);
	}
	
	void updateRecentFileList(Project project)
	{
		setRecentMenu(project.filename.getPath());
		appFrame.setTitle("TetraploidMap - " + project.filename.getName());
	}
	
	void setRecentMenu(String newStr)
	{
		mFileRecent.removeAll();
		int loc = -1;
		
		// First see if it already exists, and reorder the list if it does
		for (int i = 0; i < Prefs.gui_recent.size(); i++)
		{
			String value = (String) Prefs.gui_recent.get(i);
			
			if (value.equals(newStr))
				loc = i;
		}
		
		if (loc != -1)
		{
			Prefs.gui_recent.remove(loc);
			Prefs.gui_recent.addFirst(newStr);
		}
		else if (newStr.length() > 0)
			Prefs.gui_recent.addFirst(newStr);
			
		// Then ensure the list only contains 5 elements
		while (Prefs.gui_recent.size() > 5)
			Prefs.gui_recent.removeLast();
			
		// Finally, convert the list into menu items...
		for (int i = 0; i < Prefs.gui_recent.size(); i++)
		{
			String value = (String) Prefs.gui_recent.get(i);
			createRecentMenuItem(value, (i+1));
		}
		
		// ... and enable/disable the menu depending on its contents
		if (Prefs.gui_recent.size() == 0)
			mFileRecent.setEnabled(false);
		else
			mFileRecent.setEnabled(true);
	}
	
	private void createRecentMenuItem(final String filename, int shortcut)
	{
		JMenuItem item = new JMenuItem(shortcut + " " + filename);
		
		switch (shortcut)
		{
			case 1 : item.setMnemonic(KeyEvent.VK_1); break;
			case 2 : item.setMnemonic(KeyEvent.VK_2); break;
			case 3 : item.setMnemonic(KeyEvent.VK_3); break;
			case 4 : item.setMnemonic(KeyEvent.VK_4); break;
			case 5 : item.setMnemonic(KeyEvent.VK_5); break;
		}
			
		item.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				appFrame.openProject(filename);
		}});
		
		mFileRecent.add(item);
	}
}
package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import analyses.anova.*;
import analyses.cluster.*;
import analyses.findgeno.*;
import analyses.order.*;
import analyses.order.custom.*;
import analyses.order.ripple.*;
import analyses.order.sim.*;
import analyses.order.twopoint.*;
import analyses.simmatch.*;
import analyses.qtl.*;
import data.*;
import gui.exporter.*;
import gui.importer.*;
import gui.map.*;
import gui.nav.*;

import doe.*;

public class AppFrame extends JFrame
{
	private Prefs prefs;
	private Project project;
	
	private AppFrameMenuBar menubar;
	private AppFrameToolBar toolbar;
		
	static NavPanel navPanel;
	
	// Certain components only exist once, and it's therefore easier to get at
	// them via a static reference as many classes require access to them
	public static JSplitPane splits;
	public static MarkerDetails markerDetails;
	
	public AppFrame(Prefs preferences)
	{
		prefs = preferences;
		menubar = new AppFrameMenuBar(this);
		toolbar = new AppFrameToolBar();
		
		splits = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		navPanel = new NavPanel(this, splits);	
		markerDetails = new MarkerDetails(navPanel);
		
		
		add(toolbar, BorderLayout.NORTH);
		add(splits);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
				{ exit(); }
		});
		
		setJMenuBar(menubar);		
		setTitle("TetraploidMap");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setIconImage(Icons.APP.getImage());
		setVisible(true);
	}
	
	Project getProject() { return project; }
	
	void exit()
	{
		if (!okToContinue())
			return;
		
//		Prefs.gui_win_width = getWidth();
//		Prefs.gui_win_height = getHeight();
	
		prefs.savePreferences(System.getProperty("user.home") + Prefs.sepF
			+ ".TetraploidMap.txt");
		System.exit(0);
	}
	
	// Called whenever an option has been selected that would close the
	// current project. The user is therefore queried to find out if they
	// would like to save it first (or cancel the operation)
	boolean okToContinue()
	{
		if (project != null)
		{
			if (menubar.aFileSave.isEnabled())
			{
				int res = MsgBox.yesnocan("The current project has unsaved "
					+ "changes. Save now?", 0);
				
				if (res == JOptionPane.YES_OPTION)
				{
					if (Project.save(project, false))
						return true;
					else
						return false;
				}
				else if (res == JOptionPane.NO_OPTION)
					return true;
				else if (res == JOptionPane.CANCEL_OPTION ||
					res == JOptionPane.CLOSED_OPTION)
					return false;
			}
		}
		
		return true;
	}
	
	// Creates a new project
	void newProject()
	{
		if (!okToContinue())
			return;
		
		NewProjectDialog dialog = new NewProjectDialog(this);
		if (dialog.getProject() != null)
		{
			project = dialog.getProject();
		
			menubar.setProjectOpenedState(project);
			navPanel.clear();
			
//			MsgBox.msg("Project \"" + project.getName() + "\" successfully "
//				+ "created in " + project.getFile().getPath() + "\nYou can now "
//				+ "work with it by importing datasets via \"File->"
//				+ "import dataset\" on the menubar or with the \"Import\" "
//				+ " toolbar button.",
//				MsgBox.INF);
			MsgBox.msg("Project \"" + project.getName() + "\" successfully "
				+ "created. import datasets into it to begin analysis.",
				MsgBox.INF);
		}
	}
	
	// Opens an existing project
	void openProject(String filename)
	{
		if (!okToContinue())
			return;
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		Project temp = Project.open(filename);
		
		if (temp != null)
		{
			project = temp;
			project.logger.add("Session resumed.");
		
			menubar.setProjectOpenedState(project);
			navPanel.displayProject(project.getLinkageGroups());
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	// Saves the current project (either directly, or under a new name)
	void saveProject(boolean saveAs)
	{
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Project.save(project, saveAs);
		menubar.aFileSave.setEnabled(false);
		menubar.updateRecentFileList(project);
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	void importDataSet()
	{
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Prefs.gui_dir));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			
			LinkageGroup lGroup = null;
			
			// Read Excel
			if (file.getName().toLowerCase().endsWith(".xls"))
			{
				ImportExcelDialog dialog = new ImportExcelDialog(this, file);
				if (dialog.isOK())
					lGroup = dialog.getLinkageGroup();
			}
			// Or read plain text
			else
				lGroup = new ImportTest(file).getLinkageGroup();
				
			if (lGroup == null)
				return;

			FindGenoDialog fDialog = new FindGenoDialog(this, lGroup);
			if (fDialog.isOK() == false)
				return;
			
			SimMatchDialog sDialog = new SimMatchDialog(this, lGroup);
			if (sDialog.isOK() == false)
				return;
			
			navPanel.addRootLinkageGroup(lGroup);
			project.addLinkageGroup(lGroup);
			project.logger.add("Dataset " + file + " imported.");
		}
	}
	
	private void runFindGeno(LinkageGroup lGroup)
	{
		new FindGenoDialog(this, lGroup);
	}
	
	void runCluster()
	{		
		// Get the currently selected LinkageGroup from the navigation tree
		LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
		
		ClusterAnalysis cAnalysis = new ClusterAnalysis(this, lGroup);
		Cluster cluster = cAnalysis.getCluster();
		
		if (cluster != null)
		{
			lGroup.addCluster(cluster);
			System.out.println("Adding cluster: " + cluster.getName());
			navPanel.addCluster(cluster, lGroup, null);
			menubar.aFileSave.setEnabled(true);
			
			project.logger.add("Cluster analysis run on "
				+ navPanel.getSelectedLinkageGroupPathStr() + " - "
				+ cluster.getGroups().size() + " linkage group(s) created.");
		}
		
/*		Cluster cluster = new Cluster();
		ClusterDialog dialog = new ClusterDialog(this, lGroup, cluster);
			
		if (dialog.isOK() && cluster.getGroups().size() > 0)
		{
			lGroup.addCluster(cluster);
			navPanel.addCluster(cluster, null);
			menubar.aFileSave.setEnabled(true);
			
			project.logger.add("Cluster analysis run on "
				+ navPanel.getSelectedLinkageGroupPathStr() + " - "
				+ cluster.getGroups().size() + " linkage group(s) created.");
		}
		else if (dialog.isOK() && cluster.getGroups().size() == 0)
			MsgBox.msg("No linkage groups were returned by the analysis.",
				MsgBox.INF);
*/
	}
	
	void runOrdering()
	{
		// Get the currently selected LinkageGroup from the navigation tree
		LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
		if (lGroup.getSelectedMarkerCount() < 2)
		{
			MsgBox.msg("You must have at least 2 selected markers available to "
				+ "run the ordering algorithms.", MsgBox.ERR);
			return;
		}
		
		OrderSettingsDialog settings = new OrderSettingsDialog(this);
		if (settings.isOK() == false)
			return;
		
		long start = System.currentTimeMillis();
		
		
		// And clone it...
		LinkageGroup cGroup = lGroup.getClonedLinkageGroup(true, false);

// TWOPOINT	
		TwoPointDialog tpDialog = new TwoPointDialog(this, cGroup);
		if (tpDialog.isOK() == false)
			return;
		
		OrderedResult order = tpDialog.getOrderedResult();

// CUSTOM
		CustomDialog cuDialog = new CustomDialog(this, order, cGroup);
		if (cuDialog.isOK() == false)
			return;

// RIPPLE
		if (Prefs.sim_run_ripple)
		{
			RippleDialog rpDialog = new RippleDialog(this, order, cGroup);
			if (rpDialog.isOK() == false)
				return;
		}

// SIMANNEAL
		if (Prefs.sim_run_sim)
		{
			SimAnnealDialog smDialog = new SimAnnealDialog(this, order, cGroup);
			if (smDialog.isOK() == false)
				return;
		}
		
		// Final step is to monitor how long the analysis took...
		order.setSummary(
			new Summary(lGroup, (System.currentTimeMillis()-start)));
		
		lGroup.addOrderedResult(order);
		navPanel.addOrderedResult(order, lGroup, null);
		menubar.aFileSave.setEnabled(true);
		
		project.logger.add("Marker ordering run on "
			+ navPanel.getSelectedLinkageGroupPathStr());
	}
	
/*	void runSimAnneal()
	{
		// Get the currently selected LinkageGroup from the navigation tree
		LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
		
		OrderDialog dialog = new OrderDialog(this, null, lGroup, false);
		if (dialog.isOK() == false)
		{
			System.out.println("TwoPoint/SimAnneal failed");
			return;
		}
		
		OrderedResult order = dialog.getOrderedResult();
				
		// Did the user select to run SimAnneal - if so, we have to do it again
		if (dialog.isMultipleRun())
		{
//			LinkageGroup ordGrp = order.getLinkageGroup();
			
			dialog = new OrderDialog(this, order, lGroup, true);
			if (dialog.isOK() == false)
				return;
		}
		
		lGroup.addOrderedResult(order);
		navPanel.addOrderedResult(order, lGroup, null);
		menubar.aFileSave.setEnabled(true);
		
		project.logger.add("TwoPoint/SimAnneal analysis run on "
			+ navPanel.getSelectedLinkageGroupPathStr());
	}
*/
	
	void runQTL()
	{
		LinkageGroup root = navPanel.getRootLinkageGroupForDataSet();
		TraitFile tFile = root.getTraitFile();
		if (tFile == null)
		{
			MsgBox.msg("This dataset currently has no trait data associated "
				+ "with it. You must associate trait data before running a QTL "
				+ "analyis.", MsgBox.ERR);
			return;
		}
		
		OrderedResult order = navPanel.getCurrentOrderedResult();
		if (order.doesRowsContainZeros())
		{
			String msg = "One or more marker phases are currently undefined "
				+ "(set to 0000). Are you sure you want to continue with the "
				+ "QTL analysis at this stage?";
			if (MsgBox.yesno(msg, 1) != JOptionPane.YES_OPTION)
					return;
		}
		
		Object[] values = { "Parent 1", "Parent 2" };
		Object obj = JOptionPane.showInputDialog(MsgBox.frm,
			"Select which parent to use:", "Select Parent",
			JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
		if (obj == null)
			return;			
		int parent = (obj==values[0]) ? 1 : 2;
		
		QTLDialog dialog = new QTLDialog(this, order, tFile, parent);
		if (dialog.isOK())
		{
			QTLResult qtlResult = dialog.getQTLResult();
			qtlResult.setTraitFile(tFile);
			
			order.addQTLResult(qtlResult);
			navPanel.addQTLResult(qtlResult, order, null);
			menubar.aFileSave.setEnabled(true);
		}
	}
	
	private boolean copyFile(File src, File des)
	{
		try
		{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(des));
			
			int byteRead = in.read();			
			while (byteRead != -1)
			{
				out.write(byteRead);
				byteRead = in.read();
			}
			
			in.close();
			out.close();
			
			return true;
		}
		catch (Exception e)
		{
			System.out.println(e);
			return false;
		}
	}
	
	void selectMarkers(int selectionType)
	{
		// Get the currently selected LinkageGroup from the navigation tree
		LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
		
		if (selectionType == 1)
		{
			SelectMarkersDialog dialog = new SelectMarkersDialog(this, lGroup);
			if (dialog.isOK() == false)
				return;
		}
		
		else if (selectionType == 2)
			SelectMarkers.selectAll(lGroup);
		
		else if (selectionType == 3)
			SelectMarkers.selectNone(lGroup);
		
		else if (selectionType == 4)
			SelectMarkers.selectInvt(lGroup);
				
		navPanel.markersUpdated();
		menubar.aFileSave.setEnabled(true);
	}
	
	void print()
	{
		IPrintable toPrint = navPanel.getSelectedPrintableNode();
		toPrint.print();
	}
	
	void removeAnalysis()
	{
		String msg = "Removing this analysis object will permanently remove "
			+ "all its results from the project. Continue?";
		if (MsgBox.yesno(msg, 1) == JOptionPane.YES_OPTION)		
			navPanel.removeAnalysis();
	}
	
	void associateTraitData()
	{
		LinkageGroup root = navPanel.getRootLinkageGroupForDataSet();
		if (root.getTraitFile() != null)
		{
			if (MsgBox.yesno("This dataset already has trait data associated "
				+ "with it. Are you sure you want to overwrite this data with "
				+ "new information?", 1) != JOptionPane.YES_OPTION)
				return;
		}
		
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Prefs.gui_dir));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			
			ImportTraitFile importer = new ImportTraitFile(file);
			if (importer.doImport() == false)
				return;
				
			TraitFile tFile = importer.getTraitFile();
			
//			navPanel.addRootLinkageGroup(lGroup);
//			project.addLinkageGroup(lGroup);
//			project.logger.add("Dataset " + file + " imported.");

			
			root.setTraitFile(tFile);
			
			AppFrameMenuBar.aFileSave.setEnabled(true);
			viewTraitData();
		}
	}
	
	void viewTraitData()
	{
		LinkageGroup root = navPanel.getRootLinkageGroupForDataSet();
		if (root.getTraitFile() == null)
		{
			MsgBox.msg("No trait information has been associated with this "
			 	+ "dataset yet.", MsgBox.INF);
				return;
		}
		
		new TraitViewerDialog(this, root.getTraitFile());
	}
	
	void makeLinkageMap()
	{
		LinkageGroup root = navPanel.getRootLinkageGroupForDataSet();
		OrderedResult order = navPanel.getCurrentOrderedResult();
		
		Object[] values = { "Parent 1", "Parent 2" };
		Object obj = JOptionPane.showInputDialog(MsgBox.frm,
			"Select which parent to use:", "Select Parent",
			JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
		if (obj == null)
			return;			
		int parent = (obj==values[0]) ? 1 : 2;
		
		LinkageAnalysis la = new LinkageAnalysis(order, parent);
		la.run();
		
		LinkageMapGraph map = new LinkageMapGraph(la.getGroups());
		
		order.addLinkageMapGraph(map);				
		navPanel.addLinkageMap(map, order, null);		
		AppFrameMenuBar.aFileSave.setEnabled(true);
	}
	
	void runANOVA()
	{
		LinkageGroup root = navPanel.getRootLinkageGroupForDataSet();
		if (root.getTraitFile() == null)
		{
			MsgBox.msg("No trait information has been associated with this "
			 	+ "dataset yet.", MsgBox.INF);
				return;
		}
		
		// Get the currently selected LinkageGroup from the navigation tree
		LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
		LinkageGroup cGroup = lGroup.getClonedLinkageGroup(true, false);
		
//		ClusterAnalysis cAnalysis = new ClusterAnalysis(this, lGroup);
//		Cluster cluster = cAnalysis.getCluster();
		
		AnovaDialog dialog = new AnovaDialog(this, cGroup, root.getTraitFile());
		if (dialog.isOK())
		{
			AnovaResult results = dialog.getAnovaResults();
			
			lGroup.addAnovaResults(results);
			navPanel.addAnovaResult(results, lGroup, null);
			menubar.aFileSave.setEnabled(true);
			
			project.logger.add("ANOVA run on "
				+ navPanel.getSelectedLinkageGroupPathStr() + ".");
		}
	}
	
	void moveMarkers()
	{
		// Get the current marker		
		CMarker cm = markerDetails.getSelectedMarker();
		
		// And move it to another cluster's linkagegroup
		Cluster cluster = navPanel.getCurrentClusterHeadNode();
		if (cluster != null)
		{
			LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
			new MoveMarker(cm, lGroup, cluster);
			
			navPanel.refreshTree();
			markerDetails.displayLinkageGroup(lGroup);
		}
	}
}
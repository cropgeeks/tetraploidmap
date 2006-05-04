package gui.nav;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import data.*;
import gui.*;
import gui.map.*;

public class NavPanel extends JPanel implements TreeSelectionListener
{
	private DefaultMutableTreeNode root = null;
	private DefaultTreeModel model = null;
	private JTree tree;
	private JScrollPane sp;
	
	// Reference to the SplitsPane for assigning it panels
	private AppFrame appFrame;
	private JSplitPane splits;
	private JPanel blankPanel;
	private GradientPanel titlePanel;
	
	public NavPanel(AppFrame appFrame, JSplitPane splits)
	{
		this.appFrame = appFrame;
		this.splits = splits;
		
		root = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(root);
		
		tree = new JTree(model);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new NavPanelRenderer());
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		sp = new JScrollPane(tree);		
		setLayout(new BorderLayout());
		titlePanel = new GradientPanel("Datasets");
		add(titlePanel, BorderLayout.NORTH);
		add(sp);
		
		splits.setLeftComponent(this);
		blankPanel = new JPanel();
		blankPanel.setBackground(Color.white);
		splits.setRightComponent(blankPanel);
		splits.setDividerLocation(140);
	}
	
	public void clear()
	{
		while (root.getChildCount() > 0)
			model.removeNodeFromParent((MutableTreeNode)root.getChildAt(0));
		titlePanel.setTitle("Datasets");
		
		int location = splits.getDividerLocation();
		splits.setRightComponent(blankPanel);
		splits.setDividerLocation(location);
	}
	
	// Clears and updates the tree to display a recently opened Project file
	public void displayProject(LinkedList<LinkageGroup> groups)
	{
		clear();
		
		for (LinkageGroup lGroup: groups)
			addRootLinkageGroup(lGroup);
	}
	
	public void addRootLinkageGroup(LinkageGroup lGroup)
	{		
		// Create the nodes for the group's folder and its markers
		DefaultMutableTreeNode dataNode =
			new DefaultMutableTreeNode(lGroup.getName());
		DefaultMutableTreeNode node = addMarkerGroup(dataNode, lGroup);

		// Update the tree with the new node(s)
		model.insertNodeInto(dataNode, root, root.getChildCount());
		tree.setSelectionPath(new TreePath(node.getPath()));
		tree.scrollPathToVisible(new TreePath(node.getPath()));
		
		titlePanel.setTitle("Datasets (" + root.getChildCount() + ")");
    }
    
    private DefaultMutableTreeNode addMarkerGroup(DefaultMutableTreeNode parent, LinkageGroup lGroup)
    {
    	DefaultMutableTreeNode node = new DefaultMutableTreeNode(
			new MarkersNode(lGroup));
		parent.add(node);
		
		// Add any result clusters/twopoint to it
		for (Object obj: lGroup.getResults())
		{
			if (obj instanceof Cluster)
				addCluster((Cluster)obj, lGroup, parent);
			else if (obj instanceof OrderedResult)
				addOrderedResult((OrderedResult)obj, lGroup, parent);
			else if (obj instanceof AnovaResult)
				addAnovaResult((AnovaResult)obj, lGroup, parent);
		}
		
		return node;
    }
    
    public void addCluster(Cluster cluster, LinkageGroup owner, DefaultMutableTreeNode parent)
    {
    	// What node should we add it to?
    	if (parent == null)
    		parent = (DefaultMutableTreeNode) getSelectedTreeNode().getParent();
    	
    	// Create a folder for it
    	DefaultMutableTreeNode cFolder =
    		new DefaultMutableTreeNode(new ClusterNode(cluster, owner));
    		
    	// Add the summary and dendrogram nodes
		cFolder.add(new DefaultMutableTreeNode(new SummaryNode(cluster.getSummary())));
		cFolder.add(new DefaultMutableTreeNode(new DendrogramsNode(cluster)));
		
		// Then the individual linkage groups
		for (LinkageGroup lGroup: cluster.getGroups())
		{
			DefaultMutableTreeNode gNode =
				new DefaultMutableTreeNode(new GroupsNode(lGroup));
			addMarkerGroup(gNode, lGroup);
			cFolder.add(gNode);
		}
    	
    	model.insertNodeInto(cFolder, parent, parent.getChildCount());
    	tree.expandPath(new TreePath(cFolder.getPath()));
    	tree.scrollPathToVisible(new TreePath(cFolder.getPath()));
    }
    
    public void addAnovaResult(AnovaResult result, LinkageGroup owner, DefaultMutableTreeNode parent)
    {
    	// What node should we add it to?
    	if (parent == null)
    		parent = (DefaultMutableTreeNode) getSelectedTreeNode().getParent();
    	
    	DefaultMutableTreeNode node =
			new DefaultMutableTreeNode(new AnovaNode(result));
		
		model.insertNodeInto(node, parent, parent.getChildCount());
		tree.setSelectionPath(new TreePath(node.getPath()));
    	tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
    
    public void addOrderedResult(OrderedResult order, LinkageGroup owner, DefaultMutableTreeNode parent)
    {
    	// What node should we add it to?
    	if (parent == null)
    		parent = (DefaultMutableTreeNode) getSelectedTreeNode().getParent();
    	
    	// Create a folder for it
    	DefaultMutableTreeNode oFolder =
    		new DefaultMutableTreeNode(new OrderFolderNode(order, owner));
    	
    	// Add the nodes
		oFolder.add(new DefaultMutableTreeNode(new SummaryNode(order.getSummary())));
		oFolder.add(new DefaultMutableTreeNode(new OrderedNode(order, this)));
		
		if (order.getQTLResults() != null)
			for (QTLResult qtlResult: order.getQTLResults())
				addQTLResult(qtlResult, order, oFolder);
		if (order.getLinkageMaps() != null)
			for (LinkageMapGraph map: order.getLinkageMaps())
				addLinkageMap(map, order, oFolder);
    	    	
    	model.insertNodeInto(oFolder, parent, parent.getChildCount());
    	tree.setSelectionPath(new TreePath(oFolder.getPath()));
    	tree.scrollPathToVisible(new TreePath(oFolder.getPath()));
    }

	public void addLinkageMap(LinkageMapGraph map, OrderedResult owner, DefaultMutableTreeNode parent)
	{
		// What node should we add it to?
    	if (parent == null)
    		parent = (DefaultMutableTreeNode) getSelectedTreeNode().getParent();
		
		DefaultMutableTreeNode node =
			new DefaultMutableTreeNode(new MapNode(owner, map));
		
		model.insertNodeInto(node, parent, parent.getChildCount());
		tree.setSelectionPath(new TreePath(node.getPath()));
    	tree.scrollPathToVisible(new TreePath(node.getPath()));
	}
	
	public void addQTLResult(QTLResult qtlResult, OrderedResult owner, DefaultMutableTreeNode parent)
	{
		// What node should we add it to?
    	if (parent == null)
    		parent = (DefaultMutableTreeNode) getSelectedTreeNode().getParent();
		
		DefaultMutableTreeNode node =
			new DefaultMutableTreeNode(new QTLNode(qtlResult, owner));
		
		model.insertNodeInto(node, parent, parent.getChildCount());
		tree.setSelectionPath(new TreePath(node.getPath()));
    	tree.scrollPathToVisible(new TreePath(node.getPath()));
	}
    
    // Called when auto-selection of markers has taken place. The correct table
    // containing these markers must be retrieved and updated
    public void markersUpdated()
    {
    	MarkersNode node = (MarkersNode) getSelectedTreeNode().getUserObject();
//    	node.markerDetails.selectedMarkersChanged(node.lGroup);
		appFrame.markerDetails.selectedMarkersChanged(node.lGroup);
    }
    
    public void valueChanged(TreeSelectionEvent e)
    {
    	AppFrameMenuBar.setMenusForLinkageGroupSelected(false);
    	AppFrameMenuBar.aAnalysisQTL.setEnabled(false);
    	AppFrameMenuBar.aAnalysisMap.setEnabled(false);
    	AppFrameMenuBar.aAnalysisRemove.setEnabled(false);
    	AppFrameMenuBar.aMove.setEnabled(false);
    	AppFrameMenuBar.aTrait.setEnabled(false);
    	AppFrameMenuBar.aTraitView.setEnabled(false);
    	
    	DefaultMutableTreeNode n = (DefaultMutableTreeNode)
    		tree.getLastSelectedPathComponent();

		if (n == null) return;
		
		Object nodeInfo = n.getUserObject();
				
		int location = splits.getDividerLocation();
		
		if (nodeInfo instanceof MarkersNode)
		{
			MarkersNode node = (MarkersNode) nodeInfo;
//			splits.setRightComponent(node.markerDetails);

			// Copy the linkage group to the display table
			appFrame.markerDetails.displayLinkageGroup(node.lGroup);
			// Then make the table visible
			splits.setRightComponent(appFrame.markerDetails);
			
			AppFrameMenuBar.setMenusForLinkageGroupSelected(true);
			AppFrameMenuBar.aTrait.setEnabled(true);
			AppFrameMenuBar.aTraitView.setEnabled(true);
		}
		else if (nodeInfo instanceof ClusterNode)
		{
			AppFrameMenuBar.aAnalysisRemove.setEnabled(true);
			ClusterNode node = (ClusterNode) nodeInfo;
			splits.setRightComponent(new JLabel("" + node, JLabel.CENTER));
		}
		else if (nodeInfo instanceof AnovaNode)
		{
			AnovaNode node = (AnovaNode) nodeInfo;
			splits.setRightComponent(node.panel);
		}
		else if (nodeInfo instanceof OrderFolderNode)
		{
			AppFrameMenuBar.aAnalysisRemove.setEnabled(true);
			OrderFolderNode node = (OrderFolderNode) nodeInfo;
			splits.setRightComponent(new JLabel("" + node, JLabel.CENTER));
		}
		else if (nodeInfo instanceof GroupsNode)
		{
			GroupsNode node = (GroupsNode) nodeInfo;
			splits.setRightComponent(node.getLabel());
		}
		else if (nodeInfo instanceof OrderedNode)
		{
			OrderedNode node = (OrderedNode) nodeInfo;
			splits.setRightComponent(node.panel);
			
			AppFrameMenuBar.aAnalysisQTL.setEnabled(true);
			AppFrameMenuBar.aAnalysisMap.setEnabled(true);
			AppFrameMenuBar.aTrait.setEnabled(true);
			AppFrameMenuBar.aTraitView.setEnabled(true);
		}
		else if (nodeInfo instanceof DendrogramsNode)
		{
			DendrogramsNode node = (DendrogramsNode) nodeInfo;
			splits.setRightComponent(node.panel);
		}
		else if (nodeInfo instanceof SummaryNode)
		{
			SummaryNode node = (SummaryNode) nodeInfo;
			splits.setRightComponent(node.panel);
		}
		else if (nodeInfo instanceof MapNode)
		{
			MapNode node = (MapNode) nodeInfo;
			splits.setRightComponent(node.panel);
		}
		else if (nodeInfo instanceof QTLNode)
		{
			QTLNode node = (QTLNode) nodeInfo;
			splits.setRightComponent(node.panel);
		}
		else
			splits.setRightComponent(new JLabel("" + n, JLabel.CENTER));
		
		// Can the user print now?
		if (nodeInfo instanceof IPrintable && ((IPrintable)nodeInfo).isPrintable())
			AppFrameMenuBar.aFilePrint.setEnabled(true);
		else
			AppFrameMenuBar.aFilePrint.setEnabled(false);
					
		splits.setDividerLocation(location);
	}
	
	public LinkageGroup getSelectedLinkageGroup()
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			tree.getSelectionModel().getSelectionPath().getLastPathComponent();
		
		Object obj = node.getUserObject();
		if (obj instanceof MarkersNode)
			return ((MarkersNode)obj).lGroup;
		
		return null;
	}
	
	public String getSelectedLinkageGroupPathStr()
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			tree.getSelectionModel().getSelectionPath().getLastPathComponent();
		
		String path = "";
		TreeNode[] nodes = node.getPath();
		for (int i = 1; i < nodes.length - 1; i++)
		{
			path += "" + nodes[i];
			if (i != (nodes.length-2))
				path += "->";
		}
			
		return path;
	}
	
	public Cluster getCurrentClusterHeadNode()
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			tree.getSelectionModel().getSelectionPath().getLastPathComponent();
		
		while (true)
		{
			if (node.getParent() == null)
				return null;
				
			Object o = node.getUserObject();
			if (o instanceof ClusterNode)
				return ((ClusterNode)node.getUserObject()).cluster;
			else			
				node = (DefaultMutableTreeNode) node.getParent();
		}		
	}
	
	// Get the root linkage group for the currently selected dataset
	public LinkageGroup getRootLinkageGroupForDataSet()
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			tree.getSelectionModel().getSelectionPath().getLastPathComponent();
		
		TreeNode[] path = node.getPath();
		Object obj =
			((DefaultMutableTreeNode)path[1].getChildAt(0)).getUserObject();
		return ((MarkersNode)obj).lGroup;
	}
	
	DefaultMutableTreeNode getSelectedTreeNode()
	{
		return (DefaultMutableTreeNode)
			tree.getSelectionModel().getSelectionPath().getLastPathComponent();
	}
	
	// This can only be called when NavPanel has allowed the aFilePrint action
	// to be enabled, hence this method can ONLY return an IPrintable object
	public IPrintable getSelectedPrintableNode()
	{
		DefaultMutableTreeNode node = getSelectedTreeNode();
		return (IPrintable) node.getUserObject();			
	}
	
	public OrderedResult getCurrentOrderedResult()
	{
		DefaultMutableTreeNode node = getSelectedTreeNode();
		return ((OrderedNode) node.getUserObject()).order;
	}
	
	public void removeAnalysis()
	{
		DefaultMutableTreeNode node = getSelectedTreeNode();
		
		Object obj = node.getUserObject();
		
		if (obj instanceof ClusterNode)
		{
			ClusterNode cNode = (ClusterNode) obj;
			cNode.lGroup.getResults().remove(cNode.cluster);
		}
		else if (obj instanceof OrderFolderNode)
		{
			OrderFolderNode oNode = (OrderFolderNode) obj;
			oNode.lGroup.getResults().remove(oNode.order);
		}
		
		model.removeNodeFromParent(node);
		AppFrameMenuBar.aFileSave.setEnabled(true);
		
		int location = splits.getDividerLocation();
		splits.setRightComponent(blankPanel);
		splits.setDividerLocation(location);
	}
	
	public void refreshTree()
	{
		tree.updateUI();
	}
}
	









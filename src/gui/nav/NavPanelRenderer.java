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

class NavPanelRenderer extends JLabel implements TreeCellRenderer
{
	private static Color bColor = UIManager.getColor("Tree.selectionBackground");
	
	private boolean selected = false;
	
	public Component getTreeCellRendererComponent(
		JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();			
		setText(obj.toString());
		
		if (obj instanceof MarkersNode)
			setIcon(Icons.LINKAGE_GROUP);
		else if (obj instanceof GroupsNode)
		{
//			GroupsNode gNode = (GroupsNode) obj;
//			setText(gNode.name + " (" + gNode.markerCount + ")");
			
			if (expanded)
				setIcon(Icons.GROUP_OPEN);
			else
				setIcon(Icons.GROUP_FOLDER);
		}
		else if (obj instanceof ClusterNode)
		{
			if (expanded)
				setIcon(Icons.CLUSTER_OPEN);
			else
				setIcon(Icons.CLUSTER_FOLDER);
		}
		else if (obj instanceof SummaryNode)
			setIcon(Icons.SUMMARY);
		else if (obj instanceof OrderedNode)
			setIcon(Icons.ORDER);
		else if (obj instanceof DendrogramsNode)
			setIcon(Icons.DENDROGRAM);
		else if (obj instanceof MapNode)
			setIcon(Icons.MAP);
		else if (obj instanceof QTLNode)
			setIcon(Icons.QTL);
		else if (obj instanceof AnovaNode)
			setIcon(Icons.ANOVA);
		
		else if (expanded)
			setIcon(Icons.FOLDER_OPEN);
		else if (leaf)
			setIcon(new DefaultTreeCellRenderer().getLeafIcon());
		else
			setIcon(Icons.FOLDER);
		
		this.selected = selected;
		
		if (selected)
			setForeground((Color) UIManager.get("Tree.selectionForeground"));
		else
			setForeground((Color) UIManager.get("Tree.foreground"));
		
		return this;
	}
	
	public void paintComponent(Graphics g)
	{
		Icon icon = getIcon();
		
		int offset = 0;
		if (icon != null && getText() != null)
			offset = (icon.getIconWidth() + getIconTextGap());
		
		if (selected)
		{
			g.setColor(bColor);
			g.fillRect(offset, 0, 500 - offset, getHeight() - 1);
		}
		
		super.paintComponent(g);
	}
}
	
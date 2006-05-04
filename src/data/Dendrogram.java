package data;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.tree.*;

import pal.gui.*;
import pal.misc.*;
import pal.tree.*;

// Class that represents (in various forms) a dendrogram, both internally (using
// the Swing tree classes for its structure), and visually (via PAL). The PAL
// representation of the class is formed by converting the similarity-output
// from CLUSTER into New Hampshire tree format.
public class Dendrogram implements Serializable
{
	static final long serialVersionUID = 4071739595558341040L;
	
	// Swing-structure tree
	private DefaultMutableTreeNode root;
	
	// PAL-structure tree
	private ReadTree palTree;
	
	// StringBuffer to (temporarily) hold the NH representation
	private StringBuffer nhStr = new StringBuffer(1000);
	
	private int colorSeed = 0;
	
	// Builds up a dendrogram tree using output from CLUSTER
	public Dendrogram(LinkageGroup lGroup, LinkedList<String> denList)
		throws CreationException
	{
		// We build up groups of Nodes, adding them to the linked-list. Nodes
		// will be removed from the list and readded as a single parent node,
		// eventually leading to the list only containing the root node
		LinkedList<DefaultMutableTreeNode> nodes =
			new LinkedList<DefaultMutableTreeNode>();
		
		// Create a node for each marker and add it to the list
		int i = 1;
		for (CMarker cm: lGroup.getMarkers())
		{
			if (cm.checked == false) continue;
						
			DenNode dNode = new DenNode(i++, cm, 0);
			nodes.add(new DefaultMutableTreeNode(dNode));
		}
		
		// Now traverse the list of similarity scores to group pairs of nodes
		for (String s: denList)
		{
//			System.out.println(s);
			
			StringTokenizer st = new StringTokenizer(s);
			st.nextToken();
			
			float dblDistance = Float.parseFloat(st.nextToken());
			
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new DenNode(i++, null, dblDistance));
			newNode.add(getNode(nodes, Integer.parseInt(st.nextToken())));
			newNode.add(getNode(nodes, Integer.parseInt(st.nextToken())));
			
			nodes.add(newNode);
		}
		
		// Now that we have a single node, use it to create the PAL tree
		root = nodes.get(0);
		createPALTree(lGroup);
	}
	
	// Returns the node whose "number" parameter matches the given index
	private DefaultMutableTreeNode getNode(LinkedList<DefaultMutableTreeNode> nodes, int index)
	{
		int i = 0;
		for (DefaultMutableTreeNode node: nodes)
		{
			if (((DenNode)node.getUserObject()).number == index)
				return nodes.remove(i);
			i++;
		}
		
		return null;
	}
	
	private void createPALTree(LinkageGroup lGroup)
		throws CreationException
	{
		// Build up the NH
		traverseNode(root, -1);
		
		// Get PAL to read it in
		try
		{
			StringReader reader = new StringReader(nhStr.toString());
			palTree = new ReadTree(new PushbackReader(reader));
			
			testPal(lGroup);
		}
		catch (TreeParseException e) {
			System.out.println(e);
			throw new CreationException(CreationException.NO_PAL_TREE);
		}
		
		// Clear the StringBuffer once finished with it
		nhStr.delete(0, nhStr.length()-1);
		nhStr.trimToSize();
	}
	
	// Traverses the tree in order to build up a NH representation with suitable
	// branch lengths (determined from CLUSTER'S dblDistance (dd) scores)
	private void traverseNode(TreeNode node, float dd)
	{
		DenNode dNode = (DenNode)
			((DefaultMutableTreeNode)node).getUserObject();
		
		// Child node: branch len = parent's dd / 2
		if (node.getChildCount() == 0)
//			nhStr.append(dNode.cm.marker.getName() + ":" + (dd/2));
			nhStr.append(dNode.cm.safeName + ":" + (dd/2));
			
		// Parent node:
		//  1) Traverse left and right children
		//  2) branch len = (parent's dd - dd) / 2
		else
		{
			nhStr.append("(");
			traverseNode(node.getChildAt(0), dNode.dblDistance);
			nhStr.append(",");
			traverseNode(node.getChildAt(1), dNode.dblDistance);
			nhStr.append(")");
			
			if (dd != -1)
				nhStr.append(":" + ((dd - dNode.dblDistance)/2));
			else
				nhStr.append(";");
		}
	}
	
	public ReadTree getPALTree() { return palTree; }
	
	public double getRootSimilarity()
	{
		return ((DenNode)root.getUserObject()).similarity;
	}
	
	public double getRootDistance()
	{
		return ((DenNode)root.getUserObject()).dblDistance;
	}
	
	// Returns the number of unique groups at the given level of similarity
	public int getGroupCount(double s)
	{
		return countGroups(root, 0, s, null);
	}
	
	private int countGroups(
		DefaultMutableTreeNode node, int count, double sim, NameColouriser nc)
	{
		DenNode dNode = (DenNode) node.getUserObject();
		
		if (dNode.similarity >= sim)
		{
			if (nc != null)
			{
				setColouriserNames(node, nc);
				colorSeed++;
			}
				
			return ++count;
		}
			
		else if (dNode.cm == null)
		{
			// Traverse left
			count = countGroups(
				(DefaultMutableTreeNode)node.getChildAt(0), count, sim, nc);
			// Traverse right
			count = countGroups(
				(DefaultMutableTreeNode)node.getChildAt(1), count, sim, nc);
		}
		
		return count;
	}
	
	// Builds a PAL NameColouriser object that contains marker-name/colour pairs
	// based on how many groups the dendrogram is to be split into
	public NameColouriser getColouriser(double s)
	{
		NameColouriser nc = new NameColouriser();
		colorSeed = 1;		
		countGroups(root, 0, s, nc);
		
		return nc;
	}
	
	private void setColouriserNames(DefaultMutableTreeNode node, NameColouriser nc)
	{
		DenNode dNode = (DenNode) node.getUserObject();
		
		if (dNode.cm != null)
		{
			Random r = new Random(colorSeed);
			Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
			
//			nc.addMapping(dNode.cm.marker.getName(), c);
			nc.addMapping("" + dNode.cm.marker.getDendrogramName(), c);
		}
		else
		{
			setColouriserNames((DefaultMutableTreeNode)node.getChildAt(0), nc);
			setColouriserNames((DefaultMutableTreeNode)node.getChildAt(1), nc);
		}
	}
	
	private class DenNode implements Serializable
	{
		int number;
		float dblDistance;
		float similarity;
		CMarker cm;		
		
		DenNode(int number, CMarker cm, float dblDistance)
		{
			this.number = number;
			this.cm = cm;
			this.dblDistance = dblDistance;
			
			similarity = 1 - dblDistance;
		}
	}
	
	// Converts SafeNames in the PAL Tree (MKR003, etc) back to their original
	// markers names
	private void testPal(LinkageGroup lGroup)
	{
		int count = palTree.getExternalNodeCount();
		
		for (int i = 0; i < palTree.getExternalNodeCount(); i++)
		{
			Node node = palTree.getExternalNode(i);
			
			String safeName = node.getIdentifier().getName();
			CMarker cm = lGroup.getMarkerBySafeName(safeName);
			node.setIdentifier(new Identifier(cm.marker.getDendrogramName()));
//				new Identifier(lGroup.getMarkerName(safeName)));
		}
	}
}
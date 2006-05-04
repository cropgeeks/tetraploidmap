package gui.nav;

import data.*;
import gui.*;

class DendrogramsNode implements IPrintable
{
	DendrogramsPanel panel;
	
	DendrogramsNode(Cluster cluster)
	{
		panel = new DendrogramsPanel(cluster);
	}
	
	public String toString() { return "Dendrograms"; }
	
	public boolean isPrintable() { return true; }
	
	public void print() { panel.print(); }
}
	
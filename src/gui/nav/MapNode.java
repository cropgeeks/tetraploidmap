package gui.nav;

import data.*;
import gui.*;
import gui.map.*;

class MapNode implements IPrintable
{
	LinkageMapGraph map;
	LinkageMapPanel panel;
	
	MapNode(OrderedResult order, LinkageMapGraph map)
	{
		this.map = map;
		panel = new LinkageMapPanel(order, map.getLinkageGroups());
	}
	
	public String toString() { return map.getName(); }
	
	public boolean isPrintable() { return true; }
	
	public void print() { panel.print(); }
}
	
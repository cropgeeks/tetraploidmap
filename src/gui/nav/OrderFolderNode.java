package gui.nav;

import data.*;
import gui.*;

class OrderFolderNode
{
	OrderedResult order;
	// The linkage group that owns this object
	LinkageGroup lGroup;
	
	OrderFolderNode(OrderedResult order, LinkageGroup lGroup)
	{
		this.order = order;
		this.lGroup = lGroup;
	}
	
	public String toString() { return order.toString(); }
}
	
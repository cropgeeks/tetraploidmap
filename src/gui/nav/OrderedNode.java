package gui.nav;

import data.*;
import gui.*;

class OrderedNode
{
	OrderedResult order;
	OrderedResultPanel panel;
	
	OrderedNode(OrderedResult order, NavPanel navPanel)
	{
		this.order = order;
		panel = new OrderedResultPanel(order, navPanel);
	}
	
	public String toString() { return "Details"; }
}
	
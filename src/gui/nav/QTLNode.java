package gui.nav;

import data.*;
import gui.*;

class QTLNode
{
	QTLResult qtlResult;
	QTLResultsPanel panel;
	
	QTLNode(QTLResult qtlResult, OrderedResult order)
	{
		this.qtlResult = qtlResult;
		panel = new QTLResultsPanel(qtlResult, order);
	}
	
	public String toString() { return qtlResult.getName(); }
}
	
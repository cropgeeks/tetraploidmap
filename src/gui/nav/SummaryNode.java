package gui.nav;

import data.*;
import gui.*;

class SummaryNode
{
	SummaryPanel panel;
	
	SummaryNode(Summary summary)
	{
		panel = new SummaryPanel(summary);
	}
	
	public String toString() { return "Summary"; }
}
	
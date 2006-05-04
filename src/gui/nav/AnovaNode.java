package gui.nav;

import data.*;
import gui.*;

class AnovaNode
{
	AnovaResult result;
	AnovaResultsPanel panel;
	
	AnovaNode(AnovaResult result)
	{
		this.result = result;
		panel = new AnovaResultsPanel(result);
	}
	
	public String toString() { return "" + result; }
}
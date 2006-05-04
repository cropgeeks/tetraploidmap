package gui;

import javax.swing.*;

import data.*;

import doe.*;

class MoveMarker
{
	MoveMarker(CMarker cm, LinkageGroup currentGroup, Cluster cluster)
	{
		System.out.println("Marker to move is " + cm);
		
		int groupCount = cluster.getGroups().size();
		Object[] values = new Object[groupCount-1];
		int i = 0;
		for (LinkageGroup lGroup: cluster.getGroups())
			if (lGroup != currentGroup)
				values[i++] = lGroup.getName();
		
		Object selectedValue = JOptionPane.showInputDialog(MsgBox.frm,
			"Select which group to move to:", "Move Marker",
			JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
		
		if (selectedValue == null)
			return;
		
		System.out.println("Selected " + selectedValue);
		for (LinkageGroup lGroup: cluster.getGroups())
		{
			if (lGroup.getName().equals(selectedValue) == false)
				continue;
			
			lGroup.addMarker(cm.marker);
			currentGroup.removeMarker(cm);
			
			break;
		}
		
		AppFrameMenuBar.aFileSave.setEnabled(true);
	}
}
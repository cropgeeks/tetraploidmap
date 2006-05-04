package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.*;

import doe.*;

public class SummaryPanel extends JPanel implements ActionListener
{
	private Summary summary;
	private JButton button;
	
	public SummaryPanel(Summary summary)
	{
		this.summary = summary;
		
		JLabel timeLabel = new JLabel(summary.getTimeSummary(), JLabel.CENTER);
		JLabel mkrsLabel = new JLabel(summary.getMarkersSummary(),
			JLabel.CENTER);
		
		button = new JButton("Click here to reselect these markers");
		button.addActionListener(this);
		JPanel bPanel = new JPanel(new FlowLayout());
		bPanel.add(button);
		
		DoeLayout layout = new DoeLayout();
		layout.add(timeLabel, 0, 0, 1, 1, new Insets(5, 5, 5, 5));
		layout.add(mkrsLabel, 0, 1, 1, 1, new Insets(5, 5, 5, 5));
		layout.add(bPanel, 0, 2, 1, 1, new Insets(5, 5, 5, 5));
		
		setLayout(new BorderLayout());
		add(layout.getPanel());
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == button)
		{
			SelectMarkers.selectNone(summary.getOriginalGroup());
			
			// For each marker that was selected...
			for (CMarker cmS: summary.getSelectedGroup().getMarkers())
			{
				// ...find and reselect it in the original group
				for (CMarker cmO: summary.getOriginalGroup().getMarkers())
					if (cmS.marker == cmO.marker)
					{
						cmO.checked = true;
						break;
					} 
			}
			
			int count = summary.getOriginalGroup().getSelectedMarkerCount();
			MsgBox.msg(count + " marker" + (count == 1 ? " has " : "s have ")
				+ "been reselected in the original linkage group.", MsgBox.INF);
		}
	}
}
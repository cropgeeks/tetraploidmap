package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.*;

class MarkerListName extends JPanel
{
	private JList list;
	private DefaultListModel model;
	
	MarkerListName()
	{
		model = new DefaultListModel();
		list = new JList(model);
		list.setCellRenderer(new MarkerRenderer());
		list.setEnabled(false);
		
		setLayout(new BorderLayout());
		add(list, BorderLayout.CENTER);
	}
	
	void setLinkageGroup(LinkageGroup lGroup)
	{
		model.clear();

		for (CMarker cm: lGroup.getMarkers())
		{
			Marker m = cm.marker;
			
			if (m.getAlleleCount() == 1)
				model.addElement(m.getName());
			else
				for (int a = 1; a <= m.getAlleleCount(); a++)
					model.addElement(m.getName() + "_AL_" + a);
		}
	}
	
	private class MarkerRenderer extends JLabel implements ListCellRenderer
	{
		String name = "";
	
		public MarkerRenderer()
		{
			// Don't paint behind the component
			setOpaque(true);
		}
		
		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object obj,
			int i, boolean iss, boolean chf)
		{
			name = " " + (String) obj;
			
			// Set the font
			setFont(new Font("Monospaced", Font.PLAIN,	Prefs.gui_mSize));
			setText(name);
						
		    // Set background/foreground colours
			if (iss)
			{ 
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
	
			return this;
		}
	}
}
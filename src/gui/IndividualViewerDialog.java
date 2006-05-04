package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.*;

class IndividualViewerDialog extends JDialog
{
	private Marker marker;
	
	private JTextArea text;
	
	IndividualViewerDialog(JFrame parent, Marker marker)
	{
		super(parent, marker.getName(), true);
		this.marker = marker;
		
		add(createControls());
		
		setPreferredSize(new Dimension(text.getPreferredSize().width+50, 300));
		pack();
		setLocationRelativeTo(parent);
		setResizable(false);
		setVisible(true);
	}
	
	private JPanel createControls()
	{
		JLabel label = new JLabel((marker.getAllele(0).getStateCount()-2)
			+ " individuals per allele (plus two parents). "
			+ marker.getMissingPercentage() + "% unknown.");
		
		text = new JTextArea(getData());
		text.setEditable(false);
		text.setFont(new Font("Monospaced", Font.PLAIN, 11));
		text.setMargin(new Insets(2, 5, 2, 5));
		
		JScrollPane sp = new JScrollPane(text);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p1.add(label, BorderLayout.NORTH);
		p1.add(sp);
		
		return p1;
	}
	
	private String getData()
	{
		String str = new String();
		
		for (int a = 0; a < marker.getAlleleCount(); a++)
		{
			str += "Allele " + (a+1) + "\n";
			
			int count = 0;
			for (AlleleState state: marker.getAllele(a).getStates())
			{
				str += " " + state.getState();
				if (++count == 40)
				{
					str += "\n";
					count = 0;
				}
			}
			
			if (a < marker.getAlleleCount()-1)
				str += "\n\n";
		}
		
		return str;
	}
}
package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import data.*;

public class AnovaResultsPanel extends JPanel implements ListSelectionListener
{
	private AnovaResult results;
	
	private JList traitList;
	private DefaultListModel traitModel;
	private JSplitPane splits;
	private JTextArea details;
	
	public AnovaResultsPanel(AnovaResult ar)
	{
		results = ar;
		
		// Trait listbox
		traitModel = new DefaultListModel();
		for (AnovaTraitResult atr: results.getResults())
			traitModel.addElement(atr);
		traitList = new JList(traitModel);
		traitList.addListSelectionListener(this);
		traitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp1 = new JScrollPane(traitList);
		sp1.setPreferredSize(new Dimension(150, 150));
		
		// Details text box
		details = new JTextArea();
		details.setFont(new Font("Monospaced", Font.PLAIN, 11));
		details.setMargin(new Insets(2, 5, 2, 5));
		details.setEditable(false);
		details.setTabSize(6);
		details.setBackground(Color.white);
		JScrollPane sp2 = new JScrollPane(details);
		
		JPanel p1 = new JPanel(new BorderLayout());
//		sp1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p1.setBorder(BorderFactory.createTitledBorder("Select trait:"));
		p1.add(sp1);
		
		JPanel p2 = new JPanel(new BorderLayout());
//		sp2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p2.setBorder(BorderFactory.createTitledBorder("ANOVA Results:"));
		p2.add(sp2);
		
		JPanel p3 = new JPanel(new BorderLayout(5, 5));
		p3.add(p1, BorderLayout.NORTH);
		p3.add(p2);
		
		setLayout(new BorderLayout());
		add(new GradientPanel("ANOVA Results"), BorderLayout.NORTH);
		add(p3);
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) return;
		
		AnovaTraitResult atr = (AnovaTraitResult) traitList.getSelectedValue();
		if (atr != null)
			displayTrait(atr);
		else
			details.setText("");
	}
	
	private void displayTrait(AnovaTraitResult atr)
	{
		String str = "";
		
		str = "Prefix      Marker               KWSig.   AVSig.     Mean(0)  "
			+ "Count(0)     Mean(1)  Count(1)         SED"
			+ "\n";
		
		for (int i = 0; i < atr.getResultCount(); i++)
		{
			str += "\n" + format(atr.getMarker(i).getPrefix(), 12);
			str += atr.getMarker(i).getName(20);
			str += atr.getData(i);
		}
		
		details.setText(str);
		details.setCaretPosition(0);
	}
	
	private String format(String str, int length)
	{
		if (str.length() > length)
			return str.substring(0, length);
		
		for (int i = str.length(); i < length; i++)
			str += " ";
		return str;	
	}
}
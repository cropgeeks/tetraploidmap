package gui.importer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import data.*;
import gui.*;

import doe.*;

import jxl.*;

public class ImportExcelDialog extends JDialog implements ActionListener
{
	private LinkageGroup lGroup = null;
	private File file = null;
	private boolean isOK = false;
	
	private JButton bOK, bCancel;
	private JList primary, additional;
	private JLabel icon;
	
	
	public ImportExcelDialog(AppFrame appFrame, File file)
	{
		super(appFrame, "Import Excel Dataset", true);
		this.file = file;
		
		setLayout(new BorderLayout());
		add(createControls());
		add(createButtons(), BorderLayout.SOUTH);
		
		if (initializeListBoxes() == false)
			return;
		
		pack();
		getRootPane().setDefaultButton(bOK);
		setResizable(false);
		setLocationRelativeTo(appFrame);
		setVisible(true);
	}
	
	private JPanel createControls()
	{
		JLabel label1 = new JLabel("Data in the Primary Worksheet should be in "
			+ "the format shown below:");
		JLabel label2 = new JLabel("If data on individuals continues onto "
			+ "additional worksheets they should be selected too. Columns");
		JLabel label3 = new JLabel("A, B, and C from the example above "
			+ "should not be included on these sheets.");
		icon = new JLabel(Icons.EXCEL);
		
		primary = new JList(new DefaultListModel());
		primary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp1 = new JScrollPane(primary);
		sp1.setPreferredSize(new Dimension(10, 130));
		sp1.setBorder(BorderFactory.createTitledBorder("Primary worksheet:"));
		additional = new JList(new DefaultListModel());
		additional.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane sp2 = new JScrollPane(additional);
		sp2.setPreferredSize(new Dimension(10, 130));
		sp2.setBorder(BorderFactory.createTitledBorder("Additional worksheets:"));
		
		DoeLayout doe = new DoeLayout();
		doe.add(label1, 0, 0, 1, 2, new Insets(0, 0, 5, 0));
		doe.add(icon, 0, 1, 1, 2, new Insets(0, 0, 5, 0));
		doe.add(label2, 0, 2, 1, 2, new Insets(0, 0, 0, 0));
		doe.add(label3, 0, 3, 1, 2, new Insets(0, 0, 10, 0));
		doe.add(sp1, 0, 4, 1, 1, new Insets(0, 0, 0, 5));
		doe.add(sp2, 1, 4, 1, 1, new Insets(0, 0, 0, 0));
		
		JPanel p1 = doe.getPanel();
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		return p1;
	}
	
	private JPanel createButtons()
	{
		bOK = new JButton("OK");
		bOK.addActionListener(this);
		bCancel = new JButton("Cancel");
		bCancel.addActionListener(this);
		
		JPanel p1 = new JPanel(new GridLayout(1, 2, 5, 5));
		p1.add(bOK);
		p1.add(bCancel);
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p2.add(p1);
		
		return p2;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
			load();
		
		if (e.getSource() == bCancel)
			setVisible(false);
	}
	
	// Copies the names of the worksheets into the listboxes
	private boolean initializeListBoxes()
	{
		Workbook workbook = null;
		DefaultListModel pModel = (DefaultListModel) primary.getModel();
		DefaultListModel aModel = (DefaultListModel) additional.getModel();
		
		try
		{
			workbook = Workbook.getWorkbook(file);
			
			Sheet[] sheets = workbook.getSheets();
			for (int i = 0; i < sheets.length; i++)
			{
				pModel.add(i, sheets[i].getName());
				aModel.add(i, sheets[i].getName());
			}
		}
		catch (Exception e)
		{
			MsgBox.msg("TetraploidMap was unable to read file " + file + " due "
				+ "to the following reason:\n" + e, MsgBox.ERR);
			
			if (workbook != null)
				workbook.close();
			return false;
		}
		
		workbook.close();
		return true;
	}
	
	private void load()
	{
		// Ensure that worksheets have been selected
		if (primary.getSelectedIndex() == -1)
		{
			MsgBox.msg("You must select the primary worksheet to load data "
				+ "from before continuing.", MsgBox.ERR);
			return;
		}
		
		// Load it...
		int mainSheet = primary.getSelectedIndex();
		int[] sheets = additional.getSelectedIndices();
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		lGroup = new ExcelAFLPReader(file, mainSheet, sheets).lGroup;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		// Then return to the main frame
		if (lGroup != null)
		{
			isOK = true;
			setVisible(false);
		}
	}
	
	public LinkageGroup getLinkageGroup() { return lGroup; }
	
	public boolean isOK() { return isOK; }
}
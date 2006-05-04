package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import data.*;

import doe.*;

class TraitViewerDialog extends JDialog implements ActionListener
{
	private TraitFile tFile;
	private TraitTableModel model;
	private JTable table;
	private JButton bClose;
	
	TraitViewerDialog(AppFrame appFrame, TraitFile tFile)
	{
		super(appFrame, "Trait Selection", true);
		this.tFile = tFile;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		
		add(createControls());
		add(createButtons(), BorderLayout.SOUTH);
		setSize(300, 250);		
		
//		pack();
		
		setLocationRelativeTo(appFrame);
		setResizable(false);
		setVisible(true);
	}
	
	private JPanel createButtons()
	{
		bClose = new JButton("Close");
		bClose.addActionListener(this);
		
		JPanel p1 = new JPanel(new GridLayout(1, 1, 5, 5));
		p1.add(bClose);
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p2.add(p1);
		
		return p2;
	}
	
	private JPanel createControls()
	{
		model = new TraitTableModel();
		for (int i = 0; i < tFile.getNames().size(); i++)
		{
			model.addRow(new Object[] {
				tFile.getEnabled().get(i),
				tFile.getNames().get(i)
			});
		}
		
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		JScrollPane sp = new JScrollPane(table);
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		p1.add(sp);
		
		return p1;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}
	
	private class TraitTableModel extends DefaultTableModel
	{
		String[] titles = new String[] {
			"Selected",
			"Trait Name"
		};
		
		Class[] types = new Class[] {
			Boolean.class,
			String.class
		};
		
		TraitTableModel()
		{
			for (int i = 0; i < titles.length; i++)
				addColumn(titles[i]);
			
			this.addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e)
				{
					if (e.getType() != TableModelEvent.UPDATE) return;
					
					int r = e.getFirstRow();
					int c = e.getColumn();
											
					boolean selected = (Boolean) getValueAt(r, c);
					tFile.getEnabled().set(r, selected);
					AppFrameMenuBar.aFileSave.setEnabled(true);
				}
			});
		}
		
		public String getColumnName(int c) { return titles[c]; }
	
		public Class getColumnClass(int c) { return types[c]; }
		
		public boolean isCellEditable(int r, int c)
		{
			return (c == 0);
		}
	}
}
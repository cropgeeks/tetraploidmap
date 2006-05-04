package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import javax.swing.table.*;

import data.*;
import gui.nav.*;

import doe.*;

public class MarkerDetails extends JPanel implements ListSelectionListener, TableModelListener
{
//	private static SplitsListener sListener = new SplitsListener();
	
	private MarkerTableModel model;
	private NavPanel navPanel;
	private TableSorter sorter;
	private JTable table;
	private JScrollPane sp;
	
	private MarkerSummaryPanel infoPanel = new MarkerSummaryPanel();
	private GradientPanel gP;
	private MarkerOverview overview;
	
	// Tracks how many markers have been selected
	private int totalCount = 0, selectedCount = 0;
	
	// True when the table is being auto-updated with new selection states
	boolean selectingMarkers = false;
	
	MarkerDetails(NavPanel navPanel)
	{
		createTable();
		sp = new JScrollPane(table);
		this.navPanel = navPanel;
				
		JSplitPane splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splits.setTopComponent(sp);
		splits.setBottomComponent(infoPanel);
//		splits.setResizeWeight(0.5);
		splits.setDividerLocation(300);
//		sListener.addPane(splits);
		
//		overview = new MarkerOverview(this, lGroup);
		gP = new GradientPanel("Marker Details");
//		gP.setLinkPanel(AppFrame.splits, overview, "Click for Overview");
		
		setLayout(new BorderLayout());
		add(gP, BorderLayout.NORTH);
		add(splits);
	}
	
	// Called when a new LinkageGroup is to be passed to this component for
	// display.
	public void displayLinkageGroup(LinkageGroup lGroup)
	{
		model.populateTable(lGroup);
		infoPanel.setMarker(null);
		
		selectedCount = lGroup.getSelectedMarkerCount();
		totalCount = lGroup.getMarkerCount();
		updateCountLabel();
	}
	
	// Called when auto-selection of markers has taken place. The model must be
    // updated to reflect changes in the markers' states.
	public void selectedMarkersChanged(LinkageGroup lGroup)
	{		
		selectingMarkers = true;		
		model.selectedMarkersChanged(lGroup);
		selectingMarkers = false;
		
		// No idea how many are selected now, so we'll need to recount
		selectedCount = 0;
		for (CMarker cm: lGroup.getMarkers())
			if (cm.checked)
				selectedCount++;
		
		// Need the repaint() call otherwise the cell renderers don't know to
		// update those rows that have now changed state
		updateCountLabel();
		table.repaint();		
	}
	
	private void createTable()
	{
		model = new MarkerTableModel();
		model.addTableModelListener(this);
//		model.populateTable(lGroup);

			table = new JTable();
		sorter = new TableSorter(model);
//		sorter.setTableHeader(table.getTableHeader());
		model.setTableSorter(sorter);

			table.setModel(sorter);

//		table = new JTable(sorter);
		createClickListener();
//		sorter.addMouseListenerToHeaderInTable(table);
		table.getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION);
		
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(this);
		
		////////////////////
		// Cell renderers...
		StringRenderer sr = new StringRenderer();
		StringRenderer fl = new StringRenderer();
					
		table.setDefaultRenderer(String.class, sr);
		table.setDefaultRenderer(Double.class, fl);
		table.setDefaultRenderer(Marker.class, sr);
		table.setDefaultRenderer(Integer.class, sr);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setPreferredWidth(180);
		
		table.getColumnModel().getColumn(6).setCellRenderer(
			new PhenotypeRenderer(1));
		table.getColumnModel().getColumn(7).setCellRenderer(
			new PhenotypeRenderer(2));
		table.getColumnModel().getColumn(10).setCellRenderer(
			new RatioSigRenderer());
		////////////////////
	}
	
	// Listens for changes in the selection of the table
	public void valueChanged(ListSelectionEvent e)
	{
		// Ignore extra messages.
		if (e.getValueIsAdjusting()) return;
		
		AppFrameMenuBar.aMove.setEnabled(false);
		
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty() == false)
		{
			int selectedRow = lsm.getMinSelectionIndex();
			Object o = sorter.getValueAt(selectedRow, 2);
			infoPanel.setMarker(((CMarker)o).marker);
		}
	}
	
	CMarker getSelectedMarker()
	{
		return (CMarker) sorter.getValueAt(table.getSelectedRow(), 2);
	}
	
	// Listens for double-clicks on the table, and opens a dialog that shows
	// information on a marker's individuals when detected
	public void createClickListener()
	{
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() != 2) return;
				
				int row = table.rowAtPoint(e.getPoint());
				Object obj = sorter.getValueAt(row, 2);
				if (obj != null)
				{
					Marker mkr = ((CMarker)obj).marker;
					
					if (e.isControlDown())
					{
						Cluster cluster = navPanel.getCurrentClusterHeadNode();
						if (cluster != null)
						{
							LinkageGroup lGroup = navPanel.getSelectedLinkageGroup();
							new MoveMarker((CMarker)obj, lGroup, cluster);
						}
					}
					else					
						new IndividualViewerDialog(MsgBox.frm, mkr);
				}
			}
		});
	}
	
	// Listens for changes in the table's data model (what it's showing, not the
	// original data)
	public void tableChanged(TableModelEvent e)
	{
		if (table == null || selectingMarkers) return;
		if (e.getType() != TableModelEvent.UPDATE) return;
		
		int row = e.getFirstRow();
		
		CMarker cm = (CMarker) sorter.getValueAt(row, 2);
		
		boolean newState = ((Boolean) model.getValueAt(row, 3)).booleanValue();
		// Why do it this way? Because it seems it's possible for the table to
		// generate tableChanged() events even when the checkbox hasn't actually
		// changed its value
		if (cm.checked != newState)
		{		
			if (cm.checked) selectedCount--;
			else selectedCount++;
		}
		cm.checked = newState;
		
		updateCountLabel();		
		table.repaint();
		
		AppFrameMenuBar.aFileSave.setEnabled(true);
	}
	
	private void updateCountLabel()
	{
//		gP.setTitle("Marker Details (" + selectedCount + "/" + totalCount
//			+ " marker" + (totalCount == 1 ? "" : "s") + " selected)");
		gP.setTitle("Marker Details (" + selectedCount + "/" + totalCount
			+ " selected)");
	}
	
/*	private static class SplitsListener extends ComponentAdapter
	{
		private LinkedList<JSplitPane> panes = new LinkedList<JSplitPane>();
		
		void addPane(JSplitPane pane)
		{
			// Get the divider associated with this pane
			BasicSplitPaneDivider divider =
				((BasicSplitPaneUI)pane.getUI()).getDivider();
			
			// Set it to the current position that the others already have
			if (panes.size() > 0)
				pane.setDividerLocation(panes.get(0).getDividerLocation());
			else
				pane.setDividerLocation(300);

			// Add a listener for it
			divider.addComponentListener(this);
			panes.add(pane);
		}
		
		public void componentMoved(ComponentEvent e)
		{
			BasicSplitPaneDivider d = (BasicSplitPaneDivider) e.getSource();
			JSplitPane pane = (JSplitPane) d.getParent();
			if (pane.isShowing() == false)
				return;
			
			// Set all panes to have the same divider location
			int location = pane.getDividerLocation();
			for (JSplitPane p: panes)
				p.setDividerLocation(location);
		}
	}
*/
	
	private static Color bgClr = (Color) UIManager.get("Table.background");
	private static Color bgSel = (Color) UIManager.get("Table.selectionBackground");
	private static Color fgSel = (Color) UIManager.get("Table.selectionForeground");
	private static Color fgNor = (Color) UIManager.get("Table.foreground");
//	private static Color fgSel = Color.black;
	private static Color fgBlu = Color.blue;
	private static Color fgLht = new Color(200, 200, 200);
	private static Color fgRed = Color.red;
	
	// PhenotypeRenderer colours the Strings formatting the two parental
	// phenotypes into BLUE for present-in-that-parent or BLACK for not
	private class PhenotypeRenderer extends DefaultTableCellRenderer
	{		
		private int parent;		
		PhenotypeRenderer(int parent) { this.parent = parent; }
		
		public Component getTableCellRendererComponent(
			JTable t, Object o, boolean isSelected, boolean hasFocus, int r, int c)
		{
			CMarker cm = (CMarker) sorter.getValueAt(r, 2);
//			CMarker cm = (CMarker) model.getValueAt(r, 2);
			
			if (cm.checked)
			{
				if (cm.marker.isPresentInParent(parent))
					setForeground(fgBlu);
				else
					setForeground(fgNor);
			}
			else
				setForeground(fgLht);			
			
			setOpaque(isSelected);
			if (isSelected)
			{
				setBackground(bgSel);
				setForeground(fgSel);
			}
			else
				setBackground(bgClr);
			
			return super.getTableCellRendererComponent(
				t, o, false, hasFocus, r, c);
		}
	}
	
	// Highlights the ratio sig column in red if the value is less than 0.01
	private class RatioSigRenderer extends DefaultTableCellRenderer
	{		
		public Component getTableCellRendererComponent(
			JTable t, Object o, boolean isSelected, boolean hasFocus, int r, int c)
		{
			JLabel label = (JLabel) super.getTableCellRendererComponent(
				t, o, false, hasFocus, r, c);
			handleLabel(label, o);
			
			CMarker cm = (CMarker) sorter.getValueAt(r, 2);
//			CMarker cm = (CMarker) model.getValueAt(r, 2);
			
			double sig = cm.marker.getRatioSignificance();
			
			if (sig == 0)
				setText("0.0");
			else if (sig == 1)
				setText("1.0");
			else
				setText(d.format(sig));
			
			if (cm.checked)
			{
				if (sig < 0.01)
				{
					setForeground(fgRed);
					label.setIcon(Icons.WARNING);
				}
				else
				{
					setForeground(fgNor);
					label.setIcon(null);
				}
			}
			else
				setForeground(fgLht);			
			
			setOpaque(isSelected);
			if (isSelected)
			{
				setBackground(bgSel);
				setForeground(fgSel);
			}
			else
				setBackground(bgClr);
			
			return label;
			
//			return super.getTableCellRendererComponent(
//				t, o, false, hasFocus, r, c);
		}
	}
	
	private static DecimalFormat d = new DecimalFormat("0.0000");
	
	private class StringRenderer extends DefaultTableCellRenderer
	{		
		public Component getTableCellRendererComponent(
			JTable t, Object o, boolean isSelected, boolean hasFocus, int r, int c)
		{
			JLabel label = (JLabel) super.getTableCellRendererComponent(
				t, o, false, hasFocus, r, c);
			
			handleLabel(label, o);

			CMarker cm = (CMarker) sorter.getValueAt(r, 2);
//			CMarker cm = (CMarker) model.getValueAt(r, 2);
			
			if (o.toString().equals("bestRatioUnknown"))
			{
				label.setText("");
				label.setIcon(Icons.WARNING);
			}
			else
				label.setIcon(null);
			
			if (cm.checked)
				label.setForeground(fgNor);
//			else if (isSelected && !cm.checked || !cm.checked)
			else
				label.setForeground(fgLht);

			label.setOpaque(isSelected);
			if (isSelected)
			{
				label.setBackground(bgSel);
				label.setForeground(fgSel);
			}
			else
				label.setBackground(bgClr);

			return label;
				
//			return super.getTableCellRendererComponent(
//				t, o, false, hasFocus, r, c);
		}
	}
	
	private void handleLabel(JLabel label, Object o)
	{
		if (o.getClass().getSuperclass() == Number.class)
		{
			label.setHorizontalAlignment(JLabel.RIGHT);
			label.setHorizontalTextPosition(SwingConstants.RIGHT);
			Number n = (Number) o;
			if (n.doubleValue() % 1 != 0)
				label.setText(d.format(n.doubleValue()));
		}
		else
		{
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setHorizontalTextPosition(SwingConstants.LEFT);
		}
	}
}
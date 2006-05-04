package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.text.*;
import javax.swing.*;

import data.*;
import gui.pal.*;

import pal.tree.*;

import doe.*;

public class DendrogramCanvas extends JPanel implements Printable
{
	private final static Cursor DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor CROSSHR = new Cursor(Cursor.CROSSHAIR_CURSOR);
	
	private Dendrogram dendrogram = null;
	private TetraploidTreePainter painter = null;
		
	private JScrollPane sp;
	private JLabel label;
	private Dimension dimension;
	private boolean sizeToFit;
	
	private double min, max;
	private int xMouse = -1;
	
	public DendrogramCanvas(JScrollPane sp, Dendrogram dendrogram, JLabel label)
	{
		this.sp = sp;
		this.dendrogram = dendrogram;
		this.label = label;
		
		painter = new TetraploidTreePainter(dendrogram.getPALTree(), "", false);
		painter.setPenWidth(1);
		painter.setUsingColor(false);
		
		painter.min = dendrogram.getRootDistance();
		
		min = dendrogram.getRootSimilarity();
		max = 1;		
					
		dimension = new Dimension(painter.getPreferredSize().width + 50,
			painter.getPreferredSize().height + 50);
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{
				printSimilarity(e.getX());
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e)
			{
				xMouse = -1;
				setLabelState(0, false);
				
				repaint();
			}
			
			public void mouseClicked(MouseEvent e)
			{
				pickSimilarity(e.getX());
			}
		});
	}
	
	private void setLabelState(double sim, boolean show)
	{
		if (show)
		{
			label.setText(" Similarity: " + Prefs.d5.format(sim));
			label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			setCursor(CROSSHR);
		}
		else
		{
			setCursor(DEFAULT);
			label.setText("");
			label.setBorder(null);
		}
	}
	
	private void pickSimilarity(int x)
	{
		double sim = 0;
		
		if (sizeToFit)
			sim = painter.getSimilarityForX(x, sp.getSize().width);
		else
			sim = painter.getSimilarityForX(x, getSize().width);
		
		// Similarity scores out of range are of no interest
		if (sim > max || sim < min) return;
		
		updateColouriser(false, (float)sim);
	}
	
	private void printSimilarity(int x)
	{
		double sim = 0;
		
		if (sizeToFit)
			sim = painter.getSimilarityForX(x, sp.getSize().width);
		else
			sim = painter.getSimilarityForX(x, getSize().width);
		
		int oldMouse = xMouse;
		if (sim > max || sim < min)
		{
			setLabelState(0, false);
			xMouse = -1;
		}
		else
		{
			setLabelState(sim, true);
			xMouse = x;
		}
		
		// Using oldMouse saves repainting when the cursor isn't in a region
		// that should have the line drawn
		if (oldMouse != xMouse)
			repaint();
	}
	
	void updateColouriser(boolean prompt, float sim)
	{
		if (prompt)
		{
			String num = JOptionPane.showInputDialog(MsgBox.frm, "Enter the "
				+ "preferred similarity score to colour the dendrogram:", 
				"" + Prefs.gui_pick_similarity);
					
			if (num != null)
				try { sim = Prefs.gui_pick_similarity = Float.parseFloat(num); }
				catch (Exception e) { return; }
			else
				return;
		}
		
		int count = dendrogram.getGroupCount(sim);
		painter.setColouriser(dendrogram.getColouriser(sim));
		repaint();
		
		// Let the user know the result
		String msg = "At a similarity of " + Prefs.d5.format(sim)
			+ ", " + count + " group";			
		msg += ((count == 1) ? " was formed" : "s were formed.");
				
		MsgBox.msg(msg, MsgBox.INF);
	}
	
	void setSizeToFit(boolean sizeToFit)
	{
		this.sizeToFit = sizeToFit;
		sp.getViewport().setView(this);
		
		repaint();
	}
	
	public Dimension getPreferredSize()
	{
		if (sizeToFit)
			return new Dimension(0, 0);
		else
			return dimension;
	}
	
	public int print(Graphics graphics, PageFormat pf, int pageIndex)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		double panelWidth = pf.getImageableWidth();		//width in pixels
		double panelHeight = dimension.height;			//height in pixels
		
		double pageHeight = pf.getImageableHeight();	//height of printer page
		double pageWidth = pf.getImageableWidth();		//width of printer page
		
		// Scale factor (1:1 for printing trees)
		double scale = pageWidth/panelWidth;
		int totalNumPages = (int)Math.ceil(scale * panelHeight / pageHeight);
		
		// Make sure empty pages aren't printed
		if (pageIndex >= totalNumPages)
			return NO_SUCH_PAGE;
		
		// Shift Graphic to line up with beginning of print-imageable region
		g2.translate(pf.getImageableX(), pf.getImageableY());
		// Shift Graphic to line up with beginning of next page to print
		g2.translate(0f, -pageIndex * pageHeight);

		doPainting(g2, (int)panelWidth, (int)panelHeight);
		
//		g2.drawString("Page " + (pageIndex+1) + " of " + totalNumPages,
//			0, (int)(15 + pageIndex*pageHeight));
		
		return PAGE_EXISTS;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (sizeToFit)
			doPainting(g, sp.getSize().width, sp.getSize().height);
		else
			doPainting(g, getSize().width, getSize().height);
	}
	
	private void doPainting(Graphics g, int width, int height)
	{
		if (painter != null)
		{
			painter.paint(g, width, height);
			
			g.setColor(new Color(127, 157, 185));
			if (xMouse != -1 && sizeToFit)
				g.drawLine(xMouse, 0, xMouse, sp.getSize().height);
			else if (xMouse != -1 && !sizeToFit)
				g.drawLine(xMouse, 0, xMouse, getSize().height);
		}
	}
}
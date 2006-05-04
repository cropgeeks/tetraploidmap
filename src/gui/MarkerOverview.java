package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import data.*;


class MarkerOverview extends JPanel implements AdjustmentListener
{
	private LinkageGroup lGroup;
	
	private DrawCanvas canvas;
	private MarkerListName mList;
	private JScrollPane sp;
	private JViewport view;
	
	MarkerOverview(MarkerDetails details, LinkageGroup lGroup)
	{
		this.lGroup = lGroup;
		
		canvas = new DrawCanvas();
		mList = new MarkerListName();
		mList.setLinkageGroup(lGroup);
		
		sp = new JScrollPane(canvas);
		sp.getViewport().setBackground(new Color(230, 230, 230));
		sp.setRowHeaderView(mList);
		view = sp.getViewport();
		
		sp.getHorizontalScrollBar().addAdjustmentListener(this);
		sp.getVerticalScrollBar().addAdjustmentListener(this);
		
		GradientPanel gP = new GradientPanel("Marker Overview");
		gP.setLinkPanel(AppFrame.splits, details, "Click for Details");
		
		setLayout(new BorderLayout());
		add(gP, BorderLayout.NORTH);
		add(sp);
		
		canvas.updateDisplay();
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		canvas.redraw(view.getExtentSize(), view.getViewPosition());
	}
	
	private class DrawCanvas extends JPanel
	{
		Offset[] lookup = null;
		
		int width = 500, height = 500;
		
		// Size of an Individual and a Marker
		int iSize = 0, mSize = 0;
		// How many can currently be shown onscreen
		int iCount, mCount;
		
		// Width and height of canvas required to draw *all* the data
		Dimension dimension = new Dimension(1, 1);
		int canW = 1, canH = 1;
		
		// Top left corner of current view
		private int pX, pY;
		
		DrawCanvas()
		{
			setOpaque(false);
			setBackground(Color.white);
			
			addMouseMotionListener(new CanvasMouseMotionListener());
		}
		
		void updateDisplay()
		{
			updateLookupTable();
			
			Font font = new Font("Monospaced", Font.PLAIN, Prefs.gui_mSize);
			FontMetrics fm = new java.awt.image.BufferedImage(1,1,
				java.awt.image.BufferedImage.TYPE_INT_RGB).getGraphics()
					.getFontMetrics(font);
			
			iSize = Prefs.gui_iSize;
			mSize = fm.getHeight();
			
			canW = lGroup.getIndividualCount() * iSize + (iSize - 1);
			canH = lGroup.getAlleleCount() * mSize + (mSize - 1);
			
			dimension = new Dimension(canW, canH);
			setSize(canW, canH);
			
			sp.getHorizontalScrollBar().setUnitIncrement(iSize);
			sp.getVerticalScrollBar().setUnitIncrement(mSize);
			sp.getVerticalScrollBar().setBlockIncrement(mSize);
			
			repaint();
		}
		
		void redraw(Dimension d, Point p)
		{
			// How many individuals will fit on screen?
			iCount = (int) ((float) d.getWidth() / (float) iSize);
			// How many markers will fit on screen?
			mCount = (int) ((float) d.getHeight() / (float) mSize);			
			
			pX = p.x;
			pY = p.y;
			
			repaint();
		}

		public void paintComponent(Graphics g)
		{		
			super.paintComponent(g);
		
			// What individual to start with
			int iStart = pX / iSize;
			// What individual to end with
			int iEnd = iStart + iCount;
			
			// What marker to start with
			int mStart = lookup[pY / mSize].markerPos;
			int mEnd   = mStart + mCount;
			int yStart = pY - (lookup[pY / mSize].allelePos * mSize);
			
			
			// Current y postition
			int y = yStart;
			
			ListIterator<CMarker> itor = lGroup.getMarkers().listIterator(mStart);
			for (int cMar = mStart; itor.hasNext() && cMar <= mEnd; cMar++)
			{
				Marker marker = itor.next().marker;				
				
				int aCount = marker.getAlleleCount();
				for (int a = 0; a < aCount; a++, y+=mSize)
				{
					// Current x position
					int x = pX;
					
					Vector<AlleleState> list = marker.getAllele(a).getStates();
					ListIterator<AlleleState> itor2 = list.listIterator(iStart);
					
					for (int cInd = iStart; itor2.hasNext() && cInd <= iEnd; cInd++)			// cInd = Current Individual number
					{
						AlleleState as = itor2.next();
						
						switch (as.getState())
						{
							case AlleleState.PRESENT :
								g.setColor(Color.black);
								g.fillRect(x, y, iSize, mSize);
								break;
							
							case AlleleState.ABSENT :
								g.setColor(Color.gray);
								g.fillRect(x, y, iSize, mSize);
								break;
						}
						
						x += iSize;
					}
					
					g.setColor(new Color(224, 223, 227));
					g.drawRect(pX, y, 2000, mSize);
				}
			}
		}
		
		public Dimension getPreferredSize()
		{
			return dimension;
		}
		
		public Dimension getSize()
		{
			return dimension;
		}
		
		private void updateLookupTable()
		{
			lookup = new Offset[lGroup.getAlleleCount()];
			
			int mPos = 0;
			int index = 0;
			
			for (CMarker cm : lGroup.getMarkers())
			{
				for (int aPos = 0; aPos < cm.marker.getAlleleCount(); aPos++)
					lookup[index++] = new Offset(cm.marker, mPos, aPos);
				mPos++;
			}
		}
		
		private class Offset
		{
			Marker marker;
			int markerPos;
			int allelePos;
			
			Offset(Marker marker, int mPos, int aPos)
			{
				this.marker = marker;
				markerPos = mPos;
				allelePos = aPos;
			}
		}
		
		class CanvasMouseMotionListener extends MouseMotionAdapter
		{
			public void mouseMoved(MouseEvent e)
			{
				if (lookup == null)
					return;
					
				Point p = e.getPoint();
				
				int ind = ((p.x-pX) / iSize) + (pX / iSize);
				int marker = p.y / mSize;
				
				if (marker >= lookup.length)
					return;
				
				System.out.println("Marker " + (lookup[marker].markerPos+1)
					+ "." + (lookup[marker].allelePos+1)
					+ " Individual " + (ind+1)
					+ " (" + lookup[marker].marker.getName() + ")");
			}
		}
	}
}

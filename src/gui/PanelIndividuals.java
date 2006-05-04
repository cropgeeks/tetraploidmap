package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import data.*;

/*
class PanelIndividuals extends JPanel implements AdjustmentListener
{
	private DataSet data;
	
	private DrawCanvas canvas;
	private JScrollPane sp;
	private JViewport view;
	
	PanelIndividuals(DataSet data)
	{
		this.data = data;
		
		canvas = new DrawCanvas();
		
		sp = new JScrollPane(canvas);
		sp.getViewport().setBackground(new Color(230, 230, 230));
		view = sp.getViewport();
		
		sp.getHorizontalScrollBar().addAdjustmentListener(this);
		sp.getVerticalScrollBar().addAdjustmentListener(this);
		
		setLayout(new BorderLayout());
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
		int iSize = 12, mSize = 5;
		// How many currently can be shown onscreen
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
			
			canW = data.getMarkerCountIncludingAlleles() * mSize + (mSize - 1);
			canH = data.getIndividualCount() * iSize + (iSize - 1);
			
			dimension = new Dimension(canW, canH);
			setSize(canW, canH);
			
			sp.getHorizontalScrollBar().setUnitIncrement(mSize);
			sp.getVerticalScrollBar().setUnitIncrement(iSize);
			sp.getVerticalScrollBar().setBlockIncrement(iSize);
			
			repaint();
		}
		
		void redraw(Dimension d, Point p)
		{
			if (data == null)
				return;
			
			// How many markers will fit on screen?
			mCount = (int) ((float) d.getWidth() / (float) mSize);
			// How many individuals will fit on screen?
			iCount = (int) ((float) d.getHeight() / (float) iSize);
			
			pX = p.x;
			pY = p.y;
			
			repaint();
		}

		public void paintComponent(Graphics g)
		{		
			super.paintComponent(g);
			if (data == null)
				return;
		
			// What individual to start with
			int iStart = pY / iSize;
			// What individual to end with
			int iEnd = iStart + iCount;
			
			// What marker to start with
			int mStart = lookup[pX / mSize].markerPos;
			int mEnd   = mStart + mCount;
			int xStart = pX - (lookup[pX / mSize].allelePos * mSize);
			
			
			// Current y postition
			int y = pY;
			
			ListIterator<Individual> itor = data.getIndividuals().listIterator(iStart);
			for (int cInd = iStart; itor.hasNext() && cInd <= iEnd; cInd++)			// cInd = Current Individual number
			{
				Individual ind = itor.next();

				// Current x position
				int x = xStart;
				
				ListIterator<MarkerState> itor2 = ind.getMarkerStates().listIterator(mStart);
				for (int cMar = mStart; itor2.hasNext() && cMar <= mEnd; cMar++)
				{
					MarkerState ms = itor2.next();
					
					int aCount = ms.getMarker().getAlleleCount();
					for (int a = 0; a < aCount; a++, x+=mSize)
					{
						switch (ms.getState(a))
						{
							case MarkerState.PRESENT :
								g.setColor(Color.black);
								g.fillRect(x, y, mSize, iSize);
								break;
							
							case MarkerState.ABSENT :
								g.setColor(Color.gray);
								g.fillRect(x, y, mSize, iSize);
								break;
						}
												
						if ((cMar == data.getMarkerCount()-1) && (a == aCount-1))
						{
							g.setColor(new Color(255, 0, 0, 100));
							g.fillRect(x, y, mSize, iSize);
						}
						
						if (cInd == data.getIndividualCount()-1)
						{
							g.setColor(new Color(255, 0, 0, 100));
							g.fillRect(x, y, mSize, iSize);
						}
					}
				}
				
				y += iSize;
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
			lookup = new Offset[data.getMarkerCountIncludingAlleles()];
			
			int mPos = 0;
			int index = 0;
			
			for (Marker m : data.getMarkers())
			{
				for (int aPos = 0; aPos < m.getAlleleCount(); aPos++)
					lookup[index++] = new Offset(m, mPos, aPos);
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
				
				int marker = ((p.x-pX) / mSize) + (pX / mSize);
				int ind = p.y / iSize;
				
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
*/
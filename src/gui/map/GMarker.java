package gui.map;

import java.awt.*;

import data.*;

class GMarker
{
	// The marker's name
	String name;
	// Original Marker it represents
	CMarker marker;
	// The MapPanel object it is being drawn upon
	MapPanel panel;
	
	// Reference to the previous marker in the chromosome
	GMarker prev;
	
	// Its position in centiMorgans
	float cm;
	// Actual % position on the map (how far down the map this marker should be)
	float aPos;
	// Final position as decided graphically at display-time
	int pos;
	
	// Graphical position on the map
	Rectangle rec = new Rectangle();
		
	// Width of the string (rectangle) required to render this marker
	int recW;
	// Height of the string (rectangle) required to render this marker
	int recH;
	// Half the height of this rectangle
	int recHalf;
	
	// Are we drawing this marker topDown or bottomUp
	boolean topDown;
	
	boolean highlight = false;
	
	GMarker(CMarker marker, String name)
	{
		this.marker = marker;
		this.name = name;
	}
	
	public String toString() { return name; }
	
	// Clones (the non-graphical) fields of this object and returns a new object
	// containing them. Used so that the GMarkers on the "all" chromosome can be
	// easily copied into GMarkers on the individual chromosomes
	GMarker getClone()
	{
		GMarker gMkr = new GMarker(marker, name);
		
		gMkr.cm = cm;
		gMkr.aPos = aPos;
		
		return gMkr;
	}
	
	void setStringBounds(MapPanel panel, int width, int height)
	{
		recW = width;
		recH = height;
		recHalf = height / 2;
		
		this.panel = panel;
		topDown = panel.topDown;
	}
	
	// Given the offset of the chromosome from the top of the screen, and the
	// height of the chromosome: works out the "initial" position rectangle to
	// draw this marker's text at
	void setInitialRectangle(int C_OFFSET, int C_H, int rSide)
	{
		if (topDown)
			pos = C_OFFSET + (int)(aPos * C_H);
		else
			pos = C_OFFSET + C_H - (int)(aPos * C_H);
		
		rec = new Rectangle(rSide-recW, pos-recHalf, recW, recH);		
	}
	
	// Tests for a marker being above (or below) the top (or bottom) of the
	// chromosome, and resets its position to stop it
	boolean limitPosition()
	{
		// Ensure it's not above the top
		if (rec.y < panel.C_OFFSET-recHalf)
		{
			rec.y = panel.C_OFFSET-recHalf;
			return true;
		}
		
		// Ensure it's not below the bottom
		int bottom = panel.C_OFFSET + panel.C_H + recHalf;
		if (rec.y+recH >= bottom)
		{
			rec.y = bottom - recH;			
			return true;
		}
				
		return false;
	}

	// Optimises this marker's position rectangle by seeing if it overlaps with
	// any other marker's rectangle, and if so, asks that marker to adjust its
	// rectangle to make room
	int optimiseRectangle(int shift, boolean force)
	{		
		if (shift != -1)
		{
			if (topDown)
				rec.y = rec.y - shift;
			else
				rec.y = rec.y + shift;
		}
		
		boolean fixed = limitPosition();
		
		if (prev != null)
		{
			int overlap = overlap();
			if (overlap > -1)
			{				
				if (fixed || force)
					prev.optimiseRectangle(overlap, true);
				else
					rec.y = prev.optimiseRectangle(overlap/2, force);
			}
		}

		if (topDown)
			return rec.y + rec.height;
		else
			return rec.y - rec.height;
	}
	
	// Determines if this marker's position rectangle overlaps any of the other
	// marker's rectangles, returning -1 if not, or the amount of overlap (in
	// pixels) if it does
	private int overlap()
	{
		GMarker m1 = this;
		GMarker m2 = this.prev;
		
		while (m1 != null && m2 != null)
		{
			int m1Bot = m1.rec.y + m1.rec.height;
			int m2Bot = m2.rec.y + m2.rec.height;
			
			// Top of new rectangle is above bottom of prev
			if (topDown && m1.rec.y < m2Bot)
				return (m2Bot - m1.rec.y);
			// Bottom of new rectangle is below top of prev
			else if (!topDown && m1Bot > m2.rec.y)
				return (m1Bot - m2.rec.y);
			
			m1 = m2;
			m2 = m1.prev;
		}
		
		return -1;
	}
}
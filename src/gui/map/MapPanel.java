package gui.map;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;

public class MapPanel extends JPanel implements Printable
{
	// Chromosome height
	int C_H = 300;
	// 1/2 the chromosome width
	int C_W = 5;
	// y-offset before chromosome is painted
	int C_OFFSET = 30;
	// Current canvas width
	int W = 600;
	// Font height
	int FONT_HEIGHT;
	// Font descent
	int FONT_ASCENT;
	// Widest string
	int FONT_WIDTH;
	// Minimum canvas dimensions required
	int MIN_WIDTH, MIN_HEIGHT;
	// Width of fan arms
	int FAN = 12;
	// Total number of markers that will be drawn
	int count = 0;
	int zoom = 1;
	
	Font mkrFont = new Font("SansSerif", Font.PLAIN, 10);
	Font ttlFont = new Font("SansSerif", Font.BOLD, 11);
	
	boolean topDown = true;
	boolean isAntiAliased = false;
	boolean isOverallShown = true;
	
	GMarker highlightedMarker = null;
	float totalDistance;
	
	Vector<GMarker[]> chromosomes = null;
	
	public MapPanel(MapCreator creator)
	{
		setBackground(Color.white);
		
		chromosomes = creator.chromosomes;
		count = creator.chromoAll.length;
		totalDistance = creator.totalDistance;
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{
				processMouseMovement(e);
			}
		});
		
		initialise();
	}
	
	// Tracks the mouse cursor, and attempts to match markers with its current
	// position, redrawing the display (and highlighting that marker) if so
	private void processMouseMovement(MouseEvent e)
	{
		int x = e.getPoint().x;
		int y = e.getPoint().y;
				
		if (highlightedMarker != null)
			highlightedMarker.highlight = false;
		
		// If we're above or below the chromosome, ignore
		if (y < C_OFFSET || y > (C_OFFSET + C_H))
		{
			setToolTipText(null);
			repaint();
			return;
		}
		
		// Just search within the chromosome under the cursor to speed things up
		int num = isOverallShown ? chromosomes.size() : chromosomes.size()-1;
		int currentChromosome = (int) (x / (W/(num+1)));
		
		if (currentChromosome < num)
		{
			if (isOverallShown == false)
				currentChromosome++;
			
			GMarker[] markers = chromosomes.get(currentChromosome);
			for (int i = 0; i < markers.length; i++)
			{
				if (markers[i].rec.contains(x, y))
				{
					highlightedMarker = markers[i];
					highlightedMarker.highlight = true;
					
					setToolTipText(markers[i].name + " ("
						+ markers[i].cm + ")");
					
					repaint();
					return;
				}
			}
		}
		
		setToolTipText(null);
		repaint();
	}
	
	private void initialise()
	{
		Graphics g = new BufferedImage(
			1,1, BufferedImage.TYPE_INT_RGB).getGraphics();
		FontMetrics fm = g.getFontMetrics(mkrFont);
		
		FONT_HEIGHT = fm.getHeight();
		FONT_ASCENT = fm.getAscent();
		
		if (FONT_HEIGHT % 2 != 0)
			FONT_HEIGHT++;
		
		// For each chromosome...
		for (GMarker[] markers: chromosomes)
		{
			// For each marker in that chromosome...
			for (int i = 0; i < markers.length; i++)
			{
				int width = fm.stringWidth(markers[i].name);
				markers[i].setStringBounds(this, width, FONT_HEIGHT);
				
				if (width > FONT_WIDTH)
					FONT_WIDTH = width;
			}
		}
	}
	
	public Dimension getPreferredSize()
	{
		int num = isOverallShown ? chromosomes.size() : chromosomes.size()-1;
		
		// Max-width-of-font + the chromsome + 2 fans + gaps (and cm score)
		MIN_WIDTH  = (FONT_WIDTH + (2*C_W) + (2*FAN) + 50) * num;
		MIN_HEIGHT = ((count * FONT_HEIGHT) + (2 * C_OFFSET)) * zoom;
		
		return new Dimension(MIN_WIDTH, MIN_HEIGHT);
	}
	
	public int print(Graphics graphics, PageFormat pf, int pageIndex)
	{
		Graphics2D g2 = (Graphics2D) graphics;
		
		double panelWidth = MIN_WIDTH;				  // width in pixels
		double panelHeight = MIN_HEIGHT;			  // height in pixels
		
		double pageHeight = pf.getImageableHeight();  // height of printer page
		double pageWidth = pf.getImageableWidth();	  // width of printer page
		
		// Make sure empty pages aren't printed
		int totalNumPages = (int)Math.ceil(panelHeight / pageHeight);
		if (pageIndex >= totalNumPages)
			return NO_SUCH_PAGE;
		
		// Shift Graphic to line up with beginning of print-imageable region
		g2.translate(pf.getImageableX(), pf.getImageableY());
		// Shift Graphic to line up with beginning of next page to print
		g2.translate(0f, -pageIndex * pageHeight);
		
		// Scale (width)
		if ((pageWidth/panelWidth) < 1)
			g2.scale((pageWidth/panelWidth), 1);

		W = MIN_WIDTH;
		// Scale chromosome to fit page length (if it's smaller)
		if (MIN_HEIGHT < pageHeight)
			C_H = (int) pageHeight - (2 * C_OFFSET);
		// Or just print it normally (over multiple pages) if not
		else
			C_H = (int) MIN_HEIGHT - (2 * C_OFFSET);
		
		paintMap(g2);
				
		return PAGE_EXISTS;
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		
		W = getSize().width;
		C_H = getSize().height - (2 * C_OFFSET);
		
		paintMap(g);
	}
		
	private void paintMap(Graphics2D g)
	{
		if (isAntiAliased)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		calculateMarkerRectangles();
		
		int num = isOverallShown ? chromosomes.size() : chromosomes.size()-1;
		
		for (int i = 0; i < chromosomes.size(); i++)
		{
			if (isOverallShown == false && i == 0)
				continue;
			
			GMarker[] markers = chromosomes.get(i);
			int c = isOverallShown ? i : (i-1);
			int csX = ((c+1) * (W/(num+1)));
			
			String name = null;
			if (i == 0)
				name = "Overall";
			else
				name = "C" + i;
						
			paintChromosome(g, csX, name);
			
			for (int m = 0; m < markers.length; m++)
				paintMarker(g, markers[m], csX, m);
		}
	}
	
	private void paintChromosome(Graphics2D g, int csX, String name)
	{
//		g.setColor(new Color(169, 171, 255));
//		g.fillRect(csX-3, C_OFFSET, 6, C_H);

		g.setColor(Color.red);
//		g.drawRect(csX-C_W, C_OFFSET, C_W*2, C_H);
		g.drawRoundRect(csX-C_W, C_OFFSET-4, C_W*2, C_H+8, C_W*2, 6);
		
		g.setFont(ttlFont);
		g.setColor(Color.black);
		
		FontMetrics fm = g.getFontMetrics(ttlFont);
		int pos = csX - (fm.stringWidth(name) / 2);
		g.drawString(name, pos, 15);
	}
	
	private void paintMarker(Graphics2D g, GMarker mkr, int csX, int i)
	{
		int lSide = csX - C_W;
		int rSide = csX + C_W;
		
		Stroke s = g.getStroke();
		
		if (mkr.highlight)
		{
			g.setColor(Color.blue);
			g.setStroke(new BasicStroke(2));	
		}
		else
			g.setColor(Color.red);
		// Cross-over line
		g.drawLine(lSide-2, mkr.pos, rSide+2, mkr.pos);
		
		// Line linking marker name to cross-over line
		g.drawLine(lSide-FAN, (mkr.rec.y+mkr.recHalf), lSide-2, mkr.pos);
		// Line linking cross-over line to CM value
		g.drawLine(rSide+2, mkr.pos, rSide+FAN, (mkr.rec.y+mkr.recHalf));
		g.setStroke(s);
			
		if (mkr.highlight)
		{
			g.setColor(new Color(240, 240, 240));
			g.fillRect(lSide-FAN-mkr.recW, mkr.rec.y, mkr.recW, mkr.recH);
		}
		g.setColor(Color.black);
		g.setFont(mkrFont);
		g.drawString(mkr.name, lSide-FAN-mkr.recW, mkr.rec.y+FONT_ASCENT);
		
		g.setColor(Color.blue);
		if (topDown)
			g.drawString("" + Math.round(mkr.cm), rSide+FAN+2, mkr.rec.y+FONT_ASCENT);
		else
			// If we're drawing upside-down, then the distance is total-cM
			g.drawString("" + Math.round(totalDistance-mkr.cm), rSide+FAN+2, mkr.rec.y+FONT_ASCENT);

		if (mkr.highlight)
			g.drawRect(lSide-FAN-2-mkr.recW, mkr.rec.y, mkr.recW+2, mkr.recH);
	}
	
	private void calculateMarkerRectangles()
	{
		int num = isOverallShown ? chromosomes.size() : chromosomes.size()-1;
		
		for (int i = 0; i < chromosomes.size(); i++)
		{
			int c = isOverallShown ? i : (i-1);
			int csX = ((c+1) * (W/(num+1)));
			int lSide = csX - C_W;
			
			GMarker[] markers = chromosomes.get(i);
			for (int m = 0; m < markers.length; m++)
				markers[m].setInitialRectangle(C_OFFSET, C_H, lSide-FAN-2);
		}
		
		for (int i = 0; i < chromosomes.size(); i++)
		{
			GMarker[] markers = chromosomes.get(i);
			for (int m = 0; m < markers.length; m++)
				markers[m].optimiseRectangle(-1, (m == markers.length-1));
		}
	}
	
	void toggleAntiAliasing()
	{
		isAntiAliased = !isAntiAliased;
		repaint();
	}
	
	void zoom(int amount)
	{
		zoom += amount;
		reset();
	}
	
	void toggleOverallShown()
	{
		isOverallShown = !isOverallShown;
		reset();
	}
	
	private void reset()
	{
		initialise();		
		setSize(getPreferredSize());
		repaint();
	}
	
	// Creates a BufferedImage and draws the map onto it
	BufferedImage getSavableImage()
	{
		BufferedImage image = new BufferedImage(
			MIN_WIDTH, MIN_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		
		W   = MIN_WIDTH;
		C_H = MIN_HEIGHT - (2 * C_OFFSET);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, MIN_WIDTH, MIN_HEIGHT);
		paintMap(g);
		g.dispose();
		
		return image;
	}
	
	// Flips drawing of the chromosomes (with animation) by first increating the
	// size of the offset (the gap at the top/bottom of the screen), then
	// flipping, then decreasing the offset back to its original value
	void flip(final AbstractButton button)
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				int initialOffset = C_OFFSET;
				int midpoint = getSize().height/2;
				int increment = (midpoint - initialOffset) / 10;
				
				// Shrink...
				button.setEnabled(false);
				for (int i = initialOffset; i < midpoint; i += increment)
				{
					C_OFFSET = i;
					repaint();
					snooze();
				}
				
				// Flip...
				topDown = !topDown;
				initialise();
				
				// Expand...
				for (int i = midpoint; i >= initialOffset; i -= increment)
				{
					C_OFFSET = i;
					repaint();
					snooze();
				}				
				button.setEnabled(true);
				
				repaint();
			}
		};
		
		new Thread(r).start();
	}
		
	private void snooze()
	{
		try { Thread.sleep(50); }
		catch (InterruptedException e) {}
	}
}
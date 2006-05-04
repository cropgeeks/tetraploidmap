package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class GradientPanel extends JPanel
{
	// Data required to render the panel
	private static Color pColor = new JPanel().getBackground();
	private String title = "";
	
	// Data required to allow toggling between multiple panels
	private static FontMetrics fm;
	private JSplitPane splits;
	private JComponent component;
	private String linkName;
	private int width;
	private boolean underline = false;
	
	public GradientPanel(String title)
	{ 
		this.title = title;
		setMinimumSize(new Dimension(100, 22));
		setPreferredSize(new Dimension(100, 22));
	}
	
	public void setTitle(String title)
	{
		this.title = title;
		repaint();
	}		
	
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		
		g.setPaint(new GradientPaint(
			0, 0, new Color(140, 165, 214),
			getWidth() - 25, 0, pColor));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font("Dialog", Font.BOLD, 11));
		g.setColor(Color.black);		
		g.drawString(title, 20, 15);
		g.setColor(Color.white);
		g.drawString(title, 21, 14);
		
		if (linkName != null)
		{
			g.setColor(Color.black);		
			g.drawString(linkName, getWidth() - width - 20, 15);
			g.setColor(Color.white);
			g.drawString(linkName, getWidth() - width - 19, 14);
			
			if (underline)
			{
				g.setColor(Color.black);
				g.drawLine(getWidth() - width - 20, 18, getWidth() - 20, 18);
				g.setColor(Color.white);
				g.drawLine(getWidth() - width - 19, 17, getWidth() - 19, 17);
			}
		}
	}
	
	public void setLinkPanel(JSplitPane s, JComponent c, String n)
	{
		splits = s;
		component = c;
		linkName = n;
		
		determineBoundingBox();
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() != 1) return;
				
				if (e.getX() < getWidth() - width - 20) return;
				if (e.getX() > getWidth() - 20) return;
				
				int location = splits.getDividerLocation();
				splits.setRightComponent(component);
				splits.setDividerLocation(location);
				underline = false;
			}
			
			public void mouseExited(MouseEvent e) {	setState(false); }
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{
				boolean newState;
				if (e.getX() < getWidth() - width - 20 ||
					e.getX() > getWidth() - 20)
					newState = false;
				else
					newState = true;
				
				if (newState != underline)
					setState(newState);
			}
		});
	}
	
	private void setState(boolean state)
	{
		underline = state;
		repaint();
					
		if (underline)
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		else
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void determineBoundingBox()
	{
		if (fm == null)
		{		
			BufferedImage image =
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Dialog", Font.BOLD, 11));
			fm = g.getFontMetrics(g.getFont());
		}
		
		width = fm.stringWidth(linkName) - 2;
	}
}
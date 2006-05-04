package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.*;

class MarkerSummaryPanel extends JPanel
{
	// The marker that the panel is currently displaying
	private Marker marker;
	
	private GradientPanel gPanel;
	private JTabbedPane tabs;
	
	private MarkerInfoPanel infoPanel;
	private MarkerParentPanel p1Panel, p2Panel;
	
	MarkerSummaryPanel()
	{
		tabs = new JTabbedPane();
		
		infoPanel = new MarkerInfoPanel();
		tabs.addTab("Summary", null, infoPanel, null);
		
		p1Panel = new MarkerParentPanel(1);
		tabs.addTab("Parent1 Linkages", null, p1Panel, null);
		p2Panel = new MarkerParentPanel(2);
		tabs.addTab("Parent2 Linkages", null, p2Panel, null);
		
		setLayout(new BorderLayout());
		add(gPanel = new GradientPanel(""), BorderLayout.NORTH);
		add(tabs);
	}
	
	void setMarker(Marker marker)
	{
		AppFrameMenuBar.aMove.setEnabled(false);
		
		if (marker != null)
		{
			gPanel.setTitle(marker.getName());
			if (AppFrame.navPanel.getCurrentClusterHeadNode() != null)
				AppFrameMenuBar.aMove.setEnabled(true);
		}
		else
			gPanel.setTitle("");
		
		infoPanel.setMarker(marker);
		p1Panel.setMarker(marker);
		p2Panel.setMarker(marker);
	}
	
	
	private class MarkerInfoPanel extends JPanel
	{
		private JEditorPane text;
		
		MarkerInfoPanel()
		{
			text = new JEditorPane("text/html", "");
			text.setFont(new Font("Monospaced", Font.PLAIN, 11));
			text.setMargin(new Insets(2, 5, 2, 5));
			text.setEditable(false);
			JScrollPane sp = new JScrollPane(text);
			
			setLayout(new BorderLayout());
			add(sp);
		}
		
		void setMarker(Marker marker)
		{
			if (marker != null)
			{
				text.setText(marker.getSummaryInfo());
				text.setCaretPosition(0);
			}
		}
	}
	
	private class MarkerParentPanel extends JPanel
	{
		private JEditorPane text;
		private int parent;
		
		MarkerParentPanel(int parent)
		{
			this.parent = parent;
			
			text = new JEditorPane("text/html", "");
			text.setFont(new Font("Monospaced", Font.PLAIN, 11));
			text.setMargin(new Insets(2, 5, 2, 5));
			text.setEditable(false);
			JScrollPane sp = new JScrollPane(text);
			
			setLayout(new BorderLayout());
			add(sp);
		}
		
		void setMarker(Marker marker)
		{
			if (marker != null)
			{
				String str = "<html><pre><font size='3'>";
				str += "<b>SC Group    Marker Name         Chi^2   Sig         "
					+ "Phase</b><br><br>";
				
				if (parent == 1)
				{
					str += "<b>Simplex-duplex linkages</b>";
					for (SigLinkage sig: marker.getSimMatchData().p1A)
						str += showSig(sig);
						
					str += ("<br><br><b>Simplex-3 to 1 linkages</b>");
					for (SigLinkage sig: marker.getSimMatchData().p1B)
						str += showSig(sig);
							
					str += ("<br><br><b>Other linkages to simplex markers</b>");
					for (SigLinkage sig: marker.getSimMatchData().p1C)
						str += showSig(sig);
				}
				else
				{				
					str += ("<b>Simplex-duplex linkages</b>");
					for (SigLinkage sig: marker.getSimMatchData().p2A)
						str += showSig(sig);
						
					str += ("<br><br><b>Simplex-3 to 1 linkages</b>");
					for (SigLinkage sig: marker.getSimMatchData().p2B)
						str += showSig(sig);
						
					str += ("<br><br><b>Other linkages to simplex markers</b>");
					for (SigLinkage sig: marker.getSimMatchData().p2C)
						str += showSig(sig);
				}
				
				str += "</html>";
				text.setText(str);
				text.setCaretPosition(0);
			}
		}
		
		private String set(String value, int size)
		{
			if (value.length() < size)
				for (int i = value.length(); i < size; i++)
					value += " ";
			
			return value;
		}
		
		private String showSig(SigLinkage sig)
		{
			return "<br>" + set(sig.marker.getPrefix(), 12)
				+ set(sig.marker.getName(), 20)
				+ Prefs.d3.format(sig.chi) + "  "
				+ Prefs.d8.format(sig.sig) + "  "
				+ (sig.phase == null ? "" : sig.phase);
		}
	}
}

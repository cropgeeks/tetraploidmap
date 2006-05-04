package gui.map;

import java.awt.*;
import java.awt.image.*;
import java.awt.print.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import analyses.order.*;
import data.*;
import gui.*;

import doe.*;

public class LinkageMapPanel extends JPanel
{
	private MapPanel panel = null;
	private MapToolBar toolbar = null;
	
	public LinkageMapPanel(OrderedResult order, LinkageGroup[] lGroups)
	{
		MapCreator creator = new MapCreator(order, lGroups);
		
		panel = new MapPanel(creator);
		toolbar = new MapToolBar(this, panel);
		
		JScrollPane sp = new JScrollPane(panel);
		sp.getVerticalScrollBar().setUnitIncrement(15);
		sp.getHorizontalScrollBar().setUnitIncrement(15);
		
		setLayout(new BorderLayout());
		add(sp);
		add(toolbar, BorderLayout.EAST);
	}
	
	public void print()
	{
		Printable[] toPrint = { panel };		
		new PrinterDialog(toPrint);
	}
	
	void save(boolean asImage)
	{
		JFileChooser fc = new JFileChooser();
//		fc.addChoosableFileFilter(Filters.getFileFilter(3));
		fc.setCurrentDirectory(new File(Prefs.gui_dir));
		fc.setDialogTitle("Save Linkage Map");

		while (fc.showSaveDialog(MsgBox.frm) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			
			String ext = asImage ? ".png" : ".txt";
			
			// Make sure it has an appropriate extension
			if (!file.exists())
				if (file.getName().indexOf(".") == -1)
					file = new File(file.getPath() + ext);

			// Confirm overwrite
			if (file.exists())
			{
				int response = MsgBox.yesnocan(file + " already exists.\nDo "
					+ "you want to replace it?", 1);
					
				if (response == JOptionPane.NO_OPTION)
					continue;
				else if (response == JOptionPane.CANCEL_OPTION ||
					response == JOptionPane.CLOSED_OPTION)
					return;
			}
			
			// Otherwise it's ok to save...
			Prefs.gui_dir = "" + fc.getCurrentDirectory();
			if (asImage)
				saveImage(file);
			else
				saveText(file);
			return;
		}		
	}
	
	private void saveImage(File file)
	{
		BufferedImage image = panel.getSavableImage();
		
		try
		{
			ImageIO.write(image, "png", file);
			
			MsgBox.msg("Data successfully saved to " + file, MsgBox.INF);
		}
		catch (Exception e)
		{
			MsgBox.msg("There was an unexpected error while saving the image:"
				+ "\n" + e, MsgBox.ERR);
		}
	}
	
	private void saveText(File file)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			
			saveData(panel.chromosomes.get(0), out, "Overall");
			saveData(panel.chromosomes.get(1), out, "C1");
			saveData(panel.chromosomes.get(2), out, "C2");
			saveData(panel.chromosomes.get(3), out, "C3");
			saveData(panel.chromosomes.get(4), out, "C4");
			
			out.close();
			
			MsgBox.msg("Data successfully saved to " + file, MsgBox.INF);
		}
		catch (Exception e)
		{
			MsgBox.msg("There was an unexpected error while saving the data:"
				+ "\n" + e, MsgBox.ERR);
		}
	}
	
	private void saveData(GMarker[] data, BufferedWriter out, String title)
		throws Exception
	{
		out.write(title);
		out.newLine();
				
		for (GMarker marker: data)
		{
			out.write(Prefs.d3.format(marker.cm) + " \t" + marker.name);
			out.newLine();
		}
		
		out.newLine();
	}
}
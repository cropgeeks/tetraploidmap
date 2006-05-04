package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import javax.swing.*;
import javax.print.attribute.*;

import java.awt.font.*;
import java.util.*;
import java.text.*;

import doe.*;

public class PrinterDialog extends JDialog
{
	private static PrinterJob job = PrinterJob.getPrinterJob();
	private static PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
	
	private JLabel label;
	
	// Components that are to be printed
	private Printable[] toPrint = null;
	
	public PrinterDialog(Printable[] toPrint)
	{
		super(MsgBox.frm, "Printing", true);
		
		this.toPrint = toPrint;
		
		JLabel icon = new JLabel(Icons.PRINT);
		icon.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		label = new JLabel("Attempting communication with printers...");
		label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
		add(label);
		add(icon, BorderLayout.WEST);
		
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				new Printer().start();
			}
		});
		
		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(MsgBox.frm);
		setResizable(false);
		setVisible(true);
	}
	
	// Display the Java Printer PageSetup dialog
	static void showPageSetupDialog(AppFrame appFrame)
	{
		appFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		job.pageDialog(aset);
		appFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void updateLabel()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				label.setText("TetraploidMap is printing. Please be patient.");
				
				pack();
				setLocationRelativeTo(MsgBox.frm);
			}
		};
		
		try { SwingUtilities.invokeAndWait(r); }
		catch (Exception e) {}
	}
	
	private class Printer extends Thread
	{
		public void run()
		{
			if (job.printDialog(aset))
			{
				updateLabel();
				
				try
				{				
					for (Printable p: toPrint)
					{
						job.setPrintable(p);
						job.print(aset);
					}
				}
				catch (Exception e)
				{
					MsgBox.msg("An error occurred while printing:\n" + e,
						MsgBox.ERR);
				}
			}
			
			setVisible(false);
		}
	}
}


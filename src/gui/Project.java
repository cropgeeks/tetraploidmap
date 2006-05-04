package gui;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.*;

import data.*;

import doe.*;

public class Project implements Serializable
{
	static final long serialVersionUID = 4346495433165001405L;
	
	// A list of all the top-level LinkageGroup objects in this project. More
	// than one group can be open at a time, hence the need to keep a list.
	private LinkedList<LinkageGroup> groups = new LinkedList<LinkageGroup>();
	
	// This project's name
	private String name;
	
	// A log of everything that's ever happened with this project
	private AnalysisLog log = new AnalysisLog();
	// A static reference to the log for ease-of-use
	public static AnalysisLog logger = null;
	
	// Temporary object used to track the (most recent) file this project was
	// opened from
	public static File filename;
	
	Project(String name, File filename)
	{
		this.name = name;
		this.filename = filename;
		
		// Associate the static logger with this project
		logger = log;
		logger.add("Project created.");
		
		Project.save(this, false);
	}
		
	public String getName() { return name; }
	
	void addLinkageGroup(LinkageGroup lGroup) { groups.add(lGroup); }
	
	public LinkedList<LinkageGroup> getLinkageGroups() { return groups; }
	
	// Loads the given project from disk
	private static Project open(File filename)
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(
				new GZIPInputStream(new FileInputStream(filename)));
    
    		Project proj = (Project) in.readObject();
    		proj.filename = filename;
   
    		in.close();
    		
    		logger = proj.log;
    		return proj;
		}
		catch (InvalidClassException e1)
		{
			String msg = filename + " is in an old project format that "
				+ "is no longer supported.";
			MsgBox.msg(msg, MsgBox.ERR);
		}
		catch (Exception e2)
		{
			String msg = "There was an unexpected error while loading "
				+ filename + "\n" + e2;
			MsgBox.msg(msg, MsgBox.ERR);
		}
		
		return null;
	}
	
	// Calls load() to load the given project from disk, or opens a FileDialog
	// to prompt for the project name if filename is null
	public static Project open(String filename)
	{
		if (filename != null)
			return open(new File(filename));
	
		// Create the dialog
		JFileChooser fc = new JFileChooser();
//		fc.addChoosableFileFilter(Filters.getFileFilter(3));
		fc.setCurrentDirectory(new File(Prefs.gui_dir));
		fc.setDialogTitle("Open Project");
		
		// Loop until either valid files are picked, or cancel is selected
		while (fc.showOpenDialog(MsgBox.frm) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Prefs.gui_dir = "" + fc.getCurrentDirectory();
			
			if (file == null)
			{
				String msg = fc.getSelectedFile() + "\nFile not found.\nPlease "
					+ "verify the correct file name was given.";
				MsgBox.msg(msg, MsgBox.ERR);
			}
			else
				// Load it...
				return open(file);
		}
		
		return null;
	}
	
	static boolean save(Project proj, boolean saveAs)
	{
		if (saveAs && proj.saveAs() == false)
			return false;
		
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(
				new GZIPOutputStream(new FileOutputStream(proj.filename)));
			
			out.writeObject(proj);
			out.close();
			
			return true;
		}
		catch (Exception e)
		{
			String msg = "There was an unexpected error while saving "
				+ proj.filename + "\n" + e;
			MsgBox.msg(msg, MsgBox.ERR);
		}
		
		return false;
	}
	
	private boolean saveAs()
	{
		JFileChooser fc = new JFileChooser();
//		fc.addChoosableFileFilter(Filters.getFileFilter(3));
		fc.setDialogTitle("Save As");
		fc.setSelectedFile(filename);

		while (fc.showSaveDialog(MsgBox.frm) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			
			// Make sure it has an appropriate extension
			if (!file.exists())
				if (file.getName().indexOf(".") == -1)
					file = new File(file.getPath() + ".proj");

			// Confirm overwrite
			if (file.exists())
			{
				int response = MsgBox.yesnocan(file + " already exists.\nDo "
					+ "you want to replace it?", 1);
					
				if (response == JOptionPane.NO_OPTION)
					continue;
				else if (response == JOptionPane.CANCEL_OPTION ||
					response == JOptionPane.CLOSED_OPTION)
					return false;
			}
			
			// Otherwise it's ok to save...
			Prefs.gui_dir = "" + fc.getCurrentDirectory();
			filename = file;
			
			return true;
		}
		
		return false;
	}
}
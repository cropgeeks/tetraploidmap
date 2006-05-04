package gui;

import javax.swing.*;

import doe.*;

import pal.statistics.*;

public class TetraploidMap
{
	public static void main(String[] args)
	{
		try
		{
//			UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
//			System.setProperty("winlaf.forceTahoma", "true");
//			net.java.plaf.LookAndFeelPatchManager.initialize();

//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		}
		catch (Exception e) { System.out.println(e); }
		
		// Load the icons
		new Icons();
		// Load the preferences
		Prefs prefs = new Prefs();
		prefs.loadPreferences(System.getProperty("user.home") + Prefs.sepF
			+ ".TetraploidMap.txt");
		
		AppFrame appFrame = new AppFrame(prefs);
		new MsgBox(appFrame, "TetraploidMap");
	}
}
package gui;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import data.*;

import doe.*;

public class Prefs extends Preferences
{
	// Variables not definable by the user...
	public static DecimalFormat d1 = new DecimalFormat("0.0");
	public static DecimalFormat d3 = new DecimalFormat("0.000");
	public static DecimalFormat d4 = new DecimalFormat("0.0000");
	public static DecimalFormat d5 = new DecimalFormat("0.00000");
	public static DecimalFormat d8 = new DecimalFormat("0.00000000");
	public static DecimalFormat i3 = new DecimalFormat("000");
	
	static
	{
		// Force all output to be UK because the Fortran can't deal with 0,5
		// instead of 0.5
		d1.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
		d3.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
		d4.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
		d5.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
		d8.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
	}
	
	public static Font labelFont = (Font) UIManager.get("Label.font");
	
	public static String tools_scratch = System.getProperty("java.io.tmpdir")
		+ File.separator + "tpmap";		
	public static String tools_findgeno_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\findgeno.exe";
	public static String tools_cluster_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\cluster.exe";
	public static String tools_twopoint_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\twopoint.exe";
	public static String tools_ripple_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\ripple.exe";
	public static String tools_simanneal_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\simanneal.exe";
	public static String tools_qtl_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\realoneparrecon.exe";
	public static String tools_simmatch_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\simmatch.exe";
	public static String tools_anova_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\anova.exe";
	public static String tools_perm_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\perm.exe";
	public static String tools_permsimp_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\permsimp.exe";
	public static String tools_altqtl_path = System.getProperty("user.dir")
		+ File.separator + "lib\\binaries\\altQTLmodel.exe";
	
	// All other variables...
	public static String gui_dir = "tutorials"; //System.getProperty("user.dir");
	public static LinkedList<String> gui_recent = new LinkedList<String>();
		
	public static int gui_iSize = 7;
	public static int gui_mSize = 12;	
		
	public static boolean gui_select_on_type;				// Test for marker type
	public static int gui_select_type = Marker.AFLP;		//   with this value
	public static boolean gui_select_on_parent;				// Test for present in parent
	public static int gui_select_parent = 1;				//   1=p1, 2=p2, 3=both
	public static boolean gui_select_on_ratio;				// Test for marker ratio
	public static int gui_select_ratio = Marker.R1_1;		//   with this value
	public static float gui_select_ratio_sig = 0.05f;		//   and this signifiance
	public static boolean gui_select_on_dr;					// Test for double-reduction
	public static boolean gui_select_dr = true;				//   with this value
	public static float gui_select_dr_sig = 0.05f;			//   and this signifiance
	public static boolean gui_select_match_selected;
	
	public static float gui_pick_similarity = 0.9f;
	public static int gui_graph_scale_type = 0;
	
	public static boolean sim_run_ripple = false;
	public static boolean sim_run_sim = false;
	public static float sim_rt_value = 0.85f;
	public static float sim_t_value = 10f;
	public static float sim_nt_value = 100f;
	public static float sim_eps_value = 0.1f;
	
	
	protected void getPreferences()
	{
		for (int i = 0; i < 4; i++)
		{
			String str = getStr(p.getProperty("gui_recent_"+i), "");
			if (str.length() > 0)
				gui_recent.add(str);
		}
		
		gui_dir = getStr(p.getProperty("gui_dir"), gui_dir);
		
		gui_select_on_type = getBool(p.getProperty("gui_select_on_type"), gui_select_on_type);
		gui_select_type = getInt(p.getProperty("gui_select_type"), gui_select_type);
		gui_select_on_parent = getBool(p.getProperty("gui_select_on_parent"), gui_select_on_parent);
		gui_select_parent = getInt(p.getProperty("gui_select_parent"), gui_select_parent);
		gui_select_on_ratio = getBool(p.getProperty("gui_select_on_ratio"), gui_select_on_ratio);
		gui_select_ratio = getInt(p.getProperty("gui_select_ratio"), gui_select_ratio);
		gui_select_ratio_sig = getFloat(p.getProperty("gui_select_ratio_sig"), gui_select_ratio_sig);
		gui_select_on_dr = getBool(p.getProperty("gui_select_on_dr"), gui_select_on_dr);
		gui_select_dr = getBool(p.getProperty("gui_select_dr"), gui_select_dr);
		gui_select_dr_sig = getFloat(p.getProperty("gui_select_dr_sig"), gui_select_dr_sig);
		gui_select_match_selected = getBool(p.getProperty("gui_select_match_selected"), gui_select_match_selected);
		
		gui_pick_similarity = getFloat(p.getProperty("gui_pick_similarity"), gui_pick_similarity);
		gui_graph_scale_type = getInt(p.getProperty("gui_graph_scale_type"), gui_graph_scale_type);
		
		sim_run_ripple = getBool(p.getProperty("sim_run_ripple"), sim_run_ripple);
		sim_run_sim = getBool(p.getProperty("sim_run_sim"), sim_run_sim);
		sim_rt_value = getFloat(p.getProperty("sim_rt_value"), sim_rt_value);
		sim_t_value = getFloat(p.getProperty("sim_t_value"), sim_t_value);
		sim_nt_value = getFloat(p.getProperty("sim_nt_value"), sim_nt_value);
		sim_eps_value = getFloat(p.getProperty("sim_eps_value"), sim_eps_value);
	}
	
	protected void setPreferences()
		throws Exception
	{
		for (int i = 0; i < gui_recent.size(); i++)
				write("gui_recent_"+i, setPath(gui_recent.get(i)));
		
		write("gui_dir", setPath(gui_dir));
			
		write("gui_select_on_type", "" + gui_select_on_type);
		write("gui_select_type", "" + gui_select_type);
		write("gui_select_on_parent", "" + gui_select_on_parent);
		write("gui_select_parent", "" + gui_select_parent);
		write("gui_select_on_ratio", "" + gui_select_on_ratio);
		write("gui_select_ratio", "" + gui_select_ratio);
		write("gui_select_ratio_sig", "" + gui_select_ratio_sig);
		write("gui_select_on_dr", "" + gui_select_on_dr);
		write("gui_select_dr", "" + gui_select_dr);
		write("gui_select_dr_sig", "" + gui_select_dr_sig);
		write("gui_select_match_selected", "" + gui_select_match_selected);
	
		write("gui_pick_similarity", "" + gui_pick_similarity);
		write("gui_graph_scale_type", "" + gui_graph_scale_type);
		
		write("sim_run_ripple", "" + sim_run_ripple);
		write("sim_run_sim", "" + sim_run_sim);
		write("sim_rt_value", "" + sim_rt_value);
		write("sim_t_value", "" + sim_t_value);
		write("sim_nt_value", "" + sim_nt_value);
		write("sim_eps_value", "" + sim_eps_value);
	}
}
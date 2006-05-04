package gui;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

// Simple helper class that contains statically allocated ImageIcon objects that
// be accessed by anyone/anywhere elsewhere in the code
public class Icons
{
	public static ImageIcon OPEN, DELETE, RUN_SIMANNEAL, ORDER, WARNING, LOG;
	public static ImageIcon RUN_CLUSTER, LINKAGE_GROUP, SELECT_MARKERS, SCALE;
	public static ImageIcon FOLDER, FOLDER_OPEN, GROUP_FOLDER, GROUP_OPEN;
	public static ImageIcon CLUSTER_FOLDER, CLUSTER_OPEN, SUMMARY, DENDROGRAM;
	public static ImageIcon COLOR_DENDROGRAM, PRINT, NEW_PROJECT, SAVE, IMPORT;
	public static ImageIcon GRAPH_SCALE, FLIP, MAP, ANTIALIAS, SAVE_IMAGE, RESCAN;
	public static ImageIcon ZOOM_IN, ZOOM_OUT, EXCEL, QTL, RUN_MAP, SAVE_TEXT;
	public static ImageIcon ANOVA, RUN_ANOVA, AA, HIDE_OVERALL, APP, PERM;
	
	public Icons()
	{
		Class c = getClass();
	
		try
		{
			NEW_PROJECT = new ImageIcon(c.getResource("/res/new_project.png"));
			IMPORT = new ImageIcon(c.getResource("/res/import.png"));
			SAVE = new ImageIcon(c.getResource("/res/save.png"));
			PRINT = new ImageIcon(c.getResource("/res/print.png"));
			OPEN = new ImageIcon(c.getResource("/res/open.png"));
			RUN_SIMANNEAL = new ImageIcon(c.getResource("/res/run_simanneal.png"));
			ORDER = new ImageIcon(c.getResource("/res/order.png"));
			DELETE = new ImageIcon(c.getResource("/res/delete.png"));
			RUN_CLUSTER = new ImageIcon(c.getResource("/res/run_cluster.png"));
			LINKAGE_GROUP = new ImageIcon(c.getResource("/res/linkage_group.png"));
			SELECT_MARKERS = new ImageIcon(c.getResource("/res/select_markers.png"));
			FOLDER = new ImageIcon(c.getResource("/res/folder.png"));
			FOLDER_OPEN = new ImageIcon(c.getResource("/res/folder_open.png"));
			GROUP_FOLDER = new ImageIcon(c.getResource("/res/group_folder.png"));
			GROUP_OPEN = new ImageIcon(c.getResource("/res/group_open.png"));
			CLUSTER_FOLDER = new ImageIcon(c.getResource("/res/cluster_folder.png"));
			CLUSTER_OPEN = new ImageIcon(c.getResource("/res/cluster_open.png"));
			SUMMARY = new ImageIcon(c.getResource("/res/summary.png"));
			WARNING = new ImageIcon(c.getResource("/res/warning.png"));
			DENDROGRAM = new ImageIcon(c.getResource("/res/dendrogram.png"));
			SCALE = new ImageIcon(c.getResource("/res/scale.png"));
			COLOR_DENDROGRAM = new ImageIcon(c.getResource("/res/color_dendrogram.png"));
			LOG = new ImageIcon(c.getResource("/res/log.png"));
			GRAPH_SCALE = new ImageIcon(c.getResource("/res/graph_scale.png"));
			FLIP = new ImageIcon(c.getResource("/res/flip.png"));
			MAP = new ImageIcon(c.getResource("/res/map.png"));
			ANTIALIAS = new ImageIcon(c.getResource("/res/antialias.png"));
			SAVE_IMAGE = new ImageIcon(c.getResource("/res/save_image.png"));
			ZOOM_IN = new ImageIcon(c.getResource("/res/zoom_in.png"));
			ZOOM_OUT = new ImageIcon(c.getResource("/res/zoom_out.png"));
			EXCEL = new ImageIcon(c.getResource("/res/excel.png"));
			QTL = new ImageIcon(c.getResource("/res/qtl.png"));
			RUN_MAP = new ImageIcon(c.getResource("/res/run_map.png"));
			SAVE_TEXT = new ImageIcon(c.getResource("/res/save_text.png"));
			ANOVA = new ImageIcon(c.getResource("/res/anova.png"));
			RUN_ANOVA = new ImageIcon(c.getResource("/res/run_anova.png"));
			AA = new ImageIcon(c.getResource("/res/aa.png"));
			HIDE_OVERALL = new ImageIcon(c.getResource("/res/hide_overall.png"));
			APP = new ImageIcon(c.getResource("/res/app.png"));
			PERM = new ImageIcon(c.getResource("/res/perm.png"));
			RESCAN = new ImageIcon(c.getResource("/res/rescan.png"));
		}
		catch (NullPointerException e)
		{
			System.out.println(e);
			System.exit(1);
		}
	}
}
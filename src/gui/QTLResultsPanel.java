package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;

import doe.*;

import analyses.altqtl.*;
import analyses.perm.*;
import data.*;

public class QTLResultsPanel extends JPanel implements ListSelectionListener
{
	private OrderedResult order;
	private QTLResult qtlResult;
	
	private JList traitList;
	private DefaultListModel traitModel;
	private JSplitPane splits;
	private JTextArea details, lodDetails;
	private QTLResultsToolBar toolbar;
	private ModelPanel modelPanel;
	
	private ChartPanel chartPanel;
	private Trait currentTrait;
	
	public QTLResultsPanel(QTLResult qtlResult, OrderedResult order)
	{
		this.qtlResult = qtlResult;
		this.order = order;
		
		// Trait listbox
		traitModel = new DefaultListModel();
		for (Trait trait: qtlResult.getTraits())
			traitModel.addElement(trait);
		traitList = new JList(traitModel);
		traitList.addListSelectionListener(this);
		traitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp1 = new JScrollPane(traitList);
		sp1.setPreferredSize(new Dimension(125, 50));
		
		// Details text box
		details = new JTextArea();
		details.setFont(new Font("Monospaced", Font.PLAIN, 11));
		details.setMargin(new Insets(2, 5, 2, 5));
		details.setEditable(false);
		details.setTabSize(6);
		JScrollPane sp2 = new JScrollPane(details);
		
		lodDetails = new JTextArea();
		lodDetails.setFont(new Font("Monospaced", Font.PLAIN, 11));
		lodDetails.setMargin(new Insets(2, 5, 2, 5));
		lodDetails.setEditable(false);
		lodDetails.setTabSize(6);
		JScrollPane sp3 = new JScrollPane(lodDetails);
		
		modelPanel = new ModelPanel();
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.add(sp2, "Full Model");
		tabs.add(modelPanel, "Simple Model");
		tabs.add(sp3, "LOD Details");
		
		// The splitpane
		splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splits.setTopComponent(new JPanel());
		splits.setBottomComponent(tabs);
		splits.setResizeWeight(0.5);
		
		//pane2
		JSplitPane splits2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splits2.setLeftComponent(sp1);
		splits2.setRightComponent(splits);
		
		setLayout(new BorderLayout());
		add(new GradientPanel("QTL Analysis Results"), BorderLayout.NORTH);
	//	add(sp1, BorderLayout.WEST);
	//	add(splits);
		add(splits2);
		add(toolbar = new QTLResultsToolBar(this), BorderLayout.EAST);
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) return;
		
		Trait trait = (Trait) traitList.getSelectedValue();
		displayTrait(trait);
	}
	
	private void displayTrait(Trait trait)
	{
		modelPanel.setTrait(trait);
		
		if (trait == null)
		{
			details.setText("");
			lodDetails.setText("");
			splits.setTopComponent(new JPanel());
			currentTrait = null;
			toolbar.enableButtons(false, null);
			return;
		}
		
		StringBuffer text = new StringBuffer(1000);
		
		text.append("QTL position:         " + Prefs.d3.format(trait.qtlPosition) + " cM");
		if (trait.qtlEffects2 != null)
			text.append("\t" + Prefs.d3.format(trait.qtlPosition2) + " cM");
		text.append("\n");
		
		text.append("Maximum LOD score:    " + Prefs.d3.format(trait.maxLOD));
		if (trait.qtlEffects2 != null)
			text.append("\t\t" + Prefs.d3.format(trait.maxLOD2));
		text.append("\n");
		
		text.append("% variance explained: " + Prefs.d3.format(trait.varExplained));
		if (trait.qtlEffects2 != null)
			text.append("\t\t" + Prefs.d3.format(trait.varExplained2));
		text.append("\n");
		
		text.append("Error mean square:    " + Prefs.d3.format(trait.errMS));
		if (trait.qtlEffects2 != null)
			text.append("\t\t" + Prefs.d3.format(trait.errMS2));
		text.append("\n\n");
		
		if (trait.qtlEffects != null)
		{
			text.append("QTL Effects\n");
			text.append("Genotype   Mean      s.e.");
			if (trait.qtlEffects2 != null)
				text.append("\t\tGenotype   Mean      s.e.");
			text.append("\n");			
			
			for (int i = 0; i < 6; i++)
			{
				text.append("  " + trait.qtlEffects[i]);
				if (trait.qtlEffects2 != null && i < 2)
					text.append("\t\t  " + trait.qtlEffects2[i]);
				text.append("\n");
			}
		}
		
/*		text.append("QTL Effects\n";
		text.append(" Mean:                " + Prefs.d3.format(trait.mean)
			+ "\tse " + Prefs.d3.format(trait.mean_se) + "\n";
		for (int i = 0; i < 3; i++)
			text.append(" Effect of chrom " + (i+2) + " :  "
				+ Prefs.d3.format(trait.ch_e[i])
				+ "\tse " + Prefs.d3.format(trait.ch_se[i]) + "\n";
*/		
		// Lod details
		PermResult result = trait.getPermResult();
		if (result != null)
		{
			StringBuffer lod = new StringBuffer(1000);
			
			lod.append("90% = " + result.getSig90() + "\n");
			lod.append("95% = " + result.getSig95() + "\n\n");
			
			for (int i = 0; i < result.lodScores.length; i++)
				lod.append(result.lodScores[i] + "\n");
			
			lodDetails.setText(lod.toString());
			lodDetails.setCaretPosition(0);
		}


		details.setText(text.toString());
		currentTrait = trait;
		toolbar.enableButtons(true, qtlResult.getTraitFile());
		
		splits.setTopComponent(getChart(trait));
	}
	
	private JPanel getChart(Trait trait)
	{
		JFreeChart chart = ChartFactory.createXYLineChart(
			null,
			"Position (cM)",
			"LOD Score",
			null,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);
		
		setChartData(chart, trait);
				
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
		chart.setRenderingHints(rh);
		chart.removeLegend();
		
		XYPlot plot = chart.getXYPlot();		
		
		ValueAxis axis = plot.getRangeAxis();
		if (trait.maxLOD <= 3)
			axis.setUpperBound(3);
		
		PermResult result = trait.getPermResult();
		if (result != null)
		{
			float[] dashPattern = { 5, 5 };
			BasicStroke s1 = new BasicStroke(1, BasicStroke.CAP_BUTT,
	    		BasicStroke.JOIN_MITER, 10, dashPattern, 0);
		
			ValueMarker m1 = new ValueMarker(
				result.getSig90(), new Color(0, 0, 200, 128), s1, null, null, 0.1f);
			ValueMarker m2 = new ValueMarker(
				result.getSig95(), new Color(0, 0, 200, 128), s1, null, null, 0.1f);
        
        	plot.addRangeMarker(m1);
        	plot.addRangeMarker(m2);
        	
        	if (result.getSig95() > trait.maxLOD && result.getSig95() >= 3)
        		axis.setUpperBound(result.getSig95() * (1.05));
        }

/*		{
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        	rangeAxis.setLowerBound(0);
        	rangeAxis.setNumberFormatOverride(new DecimalFormat("0"));
        	rangeAxis.setLabelFont(Prefs.labelFont);
        }
*/

		chartPanel = new ChartPanel(chart);
		chartPanel.setPopupMenu(null);
		return chartPanel;
	}
	
	private void setChartData(JFreeChart chart, Trait trait)
	{
		XYSeriesCollection data = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Position vs LOD Score");
		XYSeries series2 = new XYSeries("Position vs LOD Score");
		
		for (int i = 0; i < trait.getPositions().size(); i++)
			series1.add(trait.getPositions().get(i),
				trait.getLODs().get(i));
	
		chart.getXYPlot().setDataset(data);
		
		// If altQTL has been run, plot its data too
		if (trait.getLODs2() != null && trait.getLODs2().size() > 0)
		{
			for (int i = 0; i < trait.getPositions().size(); i++)
				series2.add(trait.getPositions().get(i),
					trait.getLODs2().get(i));
			
			data.addSeries(series2);
		}
		
		data.addSeries(series1);
	}
	
	void savePNG()
	{
		try { chartPanel.doSaveAs(); }
		catch (Exception e)
		{
			doe.MsgBox.msg("TetraploidMap could not save the chart due to the "
				+ "following error:\n" + e, doe.MsgBox.ERR);
		}
	}
	
	void saveTXT()
	{
		JFileChooser fc = new JFileChooser();
//		fc.addChoosableFileFilter(Filters.getFileFilter(3));
		fc.setCurrentDirectory(new File(Prefs.gui_dir));
		fc.setDialogTitle("Save QTL Results");

		while (fc.showSaveDialog(MsgBox.frm) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			
			// Make sure it has an appropriate extension
			if (!file.exists())
				if (file.getName().indexOf(".") == -1)
					file = new File(file.getPath() + ".csv");

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
			saveTXTFile(file);
			return;
		}		
	}
	
	void saveTXTFile(File file)
	{
		Trait trait = currentTrait;
		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			
			for (int i = 0; i < trait.getPositions().size(); i++)
			{
				out.write(trait.getPositions().get(i) + ", "
					+ trait.getLODs().get(i));
				if (trait.getLODs2() != null)
					out.write(", " + trait.getLODs2().get(i));
				out.newLine();
			}
			
			out.close();
		}
		catch (Exception e)
		{
			doe.MsgBox.msg("TetraploidMap could not save the chart due to the "
				+ "following error:\n" + e, doe.MsgBox.ERR);
		}
	}
	
	class ModelPanel extends JPanel
	{
		JEditorPane modelDetails;
		
		ModelPanel()
		{
			modelDetails = new JEditorPane("text/html", "");
			
			modelDetails.setMargin(new Insets(2, 5, 2, 5));
			modelDetails.setEditable(false);
			modelDetails.setBackground(Color.white);
			
			JScrollPane sp = new JScrollPane(modelDetails);
			sp.setHorizontalScrollBarPolicy(sp.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			setLayout(new BorderLayout());
			add(sp);
		}
		
		void setTrait(Trait trait)
		{
			if (trait == null || trait.qtlEffects == null)
			{
				modelDetails.setText("");
				return;
			}
			
			StringBuffer text = new StringBuffer(1000);
			
			text.append("<html><head>");
			text.append("<STYLE type='text/css'>TD {text-align: right; font-family: courier; font-size: 11;}</STYLE></head>");
			
			text.append("<table cellspacing=2 cellpadding=0>");			
			text.append("<tr><td align='left'><b>Model</td><td><b>sig</td><td><b>lod</td><td><b>mean_1</td><td><b>se_1</td><td><b>mean_2</td><td><b>se_2</td></tr>");
			
			for (int i = 0; i < 10; i++)
			{
				boolean highlight = trait.modelScores[i][0] >= 0.05;
				
				text.append("<tr><td align='left'>");
				if (highlight) text.append("<b>");
				text.append((i+1) + " " + getModel(i+1) + "</td>");
				for (int j = 0; j < 6; j++)
				{
					text.append("<td>&nbsp;&nbsp;");
					if (highlight) text.append("<b><font color=\"##ff0000\">");
					
					text.append(Prefs.d3.format(trait.modelScores[i][j]) + "</td>");
				}
				text.append("</tr>");
			}
			
			text.append("</table>");
			
			modelDetails.setText(text.toString());
		}
	}
	
	private String getModel(int i)
	{
		switch (i)
		{
			case 0:  return "Full Model";
			case 1:  return "C1 vs rest";
			case 2:  return "C2 vs rest";
			case 3:  return "C3 vs rest";
			case 4:  return "C4 vs rest";
			case 5:  return "Q12 vs rest";
			case 6:  return "Q13 vs rest";
			case 7:  return "Q14 vs rest";
			case 8:  return "Q23 vs rest";
			case 9:  return "Q24 vs rest";
			case 10: return "Q34 vs rest";
		}
		
		return "";
	}
	
	void runRescan()
	{
		int model = getSelectedModel(false, getBestModel());
		if (model == -1) return;
		
		int cmLength = (int) order.getDistanceTotal();
		String tName = currentTrait.getName();
		
		AltQtlDialog dialog = new AltQtlDialog(MsgBox.frm, qtlResult.getTraitFile(),
			currentTrait, model, cmLength);
		if (dialog.isOK())
		{
			displayTrait(currentTrait);
			AppFrameMenuBar.aFileSave.setEnabled(true);
		}
		
	}
	
	void runPerm()
	{
		Object[] values = { "Full Model", "Reduced Model" };
		Object selected = JOptionPane.showInputDialog(MsgBox.frm,
			"Please select which model to use:", "Select Model",
			JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
	
		if (selected == null)
			return;
		
		// Full model or reduced?
		boolean fullModel = selected == values[0];
		
		int cmLength = (int) order.getDistanceTotal();
		String tName = currentTrait.getName();
		
		PermDialog dialog = new PermDialog(MsgBox.frm, qtlResult.getTraitFile(), fullModel, tName, cmLength);
		if (dialog.isOK())
		{
			PermResult result = dialog.getResult();
			currentTrait.setPermResult(result);
			
			displayTrait(currentTrait);
			AppFrameMenuBar.aFileSave.setEnabled(true);
		}

	}
	
	// Works out which of the simple models is a suitable replacement for the
	// Full Model (if any)
	private int getBestModel()
	{
		int bestModel = 0;		
		float bestScore = 0;
		
		for (int i = 0; i < 10; i++)
		{
			float score = currentTrait.modelScores[i][0];
			
			if (score >= 0.05 && score > bestScore)
			{
				bestModel = i;
				bestScore = score;
			}
		}
		
		System.out.println("Best is " + (bestModel));
		
		return bestModel;
	}
	
	// Displays a dialog on screen that allows the user to select from the
	// available models. allowZero=Full Model available, best=initial selection
	private int getSelectedModel(boolean allowZero, int best)
	{
		int length = allowZero ? 11 : 10;
		Object[] values = new Object[length];
		
		int m = allowZero ? 0 : 1;
		for (int i = 0; i < length; i++)
			values[i] = getModel(m++);
		
		Object selected = JOptionPane.showInputDialog(MsgBox.frm,
			"Please select which model to use:", "Select Model",
			JOptionPane.QUESTION_MESSAGE, null, values, values[best]);
	
		if (selected == null)
			return -1;
		
		for (int i = 0; i < length; i++)
			if (selected == values[i])
				if (allowZero)
					return i;
				else
					return i+1;
		
		return -1;
	}
}
package gui;

import java.awt.*;
import java.awt.print.*;
import java.text.*;
import javax.swing.*;

import data.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;
import org.jfree.data.xy.*;

class DendrogramChart extends JPanel implements Printable
{
	private Dendrogram d;
	
	private CardLayout card;
	private ChartPanel panelNormal, panelLogged;
	
	private boolean isLogged = false;
	
	DendrogramChart(Dendrogram d, boolean log)
	{
		this.d = d;
		
		card = new CardLayout();
		setLayout(card);
		
		panelNormal = getChartPanel(d, false);
		panelLogged = getChartPanel(d, true);
		
		add("false", panelNormal);
		add("true", panelLogged);		
	}
	
	ChartPanel getChartPanel(Dendrogram d, boolean log)
	{		
		JFreeChart chart = ChartFactory.createXYLineChart(
			null,
			"Similarity", 
			"No. of Groups", 
			null,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);
		
		setChartData(chart, log);
				
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
		chart.setRenderingHints(rh);
		chart.removeLegend();
		
		XYPlot plot = chart.getXYPlot();		
		if (log == false)
		{
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        	rangeAxis.setLowerBound(0);
        	rangeAxis.setNumberFormatOverride(new DecimalFormat("0"));
        	rangeAxis.setLabelFont(Prefs.labelFont);
        }
        else
        {
//	       	LogarithmicAxis logXAxis = new LogarithmicAxis("Similarity");
//			logXAxis.setAllowNegativesFlag(true);
//			LogarithmicAxis logYAxis = new LogarithmicAxis("No. Of Groups");
//			logYAxis.setAllowNegativesFlag(true);
			
//			plot.setDomainAxis(logXAxis);
//			plot.setRangeAxis(logYAxis);
        }
		
		ChartPanel chartPanel = new ChartPanel(chart);
//		chartPanel.setPopupMenu(null);
		return chartPanel;
	}
	
	private void setChartData(JFreeChart chart, boolean log)
	{
		XYSeries series = new XYSeries("Similarity vs No. of Groups");
		
		double rSim = d.getRootSimilarity();
		double diff = 1 - rSim;
		
		// TODO: What causes this?
		if (diff == 0)
			return;
			
		for (double sim = rSim; sim <= 1.0; sim += (diff/50))
		{
			int count = d.getGroupCount(sim);
			
			if (log == false)
				series.add(sim, d.getGroupCount(sim));
			else if (count > 0 && sim > 0)
				series.add(Math.log10(sim), Math.log10(count));
		}
		
		XYSeriesCollection data = new XYSeriesCollection(series);		
		chart.getXYPlot().setDataset(data);
	}
	
	void setVisibleChart(boolean log)
	{
		card.show(this, "" + log);
		isLogged = log;
	}
	
	public int print(Graphics graphics, PageFormat pf, int pageIndex)
	{
		return getCurrentChartPanel().print(graphics, pf, pageIndex);
	}
	
	void setScaling(boolean autorange, double min, double max)
	{
		JFreeChart chart = ((ChartPanel)getComponent(0)).getChart();
		ValueAxis axis = chart.getXYPlot().getDomainAxis();
		
		axis.setAutoRange(autorange);
		if (!autorange)
		{
			axis.setLowerBound(min);
			axis.setUpperBound(max);
		}
	}
	
	private ChartPanel getCurrentChartPanel()
	{
		if (isLogged == false)
			return ((ChartPanel)getComponent(0));
		else
			return ((ChartPanel)getComponent(1));
	}
}
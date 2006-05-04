package data;

import java.io.*;
import java.text.*;
import java.util.*;

import pal.statistics.*;

public class Marker implements Serializable
{
	static final long serialVersionUID = 4313476580087668492L;
	
	public static final int UNKNOWN = 0;
	
	public static final int RFLP = 1;
	public static final int AFLP = 2;
	public static final int SSR  = 3;
	
	public static final int R1_1  = 1;
	public static final int R5_1  = 5;
	public static final int R3_1  = 3;
	public static final int R11_1 = 11;
	public static final int R35_1 = 35;
		
	// This marker's name
	private String name = null;
	private String prefix = null;
	
	private Allele[] alleles;
	private int percentageUnknown;
	
	// Marker type: AFLP, SSR, etc
	private int type = UNKNOWN;
	
	// Data obtained from FindGeno analysis
		// Band pattern information
		private LinkedList<MarkerBandPattern> bands =
			new LinkedList<MarkerBandPattern>();
		// Posterior probabilities in presence of double reduction
		private LinkedList<MarkerProbability> probsPre =
			new LinkedList<MarkerProbability>();
		// Posterior probabilities in absense of double reduction
		private LinkedList<MarkerProbability> probsAbs =
			new LinkedList<MarkerProbability>();
		// Parental phenotypes
		private String[] phenotype = new String[2];
		// Double reduction test - alpha and LR values
		private float drAlpha, drLR;
		private double drSig;
		
	// Data post-FINDGENO that is calculated by this program
		// Marker ratio: 1:1, 3:1, etc
		private int ratio = UNKNOWN;
		// And the posterior probability that gives it
		private MarkerProbability bestRatio = null;
//		private boolean isBestRatioKnown = true;
		// What parents is this marker present in
		private boolean[] isPresentInParent = new boolean[2];

	// Data obtained from SimMatch analysis
		private SimMatchData simData = new SimMatchData();

	public Marker(String markerName, int alleleCount)
		throws CreationException
	{
		name = markerName;
		alleles = new Allele[alleleCount];
		
		if (alleleCount == 1)
			type = AFLP;
		else
			type = SSR;
		
/*		// Ensure marker name is <= 20 chars
		if (name.length() > 20)
			throw new CreationException(CreationException.NAME_TOO_LONG);

		// Replace illegal characters
		name = name.replace('(', '_');
		name = name.replace(')', '_');
		name = name.replace(':', '_');
		name = name.replace(';', '_');
		name = name.replace(',', '_');
*/
	}
	
	public SimMatchData getSimMatchData()
	{
		if (simData == null)
			simData = new SimMatchData();
		return simData;
	}
	
	public void setType(int type) { this.type = type; }
	
	public void addAllele(int alleleIndex, Allele a)
	{
		alleles[alleleIndex] = a;
	}
	
	public void setPhenotypeInfo(String p1, String p2)
	{
		phenotype[0] = p1;
		phenotype[1] = p2;
		
		// Test for the presence of this marker in the two parents
		if (phenotype[0].contains("1"))
			isPresentInParent[0] = true;
		if (phenotype[1].contains("1"))
			isPresentInParent[1] = true;
	}
	
	public String getPhenotypeInfo(int p) { return phenotype[p-1]; }
	
	public boolean isPresentInParent(int p) { return isPresentInParent[p-1]; }
	
	public boolean isBestRatioKnown()
	{
		if (bestRatio == null)
			return false;
		else
			return true;
//		return isBestRatioKnown;
	}
		
	public void setDRTestValues(float a, float l)
	{
		drAlpha = a;
		drLR    = l;
		
		drSig = (1 - (ChiSquareDistribution.cdf(drLR, 1)));
	}
	
	public void addMarkerBandPattern(MarkerBandPattern band)
	{
		bands.add(band);
	}
	
	public int getMarkerBandPatternCount() { return bands.size(); }
	
	public void addMarkerProbability(MarkerProbability mp, boolean dblRPresent)
	{
		if (dblRPresent)
			MarkerProbability.insert(probsPre, mp);
		else
			MarkerProbability.insert(probsAbs, mp);
	}
	
	public String getName()
		{ return name; }
	
	public String getPrefix()
	{
		if (prefix == null)
			return "";
		else
			return prefix;
	}
	
	public String getDendrogramName()
	{
		if (prefix == null)
			return name;
		else
			return "(" + prefix + ") " + name;
	}
	
	public void setPrefix(String newPrefix)
		{ prefix = newPrefix; }
	
	public String toString() { return name; }
	
	public Allele getAllele(int index) { return alleles[index]; }
	
	public int getType() { return type; }
	
	public int getAlleleCount() { return alleles.length; }
	
	public float getAlpha() { return drAlpha; }
	
	public float getLR() { return drLR; }
	
	public double getDRSignificance() { return drSig; }
	
	public String getTypeDescription(boolean full)
	{
		switch (type)
		{
			case AFLP: return full ? "AFLP" : "A";
			case SSR:  return full ? "SSR"  : "S";
			case RFLP: return full ? "RFLP" : "R";
		}
		
		return "--";
	}
	
	// Attempts to determine the ratio of this marker. The ratio is known if
	// a) it's an AFLP marker with one best
	public boolean determineRatio()
	{		
		boolean canDetermineRatio = true;
				
		// In trouble? if none were found
		if (probsAbs.size() == 0) return false;
		
		// Check to see if multiple best matches were found
		if (probsAbs.size() > 1)
		{
			if (probsAbs.get(0).pb  == probsAbs.get(1).pb &&
				probsAbs.get(0).sig == probsAbs.get(1).sig)
			{
				canDetermineRatio = false;
			}
		}
		
		bestRatio = probsAbs.get(0);
		
		// We can only do this for AFLP markers?
		// TODO: RFLPs too?
		if (type != AFLP)
		{
			if (canDetermineRatio == false)
				bestRatio = null;
			return canDetermineRatio;
		}
		
		// 1:1
		if (bestRatio.p1.equals("1000") && bestRatio.p2.equals("0000") ||
			bestRatio.p1.equals("0000") && bestRatio.p2.equals("1000"))
		{
			ratio = R1_1;
		}
		
		// 5:1
		else if (bestRatio.p1.equals("1100") && bestRatio.p2.equals("0000") ||
			bestRatio.p1.equals("0000") && bestRatio.p2.equals("1100"))
		{
			ratio = R5_1;
		}
		
		// 11:1
		else if (bestRatio.p1.equals("1100") && bestRatio.p2.equals("1000") ||
			bestRatio.p1.equals("1000") && bestRatio.p2.equals("1100"))
		{
			ratio = R11_1;
		}
		
		// 3:1
		else if (bestRatio.p1.equals("1000") && bestRatio.p2.equals("1000"))
		{
			ratio = R3_1;
		}
		
		// 35:1
		else if (bestRatio.p1.equals("1100") && bestRatio.p2.equals("1100"))
		{
			ratio = R35_1;
		}
		
		// Although the bestRatio was allowed above, it was only temporary when
		// the canDetermineRatio value is false, otherwise markers will be
		// selectable when they shouldn't be.
		if (canDetermineRatio == false)
			bestRatio = null;
		return canDetermineRatio;
	}
	
	public double getRatioSignificance()
	{
		if (bestRatio != null)
			return bestRatio.sig;
		
		return 0;
	}
	
	public String getRatioGenotypes()
	{
		if (bestRatio != null)
			return bestRatio.p1 + " " + bestRatio.p2;
		else
			return "bestRatioUnknown";
//		else if (isBestRatioKnown == false)
//			return "bestRatioUnknown";
//		else
//			return "N/A";
	}
	
	public String getRatio()
	{
		switch (ratio)
		{
			case R1_1:  return "1:1";
			case R5_1:  return "5:1";
			case R11_1: return "11:1";
			case R3_1:  return "3:1";
			case R35_1: return "35:1";
		}
		
		if (bestRatio != null)
//		if (isBestRatioKnown)
			return "N/A";
		else
			return "--";
	}
	
	public int getRatioCode() { return ratio; }
	
	public String getSummaryInfo()
	{
		String str = new String("<html><pre><font size='3'>");
		
		str += "<b>Number of alleles:</b> " + alleles.length + "<br>";
		str += "<b>Number of individuals:</b> " + (alleles[0].getStateCount()-2)
			+  " (" + getMissingPercentage() + "% unknown)<br>";
		
		str += "<br><b>Phenotype 1  Phenotype 2</b>"
			+  "<br> " + phenotype[0] + "     " + phenotype[1]
			+  "<br>";
		
		str += "<br><b>Test for double reduction</b>"
			+  "<br>MLE of alpha and LR test: "
			+  drAlpha + " " + drLR
			+  "<br>DR significance: "
			+  drSig
			+  "<br>";
		
		str += "<br><b>Marker Band Patterns</b>";
		for (MarkerBandPattern mp: bands)
			str += "<br> " + mp.pattern + "\t" + mp.count + "\t" + mp.proportion;
		str += "<br>";
		
		DecimalFormat d = new DecimalFormat("0.0000000000");
		str += "<br><b>Posterior probabilities in presence of double reduction:"
			+  "<br> Parental genotypes\tProbability\td.f.\tchisquare\t sig</b>";
		for (MarkerProbability mp: probsPre)
			str += "<br> " + mp.p1 + " " + mp.p2 + "\t\t" + d.format(mp.pb) + "\t" + mp.df + "\t" + mp.chi + "\t " + (mp.sig);
		if (probsPre.size() == 0)
			str += "<br> No data available";
		str += "<br><b>And in absence of double reduction:</b>";
		for (MarkerProbability mp: probsAbs)
			str += "<br> " + mp.p1 + " " + mp.p2 + "\t\t" + d.format(mp.pb) + "\t" + mp.df + "\t" + mp.chi + "\t " + (mp.sig);
		if (probsAbs.size() == 0)
			str += "<br> No data available";
		
		str += "</html>";
		return str;
	}
	
	public String getName(int width)
	{
		if (name.length() >= width)
			return name;
		
		String str = name;
		for (int i = name.length(); i < width; i++)
			str += " ";
		return str;
	}
	
	private void determineMissingPercentage()
	{
		int count = 0;
		for (AlleleState state: alleles[0].getStates())
			if (state.getState() == AlleleState.UNKNOWN)
				count++;
		
		float percent = (count / (float)(alleles[0].getStateCount())) * 100;
		percentageUnknown = Math.round(percent);
	}
	
	public int getMissingPercentage() { return percentageUnknown; }
	
	public boolean canEnable()
	{
		// A marker's best genotype must be known
		boolean isOK = isBestRatioKnown();
		
		// And for SSRs, information on absense of dbl-reduction must be known
		if (type == SSR && probsAbs.size() == 0)
			isOK = false;
		
		return isOK;
	}
	
	// Performs a series of checks on the allele data within this marker to
	// ensure it is valid for further analysis by the fortran routines
	void verifyAlleles()
		throws CreationException
	{
		if (getAlleleCount() == 0)
			throw new CreationException(CreationException.NO_ALLELES);
		if (getAlleleCount() > 8)
			throw new CreationException(CreationException.TOO_MANY_ALLELES);
		
		if (type == SSR)
		{
			int iCount = alleles[0].getStateCount();
			byte UNKNOWN = AlleleState.UNKNOWN;
			
			for (int i = 0; i < iCount; i++)
			{
				boolean is9 = false;
				
				// Check each individual at this position of the allele
				for (int a = 0; a < alleles.length; a++)
				{
					// If it is 9...
					is9 = (alleles[a].getStates().get(i).getState() == UNKNOWN);
					break;
				}
				
				// ... Then all individuals at this position must be set to 9
				if (is9)
					for (int a = 0; a < alleles.length; a++)
						alleles[a].getStates().get(i).setState(UNKNOWN);
			}
		}
		
		determineMissingPercentage();
	}
}
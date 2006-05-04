package gui.importer;

import java.io.*;
import java.util.*;

import data.*;
import gui.*;

import doe.*;

public class ImportTest
{
	private LinkageGroup lGroup;
	private int iCount, mCount;
	private String lastMarker;
	
	public ImportTest(File file)
	{
		lGroup = new LinkageGroup(file.getName());
				
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
		
			StringTokenizer st = new StringTokenizer(in.readLine());
			try
			{
				iCount = Integer.parseInt(st.nextToken()) + 2;
				mCount = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)	{
				throw new CreationException(CreationException.UNKNOWN_FILE);
			}
			
			System.out.println("Reading " + iCount + " and " + mCount);
						
			for (int markers = 0; markers < mCount; markers++)
				readMarkerData(in);
			
//			printData();
		
			in.close();			
			lGroup.verify();
		}
		catch (Exception e)
		{
			String msg = "TetraploidMap was unable to open " + file + " due to "
				+ "the following reason:\n" + e;
			MsgBox.msg(msg, MsgBox.ERR);
			if (lastMarker != null)
				MsgBox.msg("The error occurred after marker: " + lastMarker,
					MsgBox.ERR);
			lGroup = null;
		}
	}
	
	public LinkageGroup getLinkageGroup() { return lGroup; }
	
	private void readMarkerData(BufferedReader in)
		throws CreationException, Exception
	{
		// Header data for this marker - its name and number of alleles
		StringTokenizer st = new StringTokenizer(in.readLine());
		String name = st.nextToken();
		lastMarker = name;
		int alleleCount = Integer.parseInt(st.nextToken());
		
		// Add this marker to the dataset
		Marker marker = lGroup.getOrAddMarker(name, alleleCount);
				
		// Then get all the available input - this will give us an array
		// containing all the allele_1 data, then all the _2 data, etc
		byte[] input = getIndividualData(in, iCount * alleleCount);	
		
		// Then, add data for each Allele based on this
		for (int a = 0; a < alleleCount; a++)
		{
			// Create the Allele...
			Allele allele = new Allele(marker);
			// ...and add it to the Marker
			marker.addAllele(a, allele);
		
			// Then give it the Individual/State information
			for (int i = 0; i < iCount; i++)
				allele.addAlleleState(new AlleleState(input[(a*iCount) + i]));
		}
	}
	
	// Reads input until the given number of individuals have been found
	private byte[] getIndividualData(BufferedReader in, int count)
		throws Exception
	{
		byte[] input = new byte[count];
		int found = 0;
		
		while (found < input.length)
		{
			StringTokenizer st = new StringTokenizer(in.readLine());
			while (st.hasMoreElements())
				input[found++] = Byte.parseByte(st.nextToken());
		}
		
		return input;
	}
	
	private void printData()
	{
		for (CMarker cm : lGroup.getMarkers())
		{
			Marker m = cm.marker;
			
			System.out.println(m.getName());
			for (int a = 0; a < m.getAlleleCount(); a++)
			{
				Allele allele = m.getAllele(a);
				for (AlleleState state: allele.getStates())
					System.out.print(" " + state.getState());
				System.out.println();
			}
		}
	}
}
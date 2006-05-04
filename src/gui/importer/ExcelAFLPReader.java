package gui.importer;

import java.io.*;

import data.*;

import doe.*;

import jxl.*;

class ExcelAFLPReader
{
	LinkageGroup lGroup;
	
	ExcelAFLPReader(File file, int mainSheet, int[] sheets)
	{
		Workbook workbook = null;
		lGroup = new LinkageGroup(file.getName());
		
		try
		{
			workbook = Workbook.getWorkbook(file);
			
			// Read in the main sheet and its data
			readMarkers(workbook.getSheet(mainSheet));
			readIndividuals(workbook.getSheet(mainSheet), 1);
			
			// Read in any additional sheets too
			for (int i = 0; i < sheets.length; i++)
				readIndividuals(workbook.getSheet(sheets[i]), 0);
			
			lGroup.verify();
//			printData();
		}
		catch (Exception e)
		{
			String msg = "TetraploidMap was unable to open " + file + " due to "
				+ "the following reason:\n" + e.getMessage();
			MsgBox.msg(msg, MsgBox.ERR);
			lGroup = null;
		}
		finally
		{
			if (workbook != null)
				workbook.close();
		}		
	}
	
	private void readMarkers(Sheet sheet)
		throws Exception
	{
		// Start at A2 and read down until the end of the markers		
		for (int row = 1; row < sheet.getRows(); row++)
		{
			Cell cell = sheet.getCell(0, row);
			String name = cell.getContents();
			
			if (name.length() == 0)
				throw new Exception("The marker in row " + (row+1) + " has no name.");
			
			// Create the marker
			Marker marker = lGroup.getOrAddMarker(name, 1);
			marker.setType(Marker.AFLP);
			// Create and add its allele...
			marker.addAllele(0, new Allele(marker));
		}
	}
	
	private void readIndividuals(Sheet sheet, int colStart)
		throws Exception
	{
		Cell cell = null;
		
		// For each row of data (a marker) in the sheet...
		for (int row = 1; row < sheet.getRows(); row++)
		{
			Marker marker = lGroup.getMarker(row-1).marker;			
			Allele allele = marker.getAllele(0);
			
			// Read the actual indivudal data
			for (int col = colStart; col < sheet.getColumns(); col++)
			{
				cell = sheet.getCell(col, row);
				String data = cell.getContents();
				
				if (data.length() == 0)
					throw new Exception("Cell " + (row+1) + "," + (col+1) + " "
						+ "in worksheet " + sheet.getName() + " has no data.");
				
				byte state = 0;
				try { state = Byte.parseByte(cell.getContents()); }
				catch (Exception e)
				{
					throw new Exception("Cell " + (row+1) + "," + (col+1) + " "
						+ "in worksheet " + sheet.getName() + " does not "
						+ "contain numerical data for an individual.");
				}
				
				allele.addAlleleState(new AlleleState(state));
			}
		}
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
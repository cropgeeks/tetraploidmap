package gui.exporter;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import data.*;
import gui.*;

public class ZipHandler
{
	private File filename;
	private ZipOutputStream zOut;
	
	public ZipHandler(File filename)
	{
		this.filename = filename;
	}
	
	public void addFile(File srcFile, String srcID)
		throws Exception
	{
		boolean zipExisted = filename.exists();
System.out.println("ZipExists: " + zipExisted);
		
		// Work out the zip file handle - create from new, or clone an existing
		// zip and get a handle to append to it
		if (zipExisted)
			copyZip();
		else
		{
System.out.println("Creating new zip file: " + filename);
			zOut = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(filename)));
		}
		
		// Copy file from src to dest
System.out.println("Copying " + srcFile + " into zip");
		FileInputStream fIn = new FileInputStream(srcFile);
		copyFileIntoZip(fIn, new ZipEntry(srcID));
		
		zOut.close();
		
		// Now delete original, and rename tmp
		if (zipExisted)
		{
System.out.println("Deleting/renaming original zipfile");
			filename.delete();
			new File(filename + "_tmp").renameTo(filename);
		}
	}
	
	// Copies a zip file to a new file, returning a handle to write to the new
	// zip file (for further additions)
	private ZipOutputStream copyZip()
		throws Exception
	{
		File tmpZip = new File(filename + "_tmp");
System.out.println("Cloning zipfile to " + tmpZip);
		
		zOut = new ZipOutputStream(
			new BufferedOutputStream(new FileOutputStream(tmpZip)));
		
		ZipInputStream zIn = new ZipInputStream(new FileInputStream(filename));
		
		ZipEntry entry = zIn.getNextEntry();
		while (entry != null)
		{
		
//		ZipFile zip = new ZipFile(filename);
//		Enumeration e = zip.entries();
//		while (e.hasMoreElements())
//		{
//			ZipEntry entry = (ZipEntry) e.nextElement();
//			ZipInputStream zIn = new ZipInputStream(
//				new BufferedInputStream(zip.getInputStream(entry)));
			
System.out.println("Cloning entry " + entry.getName() + " " + entry.getSize());
			copyFileIntoZip(zIn, new ZipEntry(entry));
			
			entry = zIn.getNextEntry();
		}
		
		return zOut;
	}
	
	// Copies the File in src to dest
	private void copyFileIntoZip(InputStream src, ZipEntry entry)
		throws Exception
	{
		zOut.putNextEntry(entry);
		
		byte[] buffer = new byte[1024];
		int bytesRead;
		
		while ((bytesRead = src.read(buffer)) != -1)
			zOut.write(buffer, 0, bytesRead);
		
		zOut.closeEntry();
		src.close();
	}
	
	
/*	public static void addFile(File file, String id)
		throws Exception
	{
		File oFile = new File(Project.filename + "_qtl");
		
		// Open an output stream to the zip...
		ZipOutputStream zOut = new ZipOutputStream(
			new BufferedOutputStream(new FileOutputStream(oFile)));
			
//		// And another to write to within the zip...
//		BufferedWriter bOut = new BufferedWriter(
//			new OutputStreamWriter(zOut));
		
		System.out.println("writing id: " + id);
		ZipEntry entry = new ZipEntry(id);
		zOut.putNextEntry(entry);
		
		copyFile(file, zOut);
		zOut.closeEntry();
		
		zOut.close();	
	}
	
	private static void copyFile(File filename, ZipOutputStream zOut)
		throws Exception
	{
		byte[] buffer = new byte[1024];
		int bytesRead;
		
		FileInputStream file = new FileInputStream(filename);
		
		while ((bytesRead = file.read(buffer)) != -1)
        	zOut.write(buffer, 0, bytesRead);
		
//		in.close();
	}
	*/
}
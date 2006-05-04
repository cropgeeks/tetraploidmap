package data;

import java.io.*;
import java.util.*;

public class QTLResult implements Serializable
{
	static final long serialVersionUID = -5088939235379013495L;
	
	private TraitFile traitFile;
	
	private Vector<Trait> traits = new Vector<Trait>();
	private String name;
	
	private String backupFile;
	
	public QTLResult()
	{
	}
	
	public Vector<Trait> getTraits()
		{ return traits; }
	
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public String getBackupFile()
		{ return backupFile; }
	
	public void setBackupFile(String backupFile)
		{ this.backupFile = backupFile; }
	
	public TraitFile getTraitFile()
		{ return traitFile; }
	public void setTraitFile(TraitFile traitFile)
		{ this.traitFile = traitFile; }
}
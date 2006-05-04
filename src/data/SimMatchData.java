package data;

import java.io.*;
import java.util.*;

public class SimMatchData implements Serializable
{
	static final long serialVersionUID = -4940033113702898385L;
	
	// TODO: At some point maybe - why can't I create typesafe vector of vectors
	// or an array of Vector<SigLinkage> objects
	
	public Vector<SigLinkage> p1A;
	public Vector<SigLinkage> p2A;
	
	public Vector<SigLinkage> p1B;	
	public Vector<SigLinkage> p2B;
	
	public Vector<SigLinkage> p1C;
	public Vector<SigLinkage> p2C;
	
	public SimMatchData()
	{
		p1A = new Vector<SigLinkage>();
		p2A = new Vector<SigLinkage>();
		
		p1B = new Vector<SigLinkage>();		
		p2B = new Vector<SigLinkage>();
		
		p1C = new Vector<SigLinkage>();
		p2C = new Vector<SigLinkage>();
	}
	
	public void add(Vector<SigLinkage> array, SigLinkage data)
	{
		for (int i = 0; i < array.size(); i++)
		{
			if (array.get(i).chi <= data.chi)
			{
				array.add(i, data);
				return;
			}
		}
		
		array.add(data);
	}
}
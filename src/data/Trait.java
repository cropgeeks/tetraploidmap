package data;

import java.io.*;
import java.util.*;

public class Trait implements Serializable
{
	static final long serialVersionUID = 9029610617375265016L;
	
	private String name;
	
	private Vector<Float> positions = new Vector<Float>();
	private Vector<Float> lods = new Vector<Float>();
	private Vector<Float> lods2;
	
	private PermResult permResult;
	
	public float qtlPosition, qtlPosition2;
	public float varExplained, varExplained2;
	public float errMS, errMS2;
	public float maxLOD, maxLOD2;
/*	public float mean, mean_se;
	public float ch_e[] = new float[3];
	public float ch_se[] = new float[3];
*/	

	public String[] qtlEffects = new String[6];
	public String[] qtlEffects2;
	public float[][] modelScores = new float[10][6];
	public float[][] modelScoresExtra = new float[10][1];

	public String getName()
		{ return name; }
	
	public String toString()
		{ return name; }
	
	public Vector<Float> getPositions()
		{ return positions; }
	
	public Vector<Float> getLODs()
		{ return lods; }
	
	public Vector<Float> getLODs2()
		{ return lods2; }
	
	public void createLODs2()
	{ 
		lods2 = new Vector<Float>();
		qtlEffects2 = new String[2];
	}
	
	public void setName(String line)
	{
		name = line;
	}
	
	public PermResult getPermResult()
		{ return permResult; }
	
	public void setPermResult(PermResult permResult)
		{ this.permResult = permResult; }
}
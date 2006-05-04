package data;

import java.io.*;

public class MarkerBandPattern implements Serializable
{
	static final long serialVersionUID = 4389495040299036847L;
	
	String pattern;
	int count;
	float proportion;
	
	public MarkerBandPattern(String pattern, int count, float proportion)
	{
		this.pattern = pattern;
		this.count = count;
		this.proportion = proportion;
	}	
}

package data;

import java.io.*;

public class PermResult implements Serializable
{
	static final long serialVersionUID = 6575547173738096739L;
	
	public float[] lodScores;	
	private float sig90, sig95;
	
	public PermResult()
	{
		lodScores = new float[100];
	}
	
	public void setSigScores(float sig90, float sig95)
	{
		this.sig90 = sig90;
		this.sig95 = sig95;
	}
	
	public float getSig90()
		{ return sig90; }
	
	public float getSig95()
		{ return sig95; }
}
package data;

import java.io.*;

public class SigLinkage implements Serializable
{
	static final long serialVersionUID = 5919462699368603914L;
	
	public final Marker marker;
	
	public final float chi;
	public final float sig;
	public final String phase;
	
	public SigLinkage(Marker m, float c, float s, String ph)
	{
		marker = m;
		chi = c;
		sig = s;
		phase = ph;
	}
	
	public SigLinkage(Marker m, float c, float s)
		{ this(m, c, s, null); }
}
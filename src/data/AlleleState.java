package data;

import java.io.*;

public class AlleleState implements Serializable
{
	static final long serialVersionUID = -1163321568714213767L;	
	
	public final static byte PRESENT = 1;
	public final static byte ABSENT  = 0;
	public final static byte UNKNOWN = 9;
	
	private byte state;
	
	public AlleleState(byte state)
	{
		this.state = state;
	}
	
	public void setState(byte state) { this.state = state; }
	
	public byte getState() { return state; }
}
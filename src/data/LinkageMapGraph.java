package data;

import java.io.*;
import java.util.*;

public class LinkageMapGraph implements Serializable
{
	static final long serialVersionUID = 76713199780917726L;
	
	private String name;
	private LinkageGroup[] lGroups;
	
	public LinkageMapGraph(LinkageGroup[] lGroups)
		{ this.lGroups = lGroups; }
	
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public LinkageGroup[] getLinkageGroups()
		{ return lGroups; }
}

package gui.nav;

import data.*;
import gui.*;

class ClusterNode
{
	// The cluster
	Cluster cluster;
	// The linkage group that owns this cluster
	LinkageGroup lGroup;
	
	ClusterNode(Cluster cluster, LinkageGroup lGroup)
	{
		this.cluster = cluster;
		this.lGroup = lGroup;
	}
	
	public String toString() { return cluster.getName(); }
}
	
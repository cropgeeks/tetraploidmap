package analyses.cluster;

import data.*;
import gui.*;

import doe.*;

public class ClusterAnalysis
{
	private boolean isOK = false;
	
	private Cluster cluster = new Cluster();
	
	public ClusterAnalysis(AppFrame frame, LinkageGroup grp)
	{
		// Start with the original linkageGroup
		LinkageGroup oGroup = grp.getClonedLinkageGroup(true, false);
		
		// And create a subgroup containing just the 1:1 markers
/*		LinkageGroup nGroup = new LinkageGroup(null);
		for (CMarker cm: oGroup.getMarkers())
		{
			if (cm.marker.getRatioCode() == Marker.R1_1)
				nGroup.addMarker(cm.marker);
		}
		
		System.out.println("1:1 count is " + nGroup.getMarkerCount());
		System.out.println("Org count is " + oGroup.getMarkerCount());
		
		// Now run the clustering on this sub group
		if (nGroup.getMarkerCount() > 0)
		{
			Cluster tmp = new Cluster();
			ClusterDialog dialog = new ClusterDialog(frame, nGroup, tmp, 2);
			
			if (dialog.isOK() == false)
				return;
			
			new ThresholdDialog(frame, tmp);
		}
		
		// Modify the original group's names based on this result...
		for (CMarker cmN: nGroup.getMarkers())
			for (CMarker cmO: oGroup.getMarkers())
				if (cmO.marker.getName().equals(cmN.marker.getName()))
					cmO.setPrefix("H__");
*/				
		
		// And run the final clustering on that group
		ClusterDialog dialog = new ClusterDialog(frame, oGroup, cluster, 2);
				
		if (dialog.isOK() && cluster.getGroups().size() > 0)
			isOK = true;
			
		else if (dialog.isOK() && cluster.getGroups().size() == 0)
			MsgBox.msg("No linkage groups were returned by the analysis.",
				MsgBox.INF);
	}
	
	public Cluster getCluster()
		{ return isOK ? cluster : null; }
}

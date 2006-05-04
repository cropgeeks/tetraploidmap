package analyses.order.custom;

import java.util.*;

import data.*;

import Jama.*;

public class CustomAnalysis extends Thread
{
	// The list of markers to work with
	private LinkageGroup lGroup;
	// Object storing the phase information and final ordered list of markers
	private OrderedResult result;
	// Number of markers we are dealing with
	private int num;
	
	// A reworking of the PhasePair list to allow quick access to any mkr/mkr
	// combination, using their safeName indexes to determine position...ie
	// MKR005 is at element [5].
	private PhasePair[][] ppData;
	
	boolean isRunning = true;
	int mkrCount = 0;
	
	public CustomAnalysis(OrderedResult result, LinkageGroup lGroup)
	{
		this.result = result;
		this.lGroup = lGroup;
		
		num = lGroup.getMarkerCount();
//		createPhasePairArray();
		ppData = result.getPhasePairArray();
	}
	
	public void run()
	{
		int bestOrder = 0;
		float bestRSS = -1;
		Matrix bestC = null;
		
		CreateOrder createOrder = new CreateOrder(result, lGroup, ppData);
		
		for (int i = 0; i < num && isRunning; i++, mkrCount++)
		{
			long s = System.currentTimeMillis();
			Vector<CMarker> order = createOrder.getOrder(i);
			long e = System.currentTimeMillis();
//			System.out.println("Order creation in " + (e-s) + "ms");
			
			s = System.currentTimeMillis();
			Matrix[] result = computeMatrices(order);
			e = System.currentTimeMillis();
//			System.out.println("Matrix computation in " + (e-s) + "ms");
			
			float rSS = (float) result[1].get(0, 0);
			
			if (bestRSS == -1 || rSS < bestRSS)
			{
				bestOrder = i;
				bestRSS = rSS;
				bestC = result[0];
			}
			
//			System.out.println("For order " + i + " rSS is ");
			result[1].print(2, 10);
		}
		
//		System.out.println();
//		System.out.println("Best order is " + bestOrder + " with " + bestRSS);
		
		// Takes the final ordering and sets it to the OrderedResult object
		if (isRunning)
		{
			// Marker order
			for (CMarker cm: createOrder.getOrder(bestOrder))
				result.addMarker(cm);
			
			// Intermarker distances
			double[][] c = bestC.getArray();
			for (int r = 0; r < c.length; r++)
				result.addDistance((float)c[r][0]);
		}
		
		isRunning = false;
	}
	
	// Computes the matrices C^ and rSS for the given marker order
	private Matrix[] computeMatrices(Vector<CMarker> order)
	{
		MatrixHandler m = new MatrixHandler(order, ppData);
		Matrix R = m.getR();
		Matrix K = m.getK();
		
		// K-transposed
		Matrix Kt = K.transpose();
		// R-transposed
		Matrix Rt = R.transpose();
		// Inverse of (K-transposed * K)
		Matrix KtK_i = Kt.times(K).inverse();
		// K-transposed * R
		Matrix KtR = Kt.times(R);
		
		Matrix C = KtK_i.times(KtR);
		
		Matrix rSS = Rt.times(R).minus(Rt.times(K).times(KtK_i).times(KtR));
		
		if (MatrixHandler.print)
		{
			System.out.println("C:");
			C.print(5, 2);
		}
		
		Matrix[] result = { C, rSS };	
		return result;
	}
	
	
	// Constructs a 2D array of PP values based on the fact that we know the
	// ordering of the PP list is 0:1, 0:2, 0:3, 1:2, 1:3, 2:3, etc
	// TODO: this may stop working if the passed-in PP list is not the same as
	// the marker list - that is, markers have been removed SINCE the TwoPoint
	// analysis.
	// If this is the case, then a new way of constructing this array is needed
	// - and this array IS needed, as it's too slow to search the list each time
	// a PP must be found
/*	private void createPhasePairArray()
	{
		ppData = new PhasePair[num][num];
		
		ListIterator<PhasePair> itor = result.getPhasePairs().listIterator(0);
		for (int i = 0; i < num; i++)
		{
			for (int j = (i+1); j < num; j++)
			{
				ppData[i][j] = itor.next();
				ppData[j][i] = ppData[i][j];
			}
		}
	}
*/
	
	void exit()
	{
		isRunning = false;
	}
}
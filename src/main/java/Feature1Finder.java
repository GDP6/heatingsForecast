import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Feature1Finder {



	Double threshold = 0.0 ;
	int minLength = 3;
	int startLead = 4;
	int endFollow = 20;



	Double[] sensor2list;
	Double[] sensor3list;
	Long[] unixTimes;
	ArrayList<Cooling> listOfCoolings = new ArrayList<Cooling>();
	public Feature1Finder(Double[] sensor2list, Double[] sensor3list, Long[] unixTimes) {
		super();
		this.sensor2list = sensor2list;
		this.sensor3list = sensor3list;
		this.unixTimes = unixTimes;
	}


	public void faucetIncreases()
	{
		int start = sensor3list.length;
		for(int i = 60; i < sensor3list.length - 1; i ++)
		{
			if(unixTimes[i] < 1514640000 || unixTimes[i] > 1514715000)
			{
				if(sensor3list[i + 1] - sensor3list[i] > threshold)
				{
					if(start == sensor3list.length)
					{
						start = i;
					}
				}
				else
				{
					if(start != sensor3list.length)
					{
						boolean add = true;
						//make sure actually heating and not just fluctations 
						if(i - start < minLength)
						{
							add = false;
						}
						for(int p = start - 60; p < i + 15 ; p ++)
						{
							if(unixTimes[p + 1] - unixTimes[p] > 300)
							{
								add = false;
							}
						}
						if(add)
						{
							Double allHeat = sensor3list[i] - sensor3list[start];
							Cooling h = new Cooling(unixTimes[start],unixTimes[i],start + startLead,i + endFollow ,allHeat);
							listOfCoolings.add(h);
						}
						start = sensor3list.length;

					}
				}
			}
		}
	}

	public void averageDifferences()
	{
		Double countpos = 0.0;
		Double countavg = 0.0;
		Double[] averageLosses = new Double[listOfCoolings.size()];
		int count = 0;
		for(Cooling h: listOfCoolings)
		{
			Double startingAverage = 0.0;
			/*
			for(int p = h.startIndex - 30; p < h.startIndex ; p ++)
			{
				startingAverage += sensor2list[p];
			}
			startingAverage = startingAverage / 30;
			 */
			startingAverage = sensor2list[h.startIndex];
			Double endAverage = 0.0;
			/*
			for(int p = h.startIndex; p < h.endIndex ; p ++)
			{
				endAverage += sensor2list[p];
			}
			Integer length = (h.endIndex  - h.startIndex);
			endAverage = endAverage / length;
*/
 

			Double averageLoss = endAverage - startingAverage;
			averageLosses[count] = averageLoss;
			if(averageLoss > 0)
			{

				countpos ++;
			}
			else
			{
				countavg += averageLoss;

			}

			count ++;

		}
		System.out.println(countpos/listOfCoolings.size());
		System.out.println(countavg/listOfCoolings.size());

		SimpleRegression simpleRegression = new SimpleRegression(true);
		double[][] regression = new double[listOfCoolings.size()][];
		for(int i = 0; i < listOfCoolings.size(); i ++)
		{
			regression[i] = new double[]{listOfCoolings.get(i).allHeat,averageLosses[i]};
		}
		simpleRegression.addData(regression);



		System.out.println("slope = " + simpleRegression.getSlope());
		System.out.println("intercept = " + simpleRegression.getIntercept());
		System.out.println(Math.sqrt(simpleRegression.getRegressionSumSquares())/40);


	}


}

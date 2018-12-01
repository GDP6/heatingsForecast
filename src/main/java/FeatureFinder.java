import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class FeatureFinder {



	Double threshold = 0.07;
	int minLength = 3;
	int startLead = 20;
	int endFollow = 20;



	Double[] sensor2list;
	Double[] sensor3list;
	Long[] unixTimes;
	ArrayList<Cooling> listOfHeatings = new ArrayList<Cooling>();
	public FeatureFinder(Double[] sensor2list, Double[] sensor3list, Long[] unixTimes) {
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
							Cooling h = new Cooling(unixTimes[start],unixTimes[i],start,i,allHeat);
							listOfHeatings.add(h);
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
		Double[] averageLosses = new Double[listOfHeatings.size()];
		int count = 0;
		for(Cooling h: listOfHeatings)
		{
			Double startingAverage = 0.0;
			for(int p = h.startIndex - 30; p < h.startIndex ; p ++)
			{
				startingAverage += sensor2list[p];
			}
			startingAverage = startingAverage / 30;

			Double endAverage = 0.0;
			for(int p = h.startIndex + startLead; p < h.endIndex + endFollow ; p ++)
			{
				endAverage += sensor2list[p];
			}
			Integer length = (h.endIndex  - h.startIndex + endFollow - startLead);
			endAverage = endAverage / length;


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
		System.out.println(countpos/listOfHeatings.size());
		System.out.println(countavg/listOfHeatings.size()/ 60);

		SimpleRegression simpleRegression = new SimpleRegression(true);
		double[][] regression = new double[listOfHeatings.size()][];
		for(int i = 0; i < listOfHeatings.size(); i ++)
		{
			regression[i] = new double[]{listOfHeatings.get(i).allHeat,averageLosses[i]};
		}
		simpleRegression.addData(regression);

		
		
		System.out.println("slope = " + simpleRegression.getSlope());
		System.out.println("intercept = " + simpleRegression.getIntercept());
		System.out.println(Math.sqrt(simpleRegression.getRegressionSumSquares())/40);


	}


}

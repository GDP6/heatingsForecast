import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Feature1Finder {


	Double[] sensor2list;
	Double[] sensor3list;
	Long[] unixTimes;
	public Feature1Finder(Double[] sensor2list, Double[] sensor3list, Long[] unixTimes) {
		super();
		this.sensor2list = sensor2list;
		this.sensor3list = sensor3list;
		this.unixTimes = unixTimes;

		/*
		for(int i = 0; i < sensor2list.length - 100; i ++)
		{

			if(unixTimes[i] > 1513750000 && unixTimes[i] < 1513763000)
			{
				Double difference = sensor2list[i + 1] - sensor2list[i];
				System.out.println(difference);

			}
		}
		 */

	}

	public void findFaucetSharpIncrease()
	{
		Double sharpIncreases = 0.0;
		for(int i = 0; i < sensor3list.length - 100; i ++)
		{

			Double difference = sensor3list[i + 4] - sensor3list[i];
			if(difference > 0.8)
			{
				sharpIncreases += 1;
				i = i + 4;
			}
			
		}
		System.out.println(sharpIncreases);
	}

	public void findSharpDecreses()
	{

		Double countDrop  =0.0;
		Double added = 0.0;
		for(int i = 0; i < sensor2list.length - 100; i ++)
		{

			Double difference = sensor2list[i + 1] - sensor2list[i];
			if(difference < -0.3)
			{
				int startingDropIndex = i;
				int endingDropIndex = i;
				for(int p = i + 1; p < i + 13; p += 1)
				{
					//System.out.println("p:" + p);
					Double differencep = sensor2list[p + 1] - sensor2list[p];
					if(differencep < -0.3)
					{
						endingDropIndex = p + 1;
						i = p;
					}
					else
					{
						break;
					}
				}
				if(startingDropIndex != endingDropIndex)
				{
					System.out.println(startingDropIndex + " to " + endingDropIndex);
					Double tempDropped = sensor2list[endingDropIndex] - sensor2list[startingDropIndex];
					System.out.println("");

					if(tempDropped> 1)
					{
						for(int k = startingDropIndex - 10; k <= endingDropIndex; k++)
						{
							Double differencek = sensor3list[k + 1] - sensor3list[k];
							System.out.println(differencek);
						}

					}	
					added += 1;

					System.out.println("");




				}
			}

		}
		System.out.println(added);

	}

}

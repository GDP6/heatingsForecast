import java.util.ArrayList;

public class Feature0Finder {

	Double[] sensor2list;
	Long[] unixTimes;


	ArrayList<Integer[]> smallHeats = new ArrayList<Integer[]>(); 
	ArrayList<Long> lengthBetween = new ArrayList<Long>();
	ArrayList<Long> heatingLength = new ArrayList<Long>();
	ArrayList<Double> tempGained = new ArrayList<Double>();
	ArrayList<Double> tempLoss = new ArrayList<Double>();


	public Feature0Finder(Double[] sensor2list, Long[] unixTimes) {
		this.sensor2list = sensor2list;
		this.unixTimes = unixTimes;
	}

	public void findIncreases()
	{
		for(int i = 0; i < sensor2list.length - 2; i ++)
		{
			Double difference = sensor2list[i + 1] - sensor2list[i];
			if(difference > 0.05)
			{

				int start = i;
				int end = i + 11;
				for(int p = i + 1; p < i + 10; p ++)
				{
					Double differencep = sensor2list[p + 1] - sensor2list[p];
					if(differencep > 0.05)
					{
						end = p + 1;
					}
					else
					{
						break;
					}

				}
				i = end;
				if(end - start < 10 && end - start > 3)
				{
					smallHeats.add(new Integer[] {start,end});
				}
			}
		}

		int lastEnd = 0;
		for(Integer[] i: smallHeats)
		{
			
			
			lengthBetween.add(unixTimes[i[0]] - unixTimes[lastEnd]);
			heatingLength.add(unixTimes[i[1]] - unixTimes[i[0]]);

			tempGained.add(sensor2list[i[1]] - sensor2list[i[0]]);
			tempLoss.add(sensor2list[i[0]] - sensor2list[lastEnd]);
			lastEnd = i[1];
		}
		
		Long lengthBetweenCounter = 0l;
		for(Long l : lengthBetween)
		{
			lengthBetweenCounter += l;
		}
		Long avgLengthBetween = lengthBetweenCounter / lengthBetween.size();
		System.out.println(avgLengthBetween);
		
		Long heatingCounter = 0l;
		for(Long l : heatingLength)
		{
			heatingCounter += l;
		}
		
		Long  avgHeatingLength = heatingCounter / heatingLength.size();
		System.out.println(avgHeatingLength);
		
		Double tempCounter = 0.0;
		for(Double d : tempGained)
		{
			tempCounter += d;
		}
		
		Double avgTempGain = tempCounter / tempGained.size();
		Double avgTempGainSecond = avgTempGain / avgHeatingLength.doubleValue();
		System.out.println(avgTempGain);
		System.out.println(avgTempGainSecond);
		
		Double tempLossCounter = 0.0;
		for(Double d : tempLoss)
		{
			tempLossCounter += d;
		}
		
		Double avgTempLoss = tempLossCounter / tempLoss.size();
		Double avgTempLossSecond = avgTempLoss / (avgLengthBetween.doubleValue() - avgHeatingLength.doubleValue());
		System.out.println(avgTempLoss);
		System.out.println(avgTempLossSecond);

	}


}

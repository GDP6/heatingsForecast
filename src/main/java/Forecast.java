import java.util.ArrayList;

public class Forecast {

	double currentTemp = 51.5;
	double highestTemp = 59;
	double lowestTemp = 35;
	double threshold = 0.07;

	ArrayList<Double> sensor3list = new ArrayList<Double>();
	ArrayList<Double> sensor2forecast = new ArrayList<Double>();
	ArrayList<Long> unixTimes = new ArrayList<Long>();

	Feature0 f0 = new Feature0(1513728000L,1513728000L);

	//0 is heating, 1 is water draining, 2 is quick fill
	int mode = 0;

	public void tick(Double sensor3, Long unixTime)
	{
		sensor3list.add(sensor3);
		unixTimes.add(unixTime);

		int currentIndex = sensor3list.size() - 1;
		if(unixTimes.size() > 1)
		{
			Long timePassed = unixTimes.get(currentIndex) - unixTimes.get(currentIndex - 1);
			if(timePassed > 300 || timePassed < 0)
			{
				currentTemp = highestTemp;
				mode = 0;
			}
			else
			{
				if(mode ==0)
				{
					currentTemp = f0.nextTemp(currentTemp, unixTimes.get(currentIndex));
				}
				else if(mode == 1)
				{

				}
				else if(mode == 2)
				{

				}
			}

			stayInBounds();

		}
		//if more than 5 mins has passed since the last reading, then reset
		sensor2forecast.add(currentTemp);

	}


	public void stayInBounds()
	{
		if(currentTemp > highestTemp)
		{
			currentTemp = highestTemp;
		}
		else if(currentTemp < lowestTemp)
		{
			currentTemp = lowestTemp;
		}
	}

}





import java.util.ArrayList;

public class Forecast {

	double currentTemp = 51;
	double ambientTemp = 56;
	double lowestTemp = 45;
	double reheatTemp = 0.0004;
	double threshold = 0.05;
	int minLength = 3;
	int startLead = 20;
	int endFollow = 20;

	int startedIncreasing = 0;
	boolean cooling = false;
	int afterCooling = 0;
	
	Double coolingIntensity = 0.0;
	ArrayList<Double> sensor3list = new ArrayList<Double>();
	ArrayList<Double> sensor2forecast = new ArrayList<Double>();
	ArrayList<Long> unixTimes = new ArrayList<Long>();


	public void tick(Double sensor3, Long unixTime)
	{
		sensor3list.add(sensor3);
		unixTimes.add(unixTime);
		if(sensor3list.size() > 60)
		{

			Long timePassed = unixTimes.get(unixTimes.size() - 1) - unixTimes.get(unixTimes.size() - 2); 
			checkForFaucetIncrease();

			if(cooling == true)
			{
				System.out.println(sensor3list.size() -1 - startedIncreasing);
				currentTemp -= 0.0025 * timePassed;
			}
			else if(cooling == false)
			{
				if(afterCooling != 0)
				{
					currentTemp -= 0.00005 * timePassed;
					afterCooling -= 1;
				}
				else
				{
					currentTemp += reheatTemp * timePassed;

				}
			}

			if(currentTemp > ambientTemp)
			{
				currentTemp = ambientTemp;
			}

			sensor2forecast.add(currentTemp);

		}
		else
		{
			sensor2forecast.add(currentTemp);
		}
	}
	public void checkForFaucetIncrease()
	{

		if(sensor3list.get(sensor3list.size() - 1) - sensor3list.get(sensor3list.size() - 2) > threshold)
		{
			if(!cooling)
			{
				startedIncreasing = sensor3list.size() - 1;
				cooling = true;
				coolingIntensity = sensor3list.get(sensor3list.size() - 1) - sensor3list.get(sensor3list.size() - 2);

			}
			else
			{
				coolingIntensity = sensor3list.get(sensor3list.size() - 1) - sensor3list.get(sensor3list.size() - 2);

			}

		}
		else
		{

			if(cooling)
			{

				boolean add = true;
				//make sure actually heating and not just fluctations 

				if(sensor3list.size() - 1 - startedIncreasing < minLength)
				{
					add = false;
				}
				for(int p = startedIncreasing - 60; p < sensor3list.size() - 2 ; p ++)
				{
					if(p < 0)
					{
						p = 0;
					}
					if(unixTimes.get(p + 1) - unixTimes.get(p) > 300)
					{
						add = false;
					}
				}

				if(add)
				{
					cooling = false;
					afterCooling = 20;
				}
				//shouldnt have been cooling 
				else
				{
					cooling = false;
					afterCooling = 0;
				}

			}
		}


	}
}





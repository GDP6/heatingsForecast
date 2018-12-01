import java.util.ArrayList;

public class Forecast {

	double currentTemp = 51;
	double ambientTemp = 56;
	double lowestTemp = 45;
	double reheatTemp = 0.0004;
	double threshold = 0.07;
	int minLength = 3;
	int startLead = 20;
	int endFollow = 20;

	boolean faucetIncreasing = false;
	int startedIncreasing = 0;

	ArrayList<Cooling> listOfCoolings = new ArrayList<Cooling>();

	ArrayList<Double> sensor3list = new ArrayList<Double>();
	ArrayList<Double> sensor2forecast = new ArrayList<Double>();
	ArrayList<Long> unixTimes = new ArrayList<Long>();


	public void tick(Double sensor3, Long unixTime)
	{
		sensor3list.add(sensor3);
		unixTimes.add(unixTime);
		if(sensor3list.size() > 1)
		{
			Long timePassed = unixTimes.get(unixTimes.size() - 1) - unixTimes.get(unixTimes.size() - 2); 
			
			if(faucetIncreasing)
			{
				checkItsStoppedIncreasing();
			}
			else
			{
				checkForFaucetIncrease();
			}

			boolean cooling = false;
			if(listOfCoolings.size() != 0)
			{
				for(int i = 0; i < listOfCoolings.size(); i ++)
				{
					if(listOfCoolings.get(i).startIndex + startLead > sensor3list.size())
					{
						System.out.println("hey");
						currentTemp -= 1.19;
						cooling = true;
					}
					if(listOfCoolings.get(i).endIndex + endFollow > sensor3list.size())
					{
						listOfCoolings.remove(i);
						i -= 1;
					}
				}
			}

			if(!cooling)
			{
				currentTemp += timePassed * reheatTemp;
				
			}



			if(currentTemp > ambientTemp)
			{
				currentTemp = ambientTemp;
			}

			if(currentTemp < lowestTemp)
			{
				currentTemp = lowestTemp;
			}
		}

		sensor2forecast.add(currentTemp);




	}

	public void checkForFaucetIncrease()
	{
		int i = sensor3list.size() - 2;
		if(sensor3list.get(i + 1) - sensor3list.get(i) > threshold)
		{
			faucetIncreasing = true;
			startedIncreasing = i;
		}


	}
	public void checkItsStoppedIncreasing()
	{
		boolean add = true;
		int i = sensor3list.size() - 2;

		//make sure actually heating and not just fluctations 
		if(sensor3list.get(i + 1) - sensor3list.get(i) < threshold)
		{

			if(i - startedIncreasing > minLength)
			{
				add = false;
			}
			for(int p = startedIncreasing - 60; p < i + 1 ; p ++)
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
				Double allHeat = sensor3list.get(i) - sensor3list.get(startedIncreasing);
				Cooling c = new Cooling(unixTimes.get(startedIncreasing),unixTimes.get(i),startedIncreasing,i,allHeat);
				System.out.println(c.start);
				listOfCoolings.add(c);
			}
		}	
		faucetIncreasing = false;

	}



}

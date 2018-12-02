import java.util.ArrayList;

public class Forecast {

	double currentTemp = 51;
	double highestTemp = 56;
	double lowestTemp = 45;
	double threshold = 0.07;
	
	ArrayList<Double> sensor3list = new ArrayList<Double>();
	ArrayList<Double> sensor2forecast = new ArrayList<Double>();
	ArrayList<Long> unixTimes = new ArrayList<Long>();


	public void tick(Double sensor3, Long unixTime)
	{
		sensor3list.add(sensor3);
		unixTimes.add(unixTime);
		
		
		
		
		if(currentTemp > highestTemp)
		{
			currentTemp = highestTemp;
		}
		else if(currentTemp < lowestTemp)
		{
			currentTemp = lowestTemp;
		}
		
		sensor2forecast.add(currentTemp);
	
	}
	
}





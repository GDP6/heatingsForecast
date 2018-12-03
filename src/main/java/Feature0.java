
public class Feature0 {
	
	Long heatingTime = 495L;
	Long coolingTime = 3826L;
	Double gain = 1.0;
	Double loss = 0.0001;
	Long lastTime;
	Long firstTime;



	
	public Feature0(Long lastTime, Long firstTime) {
		this.lastTime = lastTime;
		this.firstTime = firstTime;
	}




	public Double nextTemp(Double currentTemp, Long time)
	{
		Double heatingRatio = heatingRatio(time);
		Double newTemp = currentTemp + (gain*heatingRatio + loss*(1-heatingRatio) * (time-lastTime));
		lastTime = time;
		return newTemp;
	}
	
	
	public Double heatingRatio(Long time)
	{
		
		Long currentPhase = (time - firstTime) % (heatingTime + coolingTime);
		Long lastPhase = (lastTime - firstTime) % (heatingTime + coolingTime);
		Long timeHeating = 0L;


		if(currentPhase > heatingTime && lastPhase < heatingTime)
		{
			 timeHeating =  heatingTime - lastPhase;
		}
		else if(currentPhase > heatingTime && lastPhase > heatingTime)
		{
			timeHeating = 0L;
		}
		else if(currentPhase < heatingTime && lastPhase < heatingTime)
		{
			timeHeating = currentPhase - lastPhase;
		}
		else if(currentPhase < heatingTime && lastPhase > heatingTime)
		{
			timeHeating = currentPhase;
		}
		

	
		
		Double ratio = (double) (timeHeating.doubleValue()/(time.doubleValue()-lastTime.doubleValue()));


		return ratio;
	}
	
	
	

}

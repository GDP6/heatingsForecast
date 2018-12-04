
public class Feature0 {

	Double heatingTime;
	Double coldingTime;
	Double gain;
	Double loss;
	Long lastTime;
	Long firstTime;




	public Feature0(Long lastTime, Long firstTime) {
		this.lastTime = lastTime;
		this.firstTime = firstTime;
		setAllVariables(58.0);

	}



	public Double findHeatingTime(Double currentTemp)
	{
		return 773.8850914210703 - (currentTemp * 5.209158376824304);
	}

	public Double findColdingTime(Double currentTemp)
	{
		return 667.9497021485934 + (currentTemp * 52.93806785791462);
	}

	public Double findHeatingAmount(Double currentTemp)
	{
		return 11 - (currentTemp * 0.16);
	}

	public Double findColdingAmount(Double currentTemp)
	{
		return 4.61 - (currentTemp  * 0.117);
	}

	public void setAllVariables(Double currentTemp)
	{
		heatingTime = findHeatingTime(currentTemp);
		coldingTime = findColdingTime(currentTemp);

		gain = findHeatingAmount(currentTemp)/ heatingTime;
		loss = findColdingAmount(currentTemp) / coldingTime;
	}



	public Double nextTemp(Double currentTemp, Long time)
	{

		setAllVariables(currentTemp);

		Double heatingRatio = heatingRatio(time);	
		Double gainThisTick = gain*heatingRatio * (time-lastTime);
		Double lossThisTick = loss*(1-heatingRatio) * (time-lastTime);

		Double newTemp = currentTemp + gainThisTick + lossThisTick ;
		lastTime = time;
		return newTemp;
	}


	public boolean newCycle(Long time)
	{
		Double lastPhase = (lastTime - firstTime) % (heatingTime + coldingTime);
		Double currentPhase = (time.doubleValue() - firstTime) % (heatingTime + coldingTime);


		System.out.println(currentPhase);
		System.out.println(lastPhase);

		if(currentPhase < lastPhase)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Double heatingRatio(Long time)
	{

		Double currentPhase = (time.doubleValue() - firstTime) % (heatingTime + coldingTime);
		Double lastPhase = (lastTime - firstTime) % (heatingTime + coldingTime);
		Double timeHeating = 0.0;


		if(currentPhase > heatingTime && lastPhase < heatingTime)
		{
			timeHeating =  heatingTime - lastPhase;
		}
		else if(currentPhase > heatingTime && lastPhase > heatingTime)
		{
			timeHeating = 0.0;
		}
		else if(currentPhase < heatingTime && lastPhase < heatingTime)
		{
			timeHeating = currentPhase - lastPhase;
		}
		else if(currentPhase < heatingTime && lastPhase > heatingTime)
		{
			timeHeating = currentPhase;
		}

		Double ratio = (double) (timeHeating)/(time.doubleValue()-lastTime.doubleValue());

		return ratio;
	}




}

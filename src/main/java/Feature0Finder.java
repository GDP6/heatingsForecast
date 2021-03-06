import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;

public class Feature0Finder {

	Double[] sensor2list;
	Long[] unixTimes;

	ArrayList<Double> currentTemps = new ArrayList<Double>();
	ArrayList<Double> heatingLengths = new ArrayList<Double>();
	ArrayList<Double> coldingLengths = new ArrayList<Double>();
	ArrayList<Double> heatingGains = new ArrayList<Double>();
	ArrayList<Double> coldingLosses = new ArrayList<Double>();


	public Feature0Finder(Double[] sensor2list, Long[] unixTimes) {
		this.sensor2list = sensor2list;
		this.unixTimes = unixTimes;
	}
	public void findIncreases()
	{
		for(int i = 0; i < sensor2list.length - 100; i ++)
		{

			Double difference = sensor2list[i + 1] - sensor2list[i];
			if(difference > 0.05)
			{
				int startingHeatIndex = i;
				int endingHeatIndex = i;
				for(int p = i + 1; p < i + 10; p ++)
				{
					//System.out.println("p:" + p);
					Double differencep = sensor2list[p + 1] - sensor2list[p];
					if(differencep > 0.05)
					{
						endingHeatIndex = p + 1;
					}
					else
					{
						Long timeHeating = unixTimes[endingHeatIndex] - unixTimes[startingHeatIndex];
						if(timeHeating >= 300 && timeHeating < 600)
						{
							int startingColdIndex = p;
							int endingColdIndex = p;
							for(int c = p + 1; c < p + 80; c += 6)
							{
								//System.out.println("c:" + c);
								Double differencec = sensor2list[c + 6] - sensor2list[c];
								if(differencec < 0)
								{
									endingColdIndex = c + 6;
								}
								else
								{
									Long timeColding = unixTimes[endingColdIndex] - unixTimes[startingColdIndex];
									double coldingLoss = sensor2list[endingColdIndex] - sensor2list[startingColdIndex];
									double heatGain = sensor2list[endingHeatIndex] - sensor2list[startingHeatIndex];


									if(timeColding >= 2500 && timeColding < 4200 && coldingLoss > -3.5 && heatGain < 4.0)
									{
										currentTemps.add(sensor2list[startingHeatIndex]);
										coldingLengths.add(timeColding.doubleValue());
										coldingLosses.add(coldingLoss);
										heatingLengths.add(timeHeating.doubleValue());
										heatingGains.add(heatGain);

									}

								}
							}

							i = p;
							break;

						}


					}
				}
			}
		}

	}



	public void findParamaters()
	{
		System.out.println(currentTemps.size());
		System.out.println(heatingLengths.size());

		SimpleRegression simpleRegression = new SimpleRegression(true);
		double[][] regression = new double[currentTemps.size() ][];
		for(int i = 0; i < currentTemps.size(); i ++)
		{
			regression[i] = new double[]{currentTemps.get(i),heatingLengths.get(i)};
		}
		simpleRegression.addData(regression);


		System.out.println("");
		System.out.println("Heating Lengths");
		System.out.println("slope = " + simpleRegression.getSlope());
		System.out.println("intercept = " + simpleRegression.getIntercept());

		simpleRegression = new SimpleRegression(true);
		regression = new double[currentTemps.size()][];
		for(int i = 0; i < currentTemps.size(); i ++)
		{
			regression[i] = new double[]{currentTemps.get(i),coldingLengths.get(i)};
		}
		simpleRegression.addData(regression);


		System.out.println("");
		System.out.println("Colding Lengths");
		System.out.println("slope = " + simpleRegression.getSlope());
		System.out.println("intercept = " + simpleRegression.getIntercept());

		simpleRegression = new SimpleRegression(true);
		regression = new double[currentTemps.size()][];
		for(int i = 0; i < currentTemps.size(); i ++)
		{
			regression[i] = new double[]{currentTemps.get(i),coldingLosses.get(i)};
		}
		simpleRegression.addData(regression);


		System.out.println("");
		System.out.println("Colding Losses");
		System.out.println("slope = " + simpleRegression.getSlope());
		System.out.println("intercept = " + simpleRegression.getIntercept());

		simpleRegression = new SimpleRegression(true);
		regression = new double[currentTemps.size()][];
		for(int i = 0; i < currentTemps.size(); i ++)
		{
			regression[i] = new double[]{currentTemps.get(i),heatingGains.get(i)};
		}
		simpleRegression.addData(regression);
		


		System.out.println("");
		System.out.println("Heating Gains");
		System.out.println("slope = " + simpleRegression.getSlope());
		System.out.println("intercept = " + simpleRegression.getIntercept());

	
	}

	public void createGraph()
	{
		CategoryTableXYDataset dataset = new CategoryTableXYDataset();		
		for(int i = 1; i < currentTemps.size(); i++){
			dataset.add(currentTemps.get(i),heatingGains.get(i), "Heating Gains");
		} 
		JFreeChart lineChartObject = ChartFactory.createScatterPlot("Heating Gains vs Starting Temp", "Starting Temp", "Heat Gained", dataset);

		int width = dataset.getItemCount() + 200;
		int height = 500;
		File lineChart = new File( "HeatingVsStatingTemp.jpeg" ); 
		try {
			ChartUtils.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataset = new CategoryTableXYDataset();		
		for(int i = 1; i < currentTemps.size() - 1; i++){
			dataset.add(currentTemps.get(i),coldingLengths.get(i), "Colding Length");
		} 
		lineChartObject = ChartFactory.createScatterPlot("Colding Length vs Starting Temp", "Starting Temp", "Colding Period", dataset);

		width = dataset.getItemCount() + 200;
		height = 500;
		lineChart = new File( "ColdingLengthVsStatingTemp.jpeg" ); 
		try {
			ChartUtils.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataset = new CategoryTableXYDataset();		
		for(int i = 1; i < currentTemps.size(); i++){
			dataset.add(currentTemps.get(i),coldingLosses.get(i), "Colding Losses");
		} 
		lineChartObject = ChartFactory.createScatterPlot("Colding Losses vs Starting Temp", "Starting Temp", "Colding", dataset);

		width = dataset.getItemCount() + 200;
		height = 500;
		lineChart = new File( "ColdingLossesVsStatingTemp.jpeg" ); 
		try {
			ChartUtils.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataset = new CategoryTableXYDataset();		
		for(int i = 1; i < currentTemps.size(); i++){
			dataset.add(currentTemps.get(i),heatingLengths.get(i), "Heating Length");
		} 
		lineChartObject = ChartFactory.createScatterPlot("Heatings Length vs Starting Temp", "Starting Temp", "Heating Period", dataset);

		width = dataset.getItemCount() + 200;
		height = 500;
		lineChart = new File( "HeatingLengthVsStatingTemp.jpeg" ); 
		try {
			ChartUtils.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

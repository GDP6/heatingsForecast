import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;

public class TempGraph {

	Double[] sensor2list;
	Double[] sensor3list;
	Double[] sensor2forecast;
	Long[] unixTimes;
	String filename;

	
	
	public TempGraph(Double[] sensor2list, Double[] sensor3list,Double[] sensor2forecast, Long[] unixTimes, String filename) {
		super();
		this.sensor2forecast = sensor2forecast;
		this.sensor2list = sensor2list;
		this.sensor3list = sensor3list;
		this.unixTimes = unixTimes;
		this.filename = filename;
	}



	public TempGraph(Double[] sensor2list, Double[] sensor3list, Long[] unixTimes, String filename) {
		super();
		this.sensor2list = sensor2list;
		this.sensor3list = sensor3list;
		this.unixTimes = unixTimes;
		this.filename = filename;
	}



	public void createGraph()
	{
		CategoryTableXYDataset dataset = new CategoryTableXYDataset();		
		for(int i = 0; i < sensor2list.length; i++){
			dataset.add(unixTimes[i],sensor2list[i], "True Data (sensor 2)");
		} 
		
		for(int i = 0; i < sensor3list.length; i++){
			dataset.add(unixTimes[i],sensor3list[i], "Faucet Data (sensor 3)");
		} 
		
		
		try {
			for(int i = 0; i < sensor2forecast.length; i++){
				dataset.add(unixTimes[i],sensor2forecast[i], "Forecasted Data");
			}
		} catch (Exception e1) {
		
		} 
		
		
	
	

		JFreeChart lineChartObject = ChartFactory.createXYLineChart(
				"Temp Readings","Time",
				"Temp",
				dataset,PlotOrientation.VERTICAL,
				true,true,false);

		
		int width = dataset.getItemCount() + 200;
		if(width < 700)
		{
			width = 700;
		}
		int height = 500;
		File lineChart = new File( filename ); 
		try {
			ChartUtils.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

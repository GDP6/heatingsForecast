import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;

public class TempGraph {

	double[] sensor2list;
	double[] sensor3list;
	Long[] unixTimes;
	String filename;

	
	
	public TempGraph(double[] sensor2list, double[] sensor3list, Long[] unixTimes, String filename) {
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
			dataset.add(unixTimes[i],sensor2list[i], "sensor2");
		} 
		
		for(int i = 0; i < sensor3list.length; i++){
			dataset.add(unixTimes[i],sensor3list[i], "sensor3");
		} 
		
		
	
	

		JFreeChart lineChartObject = ChartFactory.createXYLineChart(
				"Temp Readings","Time",
				"Temp",
				dataset,PlotOrientation.VERTICAL,
				true,true,false);

		int width = dataset.getItemCount() + 200;
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

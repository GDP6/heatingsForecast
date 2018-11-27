import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;

public class GradientGraph {

	double[] sensor3list;
	Long[] unixTime;

	



	public GradientGraph(double[] sensor3list, Long[] unixTime) {
		super();
		this.sensor3list = sensor3list;
		this.unixTime = unixTime;
	}

	public ArrayList<Integer> gradientAboveThreshold(double threshhold)
	{
		ArrayList<Integer> timesAbove = new ArrayList<Integer>();

		int count = 0;
		for(Double d: calculateGradients())
		{
			if(d> threshhold)
			{
				timesAbove.add(count);

			}
			count += 1;
		}
		return timesAbove;

	}

	public ArrayList<Heating> startEndHotWaterOn()
	{
		double[] gradients = calculateGradients();
		ArrayList<Heating> waterOn = new ArrayList<Heating>();
		int start = gradients.length;
		int end = 0;

		for(int i: gradientAboveThreshold(0.3))
		{
			if(i > end)
			{
				start = i;
				end = i;
				while(gradients[start] > 0 || start == 1)
				{
					start -= 1;
				}
				while(gradients[end] > 0 || end == gradients.length - 2)
				{
					end += 1;
				}

				boolean dropped = false;
				for(int p = (start - 2); p < start; p ++ )
				{
					if(p < 0)
					{
						p = 0;
					}
					if(gradients[p] < -0.3)
					{
						dropped = true;
					}
				}
				boolean restarted = false;
				for(int p = (start - 5); p <= end; p ++ )
				{
					if(p < 0)
					{
						p = 0;
					}
					Long current = unixTime[p];
					Long next = unixTime[p +1];
					if(current + 300 < next)
					{
						restarted = true;
					}
				}
				if(!dropped && !restarted)
				{
					if(unixTime[start] > unixTime[end])
					{
						System.out.println(unixTime[start]);
						System.out.println(unixTime[end]);

					}
					waterOn.add(new Heating(unixTime[start],unixTime[end],Arrays.copyOfRange(gradients, start, end)));
				}
			}
		}
		
		return waterOn;
	}

	public double[] calculateGradients()
	{
		double[] sensor3gradientsSize = new double[(sensor3list.length - 1)];
		for(int i = 0; i < sensor3gradientsSize.length; i ++)
		{
			sensor3gradientsSize[i] = (sensor3list[i + 1] - sensor3list[i]) / ((unixTime[i+1] - unixTime[i])/60);
		}

		return sensor3gradientsSize;
	}

	public void createGraph()
	{

		double[] sensor3gradientsSize = calculateGradients();
		
	


		CategoryTableXYDataset dataset = new CategoryTableXYDataset();		
		for(int i = 0; i < sensor3gradientsSize.length; i++){
			dataset.add(unixTime[i] / 10, sensor3gradientsSize[i], "sensor3");
		} 


		JFreeChart lineChartObject = ChartFactory.createXYLineChart(
				"Temp Readings","Time",
				"gradients",
				dataset,PlotOrientation.VERTICAL,
				true,true,false);

		int width = dataset.getItemCount() + 200;
		int height = 1000;
		File lineChart = new File( "gradientChart.jpeg" ); 
		try {
			ChartUtils.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}


}

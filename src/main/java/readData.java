

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataEncoding;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;



public class readData {

	public static void main(String[] args) throws IOException {

		ArrayList<BulkTempReadings> listOfReadings = new ArrayList<BulkTempReadings>();
		String csvFile = "trevor_data.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		double sensor1 =1;
		double sensor2 =1;
		double sensor3 =1;
		Long unixTime = 1L;
		try {

			br = new BufferedReader(new FileReader(csvFile));
			br.readLine();
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] readings = line.split(cvsSplitBy);
				if(readings[2].equals("T1 Temperature"))
				{
					try {
						Date d = sdf.parse(readings[0]);

						unixTime = d.getTime() / 1000L;
					} catch (ParseException e) {
						e.printStackTrace();
					}
					sensor1 = Double.parseDouble(readings[3]);
				}
				else if(readings[2].equals("T5 Temperature"))
				{
					sensor2 = Double.parseDouble(readings[3]);

				}
				else if(readings[2].equals("T2 Temperature"))
				{
					sensor3 = Double.parseDouble(readings[3]);

					if(sensor3 > 40)
					{
					listOfReadings.add(new BulkTempReadings(sensor1,sensor2,sensor3,unixTime));
					}

				}




			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(listOfReadings.size());

		double[] sensor1list = new double[listOfReadings.size()];
		double[] sensor2list = new double[listOfReadings.size()];
		double[] sensor3list = new double[listOfReadings.size()];
		Long[] unixTimes = new Long[listOfReadings.size()];

		for(int i = 0; i < listOfReadings.size(); i ++)
		{
			sensor1list[i] = listOfReadings.get(i).sensor1;
			sensor2list[i] = listOfReadings.get(i).sensor2;
			sensor3list[i] = listOfReadings.get(i).sensor3;
			unixTimes[i] = listOfReadings.get(i).timeTaken;

		}


		int startCopy = 0;
		int endCopy = 10000;

		TempGraph tg = new TempGraph(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(sensor3list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy),"tempAdj.jpeg");
		tg.createGraph();

		GradientGraph gg = new GradientGraph(Arrays.copyOfRange(sensor3list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy));
		gg.createGraph();
		ArrayList<Heating> listOfHeatings = gg.startEndHotWaterOn();

		int countpos = 0;
		int countneg = 0;
		int countdelta =0;
		int countNegDelta = 0;
		Long countHeatings = 0L;
		for(Heating h: listOfHeatings)
		{
			
			
			Long length = (h.end  - h.start) / 60 ;
			countHeatings += length;
			//System.out.println(h.start + " to " + h.end);
			int startIndex =0;
			int endIndex =0;

			for(int i = 0; i < unixTimes.length; i ++)
			{
				if(h.start.equals(unixTimes[i]))
				{
					startIndex = i;
				}
				if(h.end.equals(unixTimes[i]))
				{
					endIndex = i;
				}
			}
			double gradient = sensor2list[endIndex + 10] - sensor2list[startIndex - 10];
			//System.out.println(gradient);
			double delta =(gradient * length);
			
			if(delta < 0)
			{
				countNegDelta += delta;
			}
			//System.out.println(delta);

			/*
			if(delta > 10)
			{

				System.out.println("");

				System.out.println(h.start + " to " + h.end);
				System.out.println(startIndex + " to " + endIndex);

				System.out.println(length);

				System.out.println(gradient);

				System.out.println(delta);

			}
			*/
			if(gradient < 0)
			{
				countneg += 1;
			}
			else
			{
				countpos += 1;
			}
			countdelta += delta;


		}
		//System.out.println("");
		//System.out.println(countneg);
		//System.out.println(countpos);
		System.out.println(countNegDelta);
		System.out.println(countHeatings);

		double countAllNegDelta = 0;
		for(int i = startCopy;i< endCopy;i ++)
		{
			if(sensor2list[i] - sensor2list[i + 1] < 0)
			{
				countAllNegDelta += sensor2list[i] - sensor2list[i + 1];
			}
		}

		System.out.println(countAllNegDelta);
		System.out.println((unixTimes[endCopy] - unixTimes[startCopy])/60);

	}



}

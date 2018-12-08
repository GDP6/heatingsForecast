

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
import java.util.Scanner;

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		double sensor1 = 55.8;
		double sensor2 = 51.5;
		double sensor3 = 31.375;
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
				else if(readings[2].equals("T2 Temperature"))
				{
					sensor2 = Double.parseDouble(readings[3]);

				}
				else if(readings[2].equals("T5 Temperature"))
				{
					sensor3 = Double.parseDouble(readings[3]);

					listOfReadings.add(new BulkTempReadings(sensor1,sensor2,sensor3,unixTime));


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



		Double[] sensor1list = new Double[listOfReadings.size()];

		Double[] sensor2list = new Double[listOfReadings.size()];
		Double[] sensor3list = new Double[listOfReadings.size()];
		Long[] unixTimes = new Long[listOfReadings.size()];

		for(int i = 0; i < listOfReadings.size(); i ++)
		{
			sensor1list[i] = listOfReadings.get(i).sensor1;

			sensor2list[i] = listOfReadings.get(i).sensor2;



			sensor3list[i] = listOfReadings.get(i).sensor3;

			unixTimes[i] = listOfReadings.get(i).timeTaken;

		}




		int startCopy = 0;
		int endCopy = 20000;
		/*

		Forecast f = new Forecast();


		Scanner userInput = new Scanner(System.in);
		for(int i = startCopy; i < endCopy; i ++)
		{
			f.tick(sensor3list[i], unixTimes[i]);
			System.out.println("forecast current temp:" + f.currentTemp);
			System.out.println("real current temp:" + sensor2list[i]);
			System.out.println("diffrence temp:" + (f.currentTemp - sensor2list[i]));
			//userInput.nextLine();

		}

		Double[] forecast = f.sensor2forecast.toArray(new Double[f.sensor2forecast.size()]);




		 */


		/*
		Feature0Finder ff0 = new Feature0Finder(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy));
		ff0.findIncreases();
		ff0.findParamaters();
		ff0.createGraph();
		 */

		TempGraph tg = new TempGraph(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(sensor1list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy),"tempAdj.jpeg");
		tg.createGraph();

		Feature1Finder ff1 = new Feature1Finder(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(sensor3list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy));
		ff1.findSharpDecreses();




	}



}

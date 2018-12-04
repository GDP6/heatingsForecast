

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




		Double[] sensor2list = new Double[listOfReadings.size()];
		Double[] sensor3list = new Double[listOfReadings.size()];
		Long[] unixTimes = new Long[listOfReadings.size()];

		for(int i = 0; i < listOfReadings.size(); i ++)
		{

			sensor2list[i] = listOfReadings.get(i).sensor2;


			if(i < 20000)
			{
				sensor3list[i] = listOfReadings.get(i).sensor3;
			}
			else
			{
				Double countAverage = 0.0;
				for(int p = i - 60; p <= i; p ++)
				{
					countAverage += listOfReadings.get(p).sensor3;
				}
				sensor3list[i] = countAverage / 60;
			}
			unixTimes[i] = listOfReadings.get(i).timeTaken;

		}


		Forecast f = new Forecast();


		Scanner userInput = new Scanner(System.in);
		for(int i = 0; i < 10000; i ++)
		{
			f.tick(sensor3list[i], unixTimes[i]);
			System.out.println("forecast current temp:" + f.currentTemp);
			System.out.println("real current temp:" + sensor2list[i]);
			System.out.println("diffrence temp:" + (f.currentTemp - sensor2list[i]));
			//userInput.nextLine();

		}


		int startCopy = 0;
		int endCopy = 10000;


		Feature0Finder ff = new Feature0Finder(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy));
		ff.findIncreases();
		ff.findParamaters();
		//ff.createGraph();
		Double[] forecast = f.sensor2forecast.toArray(new Double[f.sensor2forecast.size()]);



		TempGraph tg = new TempGraph(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(sensor3list, startCopy, endCopy),forecast,Arrays.copyOfRange(unixTimes, startCopy, endCopy),"tempAdj.jpeg");
		tg.createGraph();


		//FeatureFinder ff = new FeatureFinder(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(sensor3list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy));

		//ff.faucetIncreases();
		//ff.averageDifferences();
		/*
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


		int startCopy = 2000;
		int endCopy = 5000;

		TempGraph tg = new TempGraph(Arrays.copyOfRange(sensor2list, startCopy, endCopy),Arrays.copyOfRange(sensor3list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy),"tempAdj.jpeg");
		tg.createGraph();

		GradientGraph gg = new GradientGraph(Arrays.copyOfRange(sensor3list, startCopy, endCopy),Arrays.copyOfRange(unixTimes, startCopy, endCopy));
		//gg.createGraph();
		ArrayList<Heating> listOfHeatings = gg.startEndHotWaterOn(0.5,1000l,1000l);

		int countpos = 0;
		int countneg = 0;
		double countdelta =0;
		double countNegDelta = 0;
		double countPosDelta = 0;
		Long countHeatings = 0L;
		for(Heating h: listOfHeatings)
		{


			Long length = (h.end  - h.start) / 60 ;
			countHeatings += length;
			//System.out.println(h.start + " to " + h.end);

			double delta = sensor2list[h.endIndex] - sensor2list[h.startIndex];
			System.out.println(delta);
			if(delta <= 0)
			{
				countneg += 1;
				countNegDelta += delta;
			}
			else
			{
				countpos += 1;
				countPosDelta += delta;

			}

			System.out.println(countdelta);
			countdelta += delta;


		}
		System.out.println("");
		System.out.println(countneg);
		System.out.println(countpos);
		System.out.println("");
		System.out.println(countdelta);
		System.out.println("");
		System.out.println(countNegDelta);
		System.out.println(countPosDelta);
		System.out.println("");

		System.out.println(countHeatings);
		int averageHeatingLen = (int) (countHeatings/listOfHeatings.size());

		double countAllNegDelta = 0;
		double countAllPosDelta = 0;

		for(int i = startCopy;i< endCopy;i += averageHeatingLen)
		{
			if(sensor2list[i] - sensor2list[i + averageHeatingLen] < 0)
			{
				countAllNegDelta += sensor2list[i] - sensor2list[i + averageHeatingLen];
			}
			else
			{
				countAllPosDelta += sensor2list[i] - sensor2list[i + averageHeatingLen];

			}
		}

		Long allTimes= (unixTimes[endCopy] - unixTimes[startCopy]) /60L;
		System.out.println("neg delta ratio " + countNegDelta/countAllNegDelta);
		System.out.println("pos delta ratio " + countPosDelta/countAllPosDelta);

		Long timeratio = Long.divideUnsigned(allTimes, countHeatings);
		System.out.println("time ratio 1/" + timeratio);

		 */
	}



}

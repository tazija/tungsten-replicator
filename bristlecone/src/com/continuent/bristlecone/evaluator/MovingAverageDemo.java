/**
 * Bristlecone Test Tools for Databases
 * Copyright (C) 2006-2007 Continuent Inc.
 * Contact: bristlecone@lists.forge.continuent.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 * Initial developer(s): Robert Hodges and Ralph Hannus.
 * Contributor(s):
 */

package com.continuent.bristlecone.evaluator;

/* -----------------------
 * MovingAverageDemo.java
 * -----------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 */

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example showing the calculation of a moving average for a time series.
 */
public class MovingAverageDemo extends ApplicationFrame {

    /**
     * A moving average demo.
     *
     * @param title  the frame title.
     */
    public MovingAverageDemo(String title) {

        super(title);

        // create a title...
        String chartTitle = "Legal & General Unit Trust Prices";
        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            chartTitle,
            "Date",
            "Price Per Unit",
            dataset,
            true,
            true,
            false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        if (renderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) renderer;
            rr.setBaseShapesVisible(true);
            rr.setBaseShapesFilled(true);
        }
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        //axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a dataset, one series containing unit trust prices, the other a
     * moving average.
     *
     * @return The dataset.
     */
    public XYDataset createDataset() {

        ArrayList<Double> s1 = new ArrayList<Double>();
        
       

        s1.add(181.8);
        s1.add(167.3);
        s1.add( 153.8);
        s1.add( 167.6);
        s1.add( 158.8);
        s1.add( 148.3);
        s1.add( 153.9);
        s1.add( 142.7);
        s1.add( 123.2);
        s1.add( 131.8);
        s1.add( 139.6);
        s1.add( 142.9);
        s1.add( 138.7);
        s1.add( 137.3);
        s1.add( 143.9);
        s1.add( 139.8);
        s1.add( 137.0);
        s1.add( 132.8);
        
        TimeSeries series1 = new TimeSeries("L&G European Index Trust", Millisecond.class);
        try
        {
            for(Double val : s1)
            {
                Thread.sleep(10); 
                series1.add(new Millisecond(), val);
            }
        }
        catch(InterruptedException i)
        {
            //
        }
        

        TimeSeries s2 = MovingAverage.createMovingAverage(
            series1, "Six Month Moving Average", 100, 0
        );

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(s2);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        MovingAverageDemo demo = new MovingAverageDemo(
                "JFreeChart: MovingAverageDemo.java");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}

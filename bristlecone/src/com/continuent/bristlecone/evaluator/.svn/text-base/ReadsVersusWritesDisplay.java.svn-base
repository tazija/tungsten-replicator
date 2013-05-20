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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;

import sun.awt.VerticalBagLayout;

/**
 * A demonstration application showing a time series chart where you can
 * dynamically add (random) data by clicking on a button.
 */
public class ReadsVersusWritesDisplay extends ApplicationFrame
        implements
            StatisticsListener
{
    private static final Logger logger           = Logger
                                                         .getLogger(ReadsVersusWritesDisplay.class);
    private static final long   serialVersionUID = 1L;

    static class DemoPanel extends JPanel implements ActionListener
    {
        private static final long      serialVersionUID = 1L;

        /** The number of subplots. */
        public static final int        SUBPLOT_COUNT    = 2;

        public static final int        READ_ACTIVITY    = 0;

        public static final int        WRITE_ACTIVITY   = 1;
        
        public static final int MOVING_AVG_PERIODS = 20000;
        public static final int MOVING_AVG_SKIP = 0;
        

        /** The datasets. */
        private TimeSeriesCollection[] datasets;

        /** The most recent value added to series 1. */
        private double[]               lastValue        = new double[2];

        public class GetEvaluatorData implements Runnable
        {
            public DemoPanel                  demo;

            private BlockingQueue<Statistics> statisticsQueue = new LinkedBlockingQueue<Statistics>();

            public GetEvaluatorData(BlockingQueue<Statistics> statisticsQueue)
            {
                this.statisticsQueue = statisticsQueue;
            }

            public void run()
            {
                do
                {

                    try
                    {
                        Statistics stats = statisticsQueue.take();

                        float interval = stats.getInterval();
                        if (interval > 0)
                        {
                            try
                            {
                                demo.addReadActivityRate(stats.getQueries()
                                        / stats.getInterval());

                                demo.addWriteActivityRate((double) (stats
                                        .getInserts()
                                        + stats.getDeletes() + stats
                                        .getUpdates())
                                        / stats.getInterval());
                            }
                            catch (Exception e)
                            {
                                logger.warn("Error while adding data to graph",
                                        e);
                            }
                        }
                        // }

                        // statisticsList.wait();
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }

                }
                while (true);
            }
        }

        /**
         * Creates a new self-contained demo panel.
         */
        public DemoPanel(BlockingQueue<Statistics> statisticsQueue)
        {
            super(new VerticalBagLayout());

            CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(
                    new DateAxis("Time"));

            combinedPlot.setGap(18);

            // We have 4 datasets - SUBPLOT_COUNT
            this.datasets = new TimeSeriesCollection[SUBPLOT_COUNT];

            /**
             * READ ACTIVITY PLOTS
             */
            // Set up the dataset series for the read activity
            TimeSeries series1 = new TimeSeries("Reads/sec", Millisecond.class);
            this.lastValue[READ_ACTIVITY] = 0.0;
            this.datasets[READ_ACTIVITY] = new TimeSeriesCollection(series1);
            TimeSeries readMovingAvg = MovingAverage.createMovingAverage(
                    series1, "Avg. Reads", MOVING_AVG_PERIODS, MOVING_AVG_SKIP);
            this.datasets[READ_ACTIVITY].addSeries(readMovingAvg);
            NumberAxis rangeAxis1 = new NumberAxis("Read Activity");
            rangeAxis1.setLabelFont(new Font("Dialog", Font.PLAIN, 14));
            rangeAxis1.setAutoRangeIncludesZero(true);
            rangeAxis1.setRange(0, 5000);
            rangeAxis1.setAutoRange(true);

            XYPlot readActivityPlot = new XYPlot(this.datasets[READ_ACTIVITY],
                    null, rangeAxis1, new XYLineAndShapeRenderer());

            XYItemRenderer renderer = readActivityPlot.getRenderer();
            renderer.setSeriesPaint(0, Color.cyan);
            renderer.setSeriesPaint(1, Color.blue);
            if (renderer instanceof XYLineAndShapeRenderer)
            {
                XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) renderer;
                rr.setBaseShapesVisible(false);
                rr.setBaseShapesFilled(false);
            }

            readActivityPlot.setBackgroundPaint(Color.lightGray);
            readActivityPlot.setDomainGridlinePaint(Color.white);
            readActivityPlot.setRangeGridlinePaint(Color.white);
            combinedPlot.add(readActivityPlot);

            combinedPlot.setBackgroundPaint(Color.lightGray);
            combinedPlot.setDomainGridlinePaint(Color.white);
            combinedPlot.setRangeGridlinePaint(Color.white);
            combinedPlot.setAxisOffset(new RectangleInsets(4, 4, 4, 4));

            this.lastValue[WRITE_ACTIVITY] = 0.0;
            TimeSeries series2 = new TimeSeries("Writes/sec", Millisecond.class);
            this.datasets[WRITE_ACTIVITY] = new TimeSeriesCollection(series2);
            TimeSeries writeMovingAvg = MovingAverage.createMovingAverage(
                    series2, "Avg. Writes", MOVING_AVG_PERIODS, MOVING_AVG_SKIP);
            this.datasets[WRITE_ACTIVITY].addSeries(writeMovingAvg);

            NumberAxis rangeAxis2 = new NumberAxis("Write Activity");
            rangeAxis2.setAutoRangeIncludesZero(true);
            rangeAxis2.setLabelFont(new Font("Dialog", Font.PLAIN, 14));
            rangeAxis2.setRange(0, 300);
            rangeAxis2.setAutoRange(true);

            XYPlot writeActivityPlot = new XYPlot(
                    this.datasets[WRITE_ACTIVITY], null, rangeAxis2,
                    new XYLineAndShapeRenderer());

            renderer = writeActivityPlot.getRenderer();
            renderer.setSeriesPaint(0, Color.darkGray);
            renderer.setSeriesPaint(1, Color.magenta);
            if (renderer instanceof XYLineAndShapeRenderer)
            {
                XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) renderer;
                rr.setBaseShapesVisible(false);
                rr.setBaseShapesFilled(false);
            }

            writeActivityPlot.setBackgroundPaint(Color.lightGray);
            writeActivityPlot.setDomainGridlinePaint(Color.white);
            writeActivityPlot.setRangeGridlinePaint(Color.white);

            combinedPlot.add(writeActivityPlot);

            JFreeChart combinedChart = new JFreeChart(
                    "Read/Write Performance Statistics", combinedPlot);
            LegendTitle legend = (LegendTitle) combinedChart.getSubtitle(0);
            legend.setPosition(RectangleEdge.RIGHT);
            legend
                    .setMargin(new RectangleInsets(UnitType.ABSOLUTE, 0, 4, 0,
                            4));
            legend.setItemFont(new Font("Dialog", Font.PLAIN, 14));
            combinedChart.setBorderPaint(Color.black);
            combinedChart.setBorderVisible(true);
            combinedChart.setBackgroundPaint(Color.white);

            ValueAxis axis = writeActivityPlot.getDomainAxis();
            axis.setFixedAutoRange(1000.0 * 60 * 5); // 10 minutes

            axis = readActivityPlot.getDomainAxis();
            axis.setFixedAutoRange(1000.0 * 60 * 5); // 10 minutes

            ChartPanel combinedChartPanel = new ChartPanel(combinedChart);
            combinedChartPanel
                    .setPreferredSize(new java.awt.Dimension(800, 640));
            combinedChartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,
                    5, 5));

            add(combinedChartPanel);

            System.out.println("starting data acquisition");

            GetEvaluatorData dataSource = new GetEvaluatorData(statisticsQueue);
            dataSource.demo = this;

            Thread dataThread = new Thread(dataSource, "Data acquisition");
            dataThread.start();

            System.out.println("after starting data acquisition");
        }

        /**
         * Handles a click on the button by adding new (random) data.
         * 
         * @param value the data to add
         */
        public void addReadActivityRate(double value)
        {

            this.lastValue[READ_ACTIVITY] = value;
            TimeSeries readSeries = this.datasets[READ_ACTIVITY].getSeries(0);
            TimeSeries readMovingAvg = MovingAverage.createMovingAverage(
                    readSeries, "Avg. Reads", MOVING_AVG_PERIODS, MOVING_AVG_SKIP);
            this.datasets[READ_ACTIVITY].getSeries(0).add(new Millisecond(),
                    this.lastValue[READ_ACTIVITY]);
            if (this.datasets[READ_ACTIVITY].getSeriesCount() > 1)
            {
                this.datasets[READ_ACTIVITY].removeSeries(1);
            }
            this.datasets[READ_ACTIVITY].addSeries(readMovingAvg);

        }

        public void addWriteActivityRate(double value)
        {
            this.lastValue[WRITE_ACTIVITY] = value;
            TimeSeries writeSeries = this.datasets[WRITE_ACTIVITY].getSeries(0);
            TimeSeries writeMovingAvg = MovingAverage.createMovingAverage(
                    writeSeries, "Avg. Writes", MOVING_AVG_PERIODS, MOVING_AVG_SKIP);
            this.datasets[WRITE_ACTIVITY].getSeries(0).add(new Millisecond(),
                    this.lastValue[WRITE_ACTIVITY]);
            
            if (this.datasets[WRITE_ACTIVITY].getSeriesCount() > 1)
            {
                this.datasets[WRITE_ACTIVITY].removeSeries(1);
            }
            this.datasets[WRITE_ACTIVITY].addSeries(writeMovingAvg);

        }

        public void actionPerformed(ActionEvent e)
        {
            logger.info("############################# PANIC: SHOULD NOT GET HERE. EXITING ####################");
            logger.info(Thread.currentThread().getStackTrace());
            System.exit(1);
            return;

//            if (e.getActionCommand().endsWith(String.valueOf(0)))
//            {
//                Millisecond item = new Millisecond();
//                System.out.println("Item = " + item.toString());
//                this.lastValue[READ_ACTIVITY] = this.lastValue[READ_ACTIVITY]
//                        * (0.90 + 0.2 * Math.random());
//                this.datasets[READ_ACTIVITY].getSeries(0).add(
//                        new Millisecond(), this.lastValue[READ_ACTIVITY]);
//            }
//
//            if (e.getActionCommand().endsWith(String.valueOf(1)))
//            {
//                Millisecond now = new Millisecond();
//                System.out.println("Now = " + now.toString());
//                this.lastValue[WRITE_ACTIVITY] = this.lastValue[WRITE_ACTIVITY]
//                        * (0.90 + 0.2 * Math.random());
//                this.datasets[WRITE_ACTIVITY].getSeries(0).add(
//                        new Millisecond(), this.lastValue[WRITE_ACTIVITY]);
//            }
//
//            if (e.getActionCommand().equals("ADD_ALL"))
//            {
//                Millisecond now = new Millisecond();
//                System.out.println("Now = " + now.toString());
//
//                for (int i = 0; i < SUBPLOT_COUNT; i++)
//                {
//                    this.lastValue[i] = this.lastValue[i]
//                            * (0.90 + 0.2 * Math.random());
//                    this.datasets[i].getSeries(0).add(new Millisecond(),
//                            this.lastValue[i]);
//                }
//            }

        }
    }

    private BlockingQueue<Statistics> statisticsQueue = new LinkedBlockingQueue<Statistics>();

    /**
     * Constructs a new demonstration application.
     * 
     * @param title the frame title.
     */
    public ReadsVersusWritesDisplay(String title)
    {
        super(title);
        setContentPane(new DemoPanel(statisticsQueue));
    }

    public void report(Statistics statistics)
    {

        statisticsQueue.add(statistics);

    }
}

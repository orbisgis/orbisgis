/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.gdms.driver.DriverException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.StatisticMethod;

/**
 * Panel with the chart
 * @author sennj
 */
class ChoroplethChartPanel extends JPanel {

    private ChoroplethDatas choroDatas;
    private JFreeChart chart;
    private ChartPanel chPanel;
    private ChartListener chartLis;

    /**
     * ChoroplethChartPanel Constructor
     * @param choroDatas the datas to draw
     * @param title the chart Panel title
     * @param labelX the chart Panel label on X axis
     * @param labelY the chart Panel label on Y axis
     */
    ChoroplethChartPanel(ChoroplethDatas choroDatas,String title,String labelX,String labelY) {
        super();
        this.choroDatas = choroDatas;
        this.add(initDraw(title,labelX,labelY));
    }

    /**
     * initDraw
     * Initialise the draw process of the chart Panel
     * @param title the chart Panel title
     * @param labelX the chart Panel label on X axis
     * @param labelY the chart Panel label on Y axis
     * @return a ChartPanel
     */
    public ChartPanel initDraw(String title,String labelX,String labelY) {
        choroDatas.setStatisticMethod(StatisticMethod.QUANTILES, true);
        try {
            choroDatas.resetRanges();
        } catch (DriverException ex) {
            Logger.getLogger(ChoroplethChartPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Range[] ranges = choroDatas.getRange();
        double[] values = null;
        try {
            values = choroDatas.getSortedData();
        } catch (ParameterException ex) {
            Logger.getLogger(ChoroplethChartPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Color[] colors = choroDatas.getClassesColors();
        String[] aliases = choroDatas.getAliases();

        DefaultCategoryDataset data = refreshData(aliases,ranges, values);

        // create a chart...
        chart = ChartFactory.createBarChart3D(title, labelX, labelY, data,
                PlotOrientation.VERTICAL, true, true, false);

        drawAxis(chart, values, ranges, colors,aliases);

        TextTitle annot = new TextTitle("X = 0 Y = 0");
        annot.setPosition(RectangleEdge.TOP);
        chart.addSubtitle(annot);

        chPanel = new ChartPanel(chart);

        chartLis = new ChartListener(this, chPanel, annot, choroDatas);

        chPanel.setPopupMenu(null);
        chPanel.setDomainZoomable(false);
        chPanel.setRangeZoomable(false);

        chPanel.addMouseMotionListener(chartLis);
        chPanel.addMouseListener(chartLis);

        return chPanel;
    }

    /**
     * refreshData
     * Refresh the data from the chart Panel
     * @param aliases the aliases of the category
     * @param ranges the ranges of the category
     * @param values all the value
     * @return a dataset that will be display on the chart Panel
     */
    public DefaultCategoryDataset refreshData(String[] aliases, Range[] ranges, double[] values) {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        double value;
        Range range;
        for (int i = 1; i <= values.length; i++) {
            value = values[i - 1];
            for (int j = 1; j <= ranges.length; j++) {
                range = ranges[j - 1];
                if (value > range.getMinRange() && value <= range.getMaxRange()) {
                    data.setValue(value, aliases[j-1], new Integer(i - 1));
                }
            }
        }
        return data;


    }

    /**
     * convertCoords
     * Convert the coords from the sceen value to the chart Panel value
     * @param mouse the screen coords of the mouse pointer
     * @param chPanel the JFreeChart Panel
     * @param data a dataset that will be display on the chart Panel
     * @return the converted mouse position
     */
    public Point.Double convertCoords(Point mouse, ChartPanel chPanel, DefaultCategoryDataset data) {

        Point2D p = chPanel.translateScreenToJava2D(mouse);
        Rectangle2D plotArea = chPanel.getScreenDataArea();
        CategoryPlot plotxy = (CategoryPlot) chPanel.getChart().getPlot(); // your plot
        double chartY = plotxy.getRangeAxis().java2DToValue(p.getY(), plotArea, plotxy.getRangeAxisEdge());

        double deltaStart = plotxy.getDomainAxis().getCategoryStart(0, data.getRowCount(), plotArea, plotxy.getDomainAxisEdge());
        double deltaStop = plotxy.getDomainAxis().getCategoryEnd(data.getRowCount() - 1, data.getRowCount(), plotArea, plotxy.getDomainAxisEdge());

        double mathX = (mouse.x - deltaStart);
        double distX = deltaStop - deltaStart;
        double chartX = mathX / distX * data.getColumnCount();
        chartX = ((double) ((int) (chartX * 100))) / 100;
        chartY = ((double) ((int) (chartY * 100))) / 100;

        return new Point.Double(chartX, chartY);
    }

    /**
     * drawAxis
     * the default drawAxis methode to draw the vertical axis of the categary on the chart Panel
     * @param chPanel the JFreeChart Panel
     * @param values all the value
     * @param ranges the ranges of the category
     * @param colors the colors of the category
     * @param aliases the aliases of the category
     */
    public void drawAxis(JFreeChart chPanel, double[] values, Range[] ranges, Color[] colors,String[] aliases)
    {
        drawAxis(chPanel, values, ranges, colors,aliases,-1);
    }

    /**
     * drawAxis
     * the extended drawAxis methode to draw the vertical axis of the categary on the chart Panel
     * @param chPanel the JFreeChart Panel
     * @param values all the value
     * @param ranges the ranges of the category
     * @param colors the colors of the category
     * @param aliases the aliases of the category
     * @param select a selected category
     */
    public void drawAxis(JFreeChart chPanel, double[] values, Range[] ranges, Color[] colors,String[] aliases,int select) {
        CategoryPlot plot = (CategoryPlot) chPanel.getPlot();
        int nbRange = 0;
        int ind = 0;
        while (nbRange < ranges.length - 1) {
            for (int i = ind; i < values.length; i++) {
                if (values[i] > ranges[nbRange].getMaxRange()) {
                    final CategoryMarker start = new CategoryMarker(i);
                    if(select-1 == nbRange)
                        start.setPaint(Color.RED);
                    else
                        start.setPaint(Color.BLACK);
                    start.setLabel(aliases[nbRange]);
                    start.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                    start.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                    start.setDrawAsLine(true);
                    plot.addDomainMarker(start);
                    nbRange++;
                    ind = 0;
                }
            }
            ind--;
        }
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        for(int i=1;i<=ranges.length;i++)
        {
            renderer.setSeriesPaint(i-1, colors[i-1]);
        }
    }

    /**
     * drawData
     * Draw the data on the chart Panel
     * @param chPanel the JFreeChart Panel
     * @param aliases the aliases of the category
     * @param ranges the ranges of the category
     * @param values all the value
     */
    public void drawData(ChartPanel chPanel,String[] aliases, Range[] ranges, double[] values) {
        DefaultCategoryDataset data = refreshData(aliases,ranges, values);

        CategoryPlot plot = (CategoryPlot) chPanel.getChart().getPlot();
        plot.setDataset(data);

        chPanel.repaint();
    }

    /**
     * getChartPanel
     * @return return the chart Panel
     */
    public ChartPanel getChartPanel() {
        return chPanel;
    }

    /**
     * refresh
     * Refresh all the chart Panel
     * @param choroDatas the datas to draw
     */
    public void refresh(ChoroplethDatas choroDatas) {
        try {
            CategoryPlot plot = (CategoryPlot) chPanel.getChart().getPlot();
            plot.clearDomainMarkers();
            refreshData(choroDatas.getAliases(),choroDatas.getRange(), choroDatas.getSortedData());
            drawData(chPanel,choroDatas.getAliases(), choroDatas.getRange(), choroDatas.getSortedData());
            drawAxis(chart, choroDatas.getSortedData(), choroDatas.getRange(), choroDatas.getClassesColors(),choroDatas.getAliases());
        } catch (ParameterException ex) {
            Logger.getLogger(ChoroDatasChangedListener.class.getName()).log(Level.SEVERE, null, ex);
        }

        chPanel.repaint();
    }

}

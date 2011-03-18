/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.StatisticMethod;

/**
 *
 * @author sennj
 */
class JSE_ChoroplethChartPanel extends JPanel {

    private JSE_ChartListener chartLis;

    JSE_ChoroplethChartPanel(JSE_ChoroplethDatas ChoroDatas) {
        super();
        this.add(initDraw(ChoroDatas));
    }

    public ChartPanel initDraw(JSE_ChoroplethDatas ChoroDatas) {
        ChoroDatas.setFieldIndex(0);
        ChoroDatas.setStatisticMethod(StatisticMethod.QUANTITY);
        try {
            ChoroDatas.resetRanges();
        } catch (DriverException ex) {
            Logger.getLogger(JSE_ChoroplethChartPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Range[] ranges = ChoroDatas.getRange();
        double[] values = null;
        try {
            values = ChoroDatas.getSortedData();
        } catch (ParameterException ex) {
            Logger.getLogger(JSE_ChoroplethChartPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Color[] colors = ChoroDatas.getClassesColors();

        DefaultCategoryDataset data = refreshData(ranges, values);

        // create a chart...
        JFreeChart chart = ChartFactory.createBarChart(
                "Sample Bar Chart", "X", "Y", data, PlotOrientation.VERTICAL,
                true, true, false);

        drawAxis(chart, values, ranges, colors);

        TextTitle annot = new TextTitle("X = 0 Y = 0");
        annot.setPosition(RectangleEdge.TOP);
        chart.addSubtitle(annot);

        ChartPanel chPanel = new ChartPanel(chart);

        chartLis = new JSE_ChartListener(chPanel, annot, data, ChoroDatas);

        chPanel.setPopupMenu(null);
        chPanel.setDomainZoomable(false);
        chPanel.setRangeZoomable(false);

        chPanel.addMouseMotionListener(chartLis);
        chPanel.addMouseListener(chartLis);

        return chPanel;
    }

    public static DefaultCategoryDataset refreshData(Range[] ranges, double[] values) {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        double value;
        Range range;
        for (int i = 1; i <= values.length; i++) {
            value = values[i - 1];
            for (int j = 1; j <= ranges.length; j++) {
                range = ranges[j - 1];
                if (value > range.getMinRange() && value <= range.getMaxRange()) {
                    data.setValue(value, new Integer(j), new Integer(i));
                }
            }
        }
        return data;
    }

    public static Point.Double convertCoords(Point mouse, ChartPanel chPanel, DefaultCategoryDataset data) {

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

    public static void drawAxis(JFreeChart chPanel, double[] values, Range[] ranges, Color[] colors) {
        CategoryPlot plot = (CategoryPlot) chPanel.getPlot();
        int nbRange = 0;
        int ind = 1;
        while (nbRange < ranges.length - 1) {
            for (int i = ind; i <= values.length; i++) {
                if (values[i - 1] > ranges[nbRange].getMaxRange()) {
                    final CategoryMarker start = new CategoryMarker(i - 1);
                    start.setPaint(colors[nbRange]);
                    start.setLabel(Integer.toString(nbRange+1));
                    start.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                    start.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                    start.setDrawAsLine(true);
                    plot.addDomainMarker(start);
                    nbRange++;
                    ind = i;
                }
            }
        }
    }

    public static void drawData(JSE_ChoroplethDatas ChoroDatas, ChartPanel chPanel, Range[] ranges, double[] values) {
        DefaultCategoryDataset data = refreshData(ranges, values);

        CategoryPlot plot = (CategoryPlot) chPanel.getChart().getPlot();
        plot.setDataset(data);

        chPanel.repaint();
    }
}

package org.orbisgis.view.toc.actions.cui.freqChart.render;

import java.awt.Color;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.MonoIntervalXYDataset;

/**
 * FreqChartRender the chart render
 * @author sennj
 */
public class FreqChartRender {

    /** The chart object */
    private JFreeChart chart;
    /** The chart selected threshold */
    private int selectThreshold = 0;
    /** Display or not the label */
    private boolean displayLabel;

    /**
     * Initialyze the render
     * @param title the title of the chart
     * @param labelX the x label of the chart
     * @param labelY the y label of the chart
     * @param displayLabel display or not the label
     */
    public void init(String title, String labelX, String labelY,
            boolean displayLabel) {
        this.displayLabel = displayLabel;
        chart = ChartFactory.createXYBarChart(title, labelX, false, labelY,
                null, PlotOrientation.VERTICAL, true, true, false);
        chart.getLegend().setVisible(false);
    }

    /**
     * Clear
     * clear the plot dataset
     */
    public void clear() {
        XYPlot plot = (XYPlot) chart.getPlot();
        int dataSet = plot.getDatasetCount();
        for (int i = 1; i <= dataSet; i++) {
            plot.setDataset(i - 1, null);
            plot.setRenderer(1, null);
        }
    }

    /**
     * Repaint show Create the chart panel
     * @param freqChartDataModel The frequence chart data model
     */
    public JFreeChart repaint(FreqChartDataModel freqChartDataModel) {

        List<MonoIntervalXYDataset> dataset = freqChartDataModel.getHistogramDataset();
        List<List<Double>> threshold = freqChartDataModel.getThresholdList();
        List<Color> color = freqChartDataModel.getColor();

        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setDomainGridlinesVisible(false);

        XYBarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());

        MonoIntervalXYDataset dataSerie;

        int j = 1;
        for (int i = 1; i <= dataset.size(); i++) {
            dataSerie = dataset.get(i - 1);
            plot.setDataset(i - 1, dataSerie);
            List<Double> thresholdColor = threshold.get(j - 1);
            XYBarRenderer rendererData = new XYBarRenderer();
            rendererData.setPaint(color.get(j - 1));
            plot.setRenderer(i - 1, rendererData);
            if (!(dataSerie.getStartX(0, 0).doubleValue() >= thresholdColor.get(0) && dataSerie.getEndX(0, 0).doubleValue() < thresholdColor.get(1))) {
                j++;
            }
        }

        drawAxes(plot, freqChartDataModel);

        return chart;
    }

    /**
     * Draw the axis on the plot
     * @param plot the plot
     * @param freqChartDataModel The frequence chart data model
     */
    public void drawAxes(XYPlot plot, FreqChartDataModel freqChartDataModel) {
        plot.clearDomainMarkers();

        List<List<Double>> threshold = freqChartDataModel.getThresholdList();
        List<String> labels = freqChartDataModel.getLabel();

        List<Double> border;
        for (int i = 1; i <= threshold.size(); i++) {
            border = threshold.get(i - 1);
            ValueMarker marker = new ValueMarker(border.get(0));
            if (displayLabel) {
                marker.setLabel(labels.get(i - 1));
                marker.setLabelAnchor(RectangleAnchor.TOP);
                marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            }
            if (i == 1) {
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);
            } else if (i - 1 == selectThreshold) {
                marker.setPaint(Color.WHITE);
                plot.addDomainMarker(marker);
            } else {
                marker.setPaint(Color.RED);
                plot.addDomainMarker(marker);
            }
            if (i == threshold.size()) {
                marker = new ValueMarker(border.get(1));
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);
            }
        }
    }

    /**
     * Set the selected threshold
     * @param selectThreshold
     */
    public void setSelectedThreshold(int selectThreshold) {
        this.selectThreshold = selectThreshold;
    }
}

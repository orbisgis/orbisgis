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
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.SimpleIntervalXYDataset;

/**
 * FreqChartRender the chart render
 * 
 * @author sennj
 * 
 */
public class FreqChartRender {

    private JFreeChart chart;
    private int selectSeuil = 0;
    private boolean displayLabel;

    /**
     * FreqChartRender constructor
     */
    public FreqChartRender() {
    }

    /**
     * init
     * initialyze the render
     * @param titre the title of the chart
     * @param labelX the x label of the chart
     * @param labelY the y label of the chart
     * @param displayLabel display or not the label
     */
    public void init(String titre, String labelX, String labelY,
            boolean displayLabel) {
        this.displayLabel = displayLabel;
        chart = ChartFactory.createXYBarChart(titre, labelX, false, labelY,
                null, PlotOrientation.VERTICAL, true, true, false);
        chart.getLegend().setVisible(false);
    }

    /**
     * clear
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
     * repaint show Create the chart panel
     * @param freqChartDataModel
     *            the data model to draw
     */
    public JFreeChart repaint(FreqChartDataModel freqChartDataModel) {

        List<SimpleIntervalXYDataset> dataset = freqChartDataModel.getHistogramDataset();
        List<double[]> seuil = freqChartDataModel.getSeuilList();
        List<Color> color = freqChartDataModel.getColor();

        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setDomainGridlinesVisible(false);

        XYBarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());

        SimpleIntervalXYDataset dataSerie;

        int j = 1;
        for (int i = 1; i <= dataset.size(); i++) {
            dataSerie = dataset.get(i - 1);
            plot.setDataset(i - 1, dataSerie);
            double[] seuilColor = seuil.get(j - 1);
            XYBarRenderer rendererData = new XYBarRenderer();
            rendererData.setPaint(color.get(j - 1));
            plot.setRenderer(i - 1, rendererData);
            if (!(dataSerie.getStartX(0, 0).doubleValue() >= seuilColor[0] && dataSerie.getEndX(0, 0).doubleValue() < seuilColor[1])) {
                j++;
            }
        }

        drawAxes(plot, freqChartDataModel);

        return chart;
    }

    /**
     * drawAxes Draw the axis on the plot
     *
     * @param plot
     *            The plot
     * @param freqChartDataModel
     *            the data model to draw
     */
    public void drawAxes(XYPlot plot, FreqChartDataModel freqChartDataModel) {
        plot.clearDomainMarkers();

        List<double[]> seuil = freqChartDataModel.getSeuilList();
        List<String> labels = freqChartDataModel.getLabel();

        double[] borne;
        for (int i = 1; i <= seuil.size(); i++) {
            borne = seuil.get(i - 1);
            ValueMarker marker = new ValueMarker(borne[0]);
            if (displayLabel) {
                marker.setLabel(labels.get(i - 1));
                marker.setLabelAnchor(RectangleAnchor.TOP);
                marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            }
            if (i == 1) {
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);
            } else if (i - 1 == selectSeuil) {
                marker.setPaint(Color.WHITE);
                plot.addDomainMarker(marker);
            } else {
                marker.setPaint(Color.RED);
                plot.addDomainMarker(marker);
            }
            if (i == seuil.size()) {
                marker = new ValueMarker(borne[1]);
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);
            }
        }
    }

    /**
     * setSelectedSeuil
     *
     * @param selectSeuil
     */
    public void setSelectedSeuil(int selectSeuil) {
        this.selectSeuil = selectSeuil;
    }
}

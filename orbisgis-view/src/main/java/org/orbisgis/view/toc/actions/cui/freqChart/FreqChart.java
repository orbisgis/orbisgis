package org.orbisgis.view.toc.actions.cui.freqChart;

import java.awt.Dimension;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.orbisgis.view.toc.actions.cui.choropleth.listener.DataChangeListener;
import org.orbisgis.view.toc.actions.cui.choropleth.listener.RangeAxisListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;
import org.orbisgis.view.toc.actions.cui.freqChart.render.FreqChartRender;

/**
 * FreqChart the frequence chart
 * 
 * @author sennj
 * 
 */
public class FreqChart {

    /** The frequence chart data model */
    private FreqChartDataModel freqChartDataModel;
    /** The frequence chart display render */
    private FreqChartRender freqChartRender;
    /** The frequence chart event listener */
    private FreqChartListener freqChartListener;
    /** The frequence chart title */
    private String title;
    /** The frequence chart x label */
    private String labelX;
    /** The frequence chart y label */
    private String labelY;
    /** Boolean for displaying label */
    private boolean displayLabel;
    /** The frequence chart panel */
    private ChartPanel chartPanel;

    /**
     * FreqChart constructor
     * @param freqChartDataModel The frequence chart data model
     */
    public FreqChart(FreqChartDataModel freqChartDataModel) {
        this.freqChartDataModel = freqChartDataModel;
        this.freqChartRender = new FreqChartRender();
    }

    /**
     * Set the graph title
     * @param titre The graph title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the graph X label
     * @param labelX The graph X label
     */
    public void setLabelX(String labelX) {
        this.labelX = labelX;
    }

    /**
     * Set the graph Y label
     * @param labelY The graph Y label
     */
    public void setLabelY(String labelY) {
        this.labelY = labelY;
    }

    /**
     * Set display the label
     * @param displayLabel
     */
    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }

    /**
     * Add an axis listener
     * @param ral The range axis listener
     */
    public void addAxisListener(RangeAxisListener ral) {
        freqChartDataModel.addAxisListener(ral);
    }

    /**
     * Remove an axis listener
     * @param ral The range axis listener
     */
    public void removeAxisListener(RangeAxisListener ral) {
        freqChartDataModel.removeAxisListener(ral);
    }

    /**
     * Add a data listener
     * @param dcl The data change listener
     */
    public void addDataListener(DataChangeListener dcl) {
        freqChartDataModel.addDataListener(dcl);
    }

    /**
     * Remove a data listener
     * @param dcl The data change listener
     */
    public void removeDataListener(DataChangeListener dcl) {
        freqChartDataModel.removeDataListener(dcl);
    }

    /**
     * Return the chart panel
     * @return the chart panel
     */
    public ChartPanel getPanel() {
        freqChartDataModel.generateChartData();

        freqChartRender.init(title, labelX, labelY, displayLabel);

        JFreeChart chart = freqChartRender.repaint(freqChartDataModel);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPopupMenu(null);
        chartPanel.setMouseZoomable(false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        freqChartListener = new FreqChartListener(chartPanel, freqChartDataModel, freqChartRender);
        chartPanel.addChartMouseListener(freqChartListener);
        chartPanel.addMouseMotionListener(freqChartListener);

        return chartPanel;
    }

    /**
     * Clear all plot data
     */
    public void clearData() {
        freqChartRender.clear();
        freqChartDataModel.generateChartData();
        repaint();
    }

    /**
     * Repaint the component
     */
    public void repaint() {
        freqChartDataModel.computeChartData();
        chartPanel.setChart(freqChartRender.repaint(freqChartDataModel));
        chartPanel.setMouseZoomable(false);
    }
}

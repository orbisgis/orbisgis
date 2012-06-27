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

    private FreqChartDataModel freqChartDataModel;
    private FreqChartRender freqChartRender;
    private FreqChartListener freqChartListener;
    private String titre;
    private String labelX;
    private String labelY;
    private boolean displayLabel;
    private ChartPanel chartPanel;

    /**
     * FreqChart constructor
     *
     * @param freqChartDataModel
     *            the data model to draw
     */
    public FreqChart(FreqChartDataModel freqChartDataModel) {
        this.freqChartDataModel = freqChartDataModel;
        this.freqChartRender = new FreqChartRender();
    }

    /**
     * setTitre set the graph titre
     *
     * @param titre
     *            the graph titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * setLabelX set the graph X label
     *
     * @param labelX
     *            the graph X label
     */
    public void setLabelX(String labelX) {
        this.labelX = labelX;
    }

    /**
     * setLabelY set the graph Y label
     *
     * @param labelY
     *            the graph Y label
     */
    public void setLabelY(String labelY) {
        this.labelY = labelY;
    }

    /**
     * setDisplayLabel
     * @param displayLabel
     */
    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }

    public void addAxisListener(RangeAxisListener ral) {
        freqChartDataModel.addAxisListener(ral);
    }

    public void removeAxisListener(RangeAxisListener ral) {
        freqChartDataModel.removeAxisListener(ral);
    }

    public void addDataListener(DataChangeListener dcl) {
        freqChartDataModel.addDataListener(dcl);
    }

    public void removeDataListener(DataChangeListener dcl) {
        freqChartDataModel.removeDataListener(dcl);
    }

    /**
     * getPanel return the chart panel
     *
     * @return the ChartPanel
     */
    public ChartPanel getPanel() {
        freqChartDataModel.generateChartData();

        freqChartRender.init(titre, labelX, labelY, displayLabel);

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
     * clearData clear all plot data
     */
    public void clearData() {
        freqChartRender.clear();
        freqChartDataModel.generateChartData();
        repaint();
    }

    /**
     * repaint the component
     */
    public void repaint() {
        freqChartDataModel.computeChartData();
        chartPanel.setChart(freqChartRender.repaint(freqChartDataModel));
        chartPanel.setMouseZoomable(false);
    }
}

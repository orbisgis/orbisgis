package org.orbisgis.view.toc.actions.cui.freqChart.chartListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.List;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;
import org.orbisgis.view.toc.actions.cui.freqChart.render.FreqChartRender;

/**
 * FreqChartListener
 * @author sennj
 */
public class FreqChartListener implements ChartMouseListener,
        MouseMotionListener {

     /** The frequence chart panel */
    private ChartPanel chartPanel;
    /** The frequence chart data model */
    private FreqChartDataModel freqChartDataModel;
    /** The frequence chart display render */
    private FreqChartRender freqChartRender;
    /** The old selected threshold */
    private int selectThresholdOld;

    /**
     * FreqChartListener constructor
     * @param chartPanel the ChartPanel object
     * @param freqChartDataModel The frequence chart data model
     * @param choroplethRangeTabPanel the range panel
     */
    public FreqChartListener(ChartPanel chartPanel, FreqChartDataModel freqChartDataModel,
            FreqChartRender freqChartRender) {
        this.chartPanel = chartPanel;
        this.freqChartDataModel = freqChartDataModel;
        this.freqChartRender = freqChartRender;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        Point2D p = chartPanel.translateScreenToJava2D(event.getTrigger().getPoint());
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        Rectangle2D plotArea = chartPanel.getScreenDataArea();
        double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea,
                plot.getDomainAxisEdge());
        double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea,
                plot.getRangeAxisEdge());

        Point2D point = new Point2D.Double(chartX, chartY);

        List<List<Double>> threshold = freqChartDataModel.getThresholdList();

        double thresholdMax;
        int selectThreshold = 0;
        boolean found = false;

        for (int i = 1; i < threshold.size(); i++) {
            thresholdMax = threshold.get(i - 1).get(1);
            if (thresholdMax <= chartX + 1 && thresholdMax >= chartX - 1) {
                selectThreshold = i;
                found = true;
            }
        }
        if (!found) {
            selectThreshold = 0;
        }
        if (found) {
            freqChartDataModel.fireEvent(new AxisChanged(this,
                    AxisChangedType.RANGEPRESSED, selectThreshold, point));
        }
        freqChartDataModel.fireEvent(new AxisChanged(this,
                AxisChangedType.CHARTPRESSED, selectThreshold, point));
    }

    @Override
    public void mouseDragged(MouseEvent event) {

        Point2D p = chartPanel.translateScreenToJava2D(event.getPoint());
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        Rectangle2D plotArea = chartPanel.getScreenDataArea();
        double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea,
                plot.getDomainAxisEdge());
        double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea,
                plot.getRangeAxisEdge());

        Point2D point = new Point2D.Double(chartX, chartY);
        freqChartDataModel.fireEvent(new AxisChanged(this,
                AxisChangedType.CHARTDRAG, selectThresholdOld, point));
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Point2D p = chartPanel.translateScreenToJava2D(event.getTrigger().getPoint());
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        Rectangle2D plotArea = chartPanel.getScreenDataArea();
        double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea,
                plot.getDomainAxisEdge());
        double chartXMin = plot.getDomainAxis().java2DToValue(
                p.getX() - freqChartDataModel.getPixRangeDelta(), plotArea,
                plot.getDomainAxisEdge());
        double chartXMax = plot.getDomainAxis().java2DToValue(
                p.getX() + freqChartDataModel.getPixRangeDelta(), plotArea,
                plot.getDomainAxisEdge());
        double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea,
                plot.getRangeAxisEdge());

        Point2D point = new Point2D.Double(chartX, chartY);

        List<List<Double>> threshold = freqChartDataModel.getThresholdList();

        double thresholdMax;
        int selectThreshold = 0;
        boolean found = false;

        for (int i = 1; i < threshold.size(); i++) {
            thresholdMax = threshold.get(i - 1).get(1);
            if (thresholdMax <= chartXMax && thresholdMax >= chartXMin) {
                selectThreshold = i;
                found = true;
            }
        }
        if (!found) {
            selectThreshold = 0;
        }

        freqChartRender.setSelectedThreshold(selectThreshold);
        if (selectThresholdOld != selectThreshold) {
            freqChartRender.drawAxes(plot, freqChartDataModel);
            if (found) {
                freqChartDataModel.fireEvent(new AxisChanged(this,
                        AxisChangedType.RANGESUP, selectThreshold, point));
            } else {
                freqChartDataModel.fireEvent(new AxisChanged(this,
                        AxisChangedType.RANGESDOWN, selectThresholdOld, point));
            }
        }
        freqChartDataModel.fireEvent(new AxisChanged(this,
                AxisChangedType.CHARTMOVE, selectThreshold, point));
        selectThresholdOld = selectThreshold;
    }

    /**
     * Data change type enumeration
     */
    public enum AxisChangedType {

        RANGESUP, RANGESDOWN, CHARTMOVE, CHARTPRESSED, RANGEPRESSED, CHARTDRAG
    }

    public class AxisChanged extends EventObject {

        public AxisChangedType dataType;
        public int selectThreshold;
        public Point2D point;

        public AxisChanged(Object source, AxisChangedType datachangedtype) {
            this(source, datachangedtype, -1, null);
        }

        public AxisChanged(Object source, AxisChangedType datachangedtype,
                int selectThresholdId, Point2D point) {
            super(source);
            this.dataType = datachangedtype;
            this.selectThreshold = selectThresholdId;
            this.point = point;
        }
    }
}

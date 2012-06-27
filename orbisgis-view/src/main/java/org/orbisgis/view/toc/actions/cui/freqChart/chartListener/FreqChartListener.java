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

    private ChartPanel chartPanel;
    private FreqChartDataModel freqChartDataModel;
    private FreqChartRender freqChartRender;
    private int selectSeuilOld;

    /**
     * FreqChartListener constructor
     * @param chartPanel the ChartPanel object
     * @param freqChartDataModel the data model to draw
     * @param choroplethRangeTabPanel the range panel
     */
    public FreqChartListener(ChartPanel chartPanel,
            FreqChartDataModel freqChartDataModel,
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

        List<double[]> seuil = freqChartDataModel.getSeuilList();

        double seuilMax;
        int selectSeuil = 0;
        boolean found = false;

        for (int i = 1; i < seuil.size(); i++) {
            seuilMax = seuil.get(i - 1)[1];
            if (seuilMax <= chartX + 1 && seuilMax >= chartX - 1) {
                selectSeuil = i;
                found = true;
            }
        }
        if (!found) {
            selectSeuil = 0;
        }
        if (found) {
            freqChartDataModel.fireEvent(new AxisChanged(this,
                    AxisChangedType.RANGEPRESSED, selectSeuil, point));
        }
        freqChartDataModel.fireEvent(new AxisChanged(this,
                AxisChangedType.CHARTPRESSED, selectSeuil, point));
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
                AxisChangedType.CHARTDRAG, selectSeuilOld, point));
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
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

        List<double[]> seuil = freqChartDataModel.getSeuilList();

        double seuilMax;
        int selectSeuil = 0;
        boolean found = false;

        for (int i = 1; i < seuil.size(); i++) {
            seuilMax = seuil.get(i - 1)[1];
            if (seuilMax <= chartXMax && seuilMax >= chartXMin) {
                selectSeuil = i;
                found = true;
            }
        }
        if (!found) {
            selectSeuil = 0;
        }

        freqChartRender.setSelectedSeuil(selectSeuil);
        if (selectSeuilOld != selectSeuil) {
            freqChartRender.drawAxes(plot, freqChartDataModel);
            if (found) {
                freqChartDataModel.fireEvent(new AxisChanged(this,
                        AxisChangedType.RANGESUP, selectSeuil, point));
            } else {
                freqChartDataModel.fireEvent(new AxisChanged(this,
                        AxisChangedType.RANGESDOWN, selectSeuilOld, point));
            }
        }
        freqChartDataModel.fireEvent(new AxisChanged(this,
                AxisChangedType.CHARTMOVE, selectSeuil, point));
        selectSeuilOld = selectSeuil;
    }

    /** Data change type enumeration
     */
    public enum AxisChangedType {

        RANGESUP, RANGESDOWN, CHARTMOVE, CHARTPRESSED, RANGEPRESSED, CHARTDRAG
    }

    public class AxisChanged extends EventObject {

        public AxisChangedType dataType;
        public int selectSeuil;
        public Point2D point;

        public AxisChanged(Object source, AxisChangedType datachangedtype) {
            this(source, datachangedtype, -1, null);
        }

        public AxisChanged(Object source, AxisChangedType datachangedtype,
                int selectSeuilId, Point2D point) {
            super(source);
            this.dataType = datachangedtype;
            this.selectSeuil = selectSeuilId;
            this.point = point;
        }
    }
}

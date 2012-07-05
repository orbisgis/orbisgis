package org.orbisgis.view.toc.actions.cui.choropleth.listener;

import java.util.List;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.AxisListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener.AxisChanged;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged.DataChangedType;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;

/**
 * Range axis listener
 * @author sennj
 */
public class RangeAxisListener implements AxisListener {

    /** The frequency chart panel */
    private FreqChart freqChart;
    /** The frequence chart data model */
    private FreqChartDataModel freqChartDataModel;
    /** The selected range */
    int selectedRange = 0;

    /**
     * RangeAxisListener constructor
     * @param freqChart The frequency chart panel
     * @param freqChartDataModel The frequence chart data model
     */
    public RangeAxisListener(FreqChart freqChart, FreqChartDataModel freqChartDataModel) {
        this.freqChart = freqChart;
        this.freqChartDataModel = freqChartDataModel;
    }

    @Override
    public void rangeStateChanged(AxisChanged evt) {
    }

    @Override
    public void rangePressed(AxisChanged evt) {
        selectedRange = evt.selectThreshold;
    }

    @Override
    public void chartPressed(AxisChanged evt) {
    }

    @Override
    public void chartMove(AxisChanged evt) {
    }

    @Override
    public void chartDrag(AxisChanged evt) {
        List<List<Double>> list = freqChartDataModel.getThresholdList();
        List<Double> thresholdA;
        List<Double> thresholdB;

        double pointBefore;
        double pointAfter;

        for (int i = 1; i <= list.size(); i++) {
            if (i == evt.selectThreshold) {
                thresholdA = list.get(i - 1);
                thresholdB = list.get(i);

                pointBefore = thresholdA.get(0);
                pointAfter = thresholdB.get(1);

                if (pointBefore < evt.point.getX() && pointAfter > evt.point.getX()) {
                    thresholdA.set(1, evt.point.getX());
                    thresholdB.set(0, evt.point.getX());
                    freqChartDataModel.fireDataEvent(new DataChanged(this, DataChangedType.DATACHANGE));
                }
            }
        }
        freqChart.repaint();
    }
}

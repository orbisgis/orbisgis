package org.orbisgis.view.toc.actions.cui.choropleth.listener;

import java.util.List;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.AxisListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener.AxisChanged;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged.DataChangedType;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;



/**
 * RangeAxisListener
 * @author sennj
 */
public class RangeAxisListener implements AxisListener {

    private FreqChart freqChart;
    private FreqChartDataModel freqChartDataModel;
    int selectedRange = 0;

    /**
     * RangeAxisListener constructor
     * @param freqChart the frequency chart panel
     * @param freqChartDataModel the data to model draw
     */
    public RangeAxisListener(FreqChart freqChart, FreqChartDataModel freqChartDataModel) {
        this.freqChart = freqChart;
        this.freqChartDataModel = freqChartDataModel;
    }

    @Override
    public void rangeStateChanged(AxisChanged evt) { }

    @Override
    public void rangePressed(AxisChanged evt) {
        selectedRange = evt.selectSeuil;
    }

    @Override
    public void chartPressed(AxisChanged evt) {}

    @Override
    public void chartMove(AxisChanged evt) { }

    @Override
    public void chartDrag(AxisChanged evt) {
        List<double[]> list = freqChartDataModel.getSeuilList();
        double[] seuilA;
        double[] seuilB;

        double pointBefore;
        double pointAfter;

        for (int i = 1; i <= list.size(); i++) {
            if (i == evt.selectSeuil) {
                seuilA = list.get(i - 1);
                seuilB = list.get(i);

                pointBefore = seuilA[0];
                pointAfter = seuilB[1];

                if (pointBefore < evt.point.getX() && pointAfter > evt.point.getX()) {
                    seuilA[1] = evt.point.getX();
                    seuilB[0] = evt.point.getX();
                    freqChartDataModel.fireDataEvent(new DataChanged(this, DataChangedType.DATACHANGE));
                }
            }
        }
        freqChart.repaint();
    }
}

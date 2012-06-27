package org.orbisgis.view.toc.actions.cui.choropleth.listener;


import org.orbisgis.view.toc.actions.cui.choropleth.gui.ChoroplethDistInputPanel;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.DataListener;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;

public class DataChangeListener implements DataListener {

    private ChoroplethDistInputPanel choroplethDistInputPanel;

    /**
     * DataChangeListener constructor
     * @param choroplethRangeTabPanel the range panel
     * @param freqChartDataModel the data to model draw
     */
    public DataChangeListener(ChoroplethDistInputPanel choroplethDistInputPanel, FreqChartDataModel freqChartDataModel) {
        this.choroplethDistInputPanel = choroplethDistInputPanel;
    }

    @Override
    public void dataChanged(DataChanged dataChanged) {
        choroplethDistInputPanel.updateChartInput();
    }
}

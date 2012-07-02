package org.orbisgis.view.toc.actions.cui.choropleth.listener;

import org.orbisgis.view.toc.actions.cui.choropleth.gui.ChoroplethDistInputPanel;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.DataListener;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged;

public class DataChangeListener implements DataListener {

    /** The choropleth distribution input panel */
    private ChoroplethDistInputPanel choroplethDistInputPanel;

    /**
     * DataChangeListener constructor
     * @param choroplethRangeTabPanel the range panel
     */
    public DataChangeListener(ChoroplethDistInputPanel choroplethDistInputPanel) {
        this.choroplethDistInputPanel = choroplethDistInputPanel;
    }

    @Override
    public void dataChanged(DataChanged dataChanged) {
        choroplethDistInputPanel.updateChartInput();
    }
}

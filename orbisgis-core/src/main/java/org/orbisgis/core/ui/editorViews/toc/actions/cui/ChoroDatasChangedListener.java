/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.DataChanged;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.DataChangedListener;

/**
 * Listener on data change
 * @author sennj
 */
class ChoroDatasChangedListener implements DataChangedListener {

    private ChoroplethDatas choroDatas;
    private ChoroplethRangeTabPanel rangeTabPanel;
    private ChoroplethChartPanel chartPanel;

    /**
     * ChoroDatasChangedListener Constructor
     * @param choroDatas the datas to draw
     * @param rangeTabPanel the range table Panel
     * @param chartPanel the chart Panel
     */
    public ChoroDatasChangedListener(ChoroplethDatas choroDatas, ChoroplethRangeTabPanel rangeTabPanel, ChoroplethChartPanel chartPanel) {
        this.choroDatas = choroDatas;
        this.rangeTabPanel = rangeTabPanel;
        this.chartPanel = chartPanel;
    }

    @Override
    public void dataChangedOccurred(DataChanged evt) {
        chartPanel.refresh(choroDatas);
        rangeTabPanel.refresh(choroDatas);
    }
}

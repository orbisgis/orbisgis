/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.DataChanged;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.DataChangedListener;

/**
 *
 * @author sennj
 */
class JSE_ChoroDatasChangedListener implements DataChangedListener {

    ChoroplethWizardPanel choroplethWizard;
    JSE_ChoroplethDatas ChoroDatas;
    JSE_ChoroplethInputPanel inputPanel;
    JSE_ChoroplethRangeTabPanel rangeTabPanel;
    JSE_ChoroplethChartPanel chartPanel;

    public JSE_ChoroDatasChangedListener(ChoroplethWizardPanel choroplethWizard,JSE_ChoroplethDatas ChoroDatas, JSE_ChoroplethInputPanel inputPanel ,JSE_ChoroplethRangeTabPanel rangeTabPanel, JSE_ChoroplethChartPanel chartPanel) {
        this.choroplethWizard = choroplethWizard;
        this.ChoroDatas = ChoroDatas;
        this.inputPanel = inputPanel;
        this.rangeTabPanel = rangeTabPanel;
        this.chartPanel = chartPanel;
    }

    @Override
    public void dataChangedOccurred(DataChanged evt) {
        System.out.println("CHANGE APPEND " + evt.dataType.name());

        chartPanel.refresh(ChoroDatas);
        rangeTabPanel.refresh(ChoroDatas);


    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.DataChanged;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.DataChangedListener;

/**
 *
 * @author sennj
 */
class ChoroDatasChangedListener implements DataChangedListener {

    ChoroplethWizardPanel choroplethWizard;
    ChoroplethDatas ChoroDatas;
    ChoroplethInputPanel inputPanel;
    ChoroplethRangeTabPanel rangeTabPanel;
    ChoroplethChartPanel chartPanel;

    public ChoroDatasChangedListener(ChoroplethWizardPanel choroplethWizard,ChoroplethDatas ChoroDatas, ChoroplethInputPanel inputPanel ,ChoroplethRangeTabPanel rangeTabPanel, ChoroplethChartPanel chartPanel) {
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

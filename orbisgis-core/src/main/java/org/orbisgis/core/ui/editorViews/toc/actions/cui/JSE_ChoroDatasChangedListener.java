/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.plot.CategoryPlot;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.DataChanged;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.DataChangedListener;

/**
 *
 * @author sennj
 */
class JSE_ChoroDatasChangedListener implements DataChangedListener {

    JSE_ChoroplethDatas ChoroDatas;
    JSE_ChoroplethRangeTabPanel rangeTabPanel;
    JSE_ChoroplethChartPanel chartPanel;

    public JSE_ChoroDatasChangedListener(JSE_ChoroplethDatas ChoroDatas, JSE_ChoroplethRangeTabPanel rangeTabPanel, JSE_ChoroplethChartPanel chartPanel) {
        this.ChoroDatas= ChoroDatas;
        this.rangeTabPanel = rangeTabPanel;
        this.chartPanel = chartPanel;
    }

    @Override
    public void dataChangedOccurred(DataChanged evt) {
        System.out.println("CHANGE APPEND");

        chartPanel.refresh();
        
        rangeTabPanel.refresh();
    }
}

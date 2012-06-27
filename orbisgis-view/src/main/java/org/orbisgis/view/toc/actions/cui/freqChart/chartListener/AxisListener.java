package org.orbisgis.view.toc.actions.cui.freqChart.chartListener;

import java.util.EventListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener.AxisChanged;



/**
 * AxisListener
 * @author sennj
 */
public interface AxisListener extends EventListener {

    void rangeStateChanged(AxisChanged evt);

    void rangePressed(AxisChanged evt);

    void chartPressed(AxisChanged evt);

    void chartMove(AxisChanged evt);

    void chartDrag(AxisChanged evt);
}

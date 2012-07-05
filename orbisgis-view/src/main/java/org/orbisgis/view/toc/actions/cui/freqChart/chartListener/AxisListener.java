package org.orbisgis.view.toc.actions.cui.freqChart.chartListener;

import java.util.EventListener;
import org.orbisgis.view.toc.actions.cui.freqChart.chartListener.FreqChartListener.AxisChanged;

/**
 * AxisListener
 * @author sennj
 */
public interface AxisListener extends EventListener {

    /**
     * Fire when range as changed
     * @param evt axis changed event
     */
    void rangeStateChanged(AxisChanged evt);

    /**
     * Fire when range is pressed
     * @param evt axis changed event
     */
    void rangePressed(AxisChanged evt);

    /**
     * Fire when mousse is pressed on chart
     * @param evt axis changed event
     */
    void chartPressed(AxisChanged evt);

    /**
     * Fire when mousse is moved on chart
     * @param evt axis changed event
     */
    void chartMove(AxisChanged evt);

    /**
     * Fire when mousse is dragged on chart
     * @param evt axis changed event
     */
    void chartDrag(AxisChanged evt);
}

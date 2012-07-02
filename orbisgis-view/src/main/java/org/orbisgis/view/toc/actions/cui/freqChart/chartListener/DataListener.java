package org.orbisgis.view.toc.actions.cui.freqChart.chartListener;

import java.util.EventListener;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged;

/**
 * Data changed listener
 * @author sennj
 */
public interface DataListener extends EventListener {

    /**
     * Fire when data changed
     * @param dataChanged data changed event
     */
    void dataChanged(DataChanged dataChanged);
}

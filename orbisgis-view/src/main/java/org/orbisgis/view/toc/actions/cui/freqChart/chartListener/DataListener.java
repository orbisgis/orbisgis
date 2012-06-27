package org.orbisgis.view.toc.actions.cui.freqChart.chartListener;

import java.util.EventListener;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.DataChanged;



/**
 * DataListener
 * @author sennj
 */
public interface DataListener extends EventListener{

    void dataChanged(DataChanged dataChanged);

}

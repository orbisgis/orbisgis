package org.orbisgis.view.toc.actions.cui.legends.ui;

import org.gdms.data.DataSource;
import org.orbisgis.view.toc.actions.cui.LegendContext;

/**
 * Root class for proportional UIs.
 *
 * @author Adam Gouge
 */
public abstract class PnlProportional extends PnlNonClassification {

    /**
     * DataSource associated to the layer attached to the LegendContext.
     */
    protected DataSource ds;

    /**
     * Constructor
     *
     * @param lc LegendContext from which to obtain the DataSource.
     */
    public PnlProportional(LegendContext lc) {
        this.ds = lc.getLayer().getDataSource();
    }
}

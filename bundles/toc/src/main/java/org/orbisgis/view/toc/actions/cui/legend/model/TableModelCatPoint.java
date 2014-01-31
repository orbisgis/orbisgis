package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

/**
 * @author Alexis Guéganno
 */
public class TableModelCatPoint extends TableModelInterval<PointParameters> {
    /**
     * Builds a new {@code TableModelUniqueValue} linker to {@code rl}.
     *
     * @param rl
     */
    public TableModelCatPoint(AbstractCategorizedLegend<PointParameters> rl) {
        super(rl);
    }
}
package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

/**
 * Table model dedicated to CategorizedLine representation.
 * @author Alexis Guéganno
 */
public class TableModelCatLine extends TableModelInterval<LineParameters> {
    /**
     * Builds a new {@code TableModelUniqueValue} linker to {@code rl}.
     *
     * @param rl
     */
    public TableModelCatLine(AbstractCategorizedLegend<LineParameters> rl) {
        super(rl);
    }
}

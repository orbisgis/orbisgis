package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

/**
 * @author Alexis Guéganno
 */
public class TableModelCatArea extends TableModelInterval<AreaParameters> {
    /**
     * Builds a new {@code TableModelUniqueValue} linker to {@code rl}.
     *
     * @param rl
     */
    public TableModelCatArea(AbstractCategorizedLegend <AreaParameters > rl) {
        super(rl);
    }
}

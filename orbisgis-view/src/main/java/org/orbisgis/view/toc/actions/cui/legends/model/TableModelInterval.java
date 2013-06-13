package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;

import java.util.Iterator;
import java.util.SortedSet;

/**
 * TableModel for categorized analysis.
 * @author Alexis Guéganno
 */
public class TableModelInterval<U extends LineParameters> extends AbstractLegendTableModel<Double,U> {
    private AbstractCategorizedLegend<U> cat;

    /**
     * Builds a new {@code TableModelInterval} linker to {@code rl}.
     * @param cl The input categorized analysis.
     */
    public TableModelInterval(AbstractCategorizedLegend<U> cl){
        cat = cl;
    }

    @Override
    public MappedLegend<Double,U> getMappedLegend() {
        return cat;
    }
}

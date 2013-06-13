package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.Iterator;
import java.util.SortedSet;

/**
 * TableModel for categorized analysis.
 * @author Alexis Guéganno
 */
public class TableModelInterval<U extends LineParameters> extends AbstractLegendTableModel<Double,U> {
    private AbstractCategorizedLegend<U> cat;
    private final static I18n I18N = I18nFactory.getI18n(TableModelInterval.class);

    @Override
    public String getColumnName(int col){
        if(col == KEY_COLUMN){
            return I18N.tr("Threshold");
        } else if(col == PREVIEW_COLUMN){
            return I18N.tr("Preview");
        }
        throw new IndexOutOfBoundsException("We did not found a column at index "+col+" !");
    }
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

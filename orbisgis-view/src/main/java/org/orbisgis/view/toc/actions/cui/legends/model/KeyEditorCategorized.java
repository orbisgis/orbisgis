package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

/**
 * @author Alexis Guéganno
 */
public abstract class KeyEditorCategorized<U extends LineParameters> extends KeyEditorMappedLegend<Double, U> {

    @Override
    protected Double getNotUsedKey(){
        return ((AbstractCategorizedLegend)getLegend()).getNotUsedKey(Double.valueOf(getField().getText()));
    }
}
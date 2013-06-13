package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

/**
 * @author Alexis Guéganno
 */
public abstract class KeyEditorCategorized<U extends LineParameters> extends KeyEditorMappedLegend<Double, U> {

    @Override
    protected Double getNotUsedKey(Double prev){
        try{
            Double d = Double.valueOf(getField().getText());
            return ((AbstractCategorizedLegend)getLegend()).getNotUsedKey(d);
        } catch (NumberFormatException nfe){
            return prev;
        }
    }
}
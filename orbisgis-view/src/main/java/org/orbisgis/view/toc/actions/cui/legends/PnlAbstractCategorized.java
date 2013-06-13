package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelInterval;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractCategorized<U extends LineParameters> extends PnlAbstractTableAnalysis<Double,U> {
    public static final Logger LOGGER = Logger.getLogger(PnlAbstractCategorized.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractCategorized.class);

    @Override
    public int getPreviewColumn(){
        return TableModelInterval.PREVIEW_COLUMN;
    }

    @Override
    public int getKeyColumn(){
        return TableModelInterval.KEY_COLUMN;
    }

    @Override
    public Double getNotUsedKey(){
        AbstractCategorizedLegend leg = (AbstractCategorizedLegend) getLegend();
        return leg.getNotUsedKey((Double)leg.keySet().last());
    }

    @Override
    public Class getPreviewClass() {
        return Double.class;
    }
}

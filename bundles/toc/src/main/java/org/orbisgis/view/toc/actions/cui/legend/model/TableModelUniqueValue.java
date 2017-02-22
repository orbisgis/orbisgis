/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Table model for recoded lines.
 * @author Alexis Guéganno
 */
public abstract class TableModelUniqueValue<U extends LineParameters> extends AbstractLegendTableModel<String, U>{

    private AbstractRecodedLegend<U> recodedLine;
    public final static int KEY_COLUMN = 1;
    public final static int PREVIEW_COLUMN = 0;
    private final static I18n I18N = I18nFactory.getI18n(TableModelInterval.class);

    /**
     * Builds a new {@code TableModelUniqueValue} linker to {@code rl}.
     * @param rl The input unique value analysis.
     */
    public TableModelUniqueValue(AbstractRecodedLegend<U> rl){
        recodedLine = rl;
    }

    @Override
    public MappedLegend<String, U> getMappedLegend() {
        return recodedLine;
    }

    @Override
    public String getColumnName(int col){
        if(col == KEY_COLUMN){
            return I18N.tr("Value");
        } else if(col == PREVIEW_COLUMN){
            return I18N.tr("Preview");
        }
        throw new IndexOutOfBoundsException("We did not found a column at index "+col+" !");
    }

}

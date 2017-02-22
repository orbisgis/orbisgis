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
package org.orbisgis.view.toc.actions.cui.legend.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.panels.TablePanel;
import org.orbisgis.view.toc.actions.cui.legend.panels.Util;

/**
 * Root class for (stroke and symbol) UOM combo boxes.
 *
 * @author Adam Gouge
 */
public abstract class UOMComboBox<K, U extends LineParameters> extends PreviewComboBox<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UOMComboBox.class);

    protected TablePanel<K, U> tablePanel;

    /**
     * Constructor
     *
     * @param legend     Legend
     * @param preview    Preview
     * @param tablePanel Table Panel
     */
    public UOMComboBox(Legend legend,
                       CanvasSE preview,
                       TablePanel<K, U> tablePanel) {
        super(Uom.getStrings(), legend, preview);
        this.tablePanel = tablePanel;
    }

    @Override
    protected final void updatePreview() {
        updateAttributes();
        updatePreviews();
    }

    /**
     * Update any necessary attributes before updating the preview.
     */
    protected abstract void updateAttributes();

    /**
     * Update the preview(s) (plural if classification).
     */
    private void updatePreviews() {
        if (legend instanceof MappedLegend) {
            preview.setSymbol(Util.getFallbackSymbolizer((MappedLegend) legend));
            if (tablePanel != null) {
                tablePanel.updateTable();
            } else {
                LOGGER.error("Can't update table panel because it is null.");
            }
        } else {
            preview.imageChanged();
        }
    }
}

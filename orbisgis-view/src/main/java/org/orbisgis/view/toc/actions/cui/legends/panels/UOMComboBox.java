/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Root class for (stroke and symbol) UOM combo boxes.
 */
public abstract class UOMComboBox<K, U extends LineParameters> extends AbsComboBox {

    protected TablePanel<K, U> tablePanel;

    public UOMComboBox(Legend legend,
                       CanvasSE preview,
                       TablePanel<K, U> tablePanel) {
        super(Uom.getLocalizedStrings(), legend, preview);
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
            Util.updatePreview((MappedLegend) legend, preview, tablePanel);
        } else {
            preview.imageChanged();
        }
    }
}

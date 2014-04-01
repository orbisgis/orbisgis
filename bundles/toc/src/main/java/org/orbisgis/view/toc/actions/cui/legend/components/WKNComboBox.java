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
package org.orbisgis.view.toc.actions.cui.legend.components;

import org.orbisgis.coremap.renderer.se.graphic.WellKnownName;
import org.orbisgis.legend.thematic.ConstantFormPoint;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Combo box for the symbol's well-known name.
 *
 * @author Adam Gouge
 */
public class WKNComboBox extends PreviewComboBox<ContainerItem> {

    /**
     * Constructor
     *
     * @param legend  Legend
     * @param preview Preview
     */
    public WKNComboBox(ConstantFormPoint legend,
                       CanvasSE preview) {
        super(getItems(), legend, preview);
        WellKnownName value = WellKnownName.fromString(legend.getWellKnownName());
        setSelectedItem(new ContainerItem<WellKnownName>(value, value.toLocalizedString()));
    }

    @Override
    protected void updatePreview() {
        ContainerItem<WellKnownName> si = getSelectedItem();
        ((ConstantFormPoint) legend).setWellKnownName(si.getKey().toString());
        preview.imageChanged();
    }

    /**
     * Builds the allowed items for this WKNComboBox.
     * @return The items stored in ContainerItem instances.
     */
    private static ContainerItem[] getItems(){
        WellKnownName[] values = WellKnownName.values();
        ContainerItem[] cips= new ContainerItem[values.length];
        for(int i = 0; i<values.length; i++){
            cips[i] = new ContainerItem<WellKnownName>(values[i], values[i].toLocalizedString());
        }
        return cips;
    }
}

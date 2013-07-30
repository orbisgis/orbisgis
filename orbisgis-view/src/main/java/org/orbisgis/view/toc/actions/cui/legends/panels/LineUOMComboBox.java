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
import org.orbisgis.legend.thematic.*;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class LineUOMComboBox<K, U extends LineParameters> extends UOMComboBox<K, U> {

    public LineUOMComboBox(StrokeUom legend,
                           CanvasSE preview,
                           TablePanel<K, U> tablePanel) {
        super((Legend) legend, preview, tablePanel);
        setSelectedItem(legend.getStrokeUom());
    }

    public LineUOMComboBox(StrokeUom legend,
                           CanvasSE preview) {
        this(legend, preview, null);
    }

    @Override
    protected void updateAttributes() {
        ((SymbolizerLegend) legend).setStrokeUom(
                Uom.fromString((String) getSelectedItem()));
    }
}

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
package org.orbisgis.view.toc.actions.cui.legends.ui;

import org.orbisgis.legend.Legend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;

/**
 * Root class for non-classification UIs. (That is, everything other than Value
 * and Interval Classifications).
 *
 * @author Adam Gouge
 */
public abstract class PnlNonClassification extends AbstractFieldPanel {

    private String id;
    private CanvasSE preview;

    public static final String BORDER_SETTINGS = I18n.marktr("Border settings");
    public static final String FILL_SETTINGS = I18n.marktr("Fill settings");
    public static final String MARK_SETTINGS = I18n.marktr("Mark settings");

    // *********************** AbstractFieldPanel *************************
    @Override
    public void initPreview() {
        Legend leg = getLegend();
        if (leg != null) {
            preview = new CanvasSE(leg.getSymbolizer());
            preview.imageChanged();
        }
    }

    @Override
    public CanvasSE getPreview() {
        if (preview == null) {
            initPreview();
        }
        return preview;
    }

    // ************************ ISELegendPanel ***********************
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}

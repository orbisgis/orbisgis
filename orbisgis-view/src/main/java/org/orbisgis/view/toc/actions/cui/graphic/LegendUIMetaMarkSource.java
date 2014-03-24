/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.graphic;

import javax.swing.Icon;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.WellKnownName;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.parameter.string.LegendUIMetaStringPanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public class LegendUIMetaMarkSource extends LegendUIAbstractMetaPanel {

    private LegendUIComponent comp;
    private MarkGraphic mark;
    private final Class[] classes = {WellKnownName.class};

    public LegendUIMetaMarkSource(LegendUIController ctrl, LegendUIComponent parent, MarkGraphic mark) {
        super(null, ctrl, parent, 0, false);
        this.mark = mark;


        comp = null;
        if (mark.getWkn() != null) {
            comp = getCompForClass(WellKnownName.class);
        } else if (mark.getOnlineResource() != null) {
            comp = getCompForClass(OnlineResource.class);
        }
    }

    @Override
    protected final LegendUIComponent getCompForClass(Class newClass) {
        if (newClass == WellKnownName.class) {
            LegendUIMetaStringPanel wkn = new LegendUIMetaStringPanel("WKN", controller, this, mark.getWkn(), false) {

                @Override
                public void stringChanged(StringParameter newString) {
                    mark.setWkn(newString);
                }
            };
            wkn.init();
            return wkn;
        } else {
            return null;
        }
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    public void init() {
        init(classes, comp);
    }

    @Override
    protected void switchTo(LegendUIComponent newActiveComp) {
        //this.mark TODO
    }

    @Override
    public Class getEditedClass() {
        if (mark.getWkn() != null) {
            return WellKnownName.class;
        } else {
            return null;
        }
    }
}

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
package org.orbisgis.view.toc.actions.cui.fill;

import java.awt.BorderLayout;
import javax.swing.Icon;

import org.orbisgis.coremap.renderer.se.fill.DotMapFill;
import org.orbisgis.coremap.renderer.se.fill.Fill;

import org.orbisgis.coremap.renderer.se.graphic.GraphicCollection;

import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;

import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.graphic.LegendUICompositeGraphicPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;

import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIDotMapFillPanel extends LegendUIComponent implements LegendUIFillComponent {

    private DotMapFill dFill;

    private LegendUICompositeGraphicPanel graphic;
    private LegendUIMetaRealPanel perMark;
    private LegendUIMetaRealPanel total;
    
    public LegendUIDotMapFillPanel(LegendUIController ctrl, LegendUIComponent parent, final DotMapFill dFill, boolean isNullable) {
        super("Dot-Map fill", ctrl, parent, 0, isNullable);
        this.dFill = dFill;

        if (dFill.getGraphicCollection() == null){
            dFill.setGraphicCollection(new GraphicCollection());
        }

        graphic = new LegendUICompositeGraphicPanel(ctrl, this, dFill.getGraphicCollection());

        perMark = new LegendUIMetaRealPanel("Per Mark", controller, this, dFill.getQantityPerMark(), false) {

            @Override
            public void realChanged(RealParameter newReal) {
                dFill.setQuantityPerMark(newReal);
            }
        };
        perMark.init();

        total = new LegendUIMetaRealPanel("Total", controller, this, dFill.getTotalQantity(), false) {

            @Override
            public void realChanged(RealParameter newReal) {
                dFill.setTotalQuantity(newReal);
            }
        };
        total.init();
    }

    @Override
    public Fill getFill() {
        return dFill;
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        LegendUIAbstractPanel header = new LegendUIAbstractPanel(controller);

        header.add(perMark, BorderLayout.WEST);
        header.add(total, BorderLayout.EAST);

        editor.add(header, BorderLayout.NORTH);
        editor.add(graphic, BorderLayout.SOUTH);
    }

    @Override
    public Class getEditedClass() {
        return DotMapFill.class;
    }
}

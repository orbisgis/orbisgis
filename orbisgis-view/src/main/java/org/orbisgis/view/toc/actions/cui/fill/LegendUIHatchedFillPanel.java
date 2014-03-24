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
package org.orbisgis.view.toc.actions.cui.fill;

import java.awt.BorderLayout;
import javax.swing.Icon;
import org.orbisgis.core.renderer.se.fill.DensityFill;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.HatchedFill;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.RadioSwitch;
import org.orbisgis.view.toc.actions.cui.graphic.LegendUICompositeGraphicPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.toc.actions.cui.stroke.LegendUIMetaStrokePanel;
import org.orbisgis.view.toc.actions.cui.stroke.LegendUIPenStrokePanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIHatchedFillPanel extends LegendUIComponent implements LegendUIFillComponent {

    private HatchedFill hFill;
    private LegendUIMetaStrokePanel stroke;
    private LegendUIMetaRealPanel angle;
    private LegendUIMetaRealPanel distance;
    private LegendUIMetaRealPanel offset;
    
    public LegendUIHatchedFillPanel(LegendUIController ctrl, LegendUIComponent parent, final HatchedFill hFill, boolean isNullable) {
        super("Hatched fill", ctrl, parent, 0, isNullable);
        this.hFill = hFill;

        stroke = new LegendUIMetaStrokePanel(controller, this, hFill, false);
        stroke.init();

        angle = new LegendUIMetaRealPanel("Angle", controller, this, hFill.getAngle(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                hFill.setAngle(newReal);
            }
        };
        angle.init();

        distance = new LegendUIMetaRealPanel("Distance", controller, this, hFill.getDistance(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                hFill.setDistance(newReal);
            }
        };
        distance.init();

        offset = new LegendUIMetaRealPanel("Offset", controller, this, hFill.getOffset(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                hFill.setOffset(newReal);
            }
        };
        offset.init();
    }

    @Override
    public Fill getFill() {
        return hFill;
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        LegendUIAbstractPanel header = new LegendUIAbstractPanel(controller);

        header.add(angle, BorderLayout.WEST);
        header.add(distance, BorderLayout.CENTER);
        header.add(offset, BorderLayout.EAST);

        editor.add(header, BorderLayout.NORTH);
        editor.add(stroke, BorderLayout.SOUTH);
    }

    @Override
    public Class getEditedClass() {
        return HatchedFill.class;
    }
}

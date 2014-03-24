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

import java.awt.BorderLayout;
import javax.swing.Icon;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.PointTextGraphic;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.UomInput;
import org.orbisgis.view.toc.actions.cui.label.LegendUIPointLabelPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.icons.OrbisGISIcon;


/**
 *
 * @author Maxence Laurent
 */
public class LegendUIPointTextGraphicPanel extends LegendUIComponent implements LegendUIGraphicComponent {
    private PointTextGraphic textGraphic;

    private UomInput uom;
    private LegendUIMetaRealPanel x;
    private LegendUIMetaRealPanel y;
    private LegendUIPointLabelPanel label;

    private LegendUIAbstractPanel header;

    public LegendUIPointTextGraphicPanel(LegendUIController controller, LegendUIComponent parent, PointTextGraphic ptg){
        super("Text Graphic", controller, parent, 0, false);
        this.textGraphic = ptg;

        uom = new UomInput(ptg);

        x = new LegendUIMetaRealPanel("X", controller, this, textGraphic.getX(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                textGraphic.setX(newReal);
            }
        };
        x.init();

        y = new LegendUIMetaRealPanel("Y", controller, this, textGraphic.getY(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                textGraphic.setY(newReal);
            }
        };
        y.init();

        label = new LegendUIPointLabelPanel(controller, this, textGraphic.getPointLabel(), false);

        header = new LegendUIAbstractPanel(controller);
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        header.removeAll();
        header.add(x, BorderLayout.WEST);
        header.add(y, BorderLayout.CENTER);
        header.add(uom, BorderLayout.EAST);

        editor.add(header, BorderLayout.NORTH);
        editor.add(label, BorderLayout.SOUTH);
    }

    @Override
    protected void turnOff() {
        throw new UnsupportedOperationException("Unreachable code.");
    }

    @Override
    protected void turnOn() {
        throw new UnsupportedOperationException("Unreachable code.");
    }

    @Override
    public Class getEditedClass() {
        return PointTextGraphic.class;
    }

    @Override
    public Graphic getGraphic() {
        return textGraphic;
    }

}

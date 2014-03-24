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
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.transform.Matrix;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.icons.OrbisGISIcon;


/**
 *
 * @author Maxence Laurent
 */
class LegendUIMatrixPanel extends LegendUIComponent{

    private Matrix matrix;
    private LegendUIMetaRealPanel a;
    private LegendUIMetaRealPanel b;
    private LegendUIMetaRealPanel c;
    private LegendUIMetaRealPanel d;
    private LegendUIMetaRealPanel e;
    private LegendUIMetaRealPanel f;

    private LegendUIAbstractPanel line1;
    private LegendUIAbstractPanel line2;

    public LegendUIMatrixPanel(LegendUIController controller, LegendUITransformPanel parent, Matrix m) {
        super("", controller, parent, 0, false);
        this.matrix = m;

        a = new LegendUIMetaRealPanel("A", controller, this, matrix.getA(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                matrix.setA(newReal);
            }
        };
        a.init();

        b = new LegendUIMetaRealPanel("b", controller, this, matrix.getB(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                matrix.setB(newReal);
            }
        };
        b.init();

        c = new LegendUIMetaRealPanel("c", controller, this, matrix.getC(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                matrix.setC(newReal);
            }
        };
        c.init();

        d = new LegendUIMetaRealPanel("d", controller, this, matrix.getD(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                matrix.setD(newReal);
            }
        };
        d.init();

        e = new LegendUIMetaRealPanel("e", controller, this, matrix.getE(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                matrix.setE(newReal);
            }
        };
        e.init();

        f = new LegendUIMetaRealPanel("f", controller, this, matrix.getF(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                matrix.setF(newReal);
            }
        };
        f.init();

        line1 = new LegendUIAbstractPanel(controller);
        line2 = new LegendUIAbstractPanel(controller);
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        line1.add(a, BorderLayout.WEST);
        line1.add(b, BorderLayout.CENTER);
        line1.add(c, BorderLayout.EAST);

        line2.add(d, BorderLayout.WEST);
        line2.add(e, BorderLayout.CENTER);
        line2.add(f, BorderLayout.EAST);
    }

    @Override
    protected void turnOff() {
    }

    @Override
    protected void turnOn() {
    }

    @Override
    public Class getEditedClass() {
        return Matrix.class;
    }
}

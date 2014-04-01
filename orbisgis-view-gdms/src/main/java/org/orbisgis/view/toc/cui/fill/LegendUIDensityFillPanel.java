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
import org.orbisgis.coremap.renderer.se.fill.DensityFill;
import org.orbisgis.coremap.renderer.se.fill.Fill;
import org.orbisgis.coremap.renderer.se.graphic.GraphicCollection;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.RadioSwitch;
import org.orbisgis.view.toc.actions.cui.graphic.LegendUICompositeGraphicPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.toc.actions.cui.stroke.LegendUIPenStrokePanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIDensityFillPanel extends LegendUIComponent implements LegendUIFillComponent {

    private DensityFill dFill;
    private LegendUIPenStrokePanel pStroke;
    private LegendUICompositeGraphicPanel graphic;
    private LegendUIMetaRealPanel orientation;
    private LegendUIMetaRealPanel percentage;
    
    private RadioSwitch radio;

    private GraphicCollection gCollec;
    private PenStroke pen;

    private int current;

    public LegendUIDensityFillPanel(LegendUIController ctrl, LegendUIComponent parent, final DensityFill dFill, boolean isNullable) {
        super("Density fill", ctrl, parent, 0, isNullable);
        this.dFill = dFill;

        pen = dFill.getHatches();

        if (pen == null) {
            pen = new PenStroke();
        }


        String[] choices = {"hatch", "mark"};
        if (dFill.getHatches() != null) {
            current = 0;
        } else {
            current = 1;
        }

        radio = new RadioSwitch(choices, current) {

            @Override
            protected void valueChanged(int choice) {
                current = choice;
                if (current == 0) {
                    // Switch to hatch
                    dFill.setHatches(pen);
                } else {
                    // Switch to mark
                    dFill.setGraphicCollection(gCollec);
                }
                controller.structureChanged(LegendUIDensityFillPanel.this);
            }
        };



        gCollec = dFill.getGraphicCollection();
        if (gCollec == null) {
            gCollec = new GraphicCollection();
        }

        pStroke = new LegendUIPenStrokePanel(controller, this, pen, false) {

            @Override
            protected void turnOff() {
                //dFill.setHatches(null);
            }

            @Override
            protected void turnOn() {
                //dFill.setHatches((PenStroke) this.getStroke());
            }
        };

        graphic = new LegendUICompositeGraphicPanel(ctrl, this, gCollec);

        orientation = new LegendUIMetaRealPanel("orientation", controller, this, dFill.getHatchesOrientation(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                dFill.setHatchesOrientation(newReal);
                //controller.structureChanged(this);
            }
        };
        orientation.init();

        percentage = new LegendUIMetaRealPanel("Percentage", controller, this, dFill.getPercentageCovered(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                dFill.setPercentageCovered(newReal);
                //controller.structureChanged(this);
            }
        };
        percentage.init();
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
        LegendUIAbstractPanel content = new LegendUIAbstractPanel(controller);

        header.add(radio, BorderLayout.WEST);
        header.add(percentage, BorderLayout.EAST);


        if (current == 0) {
            content.add(pStroke, BorderLayout.NORTH);
            content.add(orientation, BorderLayout.SOUTH);
        } else {
            content.add(graphic);
        }

        editor.add(header, BorderLayout.NORTH);
        editor.add(content, BorderLayout.SOUTH);
    }

    @Override
    public Class getEditedClass() {
        return DensityFill.class;
    }
}

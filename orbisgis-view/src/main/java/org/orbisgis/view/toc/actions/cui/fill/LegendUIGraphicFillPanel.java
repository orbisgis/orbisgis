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
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.UomInput;
import org.orbisgis.view.toc.actions.cui.graphic.LegendUICompositeGraphicPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIGraphicFillPanel extends LegendUIComponent implements LegendUIFillComponent {

	private GraphicFill gFill;

    private LegendUIMetaRealPanel gapX;
	private LegendUIMetaRealPanel gapY;
    private UomInput uomInput;
    private LegendUICompositeGraphicPanel graphic;


	public LegendUIGraphicFillPanel(LegendUIController controller, LegendUIComponent parent, final GraphicFill gFill, boolean isNullable) {
		super("graphic fill", controller, parent, 0, isNullable);
		this.gFill = gFill;

        this.gapX = new LegendUIMetaRealPanel("X gap", controller, this, gFill.getGapX(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                gFill.setGapX(newReal);
            }
        };
        gapX.init();

        this.gapY = new LegendUIMetaRealPanel("Y gap", controller, this, gFill.getGapY(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                gFill.setGapY(newReal);
            }
        };
        gapY.init();

        uomInput = new UomInput(gFill);

        // Make sure graphic collection exists
        if (gFill.getGraphic() == null){
            gFill.setGraphic(new GraphicCollection());
        }

        graphic = new LegendUICompositeGraphicPanel(controller, this, gFill.getGraphic());
	}

	@Override
	public Fill getFill() {
		return gFill;
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.getIcon("palette");
	}

	@Override
	protected void mountComponent() {
        LegendUIAbstractPanel header = new LegendUIAbstractPanel(controller);
        header.add(gapX, BorderLayout.WEST);
        header.add(gapY, BorderLayout.CENTER);
        header.add(uomInput, BorderLayout.EAST);

        editor.add(header, BorderLayout.NORTH);
        editor.add(graphic, BorderLayout.SOUTH);
	}

	@Override
	public Class getEditedClass() {
		return GraphicFill.class;
	}


}

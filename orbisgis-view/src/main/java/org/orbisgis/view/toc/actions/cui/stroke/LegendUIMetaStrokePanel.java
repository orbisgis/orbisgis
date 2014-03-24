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
package org.orbisgis.view.toc.actions.cui.stroke;

import javax.swing.Icon;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.stroke.CompoundStroke;
import org.orbisgis.core.renderer.se.stroke.GraphicStroke;
import org.orbisgis.core.renderer.se.stroke.TextStroke;

/**
 *
 * @author Maxence Laurent
 */
public class LegendUIMetaStrokePanel extends LegendUIAbstractMetaPanel {

	private StrokeNode sNode;
	private LegendUIComponent comp;

	private final Class[] classes = {PenStroke.class, GraphicStroke.class, TextStroke.class, CompoundStroke.class};

	public LegendUIMetaStrokePanel(LegendUIController controller, LegendUIComponent parent, StrokeNode strokeNode, boolean isNullable) {
		super(null, controller, parent, 0, isNullable);

		this.sNode = strokeNode;

		comp = null;
		if (sNode.getStroke() != null) {
			comp = getCompForClass(sNode.getStroke().getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {
		if (newClass == PenStroke.class) {
			PenStroke pStroke;
			if (sNode.getStroke() instanceof PenStroke) {
				pStroke = (PenStroke) sNode.getStroke();
			} else {
				pStroke = new PenStroke();
			}

			return new LegendUIPenStrokePanel(controller, parent, pStroke, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else if (newClass == GraphicStroke.class){
			GraphicStroke gStroke;

			if (sNode.getStroke() instanceof GraphicStroke){
				gStroke = (GraphicStroke) sNode.getStroke();
			} else {
				gStroke = new GraphicStroke();
			}

			return new LegendUIGraphicStrokePanel(controller, parent, gStroke, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};

        } else if (newClass == TextStroke.class){
            TextStroke tStroke;
            if (sNode.getStroke() instanceof TextStroke){
                tStroke = (TextStroke) sNode.getStroke();
            } else {
                tStroke = new TextStroke();
            }

            return new LegendUITextStrokePanel(controller, this, tStroke, false) {

                @Override
                protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
                }

                @Override
                protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");

                }
            };
        } else if (newClass == CompoundStroke.class) {
            CompoundStroke cStroke;
            if (sNode.getStroke() instanceof CompoundStroke){
                cStroke = (CompoundStroke) sNode.getStroke();
            } else {
                cStroke = new CompoundStroke();
            }

            return new LegendUICompoundStrokePanel(controller, this, cStroke, false) {

                @Override
                protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
                }

                @Override
                protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
                }
            };
		} else {
			return null;
		}
	}

	@Override
	public void init() {
		init(classes, comp);
	}

	@Override
	public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
	}

	@Override
	protected void switchTo(LegendUIComponent comp) {
		if (comp != null) {
			Stroke s = ((LegendUIStrokeComponent) comp).getStroke();
			sNode.setStroke(s);
		} else {
			sNode.setStroke(null);
		}
	}

	@Override
	public Class getEditedClass() {
		return sNode.getStroke().getClass();
	}
}

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

//import javax.swing.Icon;
//import org.orbisgis.view.icons.OrbisGISIcon;
//import org.orbisgis.core.renderer.se.graphic.Graphic;
//import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
//import org.orbisgis.view.toc.actions.cui.LegendUIAbstractMetaPanel;
//import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
//import org.orbisgis.view.toc.actions.cui.LegendUIController;

/**
 *
 * @author Maxence Laurent
 */
/*
public abstract class LegendUIMetaGraphicPanel extends LegendUIAbstractMetaPanel {

	private Graphic graphic;
	private LegendUIComponent comp;
	private final Class[] classes = {MarkGraphic.class};

	public LegendUIMetaGraphicPanel(String name, LegendUIController controller, LegendUIComponent parent, Graphic g, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);

		this.graphic = g;

		comp = null;
		if (graphic != null) {
			comp = getCompForClass(graphic.getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {
		if (newClass == MarkGraphic.class) {
			MarkGraphic mg;
			if (graphic instanceof MarkGraphic) {
				mg = (MarkGraphic) graphic;
			} else {
				mg = new MarkGraphic();
			}
			return new LegendUIMarkGraphicPanel(controller, this, mg);
		} else {
			return null;
		}
	}

	@Override
	public void init() {
		init(classes, comp);
	}

	@Override
	protected void switchTo(LegendUIComponent comp) {
		if (comp != null) {
			this.graphic = ((LegendUIGraphicComponent) comp).getGraphic();
			this.graphicChanged(graphic);
		} else {
			this.graphicChanged(null);
		}
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.getIcon("palette");
	}

	public abstract void graphicChanged(Graphic newGraphic);

	@Override
	public Class getEditedClass() {
		return graphic.getClass();
	}
}
 */

/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.label;

import javax.swing.Icon;
import org.orbisgis.core.renderer.se.label.ExclusionRadius;
import org.orbisgis.core.renderer.se.label.ExclusionRectangle;
import org.orbisgis.core.renderer.se.label.ExclusionZone;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 * Meta-Panel for fill edition
 * This panel will provide the ability to select fill type
 *
 * @author maxence
 */
public abstract class LegendUIMetaExclusionZonePanel extends LegendUIAbstractMetaPanel {

	private ExclusionZone zone;
	private LegendUIComponent comp;


	private final Class[] classes = {ExclusionRadius.class, ExclusionRectangle.class};

	public LegendUIMetaExclusionZonePanel(LegendUIController controller, LegendUIComponent parent, ExclusionZone zone, boolean isNullable) {
		super("Exclusion zone", controller, parent, 0, isNullable);

        this.zone = zone;

		comp = null;
		if (zone != null) {
			comp = getCompForClass(zone.getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {
		if (newClass == ExclusionRadius.class) {
			ExclusionRadius radius;
			if (zone instanceof ExclusionRadius) {
                radius = (ExclusionRadius) zone;
			} else {
                radius = new ExclusionRadius();
			}
			return new LegendUIExclusionRadiusPanel("point Label", controller, this, radius, false) {

                // Unreachable !
				@Override
				protected void turnOff() {
				}

				@Override
				protected void turnOn() {
				}
			};
		} else if (newClass == ExclusionRectangle.class){
			ExclusionRectangle rect;
			if (zone instanceof ExclusionRectangle) {
                rect = (ExclusionRectangle) zone;
			} else {
                rect = new ExclusionRectangle();
			}
			return new LegendUIExclusionRectanglePanel("point Label", controller, this, rect, false) {

                // Unreachable !
				@Override
				protected void turnOff() {
				}

				@Override
				protected void turnOn() {
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
		return OrbisGISIcon.PALETTE;
	}

	@Override
	protected void switchTo(LegendUIComponent comp) {
		if (comp != null){
			this.zone = ((LegendUIExclusionZoneComponent) comp).getExclusionZone();
            zoneChanged(zone);
		} else {
            zoneChanged(null);
		}
	}

	@Override
	public Class getEditedClass() {
		return zone.getClass();
	}

    public abstract void zoneChanged(ExclusionZone zone);
}

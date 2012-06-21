/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.geocognition;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeRenderer;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.ElementRenderer;

public class GeocognitionRenderer extends AbstractTreeRenderer {
	private ElementRenderer[] renderers = new ElementRenderer[0];

	public void setRenderers(ElementRenderer[] renderers) {
		this.renderers = renderers;
	}

	@Override
	protected void updateIconAndTooltip(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		icon = null;
		tooltip = null;

		GeocognitionElement element = (GeocognitionElement) value;
		String typeId = element.getTypeId();
		Map<String, String> properties = element.getProperties();

		for (ElementRenderer renderer : renderers) {
			try {
				Icon elementIcon = renderer.getIcon(typeId, properties);
				if (elementIcon != null) {
					icon = elementIcon;
					tooltip = renderer.getTooltip(element);
					break;
				}
			} catch (RuntimeException e) {
				Services.getErrorManager().error("bug in tree renderer", e);
			}
		}
	}
}

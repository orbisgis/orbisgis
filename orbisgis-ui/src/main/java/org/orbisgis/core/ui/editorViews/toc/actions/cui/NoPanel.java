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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Component;

import javax.swing.JLabel;

import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.utils.I18N;

public class NoPanel implements ILegendPanel {

	private Legend legend;

	public NoPanel(Legend legend) {
		this.legend = legend;
	}

	public boolean acceptsGeometryType(int geometryType) {
		return true;
	}

	public Component getComponent() {
		return new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.noPanel.noSuitableEditorLegend")); //$NON-NLS-1$
	}

	public Legend getLegend() {
		return legend;
	}

	public String getLegendTypeName() {
		throw new RuntimeException(I18N.getString("orbisgis.org.orbisgis.ui.toc.noPanel.bug")); //$NON-NLS-1$
	}

	public ILegendPanel newInstance() {
		throw new RuntimeException(I18N.getString("orbisgis.org.orbisgis.ui.toc.noPanel.bug")); //$NON-NLS-1$
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public void initialize(LegendContext lc) {

	}

	public String validateInput() {
		return null;
	}

}

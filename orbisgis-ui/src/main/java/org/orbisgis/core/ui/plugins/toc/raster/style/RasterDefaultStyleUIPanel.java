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
package org.orbisgis.core.ui.plugins.toc.raster.style;

import java.awt.Component;
import java.awt.image.ColorModel;
import java.net.URL;

import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.utils.I18N;

public class RasterDefaultStyleUIPanel extends AbstractUIPanel implements
		UIPanel {
	private RasterDefaultStylePanel rasterDefaultStylePanel;

	public RasterDefaultStyleUIPanel(final RasterLegend legend,
			ColorModel defaultColorModel) {
		rasterDefaultStylePanel = new RasterDefaultStylePanel(legend,
				defaultColorModel);
	}

	public Component getComponent() {
		return rasterDefaultStylePanel;
	}

	public URL getIconURL() {
		return IconLoader.getIconUrl("palette.png"); //$NON-NLS-1$
	}

	public String getTitle() {
		return I18N.getString("orbisgis.org.orbisgis.ui.rasterDefaultStyleUIPanel.rasterStyle"); //$NON-NLS-1$
	}

	public String initialize() {
		return null;
	}

	public String getInfoText() {
		return I18N.getString("orbisgis.org.orbisgis.ui.rasterDefaultStyleUIPanel.editRasterStyle"); //$NON-NLS-1$
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		return null;
	}

	public ColorModel getColorModel() {
		return rasterDefaultStylePanel.getColorModel();
	}

	public float getOpacity() {
		return rasterDefaultStylePanel.getOpacity();
	}
}
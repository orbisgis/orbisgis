/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoprocessing.editorViews.toc.actions.others;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.Orientations;
import org.grap.processing.operation.others.Shadows;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractRasterProcess;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class ShadowsValue extends AbstractRasterProcess {
	public final static Map<String, Orientations> orientations = new HashMap<String, Orientations>();
	static {
		orientations.put("North", Orientations.NORTH);
		orientations.put("North east", Orientations.NORTHEAST);
		orientations.put("East", Orientations.EAST);
		orientations.put("South east", Orientations.SOUTHEAST);
		orientations.put("South", Orientations.SOUTH);
		orientations.put("South west", Orientations.SOUTHWEST);
		orientations.put("West", Orientations.WEST);
		orientations.put("North west", Orientations.NORTHWEST);
	}

	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, IOException {
		final Orientations orientation = getOrientation();
		if (null != orientation) {
			return geoRasterSrc.doOperation(new Shadows(orientation));
		}

		return null;
	}

	private Orientations getOrientation() {
		final MultiInputPanel mip = new MultiInputPanel("Choose an orientation");
		mip.addInput("orientation", "Orientation", null, new ComboBoxChoice(
				orientations.keySet().toArray(new String[0])));
		if (UIFactory.showDialog(mip)) {
			return orientations.get(mip.getInput("orientation"));
		} else {
			return null;
		}
	}
}
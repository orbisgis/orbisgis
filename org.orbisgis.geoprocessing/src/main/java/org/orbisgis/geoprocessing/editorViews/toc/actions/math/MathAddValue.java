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
package org.orbisgis.geoprocessing.editorViews.toc.actions.math;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.math.AddValueOperation;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.AbstractRasterProcess;
import org.orbisgis.geoprocessing.editorViews.toc.actions.utilities.NewAbstractRasterProcess;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.IntType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class MathAddValue extends AbstractRasterProcess {
	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, IOException {
		final Integer valueToAdd = getValueToAdd();
		if (null != valueToAdd) {
			return geoRasterSrc.doOperation(new AddValueOperation(valueToAdd));
		}

		return null;
	}

	private Integer getValueToAdd() {
		final MultiInputPanel mip = new MultiInputPanel(
				"org.orbisgis.rasterProcessing.MathAddValue",
				"AddValue initialization", false);
		mip.addInput("AddValue", "Value to add", "0", new IntType());

		if (UIFactory.showDialog(mip)) {
			return new Integer(mip.getInput("AddValue"));
		} else {
			return null;
		}
	}
}
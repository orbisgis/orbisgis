/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.processing.editorViews.toc.actions.terrainAnalysis.hydrology;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.FillSinks;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.processing.editorViews.toc.actions.utilities.AbstractGray16And32Process;
import org.sif.UIFactory;
import org.sif.multiInputPanel.DoubleType;
import org.sif.multiInputPanel.MultiInputPanel;

public class ProcessFillSinks extends AbstractGray16And32Process implements
		ILayerAction {
	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, IOException {
		Double minSlope = getMinSlope();
		if (null != minSlope) {
			geoRasterSrc.open();
			Operation fillSinks = new FillSinks(minSlope);
			return geoRasterSrc.doOperation(fillSinks);
		}
		return null;
	}

	private Double getMinSlope() {
		final MultiInputPanel mip = new MultiInputPanel(
				"Fill sinks initialization");
		mip.addInput("MinSlope", "Min slope value", "0.01", new DoubleType(5));
		mip.addValidationExpression("MinSlope > 0",
				"Fill sinks must be greater than 0 !");

		if (UIFactory.showDialog(mip)) {
			return new Double(mip.getInput("MinSlope"));
		} else {
			return null;
		}
	}
}
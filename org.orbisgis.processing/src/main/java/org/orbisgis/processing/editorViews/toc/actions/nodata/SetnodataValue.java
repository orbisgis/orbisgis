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
package org.orbisgis.processing.editorViews.toc.actions.nodata;

import java.io.IOException;

import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.orbisgis.Services;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.sif.UIFactory;
import org.sif.multiInputPanel.DoubleType;
import org.sif.multiInputPanel.MultiInputPanel;

public class SetnodataValue implements
		org.orbisgis.editorViews.toc.action.ILayerAction {

	

	public boolean accepts(ILayer layer) {
		try {
			return layer.isRaster();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {

		
		try {
			GeoRaster geoRasterSrc = layer.getRaster();
		
		
		final MultiInputPanel mip = new MultiInputPanel("Set nodatavalue");
		mip.addInput("minvalue", "Min value", new Float(geoRasterSrc.getMin())
				.toString(), new DoubleType(10));
		mip.addInput("maxvalue", "Max value", new Float(geoRasterSrc.getMax())
				.toString(), new DoubleType(10));
		mip.addInput("nodatavalue", "Nodata value", new Float(geoRasterSrc
				.getNoDataValue()).toString(), new DoubleType(10));
		
		
		if (UIFactory.showDialog(mip)) {
			//final float min = new Float(mip.getInput("minvalue"));
			//final float max = new Float(mip.getInput("maxvalue"));
			
			final float nodata = new Float(mip.getInput("nodatavalue"));

			//TODO Fernando
			//mip.addValidationExpression("nodatavalue >= " + min +"and nodatavalue <="+ max, "Nodata value must be in the range min - max");

			geoRasterSrc.setNodataValue((float) nodata);
			

		}
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the raster from the layer ", e);
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + layer.getName(), e);
		}

	}
}
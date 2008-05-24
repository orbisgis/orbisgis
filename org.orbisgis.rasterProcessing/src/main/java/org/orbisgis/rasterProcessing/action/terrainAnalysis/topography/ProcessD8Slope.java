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
package org.orbisgis.rasterProcessing.action.terrainAnalysis.topography;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpSlope;
import org.grap.processing.operation.hydrology.D8OpSlopeInDegrees;
import org.grap.processing.operation.hydrology.D8OpSlopeInRadians;
import org.orbisgis.rasterProcessing.action.utilities.AbstractGray16And32Process;
import org.sif.UIFactory;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.MultiInputPanel;

public class ProcessD8Slope extends AbstractGray16And32Process {
	private MultiInputPanel mip;

	
	@Override
	protected GeoRaster evaluateResult(GeoRaster geoRasterSrc)
			throws OperationException, IOException {
		init();
		
		GeoRaster slopes = null;
		if (UIFactory.showDialog(mip)) {
			geoRasterSrc.open();

			
			String options = mip.getInput("unit");
			
			if (options.equals("radian")){
				//compute the slopes directions
				Operation slopesInRadians = new D8OpSlopeInRadians();
				slopes = geoRasterSrc.doOperation(slopesInRadians);
				
			}
			else if (options.equals("degree")){
				//				compute the slopes directions
				Operation slopesInDegrees = new D8OpSlopeInDegrees();
				slopes = geoRasterSrc.doOperation(slopesInDegrees);
			}
			else if  (options.equals("percent")){
				//compute the slopes directions
				Operation slopesInPercent = new D8OpSlope();
				slopes = geoRasterSrc.doOperation(slopesInPercent);
				slopes.getImagePlus().getProcessor().multiply(100);
				
			}
			
		}
		
		return slopes;
	}
	
	public void init() {
		
		mip = new MultiInputPanel("Calculate a slope grid (D8 method)");
		
		mip.addInput("unit", "slope unit", new ComboBoxChoice(new String[]{"radian", "degree", "percent"}));
		
		
	}
}
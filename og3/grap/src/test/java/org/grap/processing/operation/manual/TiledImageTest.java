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
package org.grap.processing.operation.manual;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.operation.Crop;
import org.grap.utilities.EnvelopeUtil;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LinearRing;

public class TiledImageTest {

	
	public static void main(String[] args) throws Exception {
		
		Long start =  System.currentTimeMillis();
		String src = "../../datas2tests/geotif/leHavre.tif";
		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);	
		
		geoRaster.open();
		
		Envelope env = geoRaster.getMetadata().getEnvelope();
			
		
		
		LinearRing[] tiles = buildRectangleTiled(env);
		GeoRaster result = null;
		
		for (int i = 0; i < tiles.length; i++) {
			
			Crop crop = new Crop((LinearRing) tiles[i]);
			result = geoRaster.doOperation(crop);
			result.save("../../datas2tests/tmp/tiled "+ i +".tif");

		}
		
		
		System.out.println(System.currentTimeMillis() - start );

	}

	
		private static LinearRing[] buildRectangleTiled(Envelope env) {
		
		double width = env.getWidth();
		double heigth = env.getHeight();
		
		LinearRing[] linearRings = new LinearRing[4];
		
		//xm, xM, ym, yM
		
		double x1 = env.getMinX() + (width/2);
		double y1 = env.getMinY() + (heigth/2);
		
		
		
		Envelope en1 = new Envelope(env.getMinX(), x1, y1, env.getMaxY() );	
		Envelope en2 = new Envelope(x1, env.getMaxX(), y1, env.getMaxY());
		Envelope en3 = new Envelope(env.getMinX(), x1, env.getMinY() , y1 );
		Envelope en4 = new Envelope(x1, env.getMaxX(), env.getMinY(), y1);
		
		linearRings[0] = (LinearRing) EnvelopeUtil.toGeometry(en1);
		linearRings[1] = (LinearRing) EnvelopeUtil.toGeometry(en2);
		linearRings[2] = (LinearRing) EnvelopeUtil.toGeometry(en3);
		linearRings[3] = (LinearRing) EnvelopeUtil.toGeometry(en4);
		
		
		return linearRings;
		
		
		
		
	}
}

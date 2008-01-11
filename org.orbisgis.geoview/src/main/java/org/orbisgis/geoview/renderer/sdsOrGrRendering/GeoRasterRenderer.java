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
package org.orbisgis.geoview.renderer.sdsOrGrRendering;

//import ij.LookUpTable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class GeoRasterRenderer {

	public GeoRasterRenderer() {
	}

	public void paint(final Graphics2D graphics, final GeoRaster geoRaster,
			final Envelope layerPixelEnvelope, final Style style)
			throws IOException, GeoreferencingException {
		graphics.setComposite(AlphaComposite.SrcOver);
		graphics.drawImage(geoRaster.getGrapImagePlus().getImage(),
				(int) layerPixelEnvelope.getMinX(), (int) layerPixelEnvelope
						.getMinY(), (int) layerPixelEnvelope.getWidth(),
				(int) layerPixelEnvelope.getHeight(), null);
	}
}
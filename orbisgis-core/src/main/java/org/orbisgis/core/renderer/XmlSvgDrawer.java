/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */



package org.orbisgis.core.renderer;

import java.awt.Graphics2D;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;

/**
 * XML-SVG as vectorial as possible !
 * @author maxence
 */
public class XmlSvgDrawer implements Drawer {

	@Override
	public void initEnv(MapTransform mt) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void startLayer(int layerLevel) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Graphics2D getGraphics2D() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void getPng() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void getJpeg() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getXMLSVG() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void drawPointSymbolizer(Feature feat, boolean selected) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void drawLineSymbolizer(Feature feat, boolean selected) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void drawAreaSymbolizer(Feature feat, boolean selected) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void drawTextSymbolizer(Feature feat, boolean selected) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void drawRasterSymbolizer(Feature feat, boolean selected) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}

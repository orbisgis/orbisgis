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
package org.orbisgis.core.geocognition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;

public class AbstractGeocognitionTest extends AbstractTest {
	protected Geocognition gc;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		super.registerDataManager();

		gc = new DefaultGeocognition();
		gc.addElementFactory(new GeocognitionSymbolFactory());
		gc.addElementFactory(new GeocognitionFunctionFactory());
		gc.addElementFactory(new GeocognitionCustomQueryFactory());
		gc.addElementFactory(new GeocognitionLegendFactory());
		gc.addElementFactory(new GeocognitionMapContextFactory());
	}

	protected void saveAndLoad() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos);
		gc.clear();
		gc.read(new ByteArrayInputStream(bos.toByteArray()));
	}

}

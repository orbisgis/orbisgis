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
package org.gdms.sql.indexes;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;

public class IndexManagementTest extends TestCase {

	private DataSourceFactory dsf;
	private IndexManager im;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		SourceManager sm = dsf.getSourceManager();
		sm.removeAll();
		sm.register("source", new File(BaseTest.internalData,
				"hedgerow.shp"));
		im = dsf.getIndexManager();
	}

	public void testDeleteIndex() throws Exception {
		testBuildSpatialIndexOnFirstFieldByDefault();
		String sql = "select DeleteIndex(the_geom) from source";
		dsf.executeSQL(sql);
		assertTrue(im.getIndex("source", "the_geom") == null);
	}

	public void testBuildSpatialIndexSpecifyingField() throws Exception {
		testBuildIndexSpecifyingField("BuildSpatialIndex", "the_geom");
	}

	public void testBuildAlphaIndexSpecifyingField() throws Exception {
		testBuildIndexSpecifyingField("BuildAlphaIndex", "gid");
	}

	public void testBuildSpatialIndexOnFirstFieldByDefault() throws Exception {
		testBuildIndexOnFirstFieldByDefault("BuildSpatialIndex", "the_geom");
	}

	public void testSpatialWrongParameters() throws Exception {
		testSpatialWrongParameters("BuildSpatialIndex", "the_geom", "gid");
	}

	public void testAlphaWrongParameters() throws Exception {
		testWrongParameters("BuildAlphaIndex", "gid", "the_geom");
	}

	public void testDeleteWrongParameters() throws Exception {
		testWrongParametersDelete("DeleteIndex", "gid");
	}

	private void testBuildIndexSpecifyingField(String indexCall, String field)
			throws Exception {
		String sql = "select " + indexCall + "(" + field + ") from source";
		dsf.executeSQL(sql);
		assertTrue(im.getIndex("source", field) != null);
	}

	private void testBuildIndexOnFirstFieldByDefault(String indexCall,
			String field) throws Exception {
		String sql = "select " + indexCall + "() from source";
		dsf.executeSQL(sql);
		assertTrue(im.getIndex("source", field) != null);
	}

	private void testSpatialWrongParameters(String indexCall, String field,
			String wrongField) throws Exception {
		try {
			testWrongParametersInSQL("select " + indexCall + "(" + "'" + field
					+ "'" + ") from source;");
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		testWrongParameters(indexCall, field, wrongField);
	}

	private void testWrongParameters(String indexCall, String field,
			String wrongField) throws Exception {
		try {
			testWrongParametersInSQL("select " + indexCall + "(" + wrongField
					+ ") from source;");
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		testWrongParametersDelete(indexCall, field);
	}

	private void testWrongParametersDelete(String indexCall, String field)
			throws Exception {
		try {
			testWrongParametersInSQL("select " + indexCall + "(" + field
					+ ") from source s1, source s2;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
		try {
			testWrongParametersInSQL("select " + indexCall + "();");
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	private void testWrongParametersInSQL(String sql) throws Exception {
		SQLProcessor pr = new SQLProcessor(dsf);
		pr.prepareInstruction(sql);
	}
}

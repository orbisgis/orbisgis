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
package org.gdms.sql.function.spatial.geometry.topology;

import java.util.Collection;
import java.util.List;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;
import org.gdms.driver.ReadAccess;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_ToLineNoder extends AbstractTableFunction {

    private static final Logger LOG = Logger.getLogger(ST_ToLineNoder.class);

        @Override
	public ReadAccess evaluate(SQLDataSourceFactory dsf, ReadAccess[] tables,
			Value[] values, ProgressMonitor pm) throws FunctionException {
            LOG.trace("Evaluating");
		try {
			final ReadAccess sds = tables[0];

                        final String spatialFieldName = values[0].toString();
                        final int spatialFieldIndex = sds.getMetadata().getFieldIndex(spatialFieldName);

			final LineNoder lineNoder = new LineNoder(sds, spatialFieldIndex);

			final Collection lines = lineNoder.getLines();
			final Geometry nodedGeom = lineNoder.getNodeLines((List) lines);
			final Collection<Geometry> nodedLines = LineNoder
					.toLines(nodedGeom);

			// build and populate the resulting driver
			final GenericObjectDriver driver = new GenericObjectDriver(
					getMetadata(null));

			int k = 0;
			final int rowCount = nodedLines.size();
                        pm.startTask("Writing", rowCount);
			for (Geometry geometry : nodedLines) {
				if (k >= 100 && k % 100 == 0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo(k);
					}
				}

				driver.addValues(new Value[] { ValueFactory.createValue(k),
						ValueFactory.createValue(geometry) });
				k++;
			}
                        pm.progressTo(rowCount);
                        pm.endTask();
			return driver.getTable("main");
		} catch (DriverException e) {
			throw new FunctionException(e);
		} catch (DriverLoadException e) {
			throw new FunctionException(e);
		}
	}

        @Override
	public String getDescription() {
		return "Build all intersection and convert the geometries into lines ";
	}

        @Override
	public String getSqlOrder() {
		return "select ST_ToLineNoder(the_geom) from myTable;";
	}

        @Override
	public String getName() {
		return "ST_ToLineNoder";
	}

        @Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });
	}

	@Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.GEOMETRY)
                        };
        }
}
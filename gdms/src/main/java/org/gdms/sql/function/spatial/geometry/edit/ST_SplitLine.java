/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.edit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import java.util.Iterator;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author ebocher
 */
public class ST_SplitLine extends AbstractTableFunction {

        private DiskBufferDriver diskBufferDriver;

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                try {
                        DataSet dataSet = tables[0];
                        Metadata metadata1 = dataSet.getMetadata();
                        diskBufferDriver = new DiskBufferDriver(dsf, metadata1);
                        int geomFieldindex = MetadataUtilities.getGeometryFieldIndex(metadata1);
                        if (geomFieldindex != -1) {
                                int dim = MetadataUtilities.getGeometryDimension(metadata1, geomFieldindex);
                                if (dim == 1) {
                                        String geomField = metadata1.getFieldName(geomFieldindex);
                                        if (!dsf.getIndexManager().isIndexed(dataSet, geomField)) {
                                                dsf.getIndexManager().buildIndex(dataSet, geomField, pm);
                                        }
                                        long rowCount = dataSet.getRowCount();
                                        pm.startTask("Start spliting geometries", 100);
                                        for (int i = 0; i < rowCount; i++) {
                                                if (i >= 100 && i % 100 == 0) {
                                                        if (pm.isCancelled()) {
                                                                break;
                                                        } else {
                                                                pm.progressTo(i);
                                                        }
                                                }
                                                Value[] vals = dataSet.getRow(i);
                                                Geometry geom = vals[geomFieldindex].getAsGeometry();                                          // query index
                                                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(geom.getEnvelopeInternal(), geomField);
                                                Iterator<Integer> iterator = dataSet.queryIndex(dsf, query);
                                                while (iterator.hasNext()) {
                                                        Integer integer = iterator.next();
                                                        if (i != integer) {
                                                                Geometry queryGeom = dataSet.getGeometry(integer, geomFieldindex);
                                                                if (queryGeom.intersects(geom)) {
                                                                        Geometry result = geom.intersection(queryGeom);
                                                                        int numGeom = result.getNumGeometries();
                                                                        for (int j = 0; j < numGeom; j++) {
                                                                                Geometry subGeom = result.getGeometryN(j);
                                                                                if (subGeom.getDimension() == 0) {
                                                                                        if (geom instanceof MultiLineString) {
                                                                                                geom = GeometryEdit.splitMultiLineString((MultiLineString) geom, (Point) subGeom);
                                                                                        } else {
                                                                                                LineString[] temp = GeometryEdit.splitLineString((LineString) geom, (Point) subGeom);
                                                                                                geom = geom.getFactory().createMultiLineString(temp);
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                        }

                                                }
                                                vals[geomFieldindex] = ValueFactory.createValue(geom);
                                                diskBufferDriver.addValues(vals);
                                        }
                                        pm.endTask();
                                } else {
                                        throw new FunctionException("This function runs only with multilinetring or linestring");
                                }
                        }
                        diskBufferDriver.writingFinished();
                        diskBufferDriver.open();
                        return diskBufferDriver;
                } catch (DriverException ex) {
                        throw new FunctionException("Cannot index the dataset", ex);
                } catch (NoSuchTableException ex) {
                        throw new FunctionException("Cannot find the dataset", ex);
                } catch (IndexException ex) {
                        throw new FunctionException("Cannot index the dataset", ex);
                }
        }

        @Override
        public void workFinished() throws DriverException {
                if (diskBufferDriver != null ) {
                        diskBufferDriver.close();
                }
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return tables[0];
        }

        @Override
        public String getDescription() {
                return "Split geometries in the input table";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY))};
        }

        @Override
        public String getName() {
                return "ST_SplitLine";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT * FROM ST_SplitLine(table)";
        }
}

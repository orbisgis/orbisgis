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

import org.apache.log4j.Logger;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadAccess;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

public final class ST_PlanarGraph extends AbstractTableFunction {

        private static final Logger LOG = Logger.getLogger(ST_PlanarGraph.class);

        @Override
        public String getName() {
                return "ST_PlanarGraph";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_PlanarGraph(the_geom) from myTable;";
        }

        @Override
        public String getDescription() {
                return "Build a planar graph based on polygons";
        }

        @Override
        public ReadAccess evaluate(SQLDataSourceFactory dsf, ReadAccess[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                try {
                        final ReadAccess sds = tables[0];

                        final String spatialFieldName = values[0].toString();
                        final int spatialFieldIndex = sds.getMetadata().getFieldIndex(spatialFieldName);

                        PlanarGraph planarGraph = new PlanarGraph(pm);

                        GenericObjectDriver edges = planarGraph.createEdges(sds, spatialFieldIndex);

                        GenericObjectDriver nodes = planarGraph.createNodes(edges);

                        GenericObjectDriver faces = planarGraph.createFaces(sds, edges);

                        dsf.getSourceManager().register(
                                dsf.getSourceManager().getUniqueName("edges"), edges);

                        dsf.getSourceManager().register(
                                dsf.getSourceManager().getUniqueName("nodes"), nodes);

                        dsf.getSourceManager().register(
                                dsf.getSourceManager().getUniqueName("faces"), faces);

                        return null;
                } catch (DriverLoadException e) {
                        throw new FunctionException(e);
                } catch (DriverException e) {
                        throw new FunctionException(e);
                } catch (NonEditableDataSourceException e) {
                        throw new FunctionException(e);
                } catch (NoSuchTableException e) {
                        throw new FunctionException(e);
                } catch (IndexException e) {
                        throw new FunctionException(e);
                }
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return null;
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

/**
 * TANATO  is a library dedicated to the modelling of water pathways based on 
 * triangulate irregular network. TANATO takes into account anthropogenic and 
 * natural artifacts to evaluate their impacts on the watershed response. 
 * It ables to compute watershed, main slope directions and water flow pathways.
 * 
 * This library has been originally created  by Erwan Bocher during his thesis 
 * “Impacts des activités humaines sur le parcours des écoulements de surface dans 
 * un bassin versant bocager : essai de modélisation spatiale. Application au 
 * Bassin versant du Jaudy-Guindy-Bizien (France)”. It has been funded by the 
 * Bassin versant du Jaudy-Guindy-Bizien and Syndicat d’Eau du Trégor.
 * 
 * The new version is developed at French IRSTV institut as part of the 
 * AvuPur project, funded by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-07-VULN-01.
 * 
 * TANATO is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
 * 
 * TANATO is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * TANATO is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * TANATO. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.tin.create;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.tin.model.TINMetadataFactory;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

/**
 * Delaunay triangulation function.
 * 
 * The goal of the function is to process a delaunay or constrained delaunay triangulation 
 * from the geometry given in input. The function returns a set of triangles.
 * If the user wants to return a planar straight line graph it must use the ST_PSLGTIN function.
 * 
 * @author Erwan Bocher
 */
public class ST_TIN extends AbstractTableFunction {
        
        private DiskBufferDriver triangles;

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                try {
                        TinBuilder tinBuilder = new TinBuilder(tables[0]);
                        if (values.length != 0) {
                                //We retrieve the values to know how we are supposed to proceed.
                                tinBuilder.setIntersection(values[0].getAsBoolean());
                                tinBuilder.setFlatTriangles(values[1].getAsBoolean());
                        }
                        tinBuilder.build();
                        triangles = tinBuilder.getTriangles(dsf);
                        triangles.open();
                        return triangles;
                } catch (DriverException ex) {
                        throw new FunctionException("Cannot build the delaunay triangulation.\n", ex);
                }
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
               return  TINMetadataFactory.createTrianglesMetadata();
        }

        @Override
        public String getDescription() {
                return "Compute a TIN (Triangular Irregular Network) from the geometry given in argument.\n "
                        + "The first argument force the intersection of all input edges.\n"
                        + "The second argument is used to delete flat triangles.\n"
                        + "If the geometry contains polygon or ligne the triangulation\n "
                        + "is enforced using a constraint method.";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY)),
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.BOOLEAN, ScalarArgument.BOOLEAN)
                        };
        }

        @Override
        public String getName() {
                return "ST_TIN";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT * FROM  ST_TIN(source_table[, false, true])";
        }
        
        @Override
        public void workFinished() throws DriverException {
                if (triangles != null) {
                        triangles.close();
                }
        }
}

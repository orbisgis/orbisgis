/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.geomark;

import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.FunctionSignature;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;

public class OG_Geomark extends AbstractExecutorFunction {

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
                final GeomarkPanel geomarkPanel = (GeomarkPanel) wbContext.getWorkbench().getFrame().getView("Geomark");
                final String prefix = ((2 == values.length) ? values[1].getAsString()
                                       : values[0].getAsString())
                        + "-";

                try {
                        final DataSource sds = dsf.getDataSource(values[0].getAsString());
                        sds.open();
                        final int rowCount = (int) sds.getRowCount();
                        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {

                                if (rowIndex / 100 == rowIndex / 100.0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo((int) (100 * rowIndex / rowCount));
                                        }
                                }

                                final Envelope envelope = sds.getGeometry(rowIndex).getEnvelopeInternal();
                                geomarkPanel.add(prefix + rowIndex, envelope);
                        }

                        sds.close();
                } catch (DriverException e) {
                        throw new FunctionException(e);
                } catch (NoSuchTableException e) {
                        throw new FunctionException(e);
                } catch (DataSourceCreationException e) {
                        throw new FunctionException(e);
                }
        }

        public String getName() {
                return "OG_Geomark";
        }

        public String getSqlOrder() {
                return "EXECUTE OG_Geomark('table_name', [optionalPrefix]);";
        }

        public String getDescription() {
                return "Stores each spatial field envelope as a new geomark.";
        }

        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return null;
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(ScalarArgument.STRING),
                                new ExecutorFunctionSignature(ScalarArgument.STRING, ScalarArgument.STRING)
                        };
        }
}
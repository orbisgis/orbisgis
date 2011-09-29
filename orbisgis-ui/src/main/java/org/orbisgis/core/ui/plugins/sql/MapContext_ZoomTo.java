/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.sql;

import com.vividsolutions.jts.geom.Envelope;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author ebocher
 */
public class MapContext_ZoomTo extends AbstractExecutorFunction {

        @Override
        public void evaluate(SQLDataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                try {
                        IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
                        if (editor != null && editor instanceof MapEditorPlugIn) {
                                MapEditorPlugIn mapEditorPlugIn = (MapEditorPlugIn) editor;
                                Envelope extend = tables[0].getFullExtent();
                                mapEditorPlugIn.getMapTransform().setExtent(extend);
                        }
                } catch (DriverException ex) {
                        throw new FunctionException("Cannot compute the full extent", ex);
                }
        }

        @Override
        public String getName() {
                return "ZoomTo";
        }

        @Override
        public String getDescription() {
                return "Zoom to according a dataset";
        }

        @Override
        public String getSqlOrder() {
                return "EXECUTE ZoomTo(dataset)";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{new ExecutorFunctionSignature(new TableArgument(TableDefinition.GEOMETRY))};
        }
}

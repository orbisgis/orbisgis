/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sql;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A function to add a table into the current mapcontext.
 * @author ebocher
 */
public class MapContext_AddLayer extends AbstractExecutorFunction {

        protected final static I18n I18N = I18nFactory.getI18n(MapContext_AddLayer.class);

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                EditorManager editorManager = Services.getService(EditorManager.class);

                for (EditableElement editable : editorManager.getEditableElements()) {
                        if (editable instanceof MapElement) {
                                MapElement mapElement = (MapElement) editable;
                                try {
                                        DataManager dataManager = (DataManager) Services.getService(DataManager.class);
                                        ILayer layer = dataManager.createLayer(values[0].getAsString());
                                        if (values.length == 2) {
                                                String seFile = values[1].getAsString();
                                                if (!seFile.isEmpty()) {
                                                        Style style = new Style(layer, seFile);
                                                        layer.addStyle(0, style);
                                                }
                                        }
                                        mapElement.getMapContext().getLayerModel().addLayer(layer);
                                } catch (InvalidStyle ex) {
                                        throw new FunctionException(I18N.tr("Cannot import the style"), ex);
                                } catch (LayerException ex) {
                                        throw new FunctionException(I18N.tr("Cannot add the layer to the mapcontext"), ex);
                                }
                        }
                }
        }

        @Override
        public String getName() {
                return "Map_AddLayer";
        }

        @Override
        public String getDescription() {
                return I18N.tr("A function to add a layer based on a table name\n"
                        + "The second argument is optional. It permits to set a style to the layer.");
        }

        @Override
        public String getSqlOrder() {
                return "EXECUTE Map_AddLayer('tableName'[,'myStyle.se'])";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{new ExecutorFunctionSignature(ScalarArgument.STRING),
                                new ExecutorFunctionSignature(ScalarArgument.STRING, ScalarArgument.STRING)};
        }
}

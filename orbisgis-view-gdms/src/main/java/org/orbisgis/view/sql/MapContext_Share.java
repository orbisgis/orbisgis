/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;
import org.orbisgis.core.Services;
import org.orbisgis.coremap.layerModel.mapcatalog.ConnectionProperties;
import org.orbisgis.coremap.layerModel.mapcatalog.RemoteMapCatalog;
import org.orbisgis.coremap.layerModel.mapcatalog.Workspace;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A function to share a mapcontext on a mapcatalog service.
 * @author ebocher
 */
public class MapContext_Share extends AbstractExecutorFunction {

        protected final static I18n I18N = I18nFactory.getI18n(MapContext_Share.class);

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                EditorManager editorManager = Services.getService(EditorManager.class);

                for (EditableElement editable : editorManager.getEditableElements()) {
                        if (editable instanceof MapElement) {
                                MapElement mapElement = (MapElement) editable;
                                try {
                                        URL apiUrl;
                                        try {
                                                apiUrl = new URL(values[0].getAsString());
                                        } catch (MalformedURLException ex) {
                                                throw new FunctionException(I18N.tr("The URL is not valid."), ex);
                                        }

                                        RemoteMapCatalog remoteMapCatalog = new RemoteMapCatalog(new ConnectionProperties(apiUrl));
                                        Iterator<Workspace> workspaces = remoteMapCatalog.getWorkspaces().iterator();
                                        String workspaceName = values[1].getAsString();
                                        boolean canBeShared = false;
                                        Workspace targetWorkspace = null;
                                        while (workspaces.hasNext() && !canBeShared) {
                                                Workspace workspace = workspaces.next();
                                                if (workspace.getWorkspaceName().equalsIgnoreCase(workspaceName)) {
                                                        canBeShared = true;
                                                        targetWorkspace = workspace;
                                                }
                                        }

                                        if (targetWorkspace != null) {
                                                targetWorkspace.publishMapContext(mapElement.getMapContext(), null);
                                        } else {
                                                throw new FunctionException(I18N.tr("The workspace doesn't exist. Please contact "
                                                        + "the administrator of the service."));
                                        }


                                } catch (IOException ex) {
                                        throw new FunctionException(I18N.tr("The URL is not valid or the service not available."), ex);
                                }


                        }
                }
        }

        @Override
        public String getName() {
                return "Map_Share";
        }

        @Override
        public String getDescription() {
                return I18N.tr("A function to share the current mapcontext in the mapcatalog service\n"
                        + "The second permits to set the workspace name. If the workspace doesn't exist a message appears.");
        }

        @Override
        public String getSqlOrder() {
                return "EXECUTE Map_Share('http://services.orbisgis.org/','myMaps')";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(ScalarArgument.STRING, ScalarArgument.STRING)};
        }
}

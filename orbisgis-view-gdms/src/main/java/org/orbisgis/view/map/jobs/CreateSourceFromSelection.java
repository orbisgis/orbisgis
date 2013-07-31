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

package org.orbisgis.view.map.jobs;

import java.io.File;
import java.util.Set;
import org.apache.log4j.Logger;
import org.gdms.data.*;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A background job to create a source from a selection.
 *
 * @author ebocher
 */
public class CreateSourceFromSelection implements BackgroundJob {

        private static final I18n I18N = I18nFactory.getI18n(CreateSourceFromSelection.class);
        
         private static final Logger GUILOGGER = Logger.getLogger("gui."+CreateSourceFromSelection.class);
   
      
        private final DataSource original;
        private final Set<Integer> selectedRows;

        public CreateSourceFromSelection(DataSource original,
                Set<Integer> selectedRows) {
                this.original = original;
                this.selectedRows = selectedRows;
        }

        @Override
        public void run(ProgressMonitor pm) {

                try {
                        DataManager dm = Services.getService(DataManager.class);

                        // Create the new source
                        DataSourceFactory dsf = dm.getDataSourceFactory();
                        File file = dsf.getResultFile();
                        DataSourceCreation dsc = new FileSourceCreation(file, original.getMetadata());
                        dsf.createDataSource(dsc);
                        FileSourceDefinition dsd = new FileSourceDefinition(file, DriverManager.DEFAULT_SINGLE_TABLE_NAME);

                        // Find an unique name to register
                        SourceManager sm = dm.getSourceManager();
                        int index = -1;
                        String newName;
                        do {
                                index++;
                                newName = original.getName() + "_selection_" + index;
                        } while (sm.getSource(newName) != null);
                        sm.register(newName, dsd);

                        // Populate the new source
                        DataSource newds = dsf.getDataSource(newName);
                        newds.open();
                        
                        for (Integer sel : selectedRows){
                                newds.insertFilledRow(original.getRow(sel));
                        }
                        newds.commit();
                        newds.close();
                } catch (SourceAlreadyExistsException e) {
                        GUILOGGER.error("The selection cannot be created.", e);
                } catch (DriverLoadException e) {
                        GUILOGGER.error("The selection cannot be created.", e);
                } catch (NoSuchTableException e) {
                        GUILOGGER.error("Cannot find the datasource.", e);
                } catch (DriverException e) {
                        GUILOGGER.error("The datasource cannot be created.", e);
                } catch (DataSourceCreationException e) {
                        GUILOGGER.error("The datasource cannot be created.", e);
                } catch (NonEditableDataSourceException e) {
                        GUILOGGER.error("The datsource is not editable.", e);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Create the datasource from the selection");
        }
        
}

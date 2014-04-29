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

package org.orbisgis.view.components.gdms;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A process to delete DataSource rows.
 * @author Nicolas Fortin
 */
public class DeleteRows implements BackgroundJob {
    private List<Integer> rowsToDelete;
    private DataSource source;
    private Logger logger = Logger.getLogger(DeleteRows.class);
    private static final I18n I18N = I18nFactory.getI18n(DeleteRows.class);
    /**
     * Construction
     * @param rowsToDelete List of row line number to remove.
     * @param source DataSource instance
     */
    public DeleteRows(Collection<Integer> rowsToDelete, DataSource source) {
        this.rowsToDelete = new ArrayList<Integer>(rowsToDelete);
        this.source = source;
    }

    @Override
    public void run(ProgressMonitor pm) {
        long rowCount = rowsToDelete.size();
        // Reverse the Row index deletion order to keep valid row index.
        Collections.sort(rowsToDelete, Collections.reverseOrder());
        long done=0;
        pm.startTask(getTaskName(),rowCount);
        try {

            source.setDispatchingMode(DataSource.STORE);
            for(int rowId : rowsToDelete) {
                pm.progressTo(done);
                source.deleteRow(rowId);
                done++;
                if(pm.isCancelled()) {
                    break;
                }
            }
        } catch (DriverException ex) {
            logger.error(ex.getLocalizedMessage(),ex);
        } finally {
            source.setDispatchingMode(DataSource.DISPATCH);
            pm.endTask();
        }
    }

    @Override
    public String getTaskName() {
        return I18N.tr("Delete selected rows");
    }
}
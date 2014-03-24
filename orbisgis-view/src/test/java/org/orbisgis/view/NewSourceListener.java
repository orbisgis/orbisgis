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

package org.orbisgis.view;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import javax.swing.*;

/**
 * Useful to wait for the availability of a DataSource.
 * - Create an new instance of NewSourceListener
 * - Call execute
 * - Use {@link SwingWorker#get(long, java.util.concurrent.TimeUnit)} to retrieve the DataSource.
 * @author Nicolas Fortin
 */
public class NewSourceListener  extends SwingWorker<DataSource,String> {
    String sourceName;
    DataSourceFactory dataSourceFactory;
    private static final long WATCH_INTERVAL = 50; //in ms

    /**
     * @param sourceName {@link javax.swing.SwingWorker#get()} method will return a DataSource instance of this DataSourceName.
     * @param dataSourceFactory DataSourceFactory instance to read.
     */
    public NewSourceListener(String sourceName, DataSourceFactory dataSourceFactory) {
        this.sourceName = sourceName;
        this.dataSourceFactory = dataSourceFactory;
    }

    @Override
    protected DataSource doInBackground() throws Exception {
        SourceManager sm = dataSourceFactory.getSourceManager();
        while(!sm.exists(sourceName) && !isCancelled()) {
            Thread.sleep(WATCH_INTERVAL);
        }
        return dataSourceFactory.getDataSource(sourceName);
    }
}
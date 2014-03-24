/**
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
package org.orbisgis.corejdbc.internal;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.progress.ProgressMonitor;

import javax.sql.DataSource;
import javax.swing.event.UndoableEditListener;
import java.sql.SQLException;

/**
 * Implementation of {@link ReversibleRowSet}
 * @author Nicolas Fortin
 */
public class ReversibleRowSetImpl extends ReadRowSetImpl implements ReversibleRowSet {
    private DataManager manager;

    public ReversibleRowSetImpl(DataSource dataSource, DataManager manager) {
        super(dataSource);
        this.manager = manager;
    }

    /**
     * Initialize this row set
     * @param location Table location
     * @param pk_name Primary key name {@link org.orbisgis.core.jdbc.ReadRowSetImpl#getPkName(javax.sql.DataSource, org.h2gis.utilities.TableLocation)}
     * @param pm Progress monitor Progression of primary key caching
     */
    public ReversibleRowSetImpl(DataSource dataSource, DataManager manager, TableLocation location, String pk_name, ProgressMonitor pm) throws SQLException {
        super(dataSource);
        this.manager = manager;
        initialize(location, pk_name, pm);
    }

    @Override
    public void addUndoableEditListener(UndoableEditListener listener) {
        manager.addUndoableEditListener(getTable(),listener);
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener listener) {
        manager.removeUndoableEditListener(getTable(),listener);
    }
}

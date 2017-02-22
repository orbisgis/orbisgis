/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.corejdbc;

/**
 * State event data
 * @author Nicolas Fortin
 */
public class StateEvent {
    /**
     * States id {@see org.h2.api.DatabaseEventListener}
     */
    public enum DB_STATES {STATE_SCAN_FILE,STATE_CREATE_INDEX,STATE_RECOVER,STATE_BACKUP_FILE,STATE_RECONNECTED,
        STATE_STATEMENT_START,STATE_STATEMENT_END,STATE_STATEMENT_PROGRESS}
    private final DB_STATES stateIdentifier;
    private final String name;
    private final int i;
    private final int max;
    // Update database view only if query starts with this command. (lowercase)
    private static final String[] updateSourceListQuery = new String[] {"drop", "create","alter"};
    private static final int MAX_LENGTH_QUERY;
    static {
        int maxLen = 0;
        for(String query : updateSourceListQuery) {
            maxLen = Math.max(maxLen, query.length());
        }
        MAX_LENGTH_QUERY = maxLen;
    }

    /**
     * @param stateIdentifier State id
     * @param name Object name
     * @param i State current progression
     * @param max State max progression
     */
    public StateEvent(DB_STATES stateIdentifier, String name, int i, int max) {
        this.stateIdentifier = stateIdentifier;
        this.name = name;
        this.i = i;
        this.max = max;
    }

    /**
     * @return True if this DB event is related to a database structure update.
     */
    public boolean isUpdateDatabaseStructure() {
        if(StateEvent.DB_STATES.STATE_STATEMENT_END.equals(stateIdentifier)) {
            // DataBase update
            if (name != null) {
                String subName = name.substring(0, MAX_LENGTH_QUERY).trim().toLowerCase();
                for (String query : updateSourceListQuery) {
                    if (subName.startsWith(query)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return State max progression
     */
    public int getMax() {
        return max;
    }

    /**
     * @return State current progression
     */
    public DB_STATES getStateIdentifier() {
        return stateIdentifier;
    }

    /**
     * @return Object name
     */
    public String getName() {
        return name;
    }

    /**
     * @return State current progression
     */
    public int getI() {
        return i;
    }
}

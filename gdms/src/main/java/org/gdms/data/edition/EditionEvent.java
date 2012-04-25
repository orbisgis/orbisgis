/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.edition;

import org.gdms.data.DataSource;

/**
 * This class stores information about the change in the contents of a
 * DataSource. It stores the row index where the change was made and which the
 * action done was. If the type is MODIFY then rowIndex and field index store
 * where the modification was done. If the type is DELETE or INSERT then
 * fieldIndex is set to -1
 *
 * @author Fernando Gonzalez Cortes
 */
public class EditionEvent extends FieldEditionEvent {

        private long rowIndex;
        public static final int MODIFY = 0;
        public static final int DELETE = 1;
        public static final int INSERT = 2;
        /**
         * Indicates the DataSource has refreshed it's contents with the ones in the
         * source. This means that all data can have changed
         */
        public static final int RESYNC = 3;
        private int type;
        private boolean undoRedo;

        /**
         * Creates a new EditionEvent.
         * @param rowIndex the row index changed
         * @param fieldIndex the field index changed
         * @param type the type of the edit event
         * @param ds the data source changed
         * @param undoRedo if undo/redo is supported.
         */
        public EditionEvent(long rowIndex, int fieldIndex, int type, DataSource ds,
                boolean undoRedo) {
                super(fieldIndex, ds);
                this.rowIndex = rowIndex;
                this.type = type;
                this.undoRedo = undoRedo;
        }

        public long getRowIndex() {
                return rowIndex;
        }

        public int getType() {
                return type;
        }

        public boolean isUndoRedo() {
                return undoRedo;
        }
}

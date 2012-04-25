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

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;

public class EditionListenerSupport {

        private List<EditionListener> listeners = new ArrayList<EditionListener>();
        private int dispatchingMode = DataSource.DISPATCH;
        private MultipleEditionEvent multipleEditionEvent;
        private DataSource dataSource;

        public EditionListenerSupport(DataSource ds) {
                this.dataSource = ds;
        }

        public void addEditionListener(EditionListener listener) {
                listeners.add(listener);
        }

        public void removeEditionListener(EditionListener listener) {
                listeners.remove(listener);
        }

        public void callSetFieldValue(long rowIndex, int fieldIndex,
                boolean undoRedo) {
                EditionEvent event = new EditionEvent(rowIndex, fieldIndex,
                        EditionEvent.MODIFY, dataSource, undoRedo);
                manageEvent(event);
        }

        public void callDeleteRow(long rowIndex, boolean undoRedo) {
                EditionEvent event = new EditionEvent(rowIndex, -1,
                        EditionEvent.DELETE, dataSource, undoRedo);
                manageEvent(event);
        }

        public void callInsert(long rowIndex, boolean undoRedo) {
                EditionEvent event = new EditionEvent(rowIndex, -1,
                        EditionEvent.INSERT, dataSource, undoRedo);
                manageEvent(event);
        }

        public void callSync() {
                EditionEvent event = new EditionEvent(-1, -1, EditionEvent.RESYNC,
                        dataSource, false);
                for (EditionListener listener : listeners) {
                        listener.singleModification(event);
                }
        }

        private void manageEvent(EditionEvent event) {
                if (dispatchingMode == DataSource.DISPATCH) {
                        callModification(event);
                } else if (dispatchingMode == DataSource.STORE) {
                        multipleEditionEvent.addEvent(event);
                }
        }

        public void setDispatchingMode(int dispatchingMode) {
                int previousMode = this.dispatchingMode;
                this.dispatchingMode = dispatchingMode;
                if (previousMode == DataSource.STORE) {
                        callMultipleModification(multipleEditionEvent);
                        multipleEditionEvent = null;
                }

                if (dispatchingMode == DataSource.STORE) {
                        multipleEditionEvent = new MultipleEditionEvent();
                }
        }

        private void callModification(EditionEvent e) {
                for (EditionListener listener : listeners) {
                        listener.singleModification(e);
                }
        }

        private void callMultipleModification(MultipleEditionEvent e) {
                for (EditionListener listener : listeners) {
                        listener.multipleModification(e);
                }
        }

        public int getDispatchingMode() {
                return dispatchingMode;
        }
}

/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.DataSource;

public class EditionListenerSupport {
	private ArrayList<EditionListener> listeners = new ArrayList<EditionListener>();

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

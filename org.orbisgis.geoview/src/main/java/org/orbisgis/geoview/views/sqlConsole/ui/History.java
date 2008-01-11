/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlConsole.ui;

public class History {
	private String[] history = new String[historySize];
	private final static int historySize = 100;

	// fixedPointer is the address of the new sqlCommand...
	private int fixedPointer = 0;

	// farFromFixedPointer is the "length" from the current sqlCommand to the
	// one that corresponds to the fixedPointer when scrolling in the stack
	// history...
	private int farFromFixedPointer = 0;

	private int start = 0;
	private int currentSize = 0;

	public History() {
		history = new String[historySize];
	}

	private int previous(final int idx) {
		return (0 == idx) ? historySize - 1 : idx - 1;
	}

	private int next(final int idx) {
		return (idx + 1) % historySize;
	}

	/**
	 * This method adds a new sqlCommand to the "stack" if and only if it is not
	 * null, not empty and not equal to the last sqlCommand that has been
	 * stored...
	 */
	public void push(final String sqlCommand) {
		if ((null != sqlCommand) && (0 < sqlCommand.length())
				&& (!sqlCommand.equals(history[previous(fixedPointer)]))) {
			// pointer is the address of the new sqlCommand...
			history[fixedPointer] = sqlCommand;
			// increment the fixedPointer
			fixedPointer = next(fixedPointer);
			// reset the farFromFixedPointer
			farFromFixedPointer = 0;

			if (currentSize < historySize) {
				currentSize++;
			} else {
				start = (start + 1) % historySize;
			}
		}
	}

	public String getPrevious() {
		if (isPreviousAvailable()) {
			farFromFixedPointer++;
			return history[(fixedPointer + historySize - farFromFixedPointer)
					% historySize];
		}
		return null;
	}

	public String getNext() {
		if (isNextAvailable()) {
			farFromFixedPointer--;
			return history[(fixedPointer + historySize - farFromFixedPointer)
					% historySize];
		}
		return null;
	}

	public boolean isPreviousAvailable() {
		return (farFromFixedPointer < currentSize);
	}

	public boolean isNextAvailable() {
		return (1 < farFromFixedPointer);
	}
}
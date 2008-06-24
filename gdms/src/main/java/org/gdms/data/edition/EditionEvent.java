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

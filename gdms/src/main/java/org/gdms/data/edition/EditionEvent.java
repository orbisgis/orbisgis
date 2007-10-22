/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.data.edition;

import org.gdms.data.DataSource;

/**
 * This class stores information about the change in the contents of a
 * DataSource. It stores the row index where the change was made and which the
 * action done was. If the type is MODIFY then rowIndex and field index store
 * where the modification was done. If the type is DELETE or INSERT then
 * fieldIndex is set to -1
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class EditionEvent extends FieldEditionEvent {
	private long rowIndex;

	public static final int MODIFY = 0;

	public static final int DELETE = 1;

	public static final int INSERT = 2;

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

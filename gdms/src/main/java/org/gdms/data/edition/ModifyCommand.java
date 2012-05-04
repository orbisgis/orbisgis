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

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class ModifyCommand extends AbstractCommand implements Command {

	private int fieldIndex;

	private Value newValue;

	private ModifyInfo info;

	public ModifyCommand(int index, EditionDecorator dataSource,
			Value newValue, int fieldIndex) {
		super(index, dataSource);
		this.fieldIndex = fieldIndex;
		this.newValue = newValue;
	}

        @Override
	public void redo() throws DriverException {
		info = dataSource.doSetFieldValue(index, fieldIndex, newValue);
	}

        @Override
	public void undo() throws DriverException {
		dataSource.undoSetFieldValue(info.previousDir, info.previousInfo,
				info.ibDir, info.previousValue, info.fieldId, info.row);
	}

        @Override
        public void clear() {
        }

	public static class ModifyInfo {
		public OriginalRowAddress previousDir;

		public EditionInfo previousInfo;

		public InternalBufferRowAddress ibDir;

		public Value previousValue;

		public long row;

		public int fieldId;

		public ModifyInfo(OriginalRowAddress previousDir,
				EditionInfo previousInfo, InternalBufferRowAddress ibDir,
				Value previousValue, long row, int fieldId) {
			super();
			this.previousDir = previousDir;
			this.previousInfo = previousInfo;
			this.ibDir = ibDir;
			this.previousValue = previousValue;
			this.row = row;
			this.fieldId = fieldId;
		}

	}
}

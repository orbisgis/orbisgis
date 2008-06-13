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

	public void redo() throws DriverException {
		info = dataSource.doSetFieldValue(index, fieldIndex, newValue);
	}

	public void undo() throws DriverException {
		dataSource.undoSetFieldValue(info.previousDir, info.previousInfo,
				info.ibDir, info.previousValue, info.fieldId, info.row);
	}

	public static class ModifyInfo {
		public OriginalDirection previousDir;

		public EditionInfo previousInfo;

		public InternalBufferDirection ibDir;

		public Value previousValue;

		public long row;

		public int fieldId;

		public ModifyInfo(OriginalDirection previousDir,
				EditionInfo previousInfo, InternalBufferDirection ibDir,
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

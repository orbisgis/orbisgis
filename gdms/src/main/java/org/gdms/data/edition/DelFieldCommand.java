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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.WriteBufferManager;

public class DelFieldCommand implements Command {

	private EditionDecorator dataSource;

	private int fieldIndex;

	private DelFieldInfo info;

	public DelFieldCommand(EditionDecorator dataSource, int index) {
		this.dataSource = dataSource;
		this.fieldIndex = index;
	}

	public void redo() throws DriverException {
		info = dataSource.doRemoveField(fieldIndex);
	}

	public void undo() throws DriverException {
		try {
			dataSource.undoDeleteField(info.fieldIndex, info.field,
					info.getFieldValues());
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public static class DelFieldInfo {
		public int fieldIndex;

		public Field field;

		private DataSourceFactory factory;

		private File fieldFile;

		public DelFieldInfo(DataSourceFactory factory, int fieldIndex,
				Field field, Value[] fieldValues) throws IOException {
			super();
			this.fieldIndex = fieldIndex;
			this.field = field;
			this.factory = factory;
			writeValues(fieldValues);
		}

		public Value[] getFieldValues() throws IOException {
			FileInputStream fis = new FileInputStream(fieldFile);
			DataInputStream dis = new DataInputStream(fis);
			byte[] buffer = new byte[(int) fis.getChannel().size()];
			dis.readFully(buffer);
			dis.close();
			ValueCollection ret = (ValueCollection) ValueFactory.createValue(Type.COLLECTION, buffer);

			return ret.getValues();
		}

		private void writeValues(Value[] fieldValues) throws IOException {
			fieldFile = new File(factory.getTempFile());
			FileChannel channel = new FileOutputStream(fieldFile).getChannel();
			WriteBufferManager wb = new WriteBufferManager(channel);
			ValueCollection v = new ValueCollection();
			v.setValues(fieldValues);
			wb.put(v.getBytes());
			wb.flush();
			channel.close();
		}

	}
}
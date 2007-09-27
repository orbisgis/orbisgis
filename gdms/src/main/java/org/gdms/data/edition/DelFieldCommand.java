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
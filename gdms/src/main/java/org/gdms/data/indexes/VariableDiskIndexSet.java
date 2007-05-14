package org.gdms.data.indexes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Implementaci�n de VariableIndexSet que escribe los �ndices en un fichero
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class VariableDiskIndexSet extends DiskIndexSet
	implements VariableIndexSet {
	private long count = 0;

	/**
	 * A�ade todos los �ndices que se pasan como par�metro
	 *
	 * @param initialSet Conjunto de �ndices que se quieren a�adir
	 *
	 * @throws IOException Si se produce un fallo al a�adir los �ndices
	 */
	public void addAll(VariableIndexSet initialSet) throws IOException {
		for (long i = 0; i < initialSet.getIndexCount(); i++) {
			addIndex(initialSet.getIndex(i));
		}

		count = initialSet.getIndexCount();
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#addIndex(long)
	 */
	public void addIndex(long index) throws IOException {
		buffer.put((byte) (index >>> 56));
		buffer.put((byte) (index >>> 48));
		buffer.put((byte) (index >>> 40));
		buffer.put((byte) (index >>> 32));
		buffer.put((byte) (index >>> 24));
		buffer.put((byte) (index >>> 16));
		buffer.put((byte) (index >>> 8));
		buffer.put((byte) (index >>> 0));

		buffer.flip();

		outputChannel.write(buffer);

		buffer.clear();

		count++;
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#getIndex(long)
	 */
	public long getIndex(long nth) throws IOException {
		buffer.clear();
		inputChannel.read(buffer, nth * 8);
		buffer.flip();

		return buffer.getLong();
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#getIndexCount()
	 */
	public long getIndexCount() {
		return count;
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() throws IOException {
		file = File.createTempFile("index", "idx");
		file.deleteOnExit();
		fos = new FileOutputStream(file);
		outputChannel = fos.getChannel();
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#close()
	 */
	public void close() throws IOException {
		inputChannel.close();
		fis.close();
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#indexSetComplete()
	 */
	public void indexSetComplete() throws IOException {
		outputChannel.close();
		fos.close();
		fis = new FileInputStream(file);
		inputChannel = fis.getChannel();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 *
	 * @see org.gdms.data.indexes.VariableIndexSet#getIndexes()
	 */
	public long[] getIndexes() throws IOException {
		long[] ret = new long[(int) getIndexCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getIndex(i);
		}

		return ret;
	}
}

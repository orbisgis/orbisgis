package org.gdms.data.indexes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Implementaci�n de FixedIndexSet que escribe los �ndices en un fichero
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FixedDiskIndexSet extends DiskIndexSet implements FixedIndexSet {
	private long size = 0;

	/**
	 * Crea un nuevo FixedDiskIndexSet.
	 *
	 * @param size DOCUMENT ME!
	 */
	public FixedDiskIndexSet(long size) {
		this.size = size;
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
		return size;
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open(File f) throws IOException {
		file = f;
		fos = new FileOutputStream(f);
		outputChannel = fos.getChannel();
		fis = new FileInputStream(file);
		inputChannel = fis.getChannel();
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#close()
	 */
	public void close() throws IOException {
		inputChannel.close();
		fis.close();
		outputChannel.close();
		fos.close();
	}

	/**
	 * @see org.gdms.data.indexes.FixedIndexSet#setIndex(long,
	 * 		long)
	 */
	public void setIndex(long index, long value) throws IOException {
		buffer.put((byte) (index >>> 56));
		buffer.put((byte) (index >>> 48));
		buffer.put((byte) (index >>> 40));
		buffer.put((byte) (index >>> 32));
		buffer.put((byte) (index >>> 24));
		buffer.put((byte) (index >>> 16));
		buffer.put((byte) (index >>> 8));
		buffer.put((byte) (index >>> 0));

		buffer.flip();

		outputChannel.write(buffer, index * 8);

		buffer.clear();

		size++;
	}
}

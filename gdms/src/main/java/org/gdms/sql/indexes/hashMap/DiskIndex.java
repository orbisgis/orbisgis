package org.gdms.sql.indexes.hashMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class DiskIndex implements Index {
	static int RECORD_SIZE = 8;

	private int socketCount = 2;

	private int recordCount;

	private int positionCount;

	private File file;

	private RandomAccessFile raf;

	private FileChannel channel;

	private ByteBuffer buffer;

	/**
	 * Crea un nuevo DiskIndex.
	 * 
	 * @param f
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public DiskIndex(File f) throws IOException {
		initIndex(f);
	}

	/**
	 * Crea un nuevo DiskIndex.
	 * 
	 * @param socketCount
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public DiskIndex(int socketCount) throws IOException {
		File f = File.createTempFile("gdms", ".gix");
		f.deleteOnExit();
		this.socketCount = socketCount;
		initIndex(f);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param f
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	private void initIndex(File f) throws IOException {
		file = f;

		if (file.length() == 0) {
			// Si el fichero no existe
			FileOutputStream fos = new FileOutputStream(file);
			FileChannel channel = fos.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);

			// N�mero de cubetas
			buffer.putInt(socketCount);

			// L�mite del fichero
			buffer.putInt(socketCount);
			buffer.flip();
			channel.write(buffer, 0);

			// Se inicializan las cubetas
			for (int i = 0; i < socketCount; i++) {
				buffer.clear();
				buffer.putInt(-1);
				buffer.putInt(-1);
				buffer.flip();
				channel.write(buffer, (long) (byteNumber(i)));
			}

			channel.force(true);

			buffer = null;
			channel.close();
			fos.close();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws IndexException
	 * 
	 * @see org.gdms.sql.indexes.hashMap.Index#start()
	 */
	public void start() throws IndexException {
		try {
			// Abrimos para lectura
			raf = new RandomAccessFile(file, "rws");
			channel = raf.getChannel();
			buffer = ByteBuffer.allocate(RECORD_SIZE);

			// Leemos el n�mero de registros
			channel.position(0);
			buffer.clear();
			channel.read(buffer);
			buffer.flip();
			positionCount = buffer.getInt();
			recordCount = buffer.getInt();
		} catch (IOException e) {
			throw new IndexException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws IndexException
	 * 
	 * @see org.gdms.sql.indexes.hashMap.Index#stop()
	 */
	public void stop() throws IndexException {
		buffer = null;

		try {
			channel.close();
			raf.close();
		} catch (IOException e) {
			throw new IndexException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param recordIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	static long byteNumber(int recordIndex) {
		return (recordIndex + 1) * RECORD_SIZE;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param v
	 *            DOCUMENT ME!
	 * @param position
	 *            DOCUMENT ME!
	 * 
	 * @throws IndexException
	 * 
	 * @see org.gdms.sql.indexes.hashMap.Index#add(org.gdms.data.values.Value,
	 *      int)
	 */
	public void add(Object v, int position) throws IndexException {
		try {
			// Obtenemos la posici�n en el hashtable
			int pos = Math.abs(v.hashCode());
			pos = (pos % positionCount);

			// Leemos el contenido
			buffer.clear();
			channel.position(byteNumber(pos));
			channel.read(buffer);
			buffer.flip();

			int value = buffer.getInt();
			int next = buffer.getInt();

			if (value == -1) {
				// Si la cubeta est� por ocupar
				buffer.clear();
				buffer.putInt(position);
				buffer.putInt(-1);
				buffer.flip();
				channel.position(byteNumber(pos));
				channel.write(buffer);
				channel.force(true);

				return;
			} else {
				// Si la cubeta est� ocupada

				/*
				 * iteramos hasta que encontremos el �ltimo nodo de la lista
				 */
				while (next != -1) {
					pos = next;

					// Se lee el siguiente registro
					buffer.clear();
					channel.position(byteNumber(pos));
					channel.read(buffer);
					buffer.flip();
					value = buffer.getInt();
					next = buffer.getInt();
				}

				// Modificamos la entrada para que enlace con la nueva
				channel.position(byteNumber(pos));
				buffer.clear();
				buffer.putInt(value);
				buffer.putInt(recordCount);
				buffer.flip();
				channel.write(buffer);

				// Ponemos la nueva
				channel.position(byteNumber(recordCount));
				buffer.clear();
				buffer.putInt(position);
				buffer.putInt(-1);
				buffer.flip();
				channel.write(buffer);

				channel.force(true);

				// Actualizamos el n�mero de registros
				recordCount++;
			}
		} catch (IOException e) {
			throw new IndexException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param v
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IndexException
	 * 
	 * @see org.gdms.sql.indexes.hashMap.Index#getPositions(org.gdms.data.values.Value)
	 */
	public PositionIterator getPositions(Object v) throws IndexException {
		try {
			return new DiskPositionIterator(channel, positionCount, v);
		} catch (IOException e) {
			throw new IndexException(e);
		}
	}
}

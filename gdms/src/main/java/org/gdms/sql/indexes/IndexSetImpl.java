package org.gdms.sql.indexes;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Implementaci�n del conjunto de �ndices que guarda en memoria hasta un l�mite
 * y a partir de ese l�mite pasa todos los �ndices a disco
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class IndexSetImpl implements VariableIndexSet {
	private VariableIndexSet set;

	/**
	 * Creates a new IndexSetImpl object.
	 */
	public IndexSetImpl() {
		set = new VariableMemoryIndexSet();
	}

	/**
	 * A�ade un �ndice al conjunto
	 *
	 * @param index
	 *            �ndice a a�adir
	 *
	 * @throws IOException
	 *             Si se produce un error al escribir en el disco
	 * @throws RuntimeException
	 */
	public synchronized void addIndex(long index) throws IOException {
		set.addIndex(index);
	}

	/**
	 * Devuelve el �ndice nth-�simo si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 *
	 * @param nth
	 *            �ndice de �ndice que se quiere obtener
	 *
	 * @return indice nth-�simo
	 *
	 * @throws IOException
	 *             Si se produce un error accediendo a disco
	 * @throws RuntimeException
	 */
	public long getIndex(long nth) throws IOException {
		return set.getIndex(nth);
	}

	/**
	 * Devuelve el n�mero de �ndices si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 *
	 * @return n�mero de �ndices
	 *
	 * @throws RuntimeException
	 */
	public long getIndexCount() {
		return set.getIndexCount();
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() throws IOException {
		set.open();
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#close()
	 */
	public void close() throws IOException {
		set.close();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 *
	 * @see org.gdms.sql.indexes.VariableIndexSet#getIndexes()
	 */
	public ArrayList<Long> getIndexes() throws IOException {
		return set.getIndexes();
	}
}

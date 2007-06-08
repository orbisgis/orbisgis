package org.gdms.sql.indexes;

import java.io.IOException;

/**
 * Implementaci�n del conjunto de �ndices que guarda en memoria hasta un l�mite
 * y a partir de ese l�mite pasa todos los �ndices a disco
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class IndexSetImpl implements VariableIndexSet {
	private VariableIndexSet set;

	private boolean complete = false;

	/**
	 * N�mero de �ndices que se guardan en memoria, a partir de dicho n�mero se
	 * guarda en disco
	 */
	private int limit;

	/**
	 * Creates a new IndexSetImpl object.
	 */
	public IndexSetImpl() {
		limit = IndexFactory.MEMORY_THRESHOLD;
		set = new VariableMemoryIndexSet(IndexFactory.MEMORY_THRESHOLD);
	}

	/**
	 * Creates a new IndexSetImpl object.
	 * 
	 * @param limit
	 *            l�mite a partir del cual se guardan todos los �ndices en disco
	 */
	public IndexSetImpl(int limit) {
		this.limit = limit;
		set = new VariableMemoryIndexSet(limit);
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
		if (complete) {
			throw new RuntimeException(
					"Cannot add more indexes after indexSetComplete");
		}

		if (set.getIndexCount() == limit) {
			// Se sustituye el �ndice de memoria por el �ndice de disco
			VariableDiskIndexSet newSet = new VariableDiskIndexSet();
			newSet.open();
			newSet.addAll(set);
			set = newSet;
		}

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
		if (!complete) {
			throw new RuntimeException("Must call indexSetComplete First");
		}

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
		if (!complete) {
			throw new RuntimeException("Must call indexSetComplete First");
		}

		return set.getIndexCount();
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#indexSetComplete()
	 */
	public void indexSetComplete() throws IOException {
		complete = true;
		set.indexSetComplete();
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
	public long[] getIndexes() throws IOException {
		return set.getIndexes();
	}
}

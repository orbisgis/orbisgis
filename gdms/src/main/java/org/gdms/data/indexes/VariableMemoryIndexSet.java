package org.gdms.data.indexes;

/**
 * Implementaci�n de un conjunto de �ndices en memoria, aunque puede haber un
 * conjunto de Long.MAXVALUE �ndices, en memoria, el tama�o  m�ximo es de
 * Integer.MAXVALUE. Otras implementaciones de VariableIndexSet en memoria
 * pueden no tener esta restricci�n
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class VariableMemoryIndexSet extends MemoryIndexSet
	implements VariableIndexSet {
	private int count = 0;

	/**
	 * Creates a new MemoryIndexSet object.
	 *
	 * @param initialCapacity Capacidad inicial del conjunto de �ndices. Deber�
	 * 		  de ser la capacidad m�xima que pueda llegar a tener el conjunto
	 */
	public VariableMemoryIndexSet(int initialCapacity) {
		indexes = new long[initialCapacity];
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() {
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#indexSetComplete
	 */
	public void indexSetComplete() {
		long[] aux = new long[count];
		System.arraycopy(indexes, 0, aux, 0, count);
		indexes = aux;
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#addIndex(long)
	 */
	public void addIndex(long index) {
		indexes[count] = index;
		count++;
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#getIndexCount(long)
	 */
	public long getIndexCount() {
		return count;
	}

	/**
	 * @see org.gdms.data.indexes.VariableIndexSet#getIndexes()
	 */
	public long[] getIndexes() {
		return this.indexes;
	}
}

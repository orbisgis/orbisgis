package org.gdms.sql.indexes;

import java.util.ArrayList;

/**
 * Implementaci�n de un conjunto de �ndices en memoria, aunque puede haber un
 * conjunto de Long.MAXVALUE �ndices, en memoria, el tama�o m�ximo es de
 * Integer.MAXVALUE. Otras implementaciones de VariableIndexSet en memoria
 * pueden no tener esta restricci�n
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class VariableMemoryIndexSet extends MemoryIndexSet implements
		VariableIndexSet {
	private int count = 0;

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#open(java.io.File)
	 */
	public void open() {
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#addIndex(long)
	 */
	public void addIndex(long index) {
		indexes.add(index);
		count++;
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#getIndexCount(long)
	 */
	public long getIndexCount() {
		return count;
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#getIndexes()
	 */
	public ArrayList<Long> getIndexes() {
		return this.indexes;
	}
}

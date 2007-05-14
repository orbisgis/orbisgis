/*
 * Created on 16-oct-2004
 */
package org.gdms.data.indexes;

import java.io.IOException;


/**
 * Interfaz a implementar por los �ndices sobre las tablas. Esta interfaz se
 * utiliza al filtrar una tabla, en la que se a�aden indices a la tabla
 * secuencialmente. Una vez se invoca el m�todo indexSetComplete ya no se
 * pueden meter m�s �ndices
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface VariableIndexSet extends BaseIndexSet {
	/**
	 * Cierra el conjunto de �ndices para el almacenamiento, a partir de una
	 * llamada a este m�todo no se puede escribir ning�n �ndice m�s, s�lamente
	 * se pueden leer
	 *
	 * @throws IOException Si se produce un fallo al cerrar los streams de
	 * 		   salida
	 */
	public void indexSetComplete() throws IOException;

	/**
	 * A�ade un �ndice al conjunto de �ndices
	 *
	 * @param value �ndice de la fila a la que apunta el �ndice que se quiere
	 * 		  a�adir
	 *
	 * @throws IOException Si se produce un fallo al escribir el �ndice
	 */
	public void addIndex(long value) throws IOException;

	/**
	 * Abre el almacenamiento del �ndice para la escritura de los �ndices. En
	 * caso de un almacenamiento permanente se usar� un fichero temporal
	 *
	 * @throws IOException Si se produce un fallo al abrir
	 */
	public void open() throws IOException;

	/**
	 * Obtiene los �ndices del conjunto de �ndices en un array
	 *
	 * @return long[]
	 *
	 * @throws IOException
	 */
	public long[] getIndexes() throws IOException;
}

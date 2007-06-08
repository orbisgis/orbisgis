/*
 * Created on 23-oct-2004
 */
package org.gdms.sql.indexes;

import java.io.IOException;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public interface BaseIndexSet {
	/**
	 * Cierra el fichero de �ndices
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al cerrar
	 */
	public void close() throws IOException;

	/**
	 * Devuelve el �ndice nth-�simo si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 * 
	 * @param nth
	 *            �ndice del �ndice que se quiere obtener
	 * 
	 * @return indice nth-�simo
	 * 
	 * @throws IOException
	 *             Si se produce un fallo al recuperar el �ndice
	 */
	public long getIndex(long nth) throws IOException;

	/**
	 * Devuelve el n�mero de �ndices si se invoc� previamente a indexSetComplete
	 * y lanza una excepci�n en caso contrario
	 * 
	 * @return Si se produce un fallo al obtener el n�mero de �ndices
	 */
	public long getIndexCount();
}

/*
 * Created on 23-oct-2004
 */
package org.gdms.data.indexes;

import java.io.File;
import java.io.IOException;


/**
 * Los �ndices fijos se establecen sobre los campos de los DataSource para
 * acelerar el acceso a un determinado valor
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface FixedIndexSet extends BaseIndexSet {
	/**
	 * establece el �ndice 'index'-�simo para que apunte a la fila 'value'
	 *
	 * @param index �ndice que se quiere cambiar
	 * @param value �ndice de la fila a la que apunta este �ndice
	 *
	 * @throws IOException Si se produce un fallo al escribir el �ndice
	 */
	public void setIndex(long index, long value) throws IOException;

	/**
	 * Abre el almacenamiento del �ndice para la escritura de los �ndices
	 *
	 * @param f fichero en el que se guardar� el �ndice
	 *
	 * @throws IOException Si se produce un fallo al abrir
	 */
	public void open(File f) throws IOException;
}

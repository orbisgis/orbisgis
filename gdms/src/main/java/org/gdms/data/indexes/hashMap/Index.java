package org.gdms.data.indexes.hashMap;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface Index {
	/**
	 * Invocado cuando se va a comenzar una operaci�n de escritura con la
	 * estructura de datos
	 *
	 * @throws IndexException DOCUMENT ME!
	 */
	public void start() throws IndexException;

	/**
	 * Invocado cuando se termina la operaci�n de escritura con el �ndice
	 *
	 * @throws IndexException DOCUMENT ME!
	 */
	public void stop() throws IndexException;

	/**
	 * A�ade la posici�n de un valor al �ndice. Posiblemente ya haya una o
	 * varias posiciones para dicho valor tomando como funci�n de identidad el
	 * m�todo equals de Value. En dicho caso se deber�n mantener todas estas
	 *
	 * @param v Valor
	 * @param position posici�n del Valor dentro del DataSource
	 *
	 * @throws IndexException
	 */
	public void add(Object v, int position) throws IndexException;

	/**
	 * Obtiene un iterador para iterar sobre las posiciones sobre las que puede
	 * haber valores iguales al que se pasa como par�metro. No todas las
	 * posiciones se deben corresponder necesariamente con registros que
	 * contengan el valor buscado pero todas las posiciones de los registros
	 * que contengan value estar�n en las posiciones que se retornen.
	 *
	 * @param v Value
	 *
	 * @return Objeto para iterar por las posiciones
	 *
	 * @throws IndexException
	 */
	public PositionIterator getPositions(Object v) throws IndexException;
}

package org.gdms.data;

/**
 * Excepci�n que indica que a partir del nombre de una tabla no ha sido posible
 * encontrar el fichero asociado. Entre las causas comunes que ocasionan una
 * excepci�n de este tipo se encuentran el escribir mal el nombre de la tabla,
 * el no haber a�adido la tabla al sistema o el haber cambiado el fichero
 * asociado a la tabla de lugar
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class NoSuchTableException extends Exception {
    /**
     * Creates a new NoSuchTableException object.
     */
    public NoSuchTableException() {
        super();
    }

    /**
     * Creates a new NoSuchTableException object.
     *
     * @param arg0
     */
    public NoSuchTableException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new NoSuchTableException object.
     *
     * @param arg0
     */
    public NoSuchTableException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Creates a new NoSuchTableException object.
     *
     * @param arg0
     * @param arg1
     */
    public NoSuchTableException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}

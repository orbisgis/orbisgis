package org.gdms.driver.driverManager;

/**
 * Interfaz que debe ser implementada por todos l
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface Driver {
    /**
     * Debe devolver un objeto que se asociar� en el manager al driver. El
     * DriverManager recibir� peticiones de un objeto y deber� devolver el
     * driver asociado
     *
     * @return Objeto asociado al driver. Se recomienda que sea un String
     */
    public String getName();
}

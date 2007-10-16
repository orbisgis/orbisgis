package org.gdms.driver.driverManager;

/**
 * Interfaz a implementar por los objetos de validaci�n
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface DriverValidation {
    /**
     * El m�todo validate se invocar� al crear los drivers, y ser� el validador
     * el que indicar� qu� driver es v�lido para la aplicaci�n y cual no
     *
     * @param d Driver a validar
     *
     * @return true o false en caso de que el driver sea o no sea valido
     */
    public boolean validate(Driver d);
}

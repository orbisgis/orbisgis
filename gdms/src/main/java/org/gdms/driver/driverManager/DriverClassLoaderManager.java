package org.gdms.driver.driverManager;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Esta clase mantiene la informaci�n sobre los classloader de los plugins con
 * la intenci�n de poder obtener dado el nombre de una clase la lista de
 * PluginClassLoader que pueden cargarla
 *
 * @author Fernando Gonz�lez Cort�s
 */
public abstract class DriverClassLoaderManager {
    private static Hashtable nombresLista = new Hashtable();

    /**
     * Registra un class loader para una clase determinada
     *
     * @param className Nombre de la clase
     * @param cl Classloader que puede cargar la clase
     */
    public static void registerClass(String className, ClassLoader cl) {
        Vector lista = (Vector) nombresLista.get(className);

        if (lista == null) {
            lista = new Vector();
            lista.add(cl);
            nombresLista.put(className, lista);
        } else {
            lista.add(cl);
        }
    }

    /**
     * Devuelve la lista de classloader que pueden cargar la clase
     *
     * @param className Nombre de la clase de la cual se quiere obtener un
     *        classloader que la cargue
     *
     * @return Vector de classLoaders que pueden cargar una clase con ese
     *         nombre
     */
    public static Vector getClassLoaderList(String className) {
        return (Vector) nombresLista.get(className);
    }
}

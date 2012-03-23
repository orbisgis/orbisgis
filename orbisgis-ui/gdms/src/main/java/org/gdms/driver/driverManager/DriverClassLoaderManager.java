/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.driver.driverManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Esta clase mantiene la informaci�n sobre los classloader de los plugins con
 * la intenci�n de poder obtener dado el nombre de una clase la lista de
 * PluginClassLoader que pueden cargarla
 *
 */
public final class DriverClassLoaderManager {
	private static HashMap<String, ArrayList<DriverClassLoader>> nombresLista = new HashMap<String, ArrayList<DriverClassLoader>>();

	/**
	 * Registra un class loader para una clase determinada
	 *
	 * @param className
	 *            Nombre de la clase
	 * @param cl
	 *            Classloader que puede cargar la clase
	 */
	public static void registerClass(String className, DriverClassLoader cl) {
		ArrayList<DriverClassLoader> lista = nombresLista.get(className);

		if (lista == null) {
			lista = new ArrayList<DriverClassLoader>();
			lista.add(cl);
			nombresLista.put(className, lista);
		} else {
			lista.add(cl);
		}
	}

	/**
	 * Devuelve la lista de classloader que pueden cargar la clase
	 *
	 * @param className
	 *            Nombre de la clase de la cual se quiere obtener un classloader
	 *            que la cargue
	 *
	 * @return Vector de classLoaders que pueden cargar una clase con ese nombre
	 */
	public static ArrayList<DriverClassLoader> getClassLoaderList(String className) {
		return nombresLista.get(className);
	}

    private DriverClassLoaderManager() {
    }
}

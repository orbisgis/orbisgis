/* gvSIG. Sistema de Informacin Geogrfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gdms.driver.shapefile;

/**
 * Clase con las constantes que representan los diferentes tipos de Shape y
 * metodos estaticos relativos a los shapes.
 * 
 * @author Vicente Caballero Navarro
 */
public class SHP {
	public static final int NULL = 0;

	public static final int POINT2D = 1;

	public static final int POLYLINE2D = 3;

	public static final int POLYGON2D = 5;

	public static final int MULTIPOINT2D = 8;

	public static final int POINT3D = 11;

	public static final int POLYLINE3D = 13;

	public static final int POLYGON3D = 15;

	public static final int MULTIPOINT3D = 18;
}

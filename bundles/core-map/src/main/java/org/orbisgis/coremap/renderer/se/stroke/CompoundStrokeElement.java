/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.stroke;

import net.opengis.se._2_0.core.AlternativeStrokeElementsType;
import net.opengis.se._2_0.core.StrokeElementType;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;

/**
 * {@code CompoundStrokeElement} is an abstract class. Thanks to it, it becomes 
 * possible to use {@code StrokeElement} and {@code AlternativeStrokeElements} 
 * instances equally in some places.
 * @author Maxence Laurent, Alexis Guéganno
 */
public abstract class CompoundStrokeElement extends AbstractSymbolizerNode {

        /**
        * Get a concrete instance of {@code CompoundStrokeElement} using the object
        * given in argument.
        * @param o
        * @return
        * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
        */
	public static CompoundStrokeElement createCompoundStrokeElement(Object o) throws InvalidStyle{
		if (o instanceof StrokeElementType){
			return new StrokeElement((StrokeElementType)o);
		}else if (o instanceof AlternativeStrokeElementsType){
			return new AlternativeStrokeElements((AlternativeStrokeElementsType)o);
		}

		return null;
	}

        /**
         * Get the JAXB representation of this object.
         * @return 
         */
	public abstract Object getJAXBType();

}

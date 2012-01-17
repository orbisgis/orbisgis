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



package org.orbisgis.core.renderer.se.stroke;

import net.opengis.se._2_0.core.AlternativeStrokeElementsType;
import net.opengis.se._2_0.core.StrokeElementType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 * {@code CompoundStrokeElement} is an abstract class. Thanks to it, it becomes 
 * possible to use {@code StrokeElement} and {@code AlternativeStrokeElements} 
 * instances equally in some places.
 * @author maxence, alexis
 */
public abstract class CompoundStrokeElement implements SymbolizerNode {

    protected SymbolizerNode parent;

        /**
        * Get a concrete instance of {@code CompoundStrokeElement} using the object
        * given in argument.
        * @param o
        * @return
        * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
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
	public abstract Object getJaxbType();

	@Override
	public Uom getUom() {
		return parent.getUom();
	}

	@Override
	public SymbolizerNode getParent() {
		return parent;
	}

	@Override
	public void setParent(SymbolizerNode node) {
		this.parent = node;
	}

}

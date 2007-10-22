/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.instruction;

import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.SimpleNode;

/**
 * Clase base para todos los adaptadores de elementos del arbol sint�ctico
 * generado por el parser a elementos descendientes de SelectInstruction
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class Adapter {
	private Adapter parent = null;

	private Adapter[] childs;

	private Node entity;

	private InstructionContext ic;

	/**
	 * set the context of the instruction being executed. Should be invoked on
	 * the root adapter to make all the adapter nodes have the same instruction
	 * context
	 * 
	 * @param ic
	 *            instruction context to set
	 */
	public void setInstructionContext(InstructionContext ic) {
		this.ic = ic;

		Adapter[] hijos = getChilds();

		for (int i = 0; i < hijos.length; i++) {
			hijos[i].setInstructionContext(ic);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public InstructionContext getInstructionContext() {
		return ic;
	}

	/**
	 * Establece la entidad del arbol sint�ctico de la que es adaptador este
	 * objeto
	 * 
	 * @param o
	 *            Nodo de arbol sint�ctico
	 */
	public void setEntity(Node o) {
		entity = o;
	}

	/**
	 * Obtiene la entidad del arbol sint�ctico de la que es adaptador este
	 * objeto
	 * 
	 * @return Nodo del arbol sint�ctico
	 */
	public SimpleNode getEntity() {
		return (SimpleNode) entity;
	}

	/**
	 * A�ade un hijo al adaptador
	 * 
	 * @param a
	 *            Adaptador hijo
	 */
	public void setChilds(Adapter[] a) {
		childs = a;
	}

	/**
	 * Obtiene el array de hijos del adaptador
	 * 
	 * @return Array de hijos del adaptador. Si no existe ning�n hijo se retorna
	 *         un array vac�o
	 */
	public Adapter[] getChilds() {
		return childs;
	}

	/**
	 * Establece el padre del nodo en el arbol de adaptadores
	 * 
	 * @param parent
	 */
	protected void setParent(Adapter parent) {
		this.parent = parent;
	}

	/**
	 * En los �rboles de expresiones es com�n tener varios adaptadores que lo
	 * �nico que hacen es devolver el valor de su �nico hijo. Para evitar esto
	 * se pone al hijo en contacto directo con el padre invocando directamente
	 * este m�todo
	 * 
	 * @param child
	 *            Hijo a sustituir
	 * @param newChild
	 *            Hijo que reemplaza al anterior
	 */
	protected void replaceChild(Adapter child, Adapter newChild) {
		for (int i = 0; i < childs.length; i++) {
			if (childs[i] == child) {
				childs[i] = newChild;
			}
		}
	}

	/**
	 * Obtiene el padre de este adaptador en el arbol de adaptadores
	 * 
	 * @return Returns the parent.
	 */
	public Adapter getParent() {
		return parent;
	}
}

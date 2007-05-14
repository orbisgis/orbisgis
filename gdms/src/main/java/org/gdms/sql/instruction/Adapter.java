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
	 * the root adapter to make all the adapter nodes have the same
	 * instruction context
	 *
	 * @param ic instruction context to set
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
	 * @param o Nodo de arbol sint�ctico
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
	 * @param a Adaptador hijo
	 */
	public void setChilds(Adapter[] a) {
		childs = a;
	}

	/**
	 * Obtiene el array de hijos del adaptador
	 *
	 * @return Array de hijos del adaptador. Si no existe ning�n hijo se
	 * 		   retorna un array vac�o
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
	 * @param child Hijo a sustituir
	 * @param newChild Hijo que reemplaza al anterior
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

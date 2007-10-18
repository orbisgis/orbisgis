package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public interface IResource {

	/**
	 * Add a child to this resource
	 *
	 * @param ressourceChild :
	 *            the child you want to add
	 */
	public void addChild(IResource child);

	/**
	 * Add a child to this resource at a specified index
	 *
	 * @param ressourceChild :
	 *            the child you want to add
	 * @param index
	 */
	public void addChild(IResource child, int index);

	/**
	 * depthChildList() allows to retrieve all children (including children of
	 * subnodes) from the deepest to the closest. EG : root->A, B; A->A1, A2,
	 * A3->A33; B->B1 root.depthChildList() will return A1, A2, A33, A3, A, B1,
	 * B
	 *
	 * @return
	 */
	public ArrayList<IResource> depthChildList();

	/**
	 *
	 * @return the child at position specified by index
	 */
	public IResource getChildAt(int index);

	/**
	 *
	 * @return the number of children
	 */
	public int getChildCount();

	/**
	 *
	 * @return the children of this resource
	 */
	public IResource[] getChildren();

	/**
	 *
	 * @return an Icon which is displayed in the catalog
	 */
	public Icon getIcon(boolean isExpanded);

	/**
	 *
	 * @param child
	 * @return the position of child
	 */
	public int getIndexOfChild(IResource child);

	/**
	 *
	 * @return a String which is the name displayed in the catalog
	 */
	public String getName();

	/**
	 *
	 * @return The parent of this resource
	 */
	public IResource getParent();

	/**
	 * Retrieves the IResources to root. First element is root, last element is
	 * this IResource
	 *
	 * @return
	 */
	public IResource[] getPath();

	/**
	 * Retrieves a list of items to add when user right clic on the node.
	 *
	 * @return
	 */
	public JMenuItem[] getPopupActions();

	/**
	 * Remove a child
	 *
	 * @param ressourceChild
	 */
	public void removeChild(IResource child);

	/**
	 *
	 * @param newName
	 *            set the name to newName
	 */
	public void setName(String newName);

	/**
	 *
	 * @return true if the name can be changed
	 */
	public boolean canChangeName();

	/**
	 * Set ressource's parent
	 *
	 * @param parent
	 */
	public void setParent(IResource parent);

}

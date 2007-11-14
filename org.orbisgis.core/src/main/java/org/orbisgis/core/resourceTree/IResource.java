package org.orbisgis.core.resourceTree;

import java.util.ArrayList;

import javax.swing.Icon;

public interface IResource {

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

	/**
	 * Adds this resource to the specified parent resource
	 *
	 * @param parent
	 */
	public void addTo(IResource parent);

	/**
	 * Removes this resource from the specified parent resource.
	 *
	 * @param parent
	 */
	public void removeFrom(IResource parent);

	/**
	 * Moves this resource to the specified destination
	 *
	 * @param dropNode
	 */
	public void move(IResource dropNode);

}

package org.orbisgis.views.geocognition.sync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.geocognition.sync.tree.TreeElement;

public class SyncManager {
	// Sets to determine additions, deletions, modifications and conflicts
	private HashSet<ArrayList<String>> added, deleted, contentModified,
			conflict;

	// Local, remote and difference root trees
	private GeocognitionElementDecorator localRoot, remoteRoot;
	private TreeElement differenceRoot;

	// Determines if the remote source is editable
	private boolean remoteEditable;

	// Contains the progress percentage of the synchronization
	private float progress;

	// Listeners
	private ArrayList<SyncListener> listenerList;

	/**
	 * Creates a new synchronization manager
	 */
	public SyncManager() {
		listenerList = new ArrayList<SyncListener>();
	}

	/**
	 * Compares the two given elements
	 * 
	 * @param local
	 *            element to compare
	 * @param remote
	 *            element to compare
	 * @param remoteEditable
	 *            flag to determine if the remote element is editable
	 * @throws IllegalArgumentException
	 *             if <code>local</code> or <code>root</code> parameters are
	 *             <code>null</code>
	 */
	public void compare(GeocognitionElement local, GeocognitionElement remote,
			boolean remoteEditable) throws IllegalArgumentException {
		compare(local, remote, remoteEditable, null);
	}

	/**
	 * Compares the two given elements
	 * 
	 * @param local
	 *            element to compare
	 * @param remote
	 *            element to compare
	 * @param remoteEditable
	 *            flag to determine if the remote element is editable
	 * @param pm
	 *            the progress monitor of the synchronization
	 * @throws IllegalArgumentException
	 *             if <code>local</code> or <code>root</code> parameters are
	 *             <code>null</code>
	 */
	public void compare(GeocognitionElement local, GeocognitionElement remote,
			boolean remoteEditable, IProgressMonitor pm)
			throws IllegalArgumentException {
		if (local == null || remote == null) {
			throw new IllegalArgumentException("Both trees must not be null");
		} else if (!local.getId().equalsIgnoreCase(remote.getId())) {
			remote.setId(local.getId());
		}

		// Get roots
		localRoot = (local instanceof GeocognitionElementDecorator) ? (GeocognitionElementDecorator) local
				: new GeocognitionElementDecorator(local);
		remoteRoot = (remote instanceof GeocognitionElementDecorator) ? (GeocognitionElementDecorator) remote
				: new GeocognitionElementDecorator(remote);

		// Initialize attributes
		this.remoteEditable = remoteEditable;
		added = new HashSet<ArrayList<String>>();
		deleted = new HashSet<ArrayList<String>>();
		contentModified = new HashSet<ArrayList<String>>();
		conflict = new HashSet<ArrayList<String>>();

		if (localRoot == remoteRoot) {
			differenceRoot = null;
		} else {
			// Synchronize
			differenceRoot = copyTreeStructure(localRoot);
			ArrayList<String> localPath = new ArrayList<String>();
			localPath.add(localRoot.getId());
			synchronize(localPath, pm);
		}
	}

	/**
	 * Copies the tree structure of the given node into a new NodeDecorator
	 * 
	 * @param original
	 *            the node with the structure to copy
	 * @return a Node with the same tree structure as the given node using
	 *         DefaultNodeContainer and DefaultNodeLeaf
	 */
	private TreeElement copyTreeStructure(GeocognitionElementDecorator original) {
		TreeElement copy = new TreeElement(original);

		if (original.isFolder()) {
			for (int i = 0; i < original.getElementCount(); i++) {
				TreeElement copyChild = copyTreeStructure(original
						.getElement(i));
				copy.addElement(copyChild);
			}
		}

		return copy;
	}

	/**
	 * Performs a synchronization for the specified path, updating the
	 * <code> added, deleted, contentModified,</code> and <code>conflict</code>
	 * sets as well as the <code>differenceRoot</code>
	 * 
	 * @param path
	 *            the path to the element to synchronize
	 * @param monitor
	 *            the progress monitor of the synchronization or
	 *            <code>null</code> if no progress monitor must be used
	 */
	private void synchronize(ArrayList<String> path, IProgressMonitor monitor) {
		if (path == null) {
			return;
		}

		unmark(path, added);
		unmark(path, deleted);
		unmark(path, conflict);
		unmark(path, contentModified);

		progress = 0;
		TreeElement subTree = doComparison(path, monitor, 100);

		if (path.size() == 1) {
			// If synchronized element is root
			differenceRoot = subTree;
		} else if (path.size() > 1) {
			// If synchronized element is not root path, get the parent of
			// the synchronized element in the difference tree
			TreeElement parent = (differenceRoot == null) ? differenceRoot
					: differenceRoot.find(path, path.size() - 1);

			// If the parent exists, remove the previous child and add the new
			// synchronized one
			if (parent != null) {
				parent.removeElement(path.get(path.size() - 1));
				if (subTree != null) {
					parent.addElement(subTree);
				}
			} else if (subTree != null) {
				// If parent is null but the synchronized element has changed,
				// build the minimum directory structure in the difference tree.
				// This code is executed to build a new difference tree when
				// the previous one has no differences (is null)
				TreeElement child = subTree;
				GeocognitionElement childElement = find(localRoot, path);
				GeocognitionElement parentElement = childElement.getParent();
				while (parentElement != null) {
					parent = new TreeElement(parentElement);
					parent.addElement(child);
					child = parent;
					childElement = parentElement;
					parentElement = parentElement.getParent();
				}
				differenceRoot = parent;
			}
		} else {
			Services.getErrorManager().error(
					"bug!",
					new RuntimeException("The given path " + path
							+ "is not a valid one"));
		}

		if (differenceRoot != null) {
			ArrayList<String> aux = new ArrayList<String>();
			aux.add(differenceRoot.getId());
			removeEmptyFolders(aux);

			if (differenceRoot.isFolder()
					&& differenceRoot.getElementCount() == 0) {
				differenceRoot = null;
			}
		}

		fireResync();
	}

	/**
	 * Compares the specified path between the local and remote roots.
	 * 
	 * @param path
	 *            the path to the element to compare
	 * @param monitor
	 *            the progress monitor of the synchronization or
	 *            <code>null</code> if no progress monitor must be used
	 * @param unitWeight
	 *            the percentage of the synchronization represented by this
	 *            path. Only used when <code>monitor</code> is not null
	 * @return a TreeElement with the structure of the differences between both
	 *         trees for the specified path or <code>null</code> if there are no
	 *         differences
	 */
	private TreeElement doComparison(ArrayList<String> path,
			IProgressMonitor monitor, float unitWeight) {
		GeocognitionElementDecorator a = find(localRoot, path);
		GeocognitionElementDecorator b = find(remoteRoot, path);

		if (a == null && b == null) {
			TreeElement parent = differenceRoot.find(path, path.size() - 1);
			if (parent != null) {
				parent.removeElement(path.get(path.size() - 1));
			} else {
				Services.getErrorManager().error(
						"bug!",
						new RuntimeException("The parent of the given path "
								+ path + "is not synchronized"));
			}
			return null;
		} else if (a == null) {
			mark(b, deleted);
			if (monitor != null) {
				monitor.progressTo(Math.round(progress += unitWeight));
			}
			return copyTreeStructure(b);
		} else if (b == null) {
			mark(a, added);
			if (monitor != null) {
				monitor.progressTo(Math.round(progress += unitWeight));
			}
			return copyTreeStructure(a);
		}

		if ((a.isFolder() ^ b.isFolder())) {
			if (monitor != null) {
				monitor.progressTo(Math.round(progress += unitWeight));
			}
			// Return the folder structure
			if (a.isFolder()) {
				mark(a, conflict);
				return copyTreeStructure(a);
			} else {
				mark(b, conflict);
				return copyTreeStructure(b);
			}
		} else if (a.isFolder() && b.isFolder()) {
			return compareFolders(a, b, monitor, unitWeight);
		} else {
			try {
				return compareLeaves(a, b, monitor, unitWeight);
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"An error has ocurred reading element contents", e);
				return null;
			}
		}
	}

	/**
	 * Compares two folders and returns a TreeElement with the structure of the
	 * differences between both folders
	 * 
	 * @param a
	 *            folder to compare
	 * @param b
	 *            folder to compare
	 * @param monitor
	 *            the progress monitor of the synchronization or
	 *            <code>null</code> if no progress monitor must be used
	 * @param unitWeight
	 *            the percentage of the synchronization represented by this
	 *            path. Only used when <code>monitor</code> is not null
	 * @return a TreeElement with the structure of the differences between both
	 *         folders or <code>null</code> if there are no differences
	 */
	private TreeElement compareFolders(GeocognitionElementDecorator a,
			GeocognitionElementDecorator b, IProgressMonitor monitor,
			float unitWeight) {
		float childWeight = unitWeight
				/ (a.getElementCount() + b.getElementCount());

		// Get id path
		ArrayList<String> path = new ArrayList<String>();
		Collections.addAll(path, a.getIdPath().split("/"));

		// Difference structure is created adding and removing elements
		// in the 'a' folder structure
		TreeElement difference = new TreeElement(a);

		// Check 'a' folder
		for (int i = 0; i < a.getElementCount(); i++) {
			GeocognitionElementDecorator aChild = a.getElement(i);
			GeocognitionElementDecorator bChild = b.getElement(aChild.getId());
			if (bChild != null) {
				// If 'a' child is in 'b' folder, compare children
				path.add(aChild.getId());
				TreeElement childTree = doComparison(path, monitor, childWeight);
				if (childTree != null) {
					difference.addElement(childTree);
				}
				path.remove(path.size() - 1);
			} else {
				// If 'a' child is not in 'b' folder
				difference.addElement(copyTreeStructure(aChild));
				mark(aChild, added);
				if (monitor != null) {
					monitor.progressTo(Math.round(progress += childWeight));
				}
			}
		}

		// Check 'b' folder
		for (int i = 0; i < b.getElementCount(); i++) {
			GeocognitionElementDecorator bChild = b.getElement(i);
			GeocognitionElementDecorator aChild = a.getElement(bChild.getId());
			// If 'b' child is not in 'a' folder
			if (aChild == null) {
				difference.addElement(copyTreeStructure(bChild));
				mark(bChild, deleted);
			}
			if (monitor != null) {
				monitor.progressTo(Math.round(progress += childWeight));
			}
		}

		return (difference.getElementCount() == 0) ? null : difference;
	}

	/**
	 * Compares two leaves and returns a TreeElement (single leaf) with the
	 * difference between both leaves
	 * 
	 * @param a
	 *            leaf to compare
	 * @param b
	 *            leaf to compare
	 * @param monitor
	 *            the progress monitor of the synchronization or
	 *            <code>null</code> if no progress monitor must be used
	 * @param unitWeight
	 *            the percentage of the synchronization represented by this
	 *            path. Only used when <code>monitor</code> is not null
	 * 
	 * @return a TreeElement (single leaf) with the difference between both
	 *         leaves or null if both leaves are equal
	 * @throws GeocognitionException
	 */
	private TreeElement compareLeaves(GeocognitionElementDecorator a,
			GeocognitionElementDecorator b, IProgressMonitor monitor,
			float unitWeight) throws GeocognitionException {
		// Get id path
		ArrayList<String> path = new ArrayList<String>();
		Collections.addAll(path, a.getIdPath().split("/"));

		TreeElement difference = null;

		if (monitor != null) {
			monitor.progressTo(Math.round(progress += unitWeight));
		}

		if (!a.getXMLContent().equals(b.getXMLContent())) {
			if (a.getTypeId().equals(b.getTypeId())) {
				ArrayList<String> aux = new ArrayList<String>(path);
				contentModified.add(aux);
			} else {
				ArrayList<String> aux = new ArrayList<String>(path);
				conflict.add(aux);
			}
			difference = new TreeElement(a);
		}

		return difference;
	}

	/**
	 * Removes the empty folders of the difference tree
	 * 
	 * @param path
	 *            the path to the folder to scan
	 * @return true if the specified path has content changed (must be shown in
	 *         the difference tree), false otherwise
	 */
	private boolean removeEmptyFolders(ArrayList<String> path) {
		TreeElement root = differenceRoot.find(path);

		if (!root.isFolder()) {
			return true;
		}

		boolean hasContent = hasChanged(path);
		for (int i = root.getElementCount() - 1; i >= 0; i--) {
			TreeElement child = root.getElement(i);
			if (child.isFolder()) {
				path.add(child.getId());
				boolean childHasContent = removeEmptyFolders(path);
				if (!childHasContent) {
					root.removeElement(child.getId());
				}
				hasContent |= childHasContent;
				path.remove(path.size() - 1);
			} else {
				hasContent = true;
			}
		}

		return hasContent;
	}

	/**
	 * Adds to the specified set the given element and all of his children
	 * 
	 * @param element
	 *            the element to mark
	 * @param markSet
	 *            the set where the element must be added
	 */
	private void mark(GeocognitionElementDecorator element,
			HashSet<ArrayList<String>> markSet) {
		ArrayList<String> path = new ArrayList<String>();
		Collections.addAll(path, element.getIdPath().split("/"));
		markSet.add(path);
		if (element.isFolder()) {
			for (int i = 0; i < element.getElementCount(); i++) {
				mark(element.getElement(i), markSet);
			}
		}
	}

	/**
	 * Removes from the given set all the occurrences with the specified
	 * starting path
	 * 
	 * @param startingPath
	 *            the starting path to check in the mark set
	 * @param markSet
	 *            the set with the elements to remove
	 */
	private void unmark(ArrayList<String> startingPath,
			HashSet<ArrayList<String>> markSet) {
		// References to the elements to remove
		ArrayList<ArrayList<String>> remove = new ArrayList<ArrayList<String>>();

		Iterator<ArrayList<String>> iterator = markSet.iterator();
		while (iterator.hasNext()) {
			ArrayList<String> marked = iterator.next();
			boolean matches = true;

			// If 'marked' starts with 'startingPath'
			if (marked.size() >= startingPath.size()) {
				for (int i = 0; matches && i < startingPath.size(); i++) {
					if (!marked.get(i).equals(startingPath.get(i))) {
						matches = false;
					}
				}
			} else {
				matches = false;
			}

			if (matches) {
				remove.add(marked);
			}
		}

		// Remove elements
		for (int i = 0; i < remove.size(); i++) {
			markSet.remove(remove.get(i));
		}
	}

	/**
	 * Performs a commit transaction on the given element
	 * 
	 * @param path
	 *            the path to the element to commit
	 * @throws UnsupportedOperationException
	 *             if the remote tree is not editable
	 */
	public void commit(ArrayList<String> path) {
		if (!remoteEditable) {
			throw new UnsupportedOperationException("The tree is not editable");
		}

		ArrayList<String> syncPath;
		if (isAdded(path)) {
			try {
				syncPath = add(localRoot, remoteRoot, path);
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"The element " + find(localRoot, path)
								+ "cannot be commited", e);
				syncPath = null;
			}
		} else if (isModified(path)) {
			syncPath = modify(localRoot, remoteRoot, path);
		} else if (isConflict(path)) {
			syncPath = replace(localRoot, remoteRoot, path);
		} else if (isDeleted(path)) {
			syncPath = delete(remoteRoot, path);
		} else {
			syncPath = null;
			// Perform commit over the children
			TreeElement last = differenceRoot.find(path);
			ArrayList<String> ids = new ArrayList<String>();
			for (int i = 0; i < last.getElementCount(); i++) {
				ids.add(last.getElement(i).getId());
			}

			for (String id : ids) {
				path.add(id);
				commit(new ArrayList<String>(path));
				path.remove(path.size() - 1);
			}
		}

		synchronize(syncPath, null);
	}

	/**
	 * Performs an update transaction over the given element
	 * 
	 * @param path
	 *            the path to the element to update
	 */
	public void update(ArrayList<String> path) {
		ArrayList<String> syncPath = path;
		if (isAdded(path)) {
			syncPath = delete(localRoot, path);
		} else if (isModified(path)) {
			modify(remoteRoot, localRoot, path);
		} else if (isConflict(path)) {
			syncPath = replace(remoteRoot, localRoot, path);
		} else if (isDeleted(path)) {
			try {
				syncPath = add(remoteRoot, localRoot, path);
			} catch (GeocognitionException e) {
				Services.getErrorManager().error(
						"The element " + find(remoteRoot, path)
								+ "cannot be updated", e);
				syncPath = null;
			}

		} else {
			syncPath = null;
			// Perform update over the children
			TreeElement last = differenceRoot.find(path);
			ArrayList<String> ids = new ArrayList<String>();
			for (int i = 0; i < last.getElementCount(); i++) {
				ids.add(last.getElement(i).getId());
			}

			for (String id : ids) {
				path.add(id);
				update(path);
				path.remove(path.size() - 1);
			}
		}

		synchronize(syncPath, null);
	}

	/**
	 * Adds the given path into the specified root
	 * 
	 * @param srcRoot
	 *            the root of the tree with the elements to add
	 * @param destRoot
	 *            the root of the tree where the elements must be added
	 * @param path
	 *            the path to the element to add
	 * @throws GeocognitionException
	 *             if the element cannot be added
	 */
	private ArrayList<String> add(GeocognitionElementDecorator srcRoot,
			GeocognitionElementDecorator destRoot, ArrayList<String> path)
			throws GeocognitionException {
		Geocognition gc = Services.getService(Geocognition.class);

		GeocognitionElementDecorator commonAncestor = getCloserCommonAncestor(
				path, destRoot);
		int index = path.indexOf(commonAncestor.getId()) + 1;
		GeocognitionElementDecorator child = find(srcRoot, path).cloneElement();

		// Create element adding structure from bottom to top
		for (int i = path.size() - 2; i >= index; i--) {
			// Create folder
			GeocognitionElement aux = gc.createFolder(path.get(i));
			GeocognitionElementDecorator parent;

			// Decorate folder
			if (!(aux instanceof GeocognitionElementDecorator)) {
				parent = new GeocognitionElementDecorator(aux);
			} else {
				parent = (GeocognitionElementDecorator) aux;
			}

			// Add child
			parent.addElement(child);
			child = parent;
		}

		// Add the top of the element structure created to the common ancestor
		commonAncestor.addElement(child);

		ArrayList<String> syncPath = new ArrayList<String>();
		Collections.addAll(syncPath, child.getIdPath().split("/"));
		return syncPath;
	}

	/**
	 * Removes the given path from the specified root
	 * 
	 * @param root
	 *            the root of the tree
	 * @param path
	 *            the elements to delete
	 */
	private ArrayList<String> delete(GeocognitionElementDecorator root,
			ArrayList<String> path) {
		GeocognitionElementDecorator parent = find(root, path, path.size() - 1);
		GeocognitionElementDecorator child = find(root, path);

		ArrayList<String> syncPath = new ArrayList<String>();
		Collections.addAll(syncPath, child.getIdPath().split("/"));

		parent.removeElement(path.get(path.size() - 1));

		return syncPath;
	}

	/**
	 * Performs a content modification for the specified path in the modifyRoot
	 * 
	 * @param originalRoot
	 *            the root with the original content
	 * @param modifyRoot
	 *            the root with the element to update
	 * @param path
	 *            the path to the element to update
	 */
	private ArrayList<String> modify(GeocognitionElementDecorator originalRoot,
			GeocognitionElementDecorator modifyRoot, ArrayList<String> path) {
		try {
			GeocognitionElementDecorator original = getCloserCommonAncestor(
					path, originalRoot);
			GeocognitionElementDecorator modify = getCloserCommonAncestor(path,
					modifyRoot);
			modify.setXMLContent(original.getXMLContent());

			ArrayList<String> syncPath = new ArrayList<String>();
			Collections.addAll(syncPath, modify.getIdPath().split("/"));
			return syncPath;
		} catch (GeocognitionException e) {
			Services.getErrorManager().error("The element cannot be commited",
					e);
			return null;
		}
	}

	/**
	 * Replaces the given path in the specified root (path must contain
	 * conflicts)
	 * 
	 * @param root
	 *            the root of the tree where the elements must be replaced
	 * @param path
	 *            the elements to replace
	 */
	private ArrayList<String> replace(
			GeocognitionElementDecorator originalRoot,
			GeocognitionElementDecorator replaceRoot, ArrayList<String> path) {
		// Get index of conflict root in the given path
		int i;
		ArrayList<String> auxPath = new ArrayList<String>();
		for (i = 0; i < path.size(); i++) {
			auxPath.add(path.get(i));
			if (isConflict(auxPath)) {
				break;
			}
		}

		if (i == path.size()) {
			Services.getErrorManager().error(
					"bug!",
					new RuntimeException("The given path " + path
							+ "is not marked as conflict"));
		}

		// index + 1 because index in the 'find' method is exclusive
		GeocognitionElementDecorator original = find(originalRoot, path, i + 1);
		GeocognitionElementDecorator parent = find(replaceRoot, path, i);

		parent.removeElement(path.get(i));
		parent.addElement(original);

		ArrayList<String> syncPath = new ArrayList<String>();
		Collections.addAll(syncPath, original.getIdPath().split("/"));

		return syncPath;
	}

	/**
	 * Gets the last element of the path that exists in the given node
	 * 
	 * @param path
	 *            the path to the searched element
	 * @param root
	 *            the node where the search is started
	 * 
	 * @return the last matching node between the path and the given node or
	 *         null if none
	 */
	private GeocognitionElementDecorator getCloserCommonAncestor(
			ArrayList<String> path, GeocognitionElementDecorator root) {
		GeocognitionElementDecorator parent = null;
		GeocognitionElementDecorator child = root;
		if (!path.get(0).equals(root.getId())) {
			return null;
		}

		for (int i = 1; i < path.size() && child != null; i++) {
			parent = child;
			child = parent.getElement(path.get(i));
		}

		return child == null ? parent : child;
	}

	/**
	 * Determines if the given path has been added in the local root
	 * 
	 * @param path
	 *            the path to the element
	 * @return true if the path is added, false otherwise
	 */
	public boolean isAdded(ArrayList<String> path) {
		return added.contains(path);
	}

	/**
	 * Determines if the given path has been deleted from the local root
	 * 
	 * @param path
	 *            the path to the element
	 * @return true if the path has been deleted, false otherwise
	 */
	public boolean isDeleted(ArrayList<String> path) {
		return deleted.contains(path);
	}

	/**
	 * Determines if the given path has content modified in the local root
	 * 
	 * @param path
	 *            the path to the element
	 * @return true if the path has content modified, false otherwise
	 */
	public boolean isModified(ArrayList<String> path) {
		return contentModified.contains(path);
	}

	/**
	 * Determines if the given path is a conflict between the local and remote
	 * root
	 * 
	 * @param path
	 *            the path to the element
	 * @return true if the path is a conflict, false otherwise
	 */
	public boolean isConflict(ArrayList<String> path) {
		return conflict.contains(path);
	}

	/**
	 * Determines if the given path is added, deleted, has content modifications
	 * or is a conflict
	 * 
	 * @param path
	 *            the path to the element to determine
	 * @return true if the element has changes, false otherwise
	 */
	private boolean hasChanged(ArrayList<String> path) {
		return isAdded(path) || isConflict(path) || isDeleted(path)
				|| isModified(path);
	}

	/**
	 * Determines if the remote root is editable
	 * 
	 * @return true if the remote root is editable, false otherwise
	 */
	public boolean isRemoteEditable() {
		return remoteEditable;
	}

	/**
	 * Gets the difference tree
	 * 
	 * @return the difference tree
	 */
	public TreeElement getDifferenceTree() {
		return differenceRoot;
	}

	/**
	 * Gets the local root element
	 * 
	 * @return the local root element
	 */
	public GeocognitionElementDecorator getLocalRoot() {
		return localRoot;
	}

	/**
	 * Gets the remote root element
	 * 
	 * @return the remote root element
	 */
	public GeocognitionElementDecorator getRemoteRoot() {
		return remoteRoot;
	}

	/**
	 * Finds the specified path in the local root
	 * 
	 * @param path
	 *            the path to the element to find
	 * @return the element for the specified path
	 */
	public GeocognitionElementDecorator findLocalElement(ArrayList<String> path) {
		return (localRoot == null) ? localRoot : find(localRoot, path);
	}

	/**
	 * Finds the specified path in the remote root
	 * 
	 * @param path
	 *            the path to the element to find
	 * @return the element for the specified path
	 */
	public GeocognitionElementDecorator findRemoteElement(ArrayList<String> path) {
		return (remoteRoot == null) ? remoteRoot : find(remoteRoot, path);
	}

	private GeocognitionElementDecorator find(
			GeocognitionElementDecorator root, ArrayList<String> path) {
		return find(root, path, path.size());
	}

	private GeocognitionElementDecorator find(
			GeocognitionElementDecorator root, ArrayList<String> path, int n) {
		if (path == null || !path.get(0).equalsIgnoreCase(root.getId())) {
			return null;
		}

		GeocognitionElementDecorator parent = root;
		for (int i = 1; i < n; i++) {
			GeocognitionElementDecorator child = parent.getElement(path.get(i));
			if (child == null) {
				return null;
			} else {
				parent = child;
			}
		}

		return parent;
	}

	/**
	 * Adds a synchronization listener to this manager
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addSyncListener(SyncListener l) {
		if (!listenerList.contains(l)) {
			listenerList.add(l);
		}
	}

	/**
	 * Removes a synchronization listener from this manager
	 * 
	 * @param l
	 *            the listener to remove
	 */
	public void removeSyncListener(SyncListener l) {
		listenerList.remove(l);
	}

	/**
	 * Calls all synchronization listeners
	 */
	private void fireResync() {
		for (SyncListener listener : listenerList) {
			listener.syncDone();
		}
	}
}

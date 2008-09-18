package org.orbisgis.views.geocognition.sync;

import java.util.ArrayList;
import java.util.HashSet;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.geocognition.sync.editor.EditorElementListener;
import org.orbisgis.views.geocognition.sync.tree.TreeElement;

public class SyncManager {
	// Sets to determine additions, deletions, modifications and conflicts
	private HashSet<IdPath> added, deleted, contentModified, conflict;

	// Paths that must be synchronized
	private ArrayList<IdPath> filterPaths;

	// Local, remote and difference root trees
	private GeocognitionElementDecorator localRoot, remoteRoot;
	private TreeElement differenceRoot;

	// Contains the progress percentage of the synchronization
	private float progress;

	// Listeners
	private ArrayList<SyncListener> listenerList;
	private EditorElementListener listener;

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
	void compare(GeocognitionElement local, GeocognitionElement remote,
			ArrayList<IdPath> filter) throws IllegalArgumentException {
		compare(local, remote, filter, null);
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
	void compare(GeocognitionElement local, GeocognitionElement remote,
			ArrayList<IdPath> filter, IProgressMonitor pm)
			throws IllegalArgumentException {
		if (local == null || remote == null) {
			throw new IllegalArgumentException("Both trees must not be null");
		} else if (!local.getId().equalsIgnoreCase(remote.getId())) {
			remote.setId(local.getId());
		}

		// Initialize attributes
		this.filterPaths = filter;
		added = new HashSet<IdPath>();
		deleted = new HashSet<IdPath>();
		contentModified = new HashSet<IdPath>();
		conflict = new HashSet<IdPath>();

		// Get roots
		localRoot = getDecoratedRoot(localRoot, local);
		remoteRoot = getDecoratedRoot(remoteRoot, remote);

		if (localRoot == remoteRoot) {
			differenceRoot = null;
		} else {
			// Synchronize
			differenceRoot = copyTreeStructure(localRoot);
			synchronize(new IdPath(localRoot.getId()), pm);
		}
	}

	/**
	 * Gets the a GeocognitionElementDecorator from the given
	 * GeocognitionElement. Removes the listener from the given root, creates a
	 * new root and adds the listener to the new root
	 * 
	 * @param root
	 *            the root to remove listener from
	 * @param element
	 *            element to decorate
	 * @return the decorated root with the listener
	 */
	private GeocognitionElementDecorator getDecoratedRoot(
			GeocognitionElementDecorator root, GeocognitionElement element) {
		if (root != null) {
			root.removeEditorElementListener(getEditorSavingListener());
		}

		root = (element instanceof GeocognitionElementDecorator) ? (GeocognitionElementDecorator) element
				: new GeocognitionElementDecorator(element, filterPaths);
		root.addEditorElementListener(getEditorSavingListener());

		return root;
	}

	/**
	 * Gets the editor saving listener for this synchronization manager
	 * 
	 * @return the editor saving listener for this synchronization manager
	 */
	private EditorElementListener getEditorSavingListener() {
		if (listener == null) {
			listener = new EditorElementListener() {
				@Override
				public void elementContentChanged(GeocognitionElement element) {
					synchronize(new IdPath(element.getIdPath()), null);
				}
			};
		}

		return listener;
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
	private void synchronize(IdPath path, IProgressMonitor monitor) {
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

			// If there's no parent for the element in the difference tree,
			// create de minimum folder structure
			if (parent == null) {
				int i = 0;
				if (differenceRoot != null) {
					// find last existing ancestor in the difference tree
					TreeElement aux = differenceRoot.find(path, 1);
					for (i = 1; i < path.size(); i++) {
						if (aux != null) {
							parent = aux;
							aux = differenceRoot.find(path, i + 1);
						} else {
							break;
						}
					}

				} else {
					differenceRoot = new TreeElement(localRoot);
					parent = differenceRoot;
				}

				for (; i < path.size(); i++) {
					TreeElement aux = new TreeElement(find(localRoot, path, i));
					parent.addElement(aux);
					parent = aux;
				}
			}

			// If the parent exists, remove the previous child and add the new
			// synchronized one
			if (parent != null) {
				parent.removeElement(path.getLast());
				if (subTree != null) {
					parent.addElement(subTree);
				}
			}
		} else {
			Services.getErrorManager().error(
					"bug!",
					new RuntimeException("The given path " + path
							+ "is not a valid one"));
		}

		if (differenceRoot != null) {
			removeEmptyFolders(new IdPath(differenceRoot.getId()));

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
	private TreeElement doComparison(IdPath path, IProgressMonitor monitor,
			float unitWeight) {
		GeocognitionElementDecorator a = find(localRoot, path);
		GeocognitionElementDecorator b = find(remoteRoot, path);

		if (a == null && b == null) {
			TreeElement parent = differenceRoot.find(path, path.size() - 1);
			if (parent != null) {
				parent.removeElement(path.getLast());
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
		IdPath path = new IdPath(a.getIdPath());

		// Difference structure is created adding and removing elements
		// in the 'a' folder structure
		TreeElement difference = new TreeElement(a);

		// Check 'a' folder
		for (int i = 0; i < a.getElementCount(); i++) {
			GeocognitionElementDecorator aChild = a.getElement(i);
			GeocognitionElementDecorator bChild = b.getElement(aChild.getId());
			if (bChild != null) {
				// If 'a' child is in 'b' folder, compare children
				path.addLast(aChild.getId());
				TreeElement childTree = doComparison(path, monitor, childWeight);
				if (childTree != null) {
					difference.addElement(childTree);
				}
				path.removeLast();
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
		TreeElement difference = null;

		if (monitor != null) {
			monitor.progressTo(Math.round(progress += unitWeight));
		}

		if (!a.getXMLContent().equals(b.getXMLContent())) {
			IdPath path = new IdPath(a.getIdPath());
			if (a.getTypeId().equals(b.getTypeId())) {
				contentModified.add(path);
			} else {
				conflict.add(path);
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
	private boolean removeEmptyFolders(IdPath path) {
		TreeElement root = differenceRoot.find(path);

		if (!root.isFolder()) {
			return true;
		}

		boolean hasContent = hasChanged(path);
		for (int i = root.getElementCount() - 1; i >= 0; i--) {
			TreeElement child = root.getElement(i);
			if (child.isFolder()) {
				path.addLast(child.getId());
				boolean childHasContent = removeEmptyFolders(path);
				if (!childHasContent) {
					root.removeElement(child.getId());
				}
				hasContent |= childHasContent;
				path.removeLast();
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
			HashSet<IdPath> markSet) {
		IdPath path = new IdPath(element.getIdPath());
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
	private void unmark(IdPath startingPath, HashSet<IdPath> markSet) {
		// References to the elements to remove
		ArrayList<IdPath> remove = new ArrayList<IdPath>();

		for (IdPath marked : markSet) {
			if (marked.startsWith(startingPath)) {
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
	public void commit(IdPath path) {
		IdPath syncPath;
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
			// Perform commit over the children
			syncPath = null;
			TreeElement last = differenceRoot.find(path);
			ArrayList<String> ids = new ArrayList<String>();
			for (int i = 0; i < last.getElementCount(); i++) {
				ids.add(last.getElement(i).getId());
			}

			for (String id : ids) {
				path.addLast(id);
				commit(path.copy());
				path.removeLast();
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
	public void update(IdPath path) {
		IdPath syncPath = path;
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
			// Perform update over the children
			syncPath = null;
			TreeElement last = differenceRoot.find(path);
			ArrayList<String> ids = new ArrayList<String>();
			for (int i = 0; i < last.getElementCount(); i++) {
				ids.add(last.getElement(i).getId());
			}

			for (String id : ids) {
				path.addLast(id);
				update(path.copy());
				path.removeLast();
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
	private IdPath add(GeocognitionElementDecorator srcRoot,
			GeocognitionElementDecorator destRoot, IdPath path)
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
				parent = new GeocognitionElementDecorator(aux, filterPaths);
			} else {
				parent = (GeocognitionElementDecorator) aux;
			}

			// Add child
			parent.addElement(child);
			child = parent;
		}

		// Add the top of the element structure created to the common ancestor
		commonAncestor.addElement(child);

		return new IdPath(child.getIdPath());
	}

	/**
	 * Removes the given path from the specified root
	 * 
	 * @param root
	 *            the root of the tree
	 * @param path
	 *            the elements to delete
	 */
	private IdPath delete(GeocognitionElementDecorator root, IdPath path) {
		GeocognitionElementDecorator parent = find(root, path, path.size() - 1);
		GeocognitionElementDecorator child = find(root, path);

		IdPath syncPath = new IdPath(child.getIdPath());

		parent.removeElement(path.getLast());

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
	private IdPath modify(GeocognitionElementDecorator originalRoot,
			GeocognitionElementDecorator modifyRoot, IdPath path) {
		try {
			GeocognitionElementDecorator original = getCloserCommonAncestor(
					path, originalRoot);
			GeocognitionElementDecorator modify = getCloserCommonAncestor(path,
					modifyRoot);
			modify.setXMLContent(original.getXMLContent());

			IdPath syncPath = new IdPath(modify.getIdPath());
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
	private IdPath replace(GeocognitionElementDecorator originalRoot,
			GeocognitionElementDecorator replaceRoot, IdPath path) {
		// Get index of conflict root in the given path
		int i;
		IdPath auxPath = new IdPath();
		for (i = 0; i < path.size(); i++) {
			auxPath.addLast(path.get(i));
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

		return new IdPath(original.getIdPath());
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
	private GeocognitionElementDecorator getCloserCommonAncestor(IdPath path,
			GeocognitionElementDecorator root) {
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
	public boolean isAdded(IdPath path) {
		return added.contains(path);
	}

	/**
	 * Determines if the given path has been deleted from the local root
	 * 
	 * @param path
	 *            the path to the element
	 * @return true if the path has been deleted, false otherwise
	 */
	public boolean isDeleted(IdPath path) {
		return deleted.contains(path);
	}

	/**
	 * Determines if the given path has content modified in the local root
	 * 
	 * @param path
	 *            the path to the element
	 * @return true if the path has content modified, false otherwise
	 */
	public boolean isModified(IdPath path) {
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
	public boolean isConflict(IdPath path) {
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
	public boolean hasChanged(IdPath path) {
		return isAdded(path) || isConflict(path) || isDeleted(path)
				|| isModified(path);
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
	public GeocognitionElementDecorator findLocalElement(IdPath path) {
		return (localRoot == null) ? localRoot : find(localRoot, path);
	}

	/**
	 * Finds the specified path in the remote root
	 * 
	 * @param path
	 *            the path to the element to find
	 * @return the element for the specified path
	 */
	public GeocognitionElementDecorator findRemoteElement(IdPath path) {
		return (remoteRoot == null) ? remoteRoot : find(remoteRoot, path);
	}

	/**
	 * Finds the specified IdPath in the given root
	 * 
	 * @param root
	 *            the element where the search must be performed
	 * @param path
	 *            the IdPath of the element to find
	 * @return the element with the specified IdPath in the given root or null
	 *         if there's no such element
	 */
	private GeocognitionElementDecorator find(
			GeocognitionElementDecorator root, IdPath path) {
		return find(root, path, path.size());
	}

	/**
	 * Finds the specified IdPath in the given root
	 * 
	 * @param root
	 *            the element where the search must be performed
	 * @param path
	 *            the IdPath of the element to find
	 * @param n
	 *            the last element of the IdPath to use <b>(exlcusive index)</b>
	 * @return the element with the specified IdPath in the given root or null
	 *         if there's no such element
	 */
	private GeocognitionElementDecorator find(
			GeocognitionElementDecorator root, IdPath path, int n) {
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

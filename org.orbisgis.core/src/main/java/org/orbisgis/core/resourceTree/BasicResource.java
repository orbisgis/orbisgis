package org.orbisgis.core.resourceTree;

import java.util.ArrayList;

public abstract class BasicResource extends AbstractResource {

	private String name = null;

	private ArrayList<IResource> children = null;

	private boolean foldersFirst = true;

	public BasicResource(String name) {
		this.name = name;
		children = new ArrayList<IResource>();
	}

	public void addChild(IResource child) {
		addChild(child, children.size());
	}

	public void addChild(IResource child, int index) {
		children.add(index, child);
		child.setParent(this);
		if (foldersFirst) {
			groupFoldersFirst();
		}
	}

	private void groupFoldersFirst() {
		int firstNoFolder = -1;
		for (int i = 0; i < children.size(); i++) {
			if (!(children.get(i) instanceof Folder)) {
				firstNoFolder = i;
				break;
			}
		}

		// Move all folders before this element
		if (firstNoFolder != -1) {
			for (int i = firstNoFolder + 1; i < children.size(); i++) {
				if (children.get(i) instanceof Folder) {
					children.add(firstNoFolder, children.remove(i));
					firstNoFolder++;
				}
			}
		}
	}

	public boolean canChangeName() {
		return true;
	}

	public ArrayList<IResource> depthChildList() {
		ArrayList<IResource> childList = new ArrayList<IResource>();

		for (IResource child : children) {
			if (child.getChildCount() == 0) {
				childList.add(child);
			} else {
				ArrayList<IResource> subChildList = child.depthChildList();
				for (IResource subChild : subChildList) {
					childList.add(subChild);
				}
				childList.add(child);
			}
		}

		return childList;
	}

	public IResource getChildAt(int index) {
		return children.get(index);
	}

	public int getChildCount() {
		return children.size();
	}

	public IResource[] getChildren() {
		return children.toArray(new IResource[0]);
	}

	public int getIndexOfChild(IResource child) {
		return children.indexOf(child);
	}

	public String getName() {
		return name;
	}

	public IResource getParent() {
		return parent;
	}

	public IResource[] getPath() {
		ArrayList<IResource> path = new ArrayList<IResource>();
		IResource current = this;
		while (current != null) {
			path.add(current);
			current = current.getParent();
		}

		// Now we must reverse the order
		ArrayList<IResource> path2 = new ArrayList<IResource>();
		int l = path.size();
		for (int i = 0; i < l; i++) {
			path2.add(i, path.get(l - i - 1));
		}

		return path2.toArray(new IResource[0]);
	}

	public void removeChild(IResource ressourceChild) {
		children.remove(ressourceChild);
	}

	public void setName(String newName) {
		if (canChangeName()) {
			name = newName;
		}
	}

	public void setFoldersFirst(boolean foldersFirst) {
		this.foldersFirst = foldersFirst;
	}

	public void addTo(IResource parent) {
		if (parent instanceof Folder) {
			((Folder) parent).addChild(this);
		} else {
			parent.getParent();
			while (parent != null) {
				if (parent instanceof Folder) {
					((Folder) parent).addChild(this);
					break;
				}
				parent = parent.getParent();
			}
		}
	}

	public void removeFrom(IResource parent) {
		if (parent instanceof BasicResource) {
			((BasicResource) parent).removeChild(this);
		}
	}

	@Override
	public void move(IResource dropNode) {
		if (parent instanceof BasicResource) {
			((BasicResource) parent).removeChild(this);
		} else {
			removeFrom(parent);
		}

		if (dropNode instanceof BasicResource) {
			((BasicResource) dropNode).addChild(this);
		} else {
			addTo(dropNode);
		}
	}

}

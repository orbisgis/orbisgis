package org.orbisgis.views.geocognition;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionListener;
import org.orbisgis.ui.resourceTree.AbstractTreeModel;
import org.orbisgis.views.geocognition.filter.IGeocognitionFilter;

public class GeocognitionTreeModel extends AbstractTreeModel implements
		TreeModel {

	private String filterText = "";
	private ArrayList<IGeocognitionFilter> filters = new ArrayList<IGeocognitionFilter>();

	public GeocognitionTreeModel(JTree tree) {
		super(tree);

		getGeocognition().addGeocognitionListener(new GeocognitionListener() {

			@Override
			public void elementRemoved(Geocognition geocognition,
					GeocognitionElement element) {
				fireEvent(null);
			}

			@Override
			public void elementAdded(Geocognition geocognition,
					GeocognitionElement parent, GeocognitionElement newElement) {
				fireEvent(null);
			}

			@Override
			public boolean elementRemoving(Geocognition geocognition,
					GeocognitionElement element) {
				return true;
			}

			@Override
			public void elementMoved(Geocognition geocognition,
					GeocognitionElement element, GeocognitionElement oldParent) {
				fireEvent(null);
			}

		});
	}

	@Override
	public Object getChild(Object parent, int index) {
		GeocognitionElement parentElement = (GeocognitionElement) parent;
		int count = 0;
		for (int i = 0; i < parentElement.getElementCount(); i++) {
			if (isFiltered(parentElement.getElement(i))) {
				if (count == index) {
					return parentElement.getElement(i);
				}
				count++;
			}
		}
		return null;
	}

	private boolean isFiltered(GeocognitionElement element) {
		if (isFiltered()) {
			if (element.isFolder()) {
				return getChildCount(element) > 0;
			} else {
				if (element.getId().toLowerCase().indexOf(filterText) == -1) {
					return false;
				} else {
					if (filters.size() > 0) {
						boolean anyMatches = false;
						for (IGeocognitionFilter filter : filters) {
							if (filter.accept(element.getTypeId())) {
								anyMatches = true;
								break;
							}
						}
						return anyMatches;
					} else {
						return true;
					}
				}
			}
		} else {
			return true;
		}
	}

	@Override
	public int getChildCount(Object parent) {
		GeocognitionElement parentElement = (GeocognitionElement) parent;
		int count = 0;
		for (int i = 0; i < parentElement.getElementCount(); i++) {
			if (isFiltered(parentElement.getElement(i))) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		GeocognitionElement node = (GeocognitionElement) parent;
		int index = 0;
		for (int i = 0; i < node.getElementCount(); i++) {
			if (isFiltered(node.getElement(i))) {
				if (node.getElement(i) == child) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return getGeocognition().getRoot();
	}

	@Override
	public boolean isLeaf(Object node) {
		GeocognitionElement elem = (GeocognitionElement) node;
		return !elem.isFolder() || elem.getElementCount() == 0;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	private Geocognition getGeocognition() {
		return (Geocognition) Services.getService(Geocognition.class);
	}

	public boolean filter(String text, ArrayList<IGeocognitionFilter> filters) {
		this.filterText = text.toLowerCase();
		this.filters = filters;
		fireEvent(null);

		return !isFiltered();
	}

	private boolean isFiltered() {
		return (!filterText.trim().equals("")) || (this.filters.size() != 0);
	}
}

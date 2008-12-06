/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.geocatalog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.images.IconLoader;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;
import org.orbisgis.ui.sif.AskValue;
import org.orbisgis.ui.text.JTextFilter;
import org.orbisgis.views.geocatalog.action.EPGeocatalogSourceActionHelper;
import org.orbisgis.views.geocatalog.action.ISourceAction;
import org.orbisgis.views.geocatalog.filter.GeocatalogFilterDecorator;
import org.orbisgis.views.geocatalog.filter.IGeocatalogFilter;
import org.orbisgis.views.geocatalog.newSourceWizard.EPSourceWizardHelper;
import org.sif.CRFlowLayout;
import org.sif.UIFactory;

public class Catalog extends JPanel implements DragGestureListener,
		DragSourceListener {

	private static final String AC_BTN_DEL_TAG = "del";

	private static final String AC_BTN_ADD_TAG = "add";

	private static final int FILTER_VISIBLE_ROW_COUNT = 6;

	private static final Logger logger = Logger.getLogger(Catalog.class);

	private HashMap<String, HashSet<String>> tagSources = new HashMap<String, HashSet<String>>();

	private SourceListModel listModel;

	private JList lstSources;

	private DragSource dragSource;

	private JTextFilter txtFilter;

	private JToggleButton btnToggleFilters;

	private JPanel pnlFilters;

	private JList lstFilters;

	private JList lstTags;

	private JButton btnDelTag;

	private DefaultListModel tagListModel;

	private EPSourceWizardHelper wh;

	public Catalog(EPSourceWizardHelper wh) {
		this.wh= wh;
		lstSources = new JList();
		lstSources.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu popup = getPopup();
					if (popup != null) {
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		listModel = new SourceListModel();
		lstSources.setModel(listModel);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(lstSources), BorderLayout.CENTER);
		this.add(getNorthPanel(), BorderLayout.NORTH);
		SourceListRenderer cellRenderer = new SourceListRenderer();
		cellRenderer.setRenderers(wh.getRenderers());
		lstSources.setCellRenderer(cellRenderer);

		dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(lstSources,
				DnDConstants.ACTION_COPY_OR_MOVE, this);

	}

	private JPanel getNorthPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		JPanel pnlTextFilter = new JPanel();
		CRFlowLayout layout = new CRFlowLayout();
		layout.setAlignment(CRFlowLayout.LEFT);
		pnlTextFilter.setLayout(layout);
		txtFilter = new JTextFilter();
		txtFilter.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				doFilter();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				doFilter();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				doFilter();
			}
		});
		pnlTextFilter.add(txtFilter);
		btnToggleFilters = new JToggleButton(IconLoader.getIcon("filter.png"));
		btnToggleFilters.setMargin(new Insets(0, 0, 0, 0));
		btnToggleFilters.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pnlFilters.setVisible(!pnlFilters.isVisible());
			}
		});
		pnlTextFilter.add(btnToggleFilters);
		ret.add(pnlTextFilter, BorderLayout.NORTH);
		JPanel pnlFilters = getFilterAndTagPanel();
		ret.add(pnlFilters, BorderLayout.CENTER);
		return ret;
	}

	private void doFilter() {
		Object[] filterObjects = lstFilters.getSelectedValues();
		ArrayList<IGeocatalogFilter> filters = new ArrayList<IGeocatalogFilter>();
		for (int i = 0; i < filterObjects.length; i++) {
			filters.add((GeocatalogFilterDecorator) filterObjects[i]);
		}

		Object[] tags = lstTags.getSelectedValues();
		for (Object object : tags) {
			String tag = object.toString();
			filters.add(new TagFilter(tag));
		}

		listModel.filter(txtFilter.getText(), filters
				.toArray(new IGeocatalogFilter[0]));
	}

	private JPanel getFilterAndTagPanel() {
		pnlFilters = new JPanel();
		pnlFilters.setVisible(false);
		pnlFilters.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		pnlFilters.add(new JLabel("Tip: Unselect with 'Ctrl'+" + "'click'"), c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		pnlFilters.add(getFilterPanel(), c);
		c.gridx = 1;
		pnlFilters.add(getTagPanel(), c);

		return pnlFilters;
	}

	private JPanel getFilterPanel() {
		JPanel ret = new JPanel();
		ret.setBorder(BorderFactory.createTitledBorder("Filters"));
		ret.setLayout(new BorderLayout());
		lstFilters = new JList(getAvailableFilters());
		lstFilters.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list,
						((GeocatalogFilterDecorator) value).getName(), index,
						isSelected, cellHasFocus);
			}
		});
		lstFilters.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						doFilter();
					}
				});
		lstFilters.setVisibleRowCount(FILTER_VISIBLE_ROW_COUNT);
		JScrollPane scroll = new JScrollPane(lstFilters);
		ret.add(scroll, BorderLayout.CENTER);
		return ret;
	}

	private JPanel getTagPanel() {
		JPanel ret = new JPanel();
		ret.setBorder(BorderFactory.createTitledBorder("Labels"));
		ret.setLayout(new BorderLayout());
		lstTags = new JList();
		tagListModel = new DefaultListModel();
		refreshTagModel();
		lstTags.setModel(tagListModel);
		lstTags.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						doFilter();
						btnDelTag.setEnabled(lstTags.getSelectedIndex() != -1);
					}
				});
		lstTags.setVisibleRowCount(FILTER_VISIBLE_ROW_COUNT - 1);
		JScrollPane scroll = new JScrollPane(lstTags);
		ret.add(scroll, BorderLayout.CENTER);
		JPanel pnlButtons = new JPanel();
		JButton btnAdd = getTagManagementButton(IconLoader.getIcon("add.png"),
				AC_BTN_ADD_TAG);
		btnDelTag = getTagManagementButton(IconLoader.getIcon("delete.png"),
				AC_BTN_DEL_TAG);
		btnDelTag.setEnabled(false);
		pnlButtons.add(btnAdd);
		pnlButtons.add(btnDelTag);
		ret.add(pnlButtons, BorderLayout.SOUTH);
		return ret;
	}

	private void refreshTagModel() {
		String[] tags = getTags();
		tagListModel.clear();
		for (String tag : tags) {
			tagListModel.addElement(tag);
		}
	}

	private JButton getTagManagementButton(ImageIcon icon, String actionCommand) {
		JButton ret = new JButton(icon);
		ret.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (AC_BTN_ADD_TAG.equals(e.getActionCommand())) {
					AskValue av = new AskValue("New tag", null, null);
					if (UIFactory.showDialog(av)) {
						String tagText = av.getValue();
						addTag(tagText);
					}
				} else if (AC_BTN_DEL_TAG.equals(e.getActionCommand())) {
					tagSources.remove(lstTags.getSelectedValue());
					refreshTagModel();
				}
			}
		});
		ret.setActionCommand(actionCommand);
		ret.setMargin(new Insets(0, 0, 0, 0));
		return ret;
	}

	public void addTag(String tagText) {
		tagSources.put(tagText, new HashSet<String>());
		refreshTagModel();
	}

	String[] getTags() {
		return tagSources.keySet().toArray(new String[0]);
	}

	private Object[] getAvailableFilters() {
		ExtensionPointManager<IGeocatalogFilter> epm = new ExtensionPointManager<IGeocatalogFilter>(
				"org.orbisgis.views.geocatalog.Filter");
		ArrayList<ItemAttributes<IGeocatalogFilter>> attributes = epm
				.getItemAttributes("/extension/source-filter");
		ArrayList<GeocatalogFilterDecorator> filters = new ArrayList<GeocatalogFilterDecorator>();
		for (ItemAttributes<IGeocatalogFilter> itemAttributes : attributes) {
			IGeocatalogFilter instance = itemAttributes.getInstance("class");
			GeocatalogFilterDecorator filter = new GeocatalogFilterDecorator(
					itemAttributes.getAttribute("id"), itemAttributes
							.getAttribute("name"), instance);
			filters.add(filter);
		}

		return filters.toArray(new GeocatalogFilterDecorator[0]);
	}

	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		IActionFactory factory = new SourceActionFactory();
		EPGeocatalogSourceActionHelper.createPopup(menuTree, factory,
				"org.orbisgis.views.geocatalog.Action");

		wh.addWizardMenus(menuTree, new SourceWizardActionFactory(),
				"org.orbisgis.views.geocatalog.file.Add");

		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		// Add tagging menus
		if (!tagSources.isEmpty() && lstSources.getSelectedIndices().length > 0) {
			JMenu menu = new JMenu("Tag");
			Iterator<String> tagsIterator = tagSources.keySet().iterator();
			while (tagsIterator.hasNext()) {
				String tag = tagsIterator.next();
				JCheckBoxMenuItem item;
				if (isSelectionTagged(tag)) {
					item = new JCheckBoxMenuItem(tag, true);
					RemoveTagActionListener removeTagAL = new RemoveTagActionListener(
							tag);
					item.addActionListener(removeTagAL);
				} else {
					item = new JCheckBoxMenuItem(tag, false);
					AddTagActionListener addTagAL = new AddTagActionListener(
							tag);
					item.addActionListener(addTagAL);
				}
				menu.add(item);
			}
			popup.addSeparator();
			popup.add(menu);
		}

		return popup;
	}

	private boolean isSelectionTagged(String tag) {
		HashSet<String> tagSourceSet = tagSources.get(tag);
		Object[] selectedValues = lstSources.getSelectedValues();
		for (Object source : selectedValues) {
			if (!tagSourceSet.contains(source)) {
				return false;
			}
		}

		return selectedValues.length > 0;
	}

	public String[] getSelectedSources() {
		Object[] selectedValues = lstSources.getSelectedValues();
		String[] sources = new String[selectedValues.length];
		for (int i = 0; i < sources.length; i++) {
			sources[i] = selectedValues[i].toString();
		}
		return sources;
	}

	private final class SourceActionFactory implements IActionFactory {

		private final class SourceAction implements IActionAdapter {
			private ISourceAction action;

			public SourceAction(Object action) {
				this.action = (ISourceAction) action;
			}

			public boolean isVisible() {
				DataManager dataManager = Services
						.getService(DataManager.class);
				SourceManager sourceManager = dataManager.getSourceManager();
				String[] res = getSelectedSources();
				ISourceAction sourceAction = (ISourceAction) action;
				boolean acceptsAllSources = true;
				if (sourceAction.acceptsSelectionCount(res.length)) {
					for (String source : res) {
						try {
							if (!sourceAction.accepts(sourceManager, source)) {
								acceptsAllSources = false;
								break;
							}
						} catch (Throwable t) {
							acceptsAllSources = false;
							logger.error("Error getting pop up", t);
						}
					}
				} else {
					acceptsAllSources = false;
				}

				return acceptsAllSources;
			}

			public boolean isEnabled() {
				return true;
			}

			public void actionPerformed() {
				EPGeocatalogSourceActionHelper.executeAction(action,
						getSelectedSources());
			}
		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new SourceAction(action);
		}

		public ISelectableActionAdapter getSelectableAction(
				Object actionObject, HashMap<String, String> attributes) {
			throw new RuntimeException(
					"Bug. Source actions should not be selectable");
		}
	}

	public Transferable getDragData(DragGestureEvent dge) {
		String[] sources = getSelectedSources();
		if (sources.length > 0) {
			return new TransferableSource(sources);
		} else {
			return null;
		}
	}

	void delete() {
		listModel.freeResources();
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Transferable dragData = getDragData(dge);
		if (dragData != null) {
			dragSource.startDrag(dge, DragSource.DefaultMoveDrop, dragData,
					this);
		}
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public String[] getActiveFiltersId() {
		Object[] filters = lstFilters.getSelectedValues();
		String[] ids = new String[filters.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = ((GeocatalogFilterDecorator) filters[i]).getId();
		}

		return ids;
	}

	public void setActiveFiltersId(String[] filterIds) {
		ListSelectionModel selectionModel = lstFilters.getSelectionModel();
		ListModel filterModel = lstFilters.getModel();
		selectionModel.clearSelection();
		selectionModel.setValueIsAdjusting(true);
		for (String filterId : filterIds) {
			GeocatalogFilterDecorator filter = new GeocatalogFilterDecorator(
					filterId, null, null);
			int index = -1;
			for (int i = 0; i < filterModel.getSize(); i++) {
				if (filterModel.getElementAt(i).equals(filter)) {
					index = i;
				}
			}
			if (index != -1) {
				selectionModel.addSelectionInterval(index, index);
			}
		}
		selectionModel.setValueIsAdjusting(false);
	}

	public class TagFilter implements IGeocatalogFilter {

		private String tag;

		public TagFilter(String tag) {
			this.tag = tag;
		}

		@Override
		public boolean accept(SourceManager sm, String sourceName) {
			HashSet<String> sources = tagSources.get(tag);
			if (sources.contains(sourceName)) {
				return true;
			}
			return false;
		}

	}

	private class AddTagActionListener implements ActionListener {

		private String tag;

		public AddTagActionListener(String tag) {
			this.tag = tag;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] sources = lstSources.getSelectedValues();
			for (Object source : sources) {
				String tagText = tag;
				tagSource(tagText, source.toString());
			}
		}

	}

	private class RemoveTagActionListener implements ActionListener {

		private String tag;

		public RemoveTagActionListener(String tag) {
			this.tag = tag;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] sources = lstSources.getSelectedValues();
			for (Object source : sources) {
				tagSources.get(tag).remove(source.toString());
			}
		}

	}

	public HashSet<String> getTaggedSources(String tag) {
		return tagSources.get(tag);
	}

	public void tagSource(String tagText, String source) {
		tagSources.get(tagText).add(source);
	}

	public boolean isTagSelected(String tag) {
		Object[] tags = lstTags.getSelectedValues();
		for (Object selectedTag : tags) {
			if (selectedTag.equals(tag)) {
				return true;
			}
		}

		return false;
	}

	public void setActiveLabels(String[] labels) {
		ListSelectionModel selectionModel = lstTags.getSelectionModel();
		selectionModel.clearSelection();
		for (String label : labels) {
			selectionModel.setValueIsAdjusting(true);
			int index = -1;
			for (int i = 0; i < tagListModel.getSize(); i++) {
				if (tagListModel.getElementAt(i).equals(label)) {
					index = i;
				}
			}
			if (index != -1) {
				selectionModel.addSelectionInterval(index, index);
			}
		}
		selectionModel.setValueIsAdjusting(false);
	}
}

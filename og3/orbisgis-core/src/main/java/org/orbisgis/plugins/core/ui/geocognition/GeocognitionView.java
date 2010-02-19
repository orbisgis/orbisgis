package org.orbisgis.plugins.core.ui.geocognition;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.plugins.core.PersistenceException;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.edition.EditableElement;
import org.orbisgis.plugins.core.edition.EditableElementListener;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.geocognition.GeocognitionListener;
import org.orbisgis.plugins.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.plugins.core.ui.components.text.JTextFilter;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.menu.MenuTree;
import org.orbisgis.plugins.core.ui.views.GeocognitionViewPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.views.geocognition.filters.IGeocognitionFilter;
import org.orbisgis.plugins.core.ui.views.geocognition.filters.Map;
import org.orbisgis.plugins.core.ui.views.geocognition.filters.SQL;
import org.orbisgis.plugins.core.ui.views.geocognition.filters.Symbology;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.plugins.core.ui.views.geocognition.wizards.NewFolder;
import org.orbisgis.plugins.core.ui.views.geocognition.wizards.NewMap;
import org.orbisgis.plugins.core.ui.views.geocognition.wizards.NewRegisteredSQLArtifact;
import org.orbisgis.plugins.core.ui.views.geocognition.wizards.NewSymbol;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.plugins.core.workspace.Workspace;
import org.orbisgis.plugins.sif.CRFlowLayout;
import org.orbisgis.plugins.sif.CarriageReturn;

public class GeocognitionView extends JPanel implements WorkbenchFrame {

	public static final String FIRST_MAP = "/Maps/MyFirstMap";
	public static final String STARTUP_GEOCOGNITION_XML = "startup.geocognition.xml";
	public static final String COGNITION_PERSISTENCE_FILE = "org.orbisgis.plugins.core.Geocognition.xml";
	private GeocognitionTree tree;

	public GeocognitionTree getTree() {
		return tree;
	}

	public MenuTree getMenuTreePopup() {
		return tree.getMenuTree();
	}

	public JPopupMenu getPopup() {
		return tree.getPopup();
	}

	private JTextFilter txtFilter;
	private JPanel controlPanel;
	private ArrayList<FilterButton> filterButtons;
	private ModificationListener modificationListener = new ModificationListener();
	private TreeListener treeListener = new TreeListener();

	public TreeListener getTreeListener() {
		return treeListener;
	}

	public GeocognitionView() {
		tree = new GeocognitionTree();
		this.setLayout(new BorderLayout());
		this.add(getControlPanel(), BorderLayout.NORTH);
		this.add(tree, BorderLayout.CENTER);
	}

	private JPanel getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JPanel();
			CRFlowLayout layout = new CRFlowLayout();
			layout.setHgap(2);
			layout.setVgap(2);
			layout.setAlignment(CRFlowLayout.LEFT);
			controlPanel.setLayout(layout);
			controlPanel.add(getFilterButtonPanel());
			controlPanel.add(new CarriageReturn());

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
			controlPanel.add(txtFilter);
		}
		return controlPanel;
	}

	private JPanel getFilterButtonPanel() {
		JPanel btnPanel = new JPanel();
		// TODO (pyf): mettre les filtres sous forme de plugins

		/*
		 * ExtensionPointManager<IGeocognitionFilter> epm = new
		 * ExtensionPointManager<IGeocognitionFilter>(
		 * "org.orbisgis.core.ui.views.geocognition.Filter");
		 * ArrayList<ItemAttributes<IGeocognitionFilter>> attributes = epm
		 * .getItemAttributes("/extension/filter");
		 */
		filterButtons = new ArrayList<FilterButton>();
		/*
		 * for (ItemAttributes<IGeocognitionFilter> itemAttributes : attributes)
		 * { String iconAttribute = itemAttributes.getAttribute("icon"); URL
		 * iconURL = this.getClass().getResource(iconAttribute); JToggleButton
		 * btn = new JToggleButton(new ImageIcon(iconURL)); btn.setMargin(new
		 * Insets(0, 0, 0, 0)); btn.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { doFilter(); }
		 * 
		 * }); btnPanel.add(btn); IGeocognitionFilter filter =
		 * itemAttributes.getInstance("class"); filterButtons.add(new
		 * FilterButton(filter, btn)); }
		 */

		URL iconURL = this.getClass().getResource(
				"/org/orbisgis/plugins/images/map.png");
		JToggleButton btn = new JToggleButton(new ImageIcon(iconURL));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doFilter();
			}

		});
		btnPanel.add(btn);
		IGeocognitionFilter filter = new Map();
		filterButtons.add(new FilterButton(filter, btn));

		iconURL = this.getClass().getResource(
				"/org/orbisgis/plugins/images/script_code.png");
		btn = new JToggleButton(new ImageIcon(iconURL));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doFilter();
			}

		});
		btnPanel.add(btn);
		filter = new SQL();
		filterButtons.add(new FilterButton(filter, btn));

		iconURL = this.getClass().getResource(
				"/org/orbisgis/plugins/images/palette.png");
		btn = new JToggleButton(new ImageIcon(iconURL));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doFilter();
			}

		});
		btnPanel.add(btn);
		filter = new Symbology();
		filterButtons.add(new FilterButton(filter, btn));

		return btnPanel;
	}

	private void doFilter() {
		ArrayList<IGeocognitionFilter> filters = new ArrayList<IGeocognitionFilter>();
		for (FilterButton filterButton : filterButtons) {
			if (filterButton.getButton().isSelected()) {
				filters.add(filterButton.getFilter());
			}
		}
		tree.filter(txtFilter.getText(), filters);
	}

	public void delete() {
		Geocognition geocog = Services.getService(Geocognition.class);
		removeListenerRecursively(geocog.getRoot());
		geocog.removeGeocognitionListener(treeListener);
	}

	private void removeListenerRecursively(GeocognitionElement element) {
		if (element.isFolder()) {
			for (int i = 0; i < element.getElementCount(); i++) {
				removeListenerRecursively(element.getElement(i));
			}
		} else {
			element.removeElementListener(modificationListener);
		}
	}

	public Component getComponent() {
		return this;
	}

	public void initialize() {

		/*
		 * EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		 * GeocognitionElementFactory[] factories =
		 * wh.getGeocognitionFactories();
		 * 
		 * // Register factories Geocognition geocog =
		 * Services.getService(Geocognition.class); for
		 * (GeocognitionElementFactory factory : factories) {
		 * geocog.addElementFactory(factory); }
		 * 
		 * // Read renderers ElementRenderer[] renderers =
		 * wh.getElementRenderers(); tree.setRenderers(renderers);
		 */

		// TODO:mettre les éléments geocognition sous forme de plugins
		Geocognition geocog = Services.getService(Geocognition.class);
		geocog.addElementFactory(new GeocognitionFunctionFactory());
		geocog.addElementFactory(new GeocognitionCustomQueryFactory());
		geocog.addElementFactory(new GeocognitionMapContextFactory());
		geocog.addElementFactory(new GeocognitionSymbolFactory());

		ElementRenderer[] renderers = new ElementRenderer[4];
		NewRegisteredSQLArtifact newRegisteredSQLArtifact = new NewRegisteredSQLArtifact();
		renderers[0] = newRegisteredSQLArtifact.getElementRenderer();
		NewMap newMap = new NewMap();
		renderers[1] = newMap.getElementRenderer();
		NewFolder newFolder = new NewFolder();
		renderers[2] = newFolder.getElementRenderer();
		NewSymbol newSymbol = new NewSymbol();
		renderers[3] = newSymbol.getElementRenderer();
		tree.setRenderers(renderers);

		// Load startup if it's the first time
		Workspace ws = Services.getService(Workspace.class);
		File cognitionFile = ws.getFile(COGNITION_PERSISTENCE_FILE);
		if (!cognitionFile.exists()) {
			try {
				InputStream is = GeocognitionViewPlugIn.class
						.getResourceAsStream(STARTUP_GEOCOGNITION_XML);
				Geocognition geocognition = Services
						.getService(Geocognition.class);
				geocognition.read(is);
				is.close();

				// Open first map
				GeocognitionElement element = geocognition
						.getGeocognitionElement(FIRST_MAP);
				if (element != null) {
					EditorManager em = Services.getService(EditorManager.class);
					em.open(element);
				} else {
					Services.getErrorManager().warning(
							"Cannot find initial map");
				}
			} catch (PersistenceException e) {
				Services.getErrorManager().error(
						"Cannot start up geocognition", e);
			} catch (IOException e) {
				Services.getErrorManager().warning(
						"Error starting up geocognition", e);
			}
		}

		// Listen modifications
		geocog.addGeocognitionListener(treeListener);
	}

	private void addListenerRecursively(GeocognitionElement element) {
		if (element.isFolder()) {
			for (int i = 0; i < element.getElementCount(); i++) {
				addListenerRecursively(element.getElement(i));
			}
		} else {
			// Just to not have twice the same listener
			element.removeElementListener(modificationListener);

			element.addElementListener(modificationListener);
		}
	}

	public void loadStatus() throws PersistenceException {
		Workspace ws = Services.getService(Workspace.class);
		File cognitionFile = ws.getFile(COGNITION_PERSISTENCE_FILE);
		if (cognitionFile.exists()) {
			try {
				Geocognition geocognition = Services
						.getService(Geocognition.class);
				InputStream is = new BufferedInputStream(new FileInputStream(
						cognitionFile));
				geocognition.read(is);
				is.close();
			} catch (IOException e) {
				throw new PersistenceException("Cannot read geocognition", e);
			}
		}

		tree.setGeocognitionModel();
	}

	public void saveStatus() throws PersistenceException {
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		File cognitionFile = ws.getFile(COGNITION_PERSISTENCE_FILE);
		Geocognition geocognition = Services.getService(Geocognition.class);
		try {
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(cognitionFile));
			geocognition.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("bug!", e);
		} catch (IOException e) {
			throw new PersistenceException("Cannot read geocognition", e);
		}
	}

	private final class TreeListener implements GeocognitionListener {
		@Override
		public boolean elementRemoving(Geocognition geocognition,
				GeocognitionElement element) {
			return closeEditorsRecursively(element);
		}

		@Override
		public void elementRemoved(Geocognition geocognition,
				GeocognitionElement element) {
			removeListenerRecursively(element);
		}

		private boolean closeEditorsRecursively(GeocognitionElement element) {
			if (element.isFolder()) {
				for (int i = 0; i < element.getElementCount(); i++) {
					if (!closeEditorsRecursively(element.getElement(i))) {
						return false;
					}
				}
			} else {
				EditorManager em = Services.getService(EditorManager.class);
				IEditor[] editors = em.getEditor(element);
				for (IEditor editor : editors) {
					if (!em.closeEditor(editor)) {
						return false;
					}
				}
			}

			return true;
		}

		@Override
		public void elementMoved(Geocognition geocognition,
				GeocognitionElement element, GeocognitionElement oldParent) {
		}

		@Override
		public void elementAdded(Geocognition geocognition,
				GeocognitionElement parent, GeocognitionElement newElement) {
			addListenerRecursively(newElement);
		}
	}

	private class FilterButton {
		private IGeocognitionFilter filter;
		private JToggleButton button;

		public FilterButton(IGeocognitionFilter filter, JToggleButton button) {
			super();
			this.filter = filter;
			this.button = button;
		}

		public IGeocognitionFilter getFilter() {
			return filter;
		}

		public JToggleButton getButton() {
			return button;
		}

	}

	private class ModificationListener implements EditableElementListener {

		@Override
		public void contentChanged(EditableElement element) {
		}

		@Override
		public void idChanged(EditableElement element) {

		}

		@Override
		public void saved(EditableElement element) {
			tree.repaint();
		}

	}
}

package org.orbisgis.core.ui.geocognition;

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
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.edition.EditableElementListener;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionListener;
import org.orbisgis.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.ui.components.text.JTextFilter;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.menu.MenuTree;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.geocognition.filters.IGeocognitionFilter;
import org.orbisgis.core.ui.plugins.views.geocognition.filters.Map;
import org.orbisgis.core.ui.plugins.views.geocognition.filters.SQL;
import org.orbisgis.core.ui.plugins.views.geocognition.filters.Symbology;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewFolder;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewMap;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewRegisteredSQLArtifact;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewSymbol;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.progress.NullProgressMonitor;

public class GeocognitionView extends JPanel implements WorkbenchFrame {

	public static final String FIRST_MAP = "/Maps/MyFirstMap";
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
		// TODO : PUT filter as plugin
		filterButtons = new ArrayList<FilterButton>();

		JToggleButton btn = new JToggleButton(OrbisGISIcon.MAP);
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

		btn = new JToggleButton(OrbisGISIcon.SCRIPT_CODE);
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
		btn = new JToggleButton(OrbisGISIcon.PALETTE);
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
		File cognitionFile = ws
				.getFile(OrbisGISPersitenceConfig.COGNITION_CREATED_FILE);
		if (!cognitionFile.exists()) {
			// Populate on the fly geocognition
			Geocognition geocognition = Services.getService(Geocognition.class);
			geocognition.clear();
			MapContext mc = new DefaultMapContext();
			geocognition.addElement(FIRST_MAP, mc);

			populateGeoCognitionWithOGCFunctions(geocognition);

			// Open first map
			GeocognitionElement element = geocognition
					.getGeocognitionElement(FIRST_MAP);

			if (element != null) {
				EditorManager em = Services.getService(EditorManager.class);
				em.open(element, new NullProgressMonitor());
			} else {
				Services.getErrorManager().warning("Cannot find initial map");
			}
		}

		// Listen modifications
		geocog.addGeocognitionListener(treeListener);
	}

	/**
	 * A simple method to populate the geocognition on the fly
	 * 
	 * @param geocognition
	 */
	private void populateGeoCognitionWithOGCFunctions(Geocognition geocognition) {

		String[] functions = FunctionManager.getFunctionNames();

		String SQLFolder = "SQL";
		geocognition.addFolder(SQLFolder);

		for (int i = 0; i < functions.length; i++) {

			String functionName = functions[i].toLowerCase();

			Function function = FunctionManager.getFunction(functionName);

			geocognition.addElement(SQLFolder + "/" + function.getName(),
					function.getClass());

		}

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
		File cognitionFile = ws
				.getFile(OrbisGISPersitenceConfig.COGNITION_CREATED_FILE);
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
		File cognitionFile = ws
				.getFile(OrbisGISPersitenceConfig.COGNITION_CREATED_FILE);
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

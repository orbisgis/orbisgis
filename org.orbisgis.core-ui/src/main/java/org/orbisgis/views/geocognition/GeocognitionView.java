package org.orbisgis.views.geocognition;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.edition.EditableElementListener;
import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionListener;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;
import org.orbisgis.view.IView;
import org.orbisgis.views.editor.EditorManager;
import org.orbisgis.views.geocognition.filter.IGeocognitionFilter;
import org.orbisgis.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.workspace.Workspace;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class GeocognitionView extends JPanel implements IView {

	public static final String FIRST_MAP = "/Maps/MyFirstMap";
	public static final String STARTUP_GEOCOGNITION_XML = "startup.geocognition.xml";
	private static final String COGNITION_PERSISTENCE_FILE = "org.orbisgis.Geocognition.xml";
	private GeocognitionTree tree;
	private JTextField txtFilter;
	private JPanel controlPanel;
	private ArrayList<FilterButton> filterButtons;
	private JButton btnClear;
	private ModificationListener modificationListener = new ModificationListener();
	private TreeListener treeListener = new TreeListener();

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

			txtFilter = new JTextField(8);
			txtFilter.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					doFilter();
				}

			});
			controlPanel.add(txtFilter);
			btnClear = new JButton(new ImageIcon(this.getClass().getResource(
					"/org/orbisgis/images/remove.png")));
			btnClear.setVisible(false);
			btnClear.setMargin(new Insets(0, 0, 0, 0));
			btnClear.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					txtFilter.setText("");
					doFilter();
				}

			});
			controlPanel.add(btnClear);
		}
		return controlPanel;
	}

	private JPanel getFilterButtonPanel() {
		JPanel btnPanel = new JPanel();
		ExtensionPointManager<IGeocognitionFilter> epm = new ExtensionPointManager<IGeocognitionFilter>(
				"org.orbisgis.views.geocognition.Filter");
		ArrayList<ItemAttributes<IGeocognitionFilter>> attributes = epm
				.getItemAttributes("/extension/filter");
		filterButtons = new ArrayList<FilterButton>();
		for (ItemAttributes<IGeocognitionFilter> itemAttributes : attributes) {
			String iconAttribute = itemAttributes.getAttribute("icon");
			URL iconURL = this.getClass().getResource(iconAttribute);
			JToggleButton btn = new JToggleButton(new ImageIcon(iconURL));
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					doFilter();
				}

			});
			btnPanel.add(btn);
			IGeocognitionFilter filter = itemAttributes.getInstance("class");
			filterButtons.add(new FilterButton(filter, btn));
		}

		return btnPanel;
	}

	private void doFilter() {
		btnClear.setVisible(txtFilter.getText().length() > 0);

		ArrayList<IGeocognitionFilter> filters = new ArrayList<IGeocognitionFilter>();
		for (FilterButton filterButton : filterButtons) {
			if (filterButton.getButton().isSelected()) {
				filters.add(filterButton.getFilter());
			}
		}
		tree.filter(txtFilter.getText(), filters);
	}

	@Override
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

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void initialize() {
		EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		GeocognitionElementFactory[] factories = wh.getGeocognitionFactories();

		// Register factories
		Geocognition geocog = Services.getService(Geocognition.class);
		for (GeocognitionElementFactory factory : factories) {
			geocog.addElementFactory(factory);
		}

		// Read renderers
		ElementRenderer[] renderers = wh.getElementRenderers();
		tree.setRenderers(renderers);

		// Load startup if it's the first time
		Workspace ws = Services.getService(Workspace.class);
		File cognitionFile = ws.getFile(COGNITION_PERSISTENCE_FILE);
		if (!cognitionFile.exists()) {
			try {
				InputStream is = GeocognitionView.class
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
		addListenerRecursively(geocog.getRoot());
		geocog.addGeocognitionListener(treeListener);
	}

	private void addListenerRecursively(GeocognitionElement element) {
		if (element.isFolder()) {
			for (int i = 0; i < element.getElementCount(); i++) {
				addListenerRecursively(element.getElement(i));
			}
		} else {
			element.addElementListener(modificationListener);
		}
	}

	@Override
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

	@Override
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

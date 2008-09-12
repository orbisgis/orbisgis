package org.orbisgis.views.geocognition.sync;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.geocognition.sync.editor.ICompareEditor;
import org.orbisgis.views.geocognition.sync.editor.text.CompareCodeEditor;
import org.orbisgis.views.geocognition.sync.editor.text.CompareTextEditor;
import org.orbisgis.views.geocognition.sync.tree.CompareTreePanel;
import org.orbisgis.views.geocognition.sync.tree.TreeElement;
import org.orbisgis.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.sif.AbstractUIPanel;

public class ComparePanel extends AbstractUIPanel {
	// String constants for the closing dialogs
	private static final String LEFT_SAVE_BEFORE_CLOSING_TEXT = "The left resource has been modified. "
			+ "Save changes?";
	private static final String LEFT_SAVE_BEFORE_CLOSING_TITLE = "Save left resource";
	private static final String RIGHT_SAVE_BEFORE_CLOSING_TEXT = "The right resource has been modified. "
			+ "Save changes?";
	private static final String RIGHT_SAVE_BEFORE_CLOSING_TITLE = "Save right resource";

	private static final String ADD_TO_GEOCOGNITION = "Add to geocognition";
	private static final String ADD_TO_FILE = "Add to file";
	private static final String REMOVE_FROM_GEOCOGNITION = "Remove from geocognition";
	private static final String REMOVE_FROM_FILE = "Remove from file";
	private static final String OVERRIDE_IN_GEOCOGNITION = "Override in geocognition";
	private static final String OVERRIDE_IN_FILE = "Override in file";
	private static final String NO_ACTION = "No action available";

	// Transaction constants
	private static final int COMMIT = 0;
	private static final int UPDATE = 1;

	// Interface
	private ArrayList<ICompareEditor> editors;
	private ICompareEditor currentEditor;
	private CompareTreePanel treePanel;
	private JPopupMenu popup;
	private JMenuItem addToGeocognition, addToFile, removeFromGeocognition,
			removeFromFile, overrideInGeocognition, overrideInFile, noAction;
	private JSplitPane split;
	private JPanel panel;

	// Model
	private SyncManager manager;
	private GeocognitionElement local, remote;
	private Object remoteSource;

	/**
	 * Creates a new CompareSplitPane
	 * 
	 * @param eds
	 *            the available editors for the comparing pane
	 */
	public ComparePanel() {
		manager = new SyncManager();
		manager.addSyncListener(new EditorSyncListener());

		// Right component
		editors = getAvailableEditors();

		// Left component
		treePanel = new CompareTreePanel(this);
		treePanel.addMouseListener(new CompareTreeListener());
		EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		treePanel.setIconRenderers(wh.getElementRenderers());

		// Popup menu
		popup = new JPopupMenu();

		addToGeocognition = new JMenuItem(ADD_TO_GEOCOGNITION);
		addToGeocognition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(UPDATE);
			}
		});

		addToFile = new JMenuItem(ADD_TO_FILE);
		addToFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(COMMIT);
			}
		});

		removeFromGeocognition = new JMenuItem(REMOVE_FROM_GEOCOGNITION);
		removeFromGeocognition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(UPDATE);
			}
		});

		removeFromFile = new JMenuItem(REMOVE_FROM_FILE);
		removeFromFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(COMMIT);
			}
		});

		overrideInGeocognition = new JMenuItem(OVERRIDE_IN_GEOCOGNITION);
		overrideInGeocognition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(UPDATE);
			}
		});

		overrideInFile = new JMenuItem(OVERRIDE_IN_FILE);
		overrideInFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(COMMIT);
			}
		});

		noAction = new JMenuItem(NO_ACTION);
		noAction.setEnabled(false);

		popup.add(addToGeocognition);
		popup.add(addToFile);
		popup.add(removeFromGeocognition);
		popup.add(removeFromFile);
		popup.add(overrideInGeocognition);
		popup.add(overrideInFile);
		popup.add(noAction);

		// Split pane
		split = new JSplitPane();
		split.setLeftComponent(treePanel);
		setEditor(null);

		// Put all together
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(split, BorderLayout.CENTER);
	}

	/**
	 * Sets the model of the comparing pane
	 * 
	 * @param left
	 *            element to compare
	 * @param right
	 *            element to compare
	 * @throws IOException
	 * @throws PersistenceException
	 */
	public void setModel(GeocognitionElement localElement, Object remoteObject)
			throws IOException, PersistenceException {
		remoteSource = remoteObject;
		local = localElement;
		remote = createTreeFromResource(remoteSource);

		// Show panel
		if (local == remote) {
			manager.compare(local, remote, isSourceEditable(remoteSource));
		} else {
			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new SynchronizingJob());
		}
		treePanel.setModel(manager);
		currentEditor = editors.get(0);
		currentEditor.setModel(null, null);
		split.setRightComponent(currentEditor.getComponent());
	}

	/**
	 * Determines if the given source is editable
	 * 
	 * @param source
	 *            the source to check
	 * @return true if the source is editable, false otherwise
	 */
	private boolean isSourceEditable(Object source) {
		if (source instanceof GeocognitionElement) {
			return true;
		} else if (source instanceof File) {
			return true;
		} else if (source instanceof URL) {
			return false;
		} else {
			Services.getErrorManager().error(
					"bug!",
					new IllegalArgumentException("The remote source " + source
							+ " is not valid"));
			return false;
		}
	}

	/**
	 * Creates a new GeocognitionElement from the given source
	 * 
	 * @param remoteSource
	 *            the source to read
	 * @return the created GeocognitionElement
	 * @throws IOException
	 *             if the source cannot be readed
	 * @throws PersistenceException
	 *             if the geocognition element cannot be created
	 */
	private GeocognitionElement createTreeFromResource(Object remoteSource)
			throws IOException, PersistenceException {
		InputStream is;
		if (remoteSource instanceof GeocognitionElement) {
			return (GeocognitionElement) remoteSource;
		} else if (remoteSource instanceof File) {
			is = new FileInputStream((File) remoteSource);
		} else if (remoteSource instanceof URL) {
			is = ((URL) remoteSource).openStream();
		} else {
			Services.getErrorManager().error(
					"bug!",
					new IllegalArgumentException("The remote source "
							+ remoteSource + " is not valid"));
			return null;
		}

		Geocognition geocognition = Services.getService(Geocognition.class);
		GeocognitionElement tree = geocognition.createTree(is);
		is.close();

		return tree;
	}

	/**
	 * Gets the local GeocognitionElement
	 * 
	 * @return the local GeocognitionElement
	 */
	public GeocognitionElement getLocalElement() {
		return local;
	}

	/**
	 * Gets the remote GeocognitionElement
	 * 
	 * @return the remote GeocognitionElement
	 */
	public GeocognitionElement getRemoteElement() {
		return remote;
	}

	/**
	 * Resynchronizes the panel with the local geocognition and the remote
	 * source
	 * 
	 * @throws IOException
	 *             if the remote source cannot be readed
	 * @throws PersistenceException
	 *             if the remote tree cannot be created
	 * @throws GeocognitionException
	 *             if the local geocognition cannot be readed
	 */
	public void synchronize() throws IOException, PersistenceException,
			GeocognitionException {
		Geocognition geocognition = Services.getService(Geocognition.class);
		local = geocognition.getGeocognitionElement(local.getIdPath());
		local = local.cloneElement();
		setModel(local, remoteSource);
	}

	/**
	 * Executes the given operation on the selected element
	 * 
	 * @param operation
	 *            the operation to execute (CompareSplitPane.COMMIT or
	 *            CompareSplitPane.UPDATE constants)
	 */
	private void execute(int operation) {
		TreePath[] paths = treePanel.getTree().getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			TreeElement last = (TreeElement) paths[i].getLastPathComponent();
			IdPath path = last.getIdPath();

			if (operation == COMMIT) {
				manager.commit(path);
			} else {
				manager.update(path);
			}
		}
	}

	/**
	 * Sets the editor of the comparing pane. Searches in the available editors
	 * specified in the constructor and set the first editor accepting the
	 * element content
	 * 
	 * @param path
	 *            the path to the element to show in the editor
	 */
	private void setEditor(IdPath path) {
		if (closeEditor()) {
			GeocognitionElementDecorator local = null;
			GeocognitionElementDecorator remote = null;
			boolean remoteEditable = false;
			if (path != null) {
				local = manager.findLocalElement(path);
				remote = manager.findRemoteElement(path);
				remoteEditable = manager.isRemoteEditable();
			}

			String type;
			if (local == null && remote != null) {
				type = remote.getTypeId();
			} else if (local != null && remote == null) {
				type = local.getTypeId();
			} else if (local == null && remote == null) {
				type = null;
			} else {
				if (local.getTypeId().equals(remote.getTypeId())) {
					type = local.getTypeId();
				} else {
					type = null;
				}
			}

			for (ICompareEditor editor : editors) {
				if (editor.accepts(type)) {
					currentEditor = editor;
					split.setRightComponent(currentEditor.getComponent());

					currentEditor.setModel(local, remote);
					currentEditor.setEnabledLeft(true);
					currentEditor.setEnabledRight(remoteEditable);
					break;
				}
			}
		}
	}

	/**
	 * Asks the user to save resources when they have been changed and closes
	 * the editor if necessary
	 * 
	 * @return true if the editor has been closed, false otherwise
	 */
	private boolean closeEditor() {
		if (currentEditor == null) {
			return true;
		}

		boolean exit = true;

		// Save left resource?
		if (currentEditor.isLeftDirty()) {
			int option = JOptionPane.showConfirmDialog(null,
					LEFT_SAVE_BEFORE_CLOSING_TEXT,
					LEFT_SAVE_BEFORE_CLOSING_TITLE,
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				currentEditor.saveLeft();
			} else if (option == JOptionPane.CANCEL_OPTION) {
				exit = false;
			}
		}

		// Save right resource?
		if (exit && currentEditor.isRightDirty()) {
			int option = JOptionPane.showConfirmDialog(null,
					RIGHT_SAVE_BEFORE_CLOSING_TEXT,
					RIGHT_SAVE_BEFORE_CLOSING_TITLE,
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				currentEditor.saveRight();
			} else if (option == JOptionPane.CANCEL_OPTION) {
				exit = false;
			}
		}

		if (exit) {
			currentEditor.close();
		}

		return exit;
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Right click on an element of the tree to synchronize contents";
	}

	@Override
	public String validateInput() {
		return null;
	}

	@Override
	public String postProcess() {
		if (closeEditor()) {
			editors = null;
			currentEditor = null;
			treePanel = null;
			popup = null;
			addToGeocognition = addToFile = removeFromGeocognition = removeFromFile = null;
			overrideInGeocognition = overrideInFile = null;
			split = null;
			panel = null;
			manager = null;
			remoteSource = null;

			return null;
		} else {
			return "Closing aborted by user";
		}
	}

	/**
	 * Gets the available editors for the CompareSplitPane
	 * 
	 * @return the available editors
	 */
	private ArrayList<ICompareEditor> getAvailableEditors() {
		ArrayList<ICompareEditor> editors = new ArrayList<ICompareEditor>();
		editors.add(new CompareCodeEditor());
		editors.add(new CompareTextEditor());

		return editors;
	}

	/**
	 * Listener for the double left click and single right click on the tree
	 * 
	 * @author Victorzinho
	 * 
	 */
	private class CompareTreeListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			JTree tree = (JTree) e.getSource();
			Object root = tree.getModel().getRoot();
			if (!(root instanceof TreeElement)) {
				tree.setSelectionRow(-1);
				return;
			}

			// Get selection path
			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			if (tree.getSelectionPaths() == null) {
				return;
			}

			// Double left click
			if (selRow != -1 && e.getButton() == MouseEvent.BUTTON1
					&& e.getClickCount() == 2) {
				TreeElement selected = (TreeElement) tree.getPathForRow(selRow)
						.getLastPathComponent();

				if (!selected.isFolder()) {
					IdPath idPath = selected.getIdPath();
					if (manager.isModified(idPath) || (manager.isAdded(idPath))
							|| (manager.isDeleted(idPath))) {
						setEditor(idPath);
					}
				}
			} else if (selRow != -1 && e.getButton() == MouseEvent.BUTTON3) {
				// Single right click
				int[] selected = tree.getSelectionRows();
				boolean contains = false;
				if (selected != null) {
					for (int i = 0; i < selected.length; i++) {
						if (selected[i] == selRow) {
							contains = true;
							break;
						}
					}
				}

				if (!contains) {
					tree.setSelectionRow(selRow);
				}

				ArrayList<IdPath> idPaths = new ArrayList<IdPath>();
				TreePath[] paths = tree.getSelectionPaths();

				for (int i = 0; i < paths.length; i++) {
					TreeElement element = (TreeElement) paths[i]
							.getLastPathComponent();
					idPaths = merge(idPaths, getChangedChildren(element));
				}

				boolean deleted = true;
				boolean added = true;
				boolean changed = true;
				for (IdPath idPath : idPaths) {
					if (deleted && !manager.isDeleted(idPath)) {
						deleted = false;
					}

					if (added && !manager.isAdded(idPath)) {
						added = false;
					}

					if (changed && !manager.isModified(idPath)
							&& !manager.isConflict(idPath)) {
						changed = false;
					}
				}

				if (manager.isRemoteEditable()) {
					addToFile.setVisible(added);
					removeFromFile.setVisible(deleted);
					overrideInFile.setVisible(changed);
				}

				addToGeocognition.setVisible(deleted);
				removeFromGeocognition.setVisible(added);
				overrideInGeocognition.setVisible(changed);
				noAction.setVisible(!deleted && !added && !changed);

				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private ArrayList<IdPath> getChangedChildren(TreeElement element) {
			ArrayList<IdPath> arrayList = new ArrayList<IdPath>();
			if (manager.hasChanged(element.getIdPath())) {
				arrayList.add(element.getIdPath());
			} else {
				for (int i = 0; i < element.getElementCount(); i++) {
					TreeElement child = element.getElement(i);
					arrayList = merge(arrayList, getChangedChildren(child));
				}
			}

			return arrayList;
		}

		@SuppressWarnings("unchecked")
		private <T> ArrayList<T> merge(ArrayList<T> dest, ArrayList<T> adding) {
			Object[] aux = adding.toArray();
			for (int i = 0; i < aux.length; i++) {
				dest.add((T) aux[i]);
			}

			return dest;
		}
	}

	/**
	 * Listener in the synchronization manager that refreshes the editor
	 * 
	 * @author Victorzinho
	 */
	private class EditorSyncListener implements SyncListener {
		@Override
		public void syncDone() {
			if (currentEditor == null) {
				return;
			}

			GeocognitionElement left = currentEditor.getLeftElement();
			GeocognitionElement right = currentEditor.getRightElement();

			if (left != null && right == null) {
				IdPath path = new IdPath(left.getIdPath());
				// Check right addition
				GeocognitionElementDecorator remote = manager
						.findRemoteElement(path);
				if (remote != null) {
					currentEditor.setModel(left, remote);
				}

				// Check left deletion
				GeocognitionElementDecorator local = manager
						.findLocalElement(path);
				if (local == null) {
					currentEditor.setModel(null, null);
				}
			} else if (right != null && left == null) {
				IdPath path = new IdPath(right.getIdPath());
				// Check left addition
				GeocognitionElementDecorator local = manager
						.findLocalElement(path);
				if (local != null) {
					currentEditor.setModel(local, right);
				}

				// Check right deletion
				GeocognitionElementDecorator remote = manager
						.findRemoteElement(path);
				if (remote == null) {
					currentEditor.setModel(null, null);
				}
			}
		}
	}

	/**
	 * Job synchronizing two {@link GeocognitionElement} in {@link SyncManager}
	 * 
	 * @author victorziho
	 * 
	 */
	private class SynchronizingJob implements BackgroundJob {

		@Override
		public String getTaskName() {
			return "Synchronizing";
		}

		@Override
		public void run(IProgressMonitor pm) {
			manager.compare(local, remote, isSourceEditable(remoteSource), pm);
		}
	}

}

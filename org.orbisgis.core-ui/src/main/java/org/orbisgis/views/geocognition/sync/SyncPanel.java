package org.orbisgis.views.geocognition.sync;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

public class SyncPanel extends AbstractUIPanel {
	// String constants for the closing dialogs
	private static final String LEFT_SAVE_BEFORE_CLOSING_TEXT = "Editor will be closed. The local geocognition has been modified. "
			+ "Save changes?";
	private static final String LEFT_SAVE_BEFORE_CLOSING_TITLE = "Save left resource";
	private static final String RIGHT_SAVE_BEFORE_CLOSING_TEXT = "Editor will be closed. The remote file has been modified. "
			+ "Save changes?";
	private static final String RIGHT_SAVE_BEFORE_CLOSING_TITLE = "Save right resource";

	// Integer constants for synchronization type
	public static final int EXPORT = 0x1 << 1;
	public static final int IMPORT = 0x1 << 2;
	public static final int SYNCHRONIZATION = EXPORT | IMPORT;

	// Transaction constants
	public static final int COMMIT = 0;
	public static final int UPDATE = 1;

	// Interface
	private ArrayList<ICompareEditor> editors;
	private ICompareEditor currentEditor;
	private CompareTreePanel treePanel;
	private JSplitPane splitPane;
	private JPanel rootPanel;

	// Model
	private SyncManager manager;
	private GeocognitionElement localRoot, remoteRoot, localSource;
	private Object remoteSource;
	private ArrayList<IdPath> filterPaths;
	private int synchronizationType;

	/**
	 * Creates a new CompareSplitPane
	 */
	public SyncPanel() {
		manager = new SyncManager();
		manager.addSyncListener(new EditorSyncListener());

		// Right component
		editors = getAvailableEditors();

		// Left component
		treePanel = new CompareTreePanel(this);
		treePanel.getTree().addMouseListener(new CompareTreeListener());
		EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		treePanel.setIconRenderers(wh.getElementRenderers());

		// Split pane
		splitPane = new JSplitPane();
		splitPane.setLeftComponent(treePanel);

		// Put all together
		rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * Sets the model of the comparing pane
	 * 
	 * @param localElement
	 *            element to compare
	 * @param remoteObject
	 *            element to compare
	 * @param syncType
	 *            the type of synchronization. Use EXPORT, IMPORT or
	 *            SYNCHRONIZATION constants.
	 * 
	 * @param filter
	 *            list with the elements to show in the synchronization panel
	 * 
	 * @throws IOException
	 *             if the remote source cannot be readed
	 * @throws PersistenceException
	 *             if the remote element cannot be created from the remote
	 *             source
	 * @throws GeocognitionException
	 */
	public void setModel(GeocognitionElement localElement, Object remoteObject,
			int syncType, ArrayList<IdPath> filter) throws IOException,
			PersistenceException, GeocognitionException {
		// Set attributes
		filterPaths = filter;
		synchronizationType = syncType;
		remoteSource = remoteObject;
		localSource = localElement;

		// Install buttons in the tree panel if necessary (advanced
		// synchronization)
		if (syncType == SYNCHRONIZATION) {
			treePanel.installAdvancedFeatures();
		}

		synchronize();
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
		// Set empty editor
		setEditor(null);

		if (localSource != remoteSource) {
			localRoot = localSource.cloneElement();

			// Get local and remote elements
			if ((synchronizationType & IMPORT) != 0) {
				remoteRoot = createTreeFromResource(remoteSource);
			} else if ((synchronizationType & EXPORT) != 0) {
				if (isRemoteEditable()) {
					try {
						remoteRoot = createTreeFromResource(remoteSource);
					} catch (IOException e) {
						Geocognition gc = Services
								.getService(Geocognition.class);
						remoteRoot = gc.createFolder(localRoot.getId());
					}
				} else {
					Geocognition gc = Services.getService(Geocognition.class);
					remoteRoot = gc.createFolder(localRoot.getId());
				}
			}

			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new SynchronizingJob());
		} else {
			localRoot = remoteRoot = localSource;
			manager.compare(localRoot, remoteRoot, filterPaths);
		}

		refresh(synchronizationType);
	}

	/**
	 * Resynchronizes the panel with the local geocognition and the remote
	 * source
	 */
	public void refresh(int syncType) {
		synchronizationType = syncType;
		treePanel.setModel(manager, synchronizationType);
		if (currentEditor != null) {
			currentEditor.setEnabledLeft(isLocalEditable());
			currentEditor.setEnabledRight(isRemoteEditable());
		}

		rootPanel.repaint();
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
		return localRoot;
	}

	/**
	 * Gets the remote GeocognitionElement
	 * 
	 * @return the remote GeocognitionElement
	 */
	public GeocognitionElement getRemoteElement() {
		return remoteRoot;
	}

	/**
	 * Executes the given operation on the selected element
	 * 
	 * @param operation
	 *            the operation to execute (CompareSplitPane.COMMIT or
	 *            CompareSplitPane.UPDATE constants)
	 */
	public void execute(int operation) {
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
	 * element content. If <code>null</code> is specified in the path parameter,
	 * the open editor is closed without confirmation and setted as invisible
	 * 
	 * @param path
	 *            the path to the element to show in the editor
	 */
	private void setEditor(IdPath path) {
		if (path == null || closeEditor()) {
			GeocognitionElementDecorator local = null;
			GeocognitionElementDecorator remote = null;
			if (path != null) {
				local = manager.findLocalElement(path);
				remote = manager.findRemoteElement(path);
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
					splitPane.setRightComponent(currentEditor.getComponent());

					currentEditor.setModel(local, remote);
					currentEditor.setEnabledLeft(isLocalEditable());
					currentEditor.setEnabledRight(isRemoteEditable());
					break;
				}
			}

			currentEditor.getComponent().setVisible(path != null);
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
		return rootPanel;
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
			splitPane = null;
			rootPanel = null;
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
			if (!(root instanceof TreeElement)
					|| tree.getSelectionPaths() == null) {
				return;
			}

			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			if (selRow != -1 && e.getButton() == MouseEvent.BUTTON1
					&& e.getClickCount() == 2) {
				// Double left click
				TreeElement selected = (TreeElement) tree.getPathForRow(selRow)
						.getLastPathComponent();

				if (!selected.isFolder()) {
					IdPath idPath = selected.getIdPath();
					if (manager.isModified(idPath) || (manager.isAdded(idPath))
							|| (manager.isDeleted(idPath))) {
						setEditor(idPath);
					}
				}
			}
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
			manager.compare(localRoot, remoteRoot, filterPaths, pm);
		}
	}

	public SyncManager getSyncManager() {
		return manager;
	}

	/**
	 * Determines if the remote source is editable
	 * 
	 * @return true if the source is editable, false otherwise
	 */
	public boolean isRemoteEditable() {
		boolean editable;

		if (remoteSource instanceof GeocognitionElement) {
			editable = true;
		} else if (remoteSource instanceof File) {
			editable = true;
		} else if (remoteSource instanceof URL) {
			editable = false;
		} else {
			Services.getErrorManager().error(
					"bug!",
					new IllegalArgumentException("The remote source "
							+ remoteSource + " is not valid"));
			editable = false;
		}
		return editable && (synchronizationType & EXPORT) != 0;
	}

	public boolean isLocalEditable() {
		return (synchronizationType & IMPORT) != 0;
	}
}

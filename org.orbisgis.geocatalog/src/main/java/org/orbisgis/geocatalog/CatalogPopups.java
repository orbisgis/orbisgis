package org.orbisgis.geocatalog;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

/**
 * CatalogPopups handles creation of the popup menu when the user right clic in
 * GeoCatalog.
 *
 * Please always keep the same order of the items in the menu so user doesn't
 * get lost. Eg : "clear catalog" is always at the bottom.
 *
 * @author Samuel CHEMLA
 *
 */
public class CatalogPopups {

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionListener acl = null;

	public CatalogPopups(ActionListener acl) {
		this.acl = acl;
	}

	/**
	 * A command to clear catalog.
	 */
	private JMenuItem getClearCatalog() {
		JMenuItem menuItem = new JMenuItem("Clear catalog");
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("CLRCATALOG");

		Icon clearIcon = new ImageIcon(this.getClass().getResource("clear.png"));
		menuItem.setIcon(clearIcon);

		return menuItem;
	}

	/**
	 * A command to delete selected node
	 */
	private JMenuItem getDelete() {
		JMenuItem menuItem = new JMenuItem("Delete");
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("DEL");

		Icon removeNodeIcon = new ImageIcon(this.getClass().getResource(
				"remove.png"));
		menuItem.setIcon(removeNodeIcon);

		return menuItem;
	}

	/**
	 * A command to add a new folder
	 */
	private JMenuItem getNewFolder() {
		JMenuItem menuItem = new JMenuItem("New folder");

		menuItem.addActionListener(acl);
		menuItem.setActionCommand("NEWFOLDER");

		Icon newFolderIcon = new ImageIcon(this.getClass().getResource(
				"new_folder.png"));
		menuItem.setIcon(newFolderIcon);

		return menuItem;
	}

	/**
	 * This creates the popup menu when user click on a Resource
	 *
	 * @param node
	 * @return
	 */
	public JPopupMenu getResourcePopup(IResource node) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem[] items = node.getPopupActions();

		// Add user actions defined by the resource itself
		if (items != null) {
			for (JMenuItem item : items) {
				popup.add(item);
			}
		}

		// Add some standard actions
		popup.add(getDelete());
		popup.add(getClearCatalog());

		return popup;
	}

	public JPopupMenu getVoidPopup() {
		JPopupMenu popup = new JPopupMenu();

		// It is from far much simpler to add this item this way than as a
		// plugin
		popup.add(getNewFolder());

		/**
		 * Loads plugins and look for popups items
		 */
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] extensions = reg
				.getExtensions("org.orbisigs.plugin.geocatalog.popupAction");
		for (int i = 0; i < extensions.length; i++) {
			Configuration conf = extensions[i].getConfiguration();

			IPopupAction popupItems = (IPopupAction) conf
					.instantiateFromAttribute("", "class");
			popupItems.setCatalog(null);
			JMenuItem[] items = popupItems.getPopupActions();

			// Add user actions
			if (items != null) {
				for (JMenuItem item : items) {
					popup.add(item);
				}
			}
		}

		/**
		 * Here we add an entry which is shared by ResourcePopup and VoidPopup
		 * so it is complicated to do it in another way.
		 */
		popup.add(getClearCatalog());

		return popup;
	}
}

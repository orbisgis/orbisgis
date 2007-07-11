package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * CatalogPopups handles creation of the popup menu when the user right clic in
 * GeoCatalog.
 * 
 * TODO : Please always keep the same order of the items in the menu so user
 * doesn't get lost. Eg : "clear catalog" is always at the bottom.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class CatalogPopups {

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionsListener acl = null;

	// Icons
	private Icon addDataIcon = new ImageIcon(this.getClass().getResource(
			"addData.png"));

	private Icon removeNodeIcon = new ImageIcon(this.getClass().getResource(
			"remove.png"));

	private Icon clearIcon = new ImageIcon(this.getClass().getResource(
			"clear.png"));

	private Icon newFolderIcon = new ImageIcon(this.getClass().getResource(
			"new_folder.png"));

	private Icon openAttributesIcon = new ImageIcon(this.getClass()
			.getResource("openattributes.png"));

	public CatalogPopups(ActionsListener acl) {
		this.acl = acl;
	}

	public JPopupMenu getNodePopup(MyNode node) {
		JPopupMenu popup = new JPopupMenu();

		if (node.getType() == MyNode.folder) {
			popup.add(getNewFolder());
			popup.add(getAddDataSource());
			popup.add(getAddRaster());
			popup.add(getAddSld());
			popup.add(getAddSql());
		}
		popup.add(getDelete());
		popup.add(getClearCatalog());

		if (node.getType() == MyNode.datasource) {
			popup.add(getOpenAttributes());
		}
		return popup;
	}

	public JPopupMenu getVoidPopup() {
		JPopupMenu popup = new JPopupMenu();

		popup.add(getNewFolder());
		popup.add(getAddDataSource());
		popup.add(getAddRaster());
		popup.add(getAddSld());
		popup.add(getAddSql());
		popup.add(getClearCatalog());

		return popup;
	}

	private JMenuItem getAddDataSource() {
		JMenuItem menuItem = new JMenuItem("Add a DataSource");
		menuItem.setIcon(addDataIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("ADDSRC");
		return menuItem;
	}

	private JMenuItem getAddRaster() {
		JMenuItem menuItem = new JMenuItem("Add a raster file");
		menuItem.setIcon(addDataIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("ADDRASTER");
		return menuItem;
	}

	private JMenuItem getAddSld() {
		JMenuItem menuItem = new JMenuItem("Add a SLD file");
		menuItem.setIcon(addDataIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("ADDSLDFILE");
		return menuItem;
	}

	private JMenuItem getAddSql() {
		JMenuItem menuItem = new JMenuItem("Add a SQL Query");
		menuItem.setIcon(addDataIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("ADDSQL");
		return menuItem;
	}

	private JMenuItem getClearCatalog() {
		JMenuItem menuItem = new JMenuItem("Clear catalog");
		menuItem.addActionListener(acl);
		menuItem.setIcon(clearIcon);
		menuItem.setActionCommand("CLRCATALOG");
		return menuItem;
	}

	private JMenuItem getDelete() {
		JMenuItem menuItem = new JMenuItem("Delete");
		menuItem.setIcon(removeNodeIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("DEL");
		return menuItem;
	}

	private JMenuItem getNewFolder() {
		JMenuItem menuItem = new JMenuItem("New folder");
		menuItem.setIcon(newFolderIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("NEWFOLDER");
		return menuItem;
	}

	private JMenuItem getOpenAttributes() {
		JMenuItem menuItem = new JMenuItem("Open attributes");
		menuItem.setIcon(openAttributesIcon);
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("OPENATTRIBUTES");
		return menuItem;
	}

}

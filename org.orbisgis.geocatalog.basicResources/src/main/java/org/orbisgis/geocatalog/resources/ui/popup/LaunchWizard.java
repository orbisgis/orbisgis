package org.orbisgis.geocatalog.resources.ui.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IPopupAction;
import org.orbisgis.geocatalog.resources.wizards.AddFlatFilePanel;
import org.orbisgis.geocatalog.resources.wizards.AddSQL;
import org.orbisgis.geocatalog.resources.wizards.AddSourceChoosePanel;
import org.orbisgis.geocatalog.resources.wizards.WizardFrame;

/**
 * This class will provide an item in GeoCatalog popup to add SLD files or
 * Datasources...
 *
 * @author Samuel CHEMLA
 *
 */
public class LaunchWizard implements IPopupAction {

	private Catalog catalog;

	public JMenuItem[] getPopupActions() {
		JMenuItem[] items = new JMenuItem[3];
		items[0] = getAddDSWizard();
		items[1] = getSLDWizard();
		items[2] = getSQLWizard();
		return items;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Popup Item for adding datasources
	 *
	 * @return
	 */
	private JMenuItem getAddDSWizard() {
		JMenuItem menuItem = new JMenuItem("Add Datasource");
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				WizardFrame wizard = new WizardFrame(
						new AddSourceChoosePanel(), catalog);
			}

		});

		Icon icon = new ImageIcon(getClass().getResource("addData.png"));
		menuItem.setIcon(icon);

		return menuItem;
	}

	/**
	 * Popup Item for adding SLD styles
	 *
	 * @return
	 */
	private JMenuItem getSLDWizard() {
		JMenuItem menuItem = new JMenuItem("Add SLD");

		final String[][] supportedFiles = { { "sld" },
				{ "SLD style files (*.sld)" } };

		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				WizardFrame wizard = new WizardFrame(new AddFlatFilePanel(
						supportedFiles), catalog);
			}

		});

		Icon icon = new ImageIcon(getClass().getResource("addData.png"));
		menuItem.setIcon(icon);

		return menuItem;
	}

	private JMenuItem getSQLWizard() {
		JMenuItem menuItem = new JMenuItem("Add SQL");

		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				WizardFrame wizard = new WizardFrame(new AddSQL(), catalog);
			}

		});

		Icon icon = new ImageIcon(getClass().getResource("addData.png"));
		menuItem.setIcon(icon);

		return menuItem;
	}

}

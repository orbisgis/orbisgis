package org.orbisgis.geocatalog.resources.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.orbisgis.geocatalog.CRFlowLayout;
import org.orbisgis.geocatalog.CarriageReturn;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.resources.IResource;

/**
 * This frame is a simple container for any Wizard which implements
 * IAddRessourceWizard.
 *
 * It contains ok/cancel buttons and will add the ressource provided by the
 * wizard to the catalog.
 *
 */
public class WizardFrame extends JFrame {

	private JButton ok = null;
	private JButton cancel = null;
	private AssistantActionListener acl = null;

	public WizardFrame(IAddRessourceWizard wizard, Catalog myCatalog) {
		super();
		acl = new AssistantActionListener(wizard, myCatalog);
		setLayout(new CRFlowLayout());

		// Adds the panel coming right from the wizard plugin
		add(wizard.getWizardUI());
		add(new CarriageReturn());

		// Adds a few controls
		ok = new JButton("OK");
		ok.setActionCommand("OK");
		ok.addActionListener(acl);
		add(ok);

		cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL");
		cancel.addActionListener(acl);
		add(cancel);

		pack();
		setVisible(true);

	}

	private class AssistantActionListener implements ActionListener {

		private IAddRessourceWizard wizard = null;
		private Catalog myCatalog = null;

		public AssistantActionListener(IAddRessourceWizard wizard,
				Catalog myCatalog) {
			super();
			this.wizard = wizard;
			this.myCatalog = myCatalog;
		}

		public void actionPerformed(ActionEvent e) {

			if ("OK".equals(e.getActionCommand())) {
				setVisible(false);

				// As the user says OK we retrieve the new ressources and add
				// them to the catalog
				IResource[] resources = wizard.getNewResources();
				if (resources != null) {
					for (IResource resource : resources) {

						if (resource != null) {
							myCatalog.addNode(resource);
						}

					}
				}

			} else if ("CANCEL".equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}

}
